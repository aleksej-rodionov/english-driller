package space.rodionov.englishdriller.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import space.rodionov.englishdriller.feature_words.domain.model.Category
import space.rodionov.englishdriller.data.PreferencesManager
import space.rodionov.englishdriller.data.WordDao
import javax.inject.Inject

// part 4 (created)
@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val wordDao: WordDao,
    private val preferencesManager: PreferencesManager/*, //DOBAVIL 17.04 // Dagger automatically Injected it, 'cause we added @Inject in PM.class
    @Assisted private val state: SavedStateHandle*/
) : ViewModel() {

    fun categoriesFlow() = wordDao.getAllCategories().asLiveData()

    private val categoriesEventChannel = Channel<CategoriesEvent>() // part 11
    val categoriesEvent = categoriesEventChannel.receiveAsFlow() // part 11

    /*private val natLangFlow = preferencesManager.nativeLanguageFlow

    val readNatLang = natLangFlow.asLiveData()*/

    fun onCategoryCheckedChanged(category: Category, isChecked: Boolean) = viewModelScope.launch {
        wordDao.updateCategory(category.copy(categoryShown = isChecked))
    }

    fun onVocabularyClick(category: Category) = viewModelScope.launch { // part 11
        categoriesEventChannel.send(CategoriesEvent.NavigateToVocabularyScreen(category))
        preferencesManager.updateCategoryChosen(category.categoryNumber) //DOBAVIL 17.04
    }

    fun turnAllCategoriessOn() = viewModelScope.launch {
        wordDao.turnAllCategoriesOn()
    }

    // part 11
    sealed class CategoriesEvent {
        data class NavigateToVocabularyScreen(val category: Category) : CategoriesEvent() // part 11
    }

}







