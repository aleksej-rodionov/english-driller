package space.rodionov.englishdriller.ui

import android.util.Log
import androidx.hilt.Assisted
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import space.rodionov.englishdriller.feature_words.domain.model.Word
import space.rodionov.englishdriller.data.WordDao
import javax.inject.Inject

private const val TAG = "LOGS"

@HiltViewModel
class AddEditWordViewModel @Inject constructor(
    private val wordDao: WordDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

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
            val newWord = Word(foreign = wordForeign, rus = wordRus, category = 7)
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
//        wordDao.insert(word) // here change
        insertWord(word)
        addEditWordEventChannel.send(AddEditWordEvent.NavigateBackWithResult(ADD_SOMETHING_RESULT_OK))
    }

    private fun insertWord(word: Word) {
      val disposable: Disposable = Single.just(word)
            .subscribeOn(Schedulers.io())
            .subscribe({
                wordDao.insert(it)
            }, {
                Log.d(TAG, it.localizedMessage)
            })
        compositeDisposable.add(disposable)
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

    fun disposeDisposables() {
        compositeDisposable.dispose()
    }
}