package space.rodionov.englishdriller.data

import android.app.Application
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import space.rodionov.englishdriller.ApplicationScope
import space.rodionov.englishdriller.R
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Word::class, CategoryItem::class], version = 1, exportSchema = false)
abstract class WordDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    class Callback @Inject constructor(
        private val context: Application, // gonna be error???
        private val database: Provider<WordDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {



        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().wordDao()

            applicationScope.launch {
                dao.insertCategory(CategoryItem("Фразочки и речевые связки", 1))
                dao.insertCategory(CategoryItem("Фразовые глаголы", 2))
                dao.insertCategory(CategoryItem("Обычные глаголы", 3))
                dao.insertCategory(CategoryItem("Просто обычные слова", 4))
                dao.insertCategory(CategoryItem("Топ 800 базовых слов", 5))
                dao.insertCategory(CategoryItem("Топ 200 базовых глаголов", 6))
                dao.insertCategory(CategoryItem("Свои слова", 7))

                val rus1 = context.resources.getStringArray(R.array.rus1).toList()
                val rus2 = context.resources.getStringArray(R.array.rus2).toList()
                val rus3 = context.resources.getStringArray(R.array.rus3).toList()
                val rus4 = context.resources.getStringArray(R.array.rus4).toList()
                val rus5 = context.resources.getStringArray(R.array.rus5).toList()
                val rus6 = context.resources.getStringArray(R.array.rus6).toList()
                val foreign1 = context.resources.getStringArray(R.array.eng1).toList()
                val foreign2 = context.resources.getStringArray(R.array.eng2).toList()
                val foreign3 = context.resources.getStringArray(R.array.eng3).toList()
                val foreign4 = context.resources.getStringArray(R.array.eng4).toList()
                val foreign5 = context.resources.getStringArray(R.array.eng5).toList()
                val foreign6 = context.resources.getStringArray(R.array.eng6).toList()

                for (i in foreign1.indices) { dao.insert(Word(rus1[i], foreign1[i], 1)) }
                for (i in foreign2.indices) { dao.insert(Word(rus2[i], foreign2[i], 2)) }
                for (i in foreign3.indices) { dao.insert(Word(rus3[i], foreign3[i], 3)) }
                for (i in foreign4.indices) { dao.insert(Word(rus4[i], foreign4[i], 4)) }
                for (i in foreign5.indices) { dao.insert(Word(rus5[i], foreign5[i], 5)) }
                for (i in foreign6.indices) { dao.insert(Word(rus6[i], foreign6[i], 6)) }

            }

        }
    }

}




