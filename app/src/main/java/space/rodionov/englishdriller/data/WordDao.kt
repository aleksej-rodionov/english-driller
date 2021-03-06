package space.rodionov.englishdriller.data

import androidx.room.*
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

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
            getCategoryNameRus(categoryNumber)
        } else {
            "Все слова"
        }

    @Query("SELECT categoryNameRus FROM category_table WHERE categoryNumber = :categoryNumber")
    suspend fun getCategoryNameRus(categoryNumber: Int): String

    @Query("SELECT * FROM word_table WHERE category IN (:categoryList) AND shown = 1 ORDER BY RANDOM() LIMIT 4")
    fun get4wordsOld(categoryList: List<Int>): Flow<List<Word>>

    @Query("SELECT * FROM word_table WHERE category IN (:categoryList) AND shown = 1 ORDER BY RANDOM() LIMIT 1")
    fun get1wordOld(categoryList: List<Int>): Flow<Word>

    @Query("SELECT * FROM word_table WHERE category IN (SELECT categoryNumber FROM category_table WHERE categoryShown = 1) AND shown = 1 ORDER BY RANDOM() LIMIT 4")
    fun get4words(): Flow<List<Word>>

    @Query("SELECT * FROM word_table WHERE category IN (SELECT categoryNumber FROM category_table WHERE categoryShown = 1) AND shown = 1 ORDER BY RANDOM() LIMIT 1")
    suspend fun get1word(): Word

    @Query("SELECT * FROM word_table WHERE category IN (SELECT categoryNumber FROM category_table WHERE categoryShown = 1) AND shown = 1 ORDER BY RANDOM() LIMIT 4")
    fun get4wordsRx(): Single<List<Word>>

    @Query("SELECT * FROM word_table WHERE category IN (SELECT categoryNumber FROM category_table WHERE categoryShown = 1) AND shown = 1 ORDER BY RANDOM() LIMIT 1")
    fun get1wordRx(): Single<Word>

    @Query("SELECT * FROM category_table")
    fun getAllCategories(): Flow<List<CategoryItem>>

    fun getWords(
        query: String,
        category: Int,
        onlyOff: Boolean
    ): Flow<List<Word>> =
        if (category != 0) {
            getWordsSortedByForeignNatRus(query, category, onlyOff)
        } else {
            getAllWordsSortedByCategoryNatRus(query, onlyOff)
        }


    @Query("SELECT * FROM word_table WHERE category = :category AND (shown != :onlyOff OR shown = 0) AND (rus LIKE '%' || :searchQuery || '%' OR `foreign` LIKE '%' || :searchQuery || '%') ORDER BY `foreign` ASC")
    fun getWordsSortedByForeignNatRus(
        searchQuery: String,
        category: Int,
        onlyOff: Boolean
    ): Flow<List<Word>>

    @Query("SELECT * FROM word_table WHERE (shown != :onlyOff OR shown = 0) AND (rus LIKE '%' || :searchQuery || '%' OR `foreign` LIKE '%' || :searchQuery || '%') ORDER BY category")
    fun getAllWordsSortedByCategoryNatRus(searchQuery: String, onlyOff: Boolean): Flow<List<Word>>

    @Query("UPDATE word_table SET shown = 1 WHERE category = :categoryNumber")
    suspend fun turnAllWordsOn(categoryNumber: Int)

    @Query("UPDATE category_table SET categoryShown = 1")
    suspend fun turnAllCategoriesOn()

}








