package space.rodionov.englishdriller.ui

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import space.rodionov.englishdriller.data.PreferencesManager
import space.rodionov.englishdriller.feature_words.domain.model.Word
import space.rodionov.englishdriller.data.WordDao
import javax.inject.Inject

private const val TAG = "DrillerViewModel"

@HiltViewModel
class DrillerViewModel @Inject constructor(
    private val wordDao: WordDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _wordList = MutableStateFlow<List<Word>>(listOf())
    val wordList: StateFlow<List<Word>> = _wordList.asStateFlow()

    fun getNewPage() = viewModelScope.launch {
        val newWords = wordDao.getNewPage()
        val fullList = _wordList.value.toMutableList()
        fullList.addAll(newWords)
        _wordList.value = fullList.toList()
    }

    //=====================================PREFERENCES==========================
    private val transDirFlow = preferencesManager.translationDirectionFlow
    val readTransDir = transDirFlow.asLiveData()

    private val modeFlow = preferencesManager.modeFlow
    val mode = modeFlow.asLiveData()
    //===================================OLD ADAPTER METHODS===============

    var mList = ArrayList<Word>()
    val mLivedataList = MutableLiveData<List<Word>>()
    val m4words = wordDao.get4wordsRx()
    val m1word = wordDao.get1wordRx()
    val composite = CompositeDisposable()

    fun getLivedataList(): LiveData<List<Word>> {
        return mLivedataList
    }

    fun get4words() {
        m4words.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :
                SingleObserver<List<Word>> {
                override fun onSubscribe(d: Disposable) {
                    composite.add(d)
                }

                override fun onSuccess(t: List<Word>) {
                    mList.addAll(t)
                    mLivedataList.value = mList
                }

                override fun onError(e: Throwable) {
                    // empty
                }

            })
    }

    fun removeAndAddWord(word: Word) {
        mList = ArrayList<Word>(mList)
        mList.remove(word)
        Log.d(TAG, "(drlr) removeAndAddWord: mList.size =  " + mList.size)
        m1word.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Word> {
                override fun onSubscribe(d: Disposable) {
                    composite.add(d)
                }

                override fun onSuccess(t: Word) {
                    mList.add(t)
                    mLivedataList.value = mList
                    Log.d(TAG, "(drlr) removeAndAddWord called, now mList contains: ")
                    mList.forEach { item -> Log.d(TAG, "(drlr) " + item.foreign) }
                }

                override fun onError(e: Throwable) {
                    // empty
                }

            })
    }

    fun update(word: Word, isShown: Boolean) = viewModelScope.launch {
        wordDao.update(word.copy(shown = isShown))
    }
}



