package space.rodionov.englishdriller.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import space.rodionov.englishdriller.data.PreferencesManager

class SettingsViewModel @ViewModelInject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val transDirFlow = preferencesManager.translationDirectionFlow

    val readTransDir = transDirFlow.asLiveData()

    fun saveTransDir(nativToForeign: Boolean) = viewModelScope.launch {
        preferencesManager.updateTranslationDirection(nativToForeign)
    }

/*
    private val natLangFlow = preferencesManager.nativeLanguageFlow

    val readNatLang = natLangFlow.asLiveData()

    fun saveNatLang(nativeLanguage: NativeLanguage) = viewModelScope.launch {
        preferencesManager.updateNativeLanguage(nativeLanguage)
    }*/

}