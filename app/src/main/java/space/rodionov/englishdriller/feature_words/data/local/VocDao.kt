package space.rodionov.englishdriller.feature_words.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.reactivex.Flowable
import space.rodionov.englishdriller.data.CategoryWithWords

@Dao
interface VocDao {

    @Transaction
    @Query("SELECT * FROM category_table")
    fun getAllCategoriesWithWords() : Flowable<List<CategoryWithWords>>


}