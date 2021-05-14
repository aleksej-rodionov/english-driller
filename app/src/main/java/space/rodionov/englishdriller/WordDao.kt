package space.rodionov.englishdriller

import androidx.room.*
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(word: Word)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(categoryItem: CategoryItem)

    @Delete
    suspend fun delete(word: Word)

    @Delete
    suspend fun deleteCategory(categoryItem: CategoryItem)

    @Update
    suspend fun update(word: Word)

    @Update
    suspend fun updateCategory(categoryItem: CategoryItem)

    @Query("DELETE FROM word_table")
    suspend fun deleteAllWords()

    @Query("SELECT categoryNumber FROM category_table")
    suspend fun getAllCategoryNumbers(): List<Int>

    @Query("SELECT categoryNumber FROM category_table WHERE categoryShown = 1")
    fun getShownCategoriesNumbers(): Flow<List<Int>>

    suspend fun getCategoryName(categoryNumber: Int/*, nativeLanguage: NativeLanguage*/) =
        if (categoryNumber != 0) {
            /*when (nativeLanguage) {
                NativeLanguage.RUS -> */getCategoryNameRus(categoryNumber)
//                NativeLanguage.ENG -> getCategoryNameEng(categoryNumber)
//            }
        } else {
            /*when (nativeLanguage) {
                NativeLanguage.RUS -> */"Все слова"
//                NativeLanguage.ENG -> "All categories"
//            }
        }

    @Query("SELECT categoryNameRus FROM category_table WHERE categoryNumber = :categoryNumber")
    suspend fun getCategoryNameRus(categoryNumber: Int): String

//    @Query("SELECT categoryNameEng FROM category_table WHERE categoryNumber = :categoryNumber")
//    suspend fun getCategoryNameEng(categoryNumber: Int): String

    @Query("SELECT * FROM word_table WHERE category IN (:categoryList) AND shown = 1 ORDER BY RANDOM() LIMIT 4")
    fun get4wordsOld(categoryList: List<Int>): Flow<List<Word>> // are "?-s" needed? // SINGLE??

    @Query("SELECT * FROM word_table WHERE category IN (:categoryList) AND shown = 1 ORDER BY RANDOM() LIMIT 1")
    fun get1wordOld(categoryList: List<Int>): Flow<Word> // are "?-s" needed? // SINGLE??

    @Query("SELECT * FROM word_table WHERE category IN (SELECT categoryNumber FROM category_table WHERE categoryShown = 1) AND shown = 1 ORDER BY RANDOM() LIMIT 4")
    fun get4words(): Flow<List<Word>> // are "?-s" needed? // SINGLE??

    @Query("SELECT * FROM word_table WHERE category IN (SELECT categoryNumber FROM category_table WHERE categoryShown = 1) AND shown = 1 ORDER BY RANDOM() LIMIT 1")
    suspend fun get1word(): Word // are "?-s" needed? // убрать суспенд и добавить флоу<>

    @Query("SELECT * FROM word_table WHERE category IN (SELECT categoryNumber FROM category_table WHERE categoryShown = 1) AND shown = 1 ORDER BY RANDOM() LIMIT 4")
    fun get4wordsRx(): Single<List<Word>> // are "?-s" needed? // SINGLE??

    @Query("SELECT * FROM word_table WHERE category IN (SELECT categoryNumber FROM category_table WHERE categoryShown = 1) AND shown = 1 ORDER BY RANDOM() LIMIT 1")
    fun get1wordRx(): Single<Word>

    @Query("SELECT * FROM category_table")
    fun getAllCategories(): Flow<List<CategoryItem>>

    fun getWords(
        query: String,
        category: Int,
        /*nativeLanguage: NativeLanguage,*/
        onlyOff: Boolean
    ): Flow<List<Word>> =
        if (category != 0) {
//            when (nativeLanguage) {
                /*NativeLanguage.RUS -> */getWordsSortedByForeignNatRus(query, category, onlyOff)
//                NativeLanguage.ENG -> getWordsSortedByForeignNatEng(query, category, onlyOff)
//            }
        } else {
            /*when (nativeLanguage) {
                NativeLanguage.RUS ->*/ getAllWordsSortedByCategoryNatRus(query, onlyOff)
//                NativeLanguage.ENG -> getAllWordsSortedByCategoryNatEng(query, onlyOff)
//            }
        }

//    @Query("SELECT * FROM word_table WHERE category = :category AND (`nativ` LIKE '%' || :searchQuery || '%' OR `foreign` LIKE '%' || :searchQuery || '%') ORDER BY category DESC")
//    fun getWordsSortedByCategory(searchQuery: String, category: Int): Flow<List<Word>>

    @Query("SELECT * FROM word_table WHERE category = :category AND (shown != :onlyOff OR shown = 0) AND (rus LIKE '%' || :searchQuery || '%' OR `foreign` LIKE '%' || :searchQuery || '%') ORDER BY `foreign` ASC")
    fun getWordsSortedByForeignNatRus(
        searchQuery: String,
        category: Int,
        onlyOff: Boolean
    ): Flow<List<Word>>

    @Query("SELECT * FROM word_table WHERE (shown != :onlyOff OR shown = 0) AND (rus LIKE '%' || :searchQuery || '%' OR `foreign` LIKE '%' || :searchQuery || '%') ORDER BY category")
    fun getAllWordsSortedByCategoryNatRus(searchQuery: String, onlyOff: Boolean): Flow<List<Word>>

//    @Query("SELECT * FROM word_table WHERE category = :category AND (shown != :onlyOff OR shown = 0) AND (eng LIKE '%' || :searchQuery || '%' OR `foreign` LIKE '%' || :searchQuery || '%') ORDER BY `foreign` ASC")
//    fun getWordsSortedByForeignNatEng(
//        searchQuery: String,
//        category: Int,
//        onlyOff: Boolean
//    ): Flow<List<Word>>

//    @Query("SELECT * FROM word_table WHERE (shown != :onlyOff OR shown = 0) AND (eng LIKE '%' || :searchQuery || '%' OR `foreign` LIKE '%' || :searchQuery || '%') ORDER BY category")
//    fun getAllWordsSortedByCategoryNatEng(searchQuery: String, onlyOff: Boolean): Flow<List<Word>>

//    @Query("SELECT * FROM word_table WHERE (`nativ` LIKE '%' || :searchQuery || '%' OR `foreign` LIKE '%' || :searchQuery || '%') ORDER BY `foreign` ASC")
//    fun getAllWordsSortedByForeign(searchQuery: String): Flow<List<Word>>

//    @Query("SELECT * FROM word_table")
//    fun getAllWords(): Flow<List<Word>>

    @Query("UPDATE word_table SET shown = 1 WHERE category = :categoryNumber")
    suspend fun turnAllWordsOn(categoryNumber: Int)

    @Query("UPDATE category_table SET categoryShown = 1")
    suspend fun turnAllCategoriesOn()

}








