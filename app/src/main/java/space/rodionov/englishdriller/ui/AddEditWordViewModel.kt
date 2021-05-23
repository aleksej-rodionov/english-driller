package space.rodionov.englishdriller.ui

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import space.rodionov.englishdriller.data.Word
import space.rodionov.englishdriller.data.WordDao

class AddEditWordViewModel @ViewModelInject constructor(
    private val wordDao: WordDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val word = state.get<Word>("word")
    var wordForeign = state.get<String>("wordForeign") ?: word?.foreign ?: ""
        set(value) {
            field = value
            state.set("wordForeign", value)
        }
    var wordRus = state.get<String>("wordRus") ?: word?.rus ?: ""
        set(value) {
            field = value
            state.set("wordRus", value)
        }

    private val addEditWordEventChannel = Channel<AddEditWordEvent>()
    val addEditWordEvent = addEditWordEventChannel.receiveAsFlow()

    fun onSaveClick() {
        if (wordForeign.isBlank() || wordRus.isBlank()) {
            showInvalidInputMessage("Заполните все поля")
            return
        }

        if (word != null) {
            val updatedWord =
                word.copy(foreign = wordForeign, rus = wordRus/*, category = word.category*/)
            updateWord(updatedWord)
        } else {
            val newWord = Word(foreign = wordForeign, rus = wordRus, category = 5)
            createWord(newWord)
            /*viewModelScope.launch {
                if (!getAllCategoryNumbers().contains(5)) {
                    wordDao.insertCategory(CategoryItem("Свои слова", 5))
                }
            }*/
        }
    }

    private suspend fun getAllCategoryNumbers() = //viewModelScope.launch {
        wordDao.getAllCategoryNumbers()
//    }

    private fun createWord(word: Word) = viewModelScope.launch {
        wordDao.insert(word)
        addEditWordEventChannel.send(AddEditWordEvent.NavigateBackWithResult(ADD_SOMETHING_RESULT_OK))
    }

    private fun updateWord(word: Word) = viewModelScope.launch {
        wordDao.update(word)
        addEditWordEventChannel.send(
            AddEditWordEvent.NavigateBackWithResult(
                EDIT_SOMETHING_RESULT_OK
            )
        )
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        addEditWordEventChannel.send(AddEditWordEvent.ShowInvalidInputMessage(text))
    }

    sealed class AddEditWordEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddEditWordEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditWordEvent()
    }
}