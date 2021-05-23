package space.rodionov.englishdriller.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesManager"

//enum class NativeLanguage { RUS, ENG }

data class FilterCatNumNatLangOnlyOff(val categoryChosen: Int, /*val nativeLanguage: NativeLanguage,*/ val onlyOff: Boolean)

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.createDataStore("user_preferences")

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
            val categoryChosen = preferences[PreferencesKeys.CATEGORY_CHOSEN] ?: 0 // сам поставил нуль, хуй знает чем это кончится
//            val nativeLanguage = NativeLanguage.valueOf(
//                preferences[PreferencesKeys.NATIVE_LANGUAGE] ?: NativeLanguage.RUS.name
//            )
            val onlyOff = preferences[PreferencesKeys.ONLY_OFF] ?: false
            FilterCatNumNatLangOnlyOff(categoryChosen, /*nativeLanguage, */onlyOff)
        }

    /*val nativeLanguageFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val nativeLanguage = NativeLanguage.valueOf(
                preferences[PreferencesKeys.NATIVE_LANGUAGE] ?: NativeLanguage.RUS.name
            )
            nativeLanguage
        }*/

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
            val categoryChosen = preferences[PreferencesKeys.CATEGORY_CHOSEN] ?: 0 // сам поставил нуль, хуй знает чем это кончится
//            val nativToForeign = preferences[PreferencesKeys.NATIV_TO_FOREIGN] ?: false
//            FilterPreferences(categoryChosen, nativToForeign)
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
//            FilterTranslationDirection(nativToForeign)
            Log.d(TAG, "So, trans nativeTOForeign: " + nativToForeign)
            nativToForeign
        }

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
            Log.d(TAG, "updateTranslationDirection: $onlyOff")
        }
    }

//    suspend fun updateNativeLanguage(nativeLanguage: NativeLanguage) {
//        dataStore.edit { preferences ->
//            preferences[PreferencesKeys.NATIVE_LANGUAGE] = nativeLanguage.name
//        }
//    }

    private object PreferencesKeys {
        val CATEGORY_CHOSEN = preferencesKey<Int>("category_chosen")
        val NATIV_TO_FOREIGN = preferencesKey<Boolean>("nativ_to_foreign")
        val ONLY_OFF = preferencesKey<Boolean>("only_off")
//        val NATIVE_LANGUAGE = preferencesKey<String>("native_language")
    }

}