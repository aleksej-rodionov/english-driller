package space.rodionov.englishdriller.feature_driller.data.local

import android.app.Application
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import space.rodionov.englishdriller.R
import space.rodionov.englishdriller.feature_driller.data.local.entity.CategoryEntity
import space.rodionov.englishdriller.feature_driller.data.local.entity.WordEntity
import space.rodionov.englishdriller.feature_driller.di.ApplicationScope
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [CategoryEntity::class, WordEntity::class], version = 1)
abstract class WordDatabase : RoomDatabase() {

    abstract val dao: WordDao

    class Callback @Inject constructor(
        private val app: Application,
        private val database: Provider<WordDatabase>,
        @ApplicationScope private val appScope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().dao

            appScope.launch {
                dao.insertCategory(CategoryEntity(app.resources.getString(R.string.top_800_words)))
                dao.insertCategory(CategoryEntity(app.resources.getString(R.string.top_200_verbs)))
                dao.insertCategory(CategoryEntity(app.resources.getString(R.string.usual_words)))
                dao.insertCategory(CategoryEntity(app.resources.getString(R.string.usual_verbs)))
                dao.insertCategory(CategoryEntity(app.resources.getString(R.string.phrasal_verbs)))
                dao.insertCategory(CategoryEntity(app.resources.getString(R.string.phrases)))

                val rus1 = app.resources.getStringArray(R.array.rus1).toList()
                val rus2 = app.resources.getStringArray(R.array.rus2).toList()
                val rus3 = app.resources.getStringArray(R.array.rus3).toList()
                val rus4 = app.resources.getStringArray(R.array.rus4).toList()
                val rus5 = app.resources.getStringArray(R.array.rus5).toList()
                val rus6 = app.resources.getStringArray(R.array.rus6).toList()
                val foreign1 = app.resources.getStringArray(R.array.eng1).toList()
                val foreign2 = app.resources.getStringArray(R.array.eng2).toList()
                val foreign3 = app.resources.getStringArray(R.array.eng3).toList()
                val foreign4 = app.resources.getStringArray(R.array.eng4).toList()
                val foreign5 = app.resources.getStringArray(R.array.eng5).toList()
                val foreign6 = app.resources.getStringArray(R.array.eng6).toList()

                for (i in foreign1.indices) { dao.insertWord(WordEntity(rus1[i], foreign1[i], app.resources.getString(R.string.phrases))) }
                for (i in foreign2.indices) { dao.insertWord(WordEntity(rus2[i], foreign2[i], app.resources.getString(R.string.phrasal_verbs))) }
                for (i in foreign3.indices) { dao.insertWord(WordEntity(rus3[i], foreign3[i], app.resources.getString(R.string.usual_verbs))) }
                for (i in foreign4.indices) { dao.insertWord(WordEntity(rus4[i], foreign4[i], app.resources.getString(R.string.usual_words))) }
                for (i in foreign5.indices) { dao.insertWord(WordEntity(rus5[i], foreign5[i], app.resources.getString(R.string.top_200_verbs))) }
                for (i in foreign6.indices) { dao.insertWord(WordEntity(rus6[i], foreign6[i], app.resources.getString(R.string.top_800_words))) }
            }
        }
    }
}

