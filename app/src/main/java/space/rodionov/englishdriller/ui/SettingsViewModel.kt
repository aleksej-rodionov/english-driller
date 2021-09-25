package space.rodionov.englishdriller.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import space.rodionov.englishdriller.data.PreferencesManager
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val transDirFlow = preferencesManager.translationDirectionFlow

    val readTransDir = transDirFlow.asLiveData()

    fun saveTransDir(nativToForeign: Boolean) = viewModelScope.launch {
        preferencesManager.updateTranslationDirection(nativToForeign)
    }
}