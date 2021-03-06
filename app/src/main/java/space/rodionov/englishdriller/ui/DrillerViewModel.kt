package space.rodionov.englishdriller.ui

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import space.rodionov.englishdriller.data.PreferencesManager
import space.rodionov.englishdriller.data.Word
import space.rodionov.englishdriller.data.WordDao

private const val TAG = "DrillerViewModel"

class DrillerViewModel @ViewModelInject constructor(
    private val wordDao: WordDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    var mList = ArrayList<Word>()
    val mLivedataList = MutableLiveData<List<Word>>()
    val m4words = wordDao.get4wordsRx()
    val m1word = wordDao.get1wordRx()
    val composite = CompositeDisposable()


    private val transDirFlow = preferencesManager.translationDirectionFlow
    val readTransDir = transDirFlow.asLiveData()

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

    fun update(word: Word) = viewModelScope.launch {
        wordDao.update(word)
    }

}



