package space.rodionov.englishdriller.feature_driller.presentation.wordlist

import android.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class WordlistViewModel @Inject constructor(
    private val catNameFromStorageUseCase: CatNameFromStorageUseCase,
    private val updateCatNameStorageUseCase: UpdateCatNameStorageUseCase,
    private val observeWordsSearchQueryUseCase: ObserveWordsSearchQueryUseCase,
    private val observeWordUseCase: ObserveWordUseCase,
    private val updateWordIsActiveUseCase: UpdateWordIsActiveUseCase,
    private val updateIsWordActiveUseCase: UpdateIsWordActiveUseCase,
    private val observeModeUseCase: ObserveModeUseCase,
    private val state: SavedStateHandle
) : ViewModel() {
    //    var wordLivedata = state.getLiveData<Word?>("wordLivedata", null)
    var nativLivedata = state.getLiveData<String>("nativLivedata", null)
    var foreignLivedata = state.getLiveData<String>("foreignLivedata", null)
    var catNameLivedata = state.getLiveData<String?>("catNameLivedata", null)

    private val _word = combine(
        nativLivedata.asFlow(),
        foreignLivedata.asFlow(),
        catNameLivedata.asFlow()
    ) { nativ, foreign, catName ->
        Triple(nativ, foreign, catName)
    }.flatMapLatest { (nativ, foreign, catName) ->
        observeWordUseCase.invoke(nativ, foreign, catName)
    }
    val word = _word.stateIn(viewModelScope, SharingStarted.Lazily, null)

//    private val _word = observeWordUseCase.invoke(wordInDialog.value)
//    val word = _word.stateIn(viewModelScope, SharingStarted.Lazily, null)

    var catToSearchIn = state.getLiveData<Category>("category", null)
    val catNameFlow = catNameFromStorageUseCase.invoke()

    val searchQuery = state.getLiveData("searchQuery", "")

    private val _mode = observeModeUseCase.invoke()
    val mode = _mode.stateIn(viewModelScope, SharingStarted.Lazily, MODE_LIGHT)

    private val _eventFlow = MutableSharedFlow<WordlistEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class WordlistEvent {
        data class OpenWordBottomSheet(val word: Word) : WordlistEvent()
        data class SpeakWord(val word: String) :WordlistEvent()
    }

    private val wordsFlow = combine(
        catNameFlow,
        searchQuery.asFlow()
//        triggerSearchQuery()
    ) { catName, query ->
        Pair(catName, query)
    }.flatMapLatest { (catName, query) ->
        observeWordsSearchQueryUseCase.invoke(catName, query)
    }

    val words = wordsFlow.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private var searchJob: Job? = null
    fun onSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L)
            searchQuery.value = query
            Log.d(TAG_PETR, "onSearch: query updated as $query")
        }
    }

    fun updateCatStorage(catName: String) = viewModelScope.launch {
        updateCatNameStorageUseCase.invoke(catName)
    }

    fun openWordBottomSheet(word: Word) = viewModelScope.launch {
//        wordInDialog.value = word
        _eventFlow.emit(WordlistEvent.OpenWordBottomSheet(word))
    }

    fun activateWord() = viewModelScope.launch {
        nativLivedata.value?.let { nativ ->
            foreignLivedata.value?.let { foreign ->
                catNameLivedata.value?.let { catName ->
                    updateIsWordActiveUseCase.invoke(nativ, foreign, catName, true)
                }
            }
        }
    }

    fun inactivateWord() = viewModelScope.launch {
        nativLivedata.value?.let { nativ ->
            foreignLivedata.value?.let { foreign ->
                catNameLivedata.value?.let { catName ->
                    updateIsWordActiveUseCase.invoke(nativ, foreign, catName, false)
                }
            }
        }
    }

    fun speakWord(word: String) = viewModelScope.launch {
        _eventFlow.emit(WordlistEvent.SpeakWord(word))
    }
}


