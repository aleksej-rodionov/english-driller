package space.rodionov.englishdriller

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val TAG = "VocabularyViewModel"

class VocabularyViewModel @ViewModelInject constructor(
    private val wordDao: WordDao,
    private val preferencesManager: PreferencesManager,
    @Assisted private val state: SavedStateHandle // part 11 navargs
) : ViewModel() {

    private val vocabularyEventChannel = Channel<VocabularyEvent>() // SEALED_EVENT 2,
    val vocabularyEvent = vocabularyEventChannel.receiveAsFlow() // SEALED_EVENT samt 4,

    var categoryChosen = state.get<Int>("categoryChosen") // part 11

    //    val searchQuery = MutableStateFlow("") // before part11 nav args implemented
    val searchQuery = state.getLiveData("searchQuery", "") // part 11 nav args new line in the code

    val catNumFlow = preferencesManager.categoryNumberFlow
    val catNumOnlyOffFlow = preferencesManager.catNumNatLangOnlyOffFlow
//    private val natLangFlow = preferencesManager.nativeLanguageFlow

    private val wordsFlow = combine(
        searchQuery.asFlow(), // part 11 added ".asFlow()"
        catNumOnlyOffFlow
    ) { query, /*categoryChosen*/ filterPreferences ->
        Pair(query, /*categoryChosen*/ filterPreferences)
    }.flatMapLatest { (query, /*categoryChosen*/ filterPreferences) ->
        wordDao.getWords(query, filterPreferences.categoryChosen, /*filterPreferences.nativeLanguage,*/ filterPreferences.onlyOff)
    }

    val words = wordsFlow.asLiveData() // PAY ATTEITION - THIS INSTEAD OF MLIVEDATALIST?

    val categoryNumber = catNumFlow.asLiveData()
//    val readNatLang = natLangFlow.asLiveData()

    suspend fun getCategoryName(categoryNumber: Int) = wordDao.getCategoryName(categoryNumber)

//    suspend fun getCategoryNameFlow(): Flow<String> = combine( catNumFlow, natLangFlow ) {
//            cN, nL -> wordDao.getCategoryName(cN, nL)
//    }

    fun onShowOnlyOffClick(onlyOff: Boolean) = viewModelScope.launch {
        preferencesManager.updateShowOnlyOff(onlyOff)
    }

    fun onChooseCategoryClick(chosenCategory: Int) = viewModelScope.launch {
        preferencesManager.updateCategoryChosen(chosenCategory) // I NEED TO USE THIS METHOD !!! (OR CHANGE FROM INT TO CATEGORYITEM??)
    }

    // part9(created), part11(filled)
    fun onWordSelected(word: Word) = viewModelScope.launch {
        vocabularyEventChannel.send(VocabularyEvent.NavigateToEditWordScreen(word))
    }

    fun onVocabularyCheckedChanged(word: Word, isChecked: Boolean) = viewModelScope.launch {
        wordDao.update(word.copy(shown = isChecked))
    }

    fun onWordSwiped(word: Word) = viewModelScope.launch {
        wordDao.delete(word)
        vocabularyEventChannel.send(VocabularyEvent.ShowUndoDeleteWordMessage(word))
    }

    fun onUndoDeleteClick(word: Word) = viewModelScope.launch {
        wordDao.insert(word)
    }

    suspend fun turnAllWordsOn(categoryNumber: Int) = wordDao.turnAllWordsOn(categoryNumber)

    fun onNewWordClick() = viewModelScope.launch {
        vocabularyEventChannel.send(VocabularyEvent.NavigateToAddWordScreen) // SEALED_EVENT 3
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_SOMETHING_RESULT_OK -> showWordSavedConfirmationMessage("Слово добавлено")
            EDIT_SOMETHING_RESULT_OK -> showWordSavedConfirmationMessage("Слово обновлено")
        }
    }

    private fun showWordSavedConfirmationMessage(text: String) = viewModelScope.launch {
        vocabularyEventChannel.send(VocabularyEvent.ShowWordSavedConfirmationMessage(text))
    }

    sealed class VocabularyEvent { // SEALED_EVENT 0
        object NavigateToAddWordScreen : VocabularyEvent() // SEALED_EVENT 1
        data class ShowUndoDeleteWordMessage(val word: Word) : VocabularyEvent()
        data class NavigateToEditWordScreen(val word: Word) : VocabularyEvent()
        data class ShowWordSavedConfirmationMessage(val msg: String) : VocabularyEvent()
    }
}





