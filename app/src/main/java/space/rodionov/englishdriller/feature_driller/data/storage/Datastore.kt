package space.rodionov.englishdriller.feature_driller.data.storage

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.emptyPreferences
import space.rodionov.englishdriller.feature_driller.Constants.TAG_PETR
import java.io.IOException

private val Context.datastore by preferencesDataStore("datastore")

//@Singleton
class Datastore /*@Inject constructor*/(
    /* @ApplicationContext*/ app: Application
) {

    private val datastore = app.datastore

    private object PrefKeys {
        val CATEGORY = stringPreferencesKey("catName")
        val TRANSLATION_DIRECTION = booleanPreferencesKey("transDir")
        val MODE = intPreferencesKey("mode")
        val FOLLOW_SYSTEM_MODE = booleanPreferencesKey("followSystemMode")
    }

    //==========================CATEGORY OPENED IN WORD COLLECTION============================================
    val categoryFlow = datastore.data
        .catch {exception ->
            if (exception is IOException) {
                Log.e(TAG_PETR, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            val category = prefs[PrefKeys.CATEGORY] ?: ""
            category
        }

    suspend fun updateCategoryChosen(category: String) {
        datastore.edit { preferences ->
            preferences[PrefKeys.CATEGORY] = category
            Log.d(TAG_PETR, "updateCategoryChosen: $category")
        }
    }

    //==========================TRANSLATION DIRECTION============================================
    val translationDirectionFlow = datastore.data
        .catch {exception ->
            if (exception is IOException) {
                Log.e(TAG_PETR, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            val nativeToForeign = prefs[PrefKeys.TRANSLATION_DIRECTION] ?: false
            nativeToForeign
        }

    suspend fun updatetranslationDirection(nativeToForeign: Boolean) {
        datastore.edit { preferences ->
            preferences[PrefKeys.TRANSLATION_DIRECTION] = nativeToForeign
            Log.d(TAG_PETR, "updateCategoryChosen: $nativeToForeign")
        }
    }

    //==========================MODE============================================
    val modeFlow = datastore.data
        .catch {exception ->
            if (exception is IOException) {
                Log.e(TAG_PETR, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            val mode = prefs[PrefKeys.MODE] ?: 0
            mode
        }

    suspend fun updateMode(mode: Int) {
        datastore.edit { prefs ->
            prefs[PrefKeys.MODE] = mode
        }
    }

    //===============================IS FOLLOWING SYSTEM MODE=======================
    val followSystemModeFlow = datastore.data
        .catch {exception ->
            if (exception is IOException) {
                Log.e(TAG_PETR, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            val follow = prefs[PrefKeys.FOLLOW_SYSTEM_MODE] ?: false
            follow
        }

    suspend fun updateFollowSystemMode(follow: Boolean) {
        datastore.edit { it[PrefKeys.FOLLOW_SYSTEM_MODE] = follow }
    }
}



