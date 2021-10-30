package space.rodionov.englishdriller.feature_words.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import space.rodionov.englishdriller.feature_words.domain.model.Category
import space.rodionov.englishdriller.feature_words.domain.model.Word1

@Database(entities = [Word1::class, Category::class], version = 1, exportSchema = false)
abstract class VocDatabase : RoomDatabase() {

    abstract fun vocDao(): VocDao
}