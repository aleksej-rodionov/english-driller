package space.rodionov.englishdriller.ui

import android.util.Log
import androidx.hilt.Assisted
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import space.rodionov.englishdriller.data.PreferencesManager
import space.rodionov.englishdriller.feature_words.domain.model.Word
import space.rodionov.englishdriller.data.WordDao
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "LOGS"

@HiltViewModel
class VocabularyViewModel @Inject constructor(
    private val wordDao: WordDao,
    private val preferencesManager: PreferencesManager,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    var categoryChosen = state.get<Int>("categoryChosen")
    val searchQuery = state.getLiveData("searchQuery", "")

    val catNumFlow = preferencesManager.categoryNumberFlow
    val onlyOffFlow = preferencesManager.onlyOffFlow

    val catNumOnlyOffFlow = preferencesManager.catNumNatLangOnlyOffFlow

//=====================================RXJAVA VARIANT===================================

    //    private val _wordsLivedata = MutableLiveData<List<Word>>()
    private val wordsMediatorLivedata = MediatorLiveData<List<Word>>()
    val wordsLivedata: LiveData<List<Word>> = wordsMediatorLivedata

    val catNumSubject = BehaviorSubject.create<Int>()
    val catNumObservable = catNumSubject.subscribeOn(Schedulers.io())

    val onlyOffSubject = BehaviorSubject.create<Boolean>()
    val onlyOffObservable = onlyOffSubject.subscribeOn(Schedulers.io())

    val searchSubject = BehaviorSubject.create<String>()
    val searchObservable = searchSubject.debounce(600, TimeUnit.MILLISECONDS)
        .subscribeOn(Schedulers.io())

//    val wordsSubject = BehaviorSubject.create<List<Word>>()
//    val wordsObservable = wordsSubject.subscribeOn(Schedulers.io())

    private fun wordsFlowable(query: String, catNum: Int, onlyOff: Boolean) =
        wordDao.getWordsRx(query, catNum, onlyOff)

//    val wordsLivedata: LiveData<List<Word>> = LiveDataReactiveStreams.fromPublisher(
//        getWordsRx.toFlowable(BackpressureStrategy.LATEST)
//    )

    init {
        viewModelScope.launch {
            catNumFlow.collect {
                catNumSubject.onNext(it)
            }
        }

        viewModelScope.launch {
            onlyOffFlow.collect {
                onlyOffSubject.onNext(it)
            }
        }

        Observable.combineLatest(
            searchObservable,
            catNumObservable,
            onlyOffObservable
        ) { o1, o2, o3 ->
            Triple(o1, o2, o3)
        }.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(object : Observer<Triple<String, Int, Boolean>> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                    Log.d(TAG, "MAIN onSubscribe: called")
                }

                override fun onNext(t: Triple<String, Int, Boolean>) {
                    Log.d(TAG, "MAIN onNext: called, t = ${t.first}, ${t.second}, ${t.third}")
                    val wordsSource: LiveData<List<Word>> =
                        LiveDataReactiveStreams.fromPublisher(wordDao.getWordsRx(
                            t.first, t.second, t.third))
                    wordsMediatorLivedata.addSource(wordsSource, Observer {
                        wordsMediatorLivedata.value = it
                        wordsMediatorLivedata.removeSource(wordsSource)
                    })
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "onError: ${e.localizedMessage}")
                }

                override fun onComplete() {
                    Log.d(TAG, "onComplete: called")
                }
            })

        /*searchObservable.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
//                    Log.d(TAG, "onSubscribe: called")
                }

                override fun onNext(t: String) {
//                    Log.d(TAG, "onNext: called, t = $t") // here is the searchQuery
//                    wordsFlowable()
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "onError: ${e.localizedMessage}")
                }

                override fun onComplete() {
                    Log.d(TAG, "onComplete: called")
                }
            })

        catNumObservable.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(object : Observer<Int> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
//                    Log.d(TAG, "onSubscribe: called")
                }

                override fun onNext(t: Int) {
//                    Log.d(TAG, "onNext: called, t = $t") // here is the catNumber
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "onError: ${e.localizedMessage}")
                }

                override fun onComplete() {
                    Log.d(TAG, "onComplete: called")
                }
            })

        onlyOffObservable.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(object : Observer<Boolean> {
                override fun onSubscribe(d: Disposable) {
//                    Log.d(TAG, "onSubscribe: called")
                }

                override fun onNext(t: Boolean) {
//                    Log.d(TAG, "onNext: called, t = $t") // here is the isOnlyOff boolean value
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "onError: ${e.localizedMessage}")
                }

                override fun onComplete() {
                    Log.d(TAG, "onComplete: called")
                }
            })*/
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

//================================COROUTINES VARIANT===================================

    private val vocabularyEventChannel = Channel<VocabularyEvent>()
    val vocabularyEvent = vocabularyEventChannel.receiveAsFlow()

    private val wordsFlow = combine(
        searchQuery.asFlow(),
        catNumOnlyOffFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        wordDao.getWords(
            query,
            filterPreferences.categoryChosen,
            filterPreferences.onlyOff
        )
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





