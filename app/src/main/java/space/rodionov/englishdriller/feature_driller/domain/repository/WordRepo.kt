package space.rodionov.englishdriller.feature_driller.domain.repository

import kotlinx.coroutines.flow.Flow
import space.rodionov.englishdriller.core.Resource
import space.rodionov.englishdriller.feature_driller.domain.models.CatWithWords
import space.rodionov.englishdriller.feature_driller.domain.models.Category
import space.rodionov.englishdriller.feature_driller.domain.models.Word

interface WordRepo {

    fun getTenWords(): Flow<Resource<List<Word>>>

//    fun observeWords(query: String): Flow<Resource<List<Word>>>

    suspend fun updateWordIsActive(word: Word, isActive: Boolean)
    suspend fun updateIsWordActive(nativ: String, foreign: String, catName: String, isActive: Boolean)

    suspend fun getRandomWordFromActiveCats(activeCatsNames: List<String>) : Word

    fun observeWord(nativ: String, foreign: String, categoryName: String) : Flow<Word>

    fun wordsBySearchQuery(catName: String, searchQuery: String) : Flow<List<Word>>

//    fun observeActiveWordsByCat(catName: String) : Flow<List<WordEntity>>
//
//    fun observeAllWordsByCat(catName: String) : Flow<List<WordEntity>>

    fun observeAllCategories() : Flow<List<Category>>

    fun observeAllCategoriesWithWords() : Flow<List<CatWithWords>>

    suspend fun makeCategoryActive(catName: String, makeActive: Boolean)

    suspend fun getAllActiveCatsNames(): List<String>

    suspend fun getAllCatsNames(): List<String>

    suspend fun isCatActive(name: String): Boolean

    fun observeAllActiveCatsNames(): Flow<List<String>>

    fun getMode(): Flow<Int>
    suspend fun setMode(mode: Int)

    fun getFollowSystemMode(): Flow<Boolean>
    suspend fun setFollowSystemMode(follow: Boolean)

    fun getTransDir() : Flow<Boolean>
    suspend fun setTransDir(nativeToForeign: Boolean)

    fun storageCatName() : Flow<String>
    suspend fun updateStorageCat(catName: String)
}