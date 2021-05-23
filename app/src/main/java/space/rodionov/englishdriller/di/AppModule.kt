package space.rodionov.englishdriller

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import space.rodionov.englishdriller.data.WordDatabase
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
        callback: WordDatabase.Callback
    ) = Room.databaseBuilder(app, WordDatabase::class.java, "word_database")
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

    @Provides
    fun provideWordDao(db: WordDatabase) = db.wordDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScopre() = CoroutineScope(SupervisorJob())

}

// part 4
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope
