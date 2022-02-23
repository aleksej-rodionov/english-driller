package space.rodionov.englishdriller.feature_driller.presentation.driller

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import space.rodionov.englishdriller.core.Resource
import space.rodionov.englishdriller.feature_driller.domain.models.Word
import space.rodionov.englishdriller.feature_driller.domain.use_cases.*
import javax.inject.Inject

@HiltViewModel
class DrillerViewModel @Inject constructor(
    private val getTenWordsUseCase: GetTenWordsUseCase,
    private val updateWordIsActiveUseCase: UpdateWordIsActiveUseCase,
    private val observeAllCategories: ObserveAllCategoriesUseCase,
    private val makeCategoryActiveUseCase: MakeCategoryActiveUseCase,
    private val getAllActiveCatsNamesUseCase: GetAllActiveCatsNamesUseCase,
    private val getAllCatsNamesUseCase: GetAllCatsNamesUseCase,
    private val isCategoryActive: IsCategoryActiveUseCase,
    private val getRandomWord: GetRandomWordUseCase,
    private val observeTranslationDirectionUseCase: ObserveTranslationDirectionUseCase,
    private val observeMode: ObserveModeUseCase,
    private val state: SavedStateHandle
) : ViewModel() {
    var savedPosition = state.get<Int>("savedPos") ?: 0
        set(value) {
            field = value
            state.set("savedPos", value)
        }

    var rememberPositionAfterSwitchingFragment = state.get<Boolean>("memorizePosInBackstack") ?: false
        set(value) {
            field = value
            state.set("memorizePosInBackstack", value)
        }

    var rememberPositionAfterDestroy = state.get<Boolean>("memorizePosOnDestroy") ?: false
        set(value) {
            field = value
            state.set("memorizePosOnDestroy", value)
        }

    private val _transDir = observeTranslationDirectionUseCase.invoke()
    val transDir = _transDir.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val _mode = observeMode.invoke()
    val mode = _mode.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    private val snapshotCatsInCaseUncheckAll = mutableListOf<String>()
    var rememberPositionAfterChangingStack = false

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition = _currentPosition.asStateFlow() // todo сохранять currentPosition в savedStateHandle

    private val _categories = observeAllCategories.invoke()
    val categories = _categories.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val _wordsState = MutableStateFlow(WordState())
    val wordsState = _wordsState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<DrillerEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class DrillerEvent {
        data class ShowSnackbar(val msg: String) : DrillerEvent()
        object ScrollToCurrentPosition : DrillerEvent()
        object ScrollToSavedPosition : DrillerEvent()
        object NavigateToCollectionScreen : DrillerEvent()
        object NavigateToSettings : DrillerEvent()
        object OpenFilterBottomSheet : DrillerEvent()
        data class SpeakWord(val word: String) : DrillerEvent()
    }

    init {
        newRound()
        makeSnapshot()
    }

    fun addTenWords() = viewModelScope.launch {
//        Log.d(TAG_PETR, "VM addTenWords: CALLED")
        getTenWordsUseCase().onEach { result -> // onEach = on each emission of the flow
            val oldPlusNewWords = mutableListOf<Word>()
            oldPlusNewWords.addAll(wordsState.value.words)

            when (result) {
                is Resource.Loading -> {
                    oldPlusNewWords.addAll(result.data ?: emptyList())
                    _wordsState.value = wordsState.value.copy(
                        words = oldPlusNewWords,
                        isLoading = true
                    )
                }
                is Resource.Success -> {
                    oldPlusNewWords.addAll(result.data ?: emptyList())
                    _wordsState.value = wordsState.value.copy(
                        words = oldPlusNewWords,
                        isLoading = false
                    )
                }
                is Resource.Error -> {
                    // этот вариант (ошибка) никогда не приходит, т.к. в repo никогда не эмиттится
                    _wordsState.value = wordsState.value.copy(
                        words = wordsState.value.words ?: mutableListOf(),
                        isLoading = false
                        // todo обработать ошибку?
                    )
                }
            }
        }.launchIn(this) // this относится к viewModelScope, в котором onEach этот завернут
    }

    fun newRound() {
        _wordsState.value = WordState()
        addTenWords()
    }

    fun updateCurrentPosition(pos: Int) {
        _currentPosition.value = pos
//        updateSavedPosition(pos)
    }

    fun updateSavedPosition(pos: Int) {
        savedPosition = pos
    }

    fun inactivateCurrentWord() = viewModelScope.launch {
        val word = wordsState.value.words[currentPosition.value]
        updateWordIsActiveUseCase(word, false)
    }

//====================METHODS FOR BOTTOMSHEET CHIPGROUP================================

    fun onChipTurnedOn(catName: String) = viewModelScope.launch {
        if (checkIfOnlyOneInactiveCat()) {
            makeSnapshot()
        }
        activateCategory(catName)
    }

    fun onChipTurnedOff(catName: String) = viewModelScope.launch {
        inactivateCategory(catName)
    }

    fun onCheckBoxTurnedOn() = viewModelScope.launch {
        makeSnapshot()
        val allCats = getAllCatsNamesUseCase.invoke()
        allCats.forEach { catName ->
            activateCategory(catName)
        }
//        Log.d(TAG_PETR, "onCheckBoxTurnedOn: allActive.size = ${getAllActiveCatsNamesUseCase.invoke().size}")
//        Log.d(TAG_PETR, "onCheckBoxTurnedOn: snapshot.size = ${snapshotCatsInCaseUncheckAll.size}")
    }

    fun onCheckBoxTurnedOff() = viewModelScope.launch {
        val allCatsNames = getAllCatsNamesUseCase.invoke()
        allCatsNames.forEach { name ->
            if (!snapshotCatsInCaseUncheckAll.contains(name)) {
                inactivateCategory(name)
            }
        }
    }

    private suspend fun checkIfOnlyOneInactiveCat(): Boolean {
        val allCats = getAllCatsNamesUseCase.invoke()
        val allActiveCats = getAllActiveCatsNamesUseCase.invoke()
        return allCats.size - allActiveCats.size == 1
    }

    private fun addCatNameToSnapshot(name: String) {
        if (!snapshotCatsInCaseUncheckAll.contains(name)) {
            snapshotCatsInCaseUncheckAll.add(name)
        }
    }

    private fun refulfillSnapshotByNewNames(newNames: List<String>) {
        snapshotCatsInCaseUncheckAll.clear()
        newNames.forEach { name ->
            addCatNameToSnapshot(name)
        }
    }

    private fun activateCategory(catName: String) = viewModelScope.launch {
        makeCategoryActiveUseCase(catName, true)
    }

    private suspend fun inactivateCategory(catName: String) {
        makeCategoryActiveUseCase(catName, false)
    }

    fun showNotLessThanOneCategory(msg: String) = viewModelScope.launch {
        _eventFlow.emit(DrillerEvent.ShowSnackbar(msg))
    }

    private fun makeSnapshot() = viewModelScope.launch {
        val allCats = getAllCatsNamesUseCase.invoke()
        val allActiveCats = getAllActiveCatsNamesUseCase.invoke()
        if (allActiveCats.size == allCats.size && allActiveCats.isNotEmpty()) {
            val singleCat = listOf(allActiveCats[0])
            refulfillSnapshotByNewNames(singleCat)
        } else {
            refulfillSnapshotByNewNames(allActiveCats)
        }
//        Log.d(TAG_PETR, "snapshot created. Size = ${snapshotCatsInCaseUncheckAll.size}")
    }

    fun scrollToCurPos() = viewModelScope.launch {
        _eventFlow.emit(DrillerEvent.ScrollToCurrentPosition)
//        Log.d(TAG_PETR, "scrollToCurPos: CALLED, curPos = ${currentPosition.value}")
    }

    fun scrollToSavedPosIfItIsSaved() = viewModelScope.launch {
        if (rememberPositionAfterSwitchingFragment || rememberPositionAfterDestroy) {
            _eventFlow.emit(DrillerEvent.ScrollToSavedPosition)
//            Log.d(TAG_PETR, "scrollToSavedPos: CALLED, savedPos = $savedPosition")
        } else {
//            Log.d(TAG_PETR, "saved Position is not remembered")
        }
    }

    fun acceptCatListChange() = viewModelScope.launch {
        val wholeList = mutableListOf<Word>()
        wholeList.addAll(wordsState.value.words)
        _wordsState.value = wordsState.value.copy(
            words = wholeList,
            isLoading = true
        )
//        Log.d(TAG_PETR, "wholeList.size = ${wholeList.size}, curPos = ${currentPosition.value}, curPosWord = ${wholeList.elementAt(currentPosition.value)}")
        delay(500L)
        val newWholeList = mutableListOf<Word>()
        val allActiveCatsNames = getAllActiveCatsNamesUseCase.invoke()
        newWholeList.addAll(wholeList.map { word ->
            if (!isCategoryActive.invoke(word.categoryName)) {
                val newWord = getRandomWord.invoke(allActiveCatsNames)
                newWord
            } else word
        })
        _wordsState.value = wordsState.value.copy(
            words = newWholeList,
            isLoading = false
        )
        rememberPositionAfterChangingStack = true
//        Log.d(TAG_PETR, "newWholeList.size = ${newWholeList.size}, curPos = ${currentPosition.value}, curPosWord = ${wholeList.elementAt(currentPosition.value)}")
    }

    fun navigateToCollectionScreen() = viewModelScope.launch {
        _eventFlow.emit(DrillerEvent.NavigateToCollectionScreen)
    }

    fun navigateToSettings() = viewModelScope.launch {
        _eventFlow.emit(DrillerEvent.NavigateToSettings)
    }

    fun openFilterBottomSheet() = viewModelScope.launch {
        _eventFlow.emit(DrillerEvent.OpenFilterBottomSheet)
    }

    fun rememberPositionAfterSwitchFragment() {
        updateSavedPosition(currentPosition.value)
        rememberPositionAfterSwitchingFragment = true
    }

    fun rememberPositionInCaseOfDestroy() {
        updateSavedPosition(currentPosition.value)
        rememberPositionAfterDestroy = true
    }

    fun speakWord(word: String) = viewModelScope.launch {
        _eventFlow.emit(DrillerEvent.SpeakWord(word))
    }
}

data class WordState(
    val words: MutableList<Word> = mutableListOf(),
    val isLoading: Boolean = false
)