package space.rodionov.englishdriller.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.*
import androidx.datastore.preferences.core.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PrefManager LOGS"

private val Context.dataStore by preferencesDataStore("user_preferences")

data class FilterCatNumNatLangOnlyOff(val categoryChosen: Int, val onlyOff: Boolean)

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val CATEGORY_CHOSEN = intPreferencesKey("category_chosen")
        val NATIV_TO_FOREIGN = booleanPreferencesKey("nativ_to_foreign")
        val ONLY_OFF = booleanPreferencesKey("only_off")
    }

//=============================GETTERS==================================

    val catNumNatLangOnlyOffFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val categoryChosen = preferences[PreferencesKeys.CATEGORY_CHOSEN] ?: 0
            val onlyOff = preferences[PreferencesKeys.ONLY_OFF] ?: false
            FilterCatNumNatLangOnlyOff(categoryChosen, onlyOff)
        }

    val onlyOffFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val onlyDisabledWords = preferences[PreferencesKeys.ONLY_OFF] ?: false
            onlyDisabledWords
        }

    val categoryNumberFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val categoryChosen = preferences[PreferencesKeys.CATEGORY_CHOSEN] ?: 0
            categoryChosen
        }

    val translationDirectionFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val nativToForeign = preferences[PreferencesKeys.NATIV_TO_FOREIGN] ?: false
            Log.d(TAG, "So, trans nativeTOForeign: " + nativToForeign)
            nativToForeign
        }

//===============================SETTERS===========================================

    suspend fun updateCategoryChosen(categoryChosen: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CATEGORY_CHOSEN] = categoryChosen
            Log.d(TAG, "updateCategoryChosen: $categoryChosen")
        }
    }

    suspend fun updateTranslationDirection(nativToForeign: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NATIV_TO_FOREIGN] = nativToForeign
            Log.d(TAG, "updateTranslationDirection: $nativToForeign")
        }
    }

    suspend fun updateShowOnlyOff(onlyOff: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONLY_OFF] = onlyOff
            Log.d(TAG, "showOnlyOff: $onlyOff")
        }
    }


}