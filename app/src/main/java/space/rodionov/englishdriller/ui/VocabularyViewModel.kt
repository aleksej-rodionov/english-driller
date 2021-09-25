package space.rodionov.englishdriller.ui

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import space.rodionov.englishdriller.data.PreferencesManager
import space.rodionov.englishdriller.data.Word
import space.rodionov.englishdriller.data.WordDao

private const val TAG = "VocabularyViewModel"

class VocabularyViewModel @ViewModelInject constructor(
    private val wordDao: WordDao,
    private val preferencesManager: PreferencesManager,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    private val vocabularyEventChannel = Channel<VocabularyEvent>()
    val vocabularyEvent = vocabularyEventChannel.receiveAsFlow()

    var categoryChosen = state.get<Int>("categoryChosen")

    val searchQuery = state.getLiveData("searchQuery", "")

    val catNumFlow = preferencesManager.categoryNumberFlow
    val catNumOnlyOffFlow = preferencesManager.catNumNatLangOnlyOffFlow

    private val wordsFlow = combine(
        searchQuery.asFlow(),
        catNumOnlyOffFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        wordDao.getWords(query, filterPreferences.categoryChosen, filterPreferences.onlyOff)
    }

    val words = wordsFlow.asLiveData()

    val categoryNumber = catNumFlow.asLiveData()

    suspend fun getCategoryName(categoryNumber: Int) = wordDao.getCategoryName(categoryNumber)


    fun onShowOnlyOffClick(onlyOff: Boolean) = viewModelScope.launch {
        preferencesManager.updateShowOnlyOff(onlyOff)
    }

    fun onChooseCategoryClick(chosenCategory: Int) = viewModelScope.launch {
        preferencesManager.updateCategoryChosen(chosenCategory)
    }

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
        vocabularyEventChannel.send(VocabularyEvent.NavigateToAddWordScreen)
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

    sealed class VocabularyEvent {
        object NavigateToAddWordScreen : VocabularyEvent()
        data class ShowUndoDeleteWordMessage(val word: Word) : VocabularyEvent()
        data class NavigateToEditWordScreen(val word: Word) : VocabularyEvent()
        data class ShowWordSavedConfirmationMessage(val msg: String) : VocabularyEvent()
    }
}





