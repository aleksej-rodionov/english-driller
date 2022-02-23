package space.rodionov.englishdriller.feature_driller.data.repository

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import space.rodionov.englishdriller.core.Resource
import space.rodionov.englishdriller.feature_driller.data.local.WordDao
import space.rodionov.englishdriller.feature_driller.data.storage.Datastore
import space.rodionov.englishdriller.feature_driller.domain.models.CatWithWords
import space.rodionov.englishdriller.feature_driller.domain.models.Category
import space.rodionov.englishdriller.feature_driller.domain.models.Word
import space.rodionov.englishdriller.feature_driller.domain.repository.WordRepo
import space.rodionov.englishdriller.feature_driller.utils.Constants.TAG_PETR

class WordRepoImpl(
    private val dao: WordDao,
    private val datastore: Datastore
) : WordRepo {

    override fun getTenWords(): Flow<Resource<List<Word>>> = flow {
        emit(Resource.Loading())
        delay(500L) // для пробы
        val words = dao.getTenWords().map { it.toWord() }
        emit(Resource.Success(words))
        // todo обработать Resource.Error ??
    }

    override suspend fun updateWordIsActive(word: Word, isActive: Boolean) {
        val wordEntity = dao.getWord(word.nativ, word.foreign, word.categoryName)
        wordEntity.let {
            Log.d(TAG_PETR, "updateWordIsActive: word found and changed")
            dao.updateWord(it.copy(isWordActive = isActive))
        }
    }

    override suspend fun updateIsWordActive(
        nativ: String,
        foreign: String,
        catName: String,
        isActive: Boolean
    ) {
        val wordEntity = dao.getWord(nativ, foreign, catName)
        wordEntity.let {
            Log.d(TAG_PETR, "updateWordIsActive: word found and changed")
            dao.updateWord(it.copy(isWordActive = isActive))
        }
    }

    override fun observeWord(
        nativ: String,
        foreign: String,
        categoryName: String
    ): Flow<Word> {
        return dao.observeWord(nativ, foreign, categoryName).map {
            it.let { we ->
                we.toWord()
            }
        }
    }

    override suspend fun getRandomWordFromActiveCats(activeCatsNames: List<String>): Word {
        return dao.getRandomWordFromActiveCats(activeCatsNames).toWord()
    }

    override fun wordsBySearchQuery(catName: String, searchQuery: String) =
        dao.observeWords(catName, searchQuery).map { words ->
            words.map {
                it.toWord()
            }
        }

    override fun observeAllCategories(): Flow<List<Category>> =
        dao.observeAllCategories().map { cats ->
            cats.map { it.toCategory() }
        }

    override fun observeAllCategoriesWithWords(): Flow<List<CatWithWords>> {
        return dao.observeAllCategoriesWithWords().map { cwws ->
            cwws.map {
                val category = it.categoryEntity.toCategory()
                val words = it.words.map { we -> we.toWord() }
                CatWithWords(category, words)
            }
        }
    }

    override suspend fun makeCategoryActive(catName: String, makeActive: Boolean) {
        val categoryEntity = dao.getCategoryByName(catName)
        dao.updateCategory(categoryEntity.copy(isCategoryActive = makeActive))
    }

    override suspend fun getAllActiveCatsNames(): List<String> {
        return dao.getALlActiveCatsNames()
    }

    override suspend fun getAllCatsNames(): List<String> {
        return dao.getAllCatNames()
    }

    override suspend fun isCatActive(name: String): Boolean = dao.isCategoryActive(name)

    override fun observeAllActiveCatsNames(): Flow<List<String>> = dao.observeAllActiveCatsNames()

    override fun getMode(): Flow<Int> = datastore.modeFlow
    override suspend fun setMode(mode: Int) = datastore.updateMode(mode)

    override fun getFollowSystemMode(): Flow<Boolean> = datastore.followSystemModeFlow
    override suspend fun setFollowSystemMode(follow: Boolean) = datastore.updateFollowSystemMode(follow)


    override fun getTransDir(): Flow<Boolean> = datastore.translationDirectionFlow
    override suspend fun setTransDir(nativeToForeign: Boolean) {
        datastore.updatetranslationDirection(nativeToForeign)
    }

    override fun storageCatName(): Flow<String> = datastore.categoryFlow
    override suspend fun updateStorageCat(catName: String) = datastore.updateCategoryChosen(catName)
}