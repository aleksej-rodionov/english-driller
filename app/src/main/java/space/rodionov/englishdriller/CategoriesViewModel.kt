package space.rodionov.englishdriller

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

// part 4 (created)
class CategoriesViewModel @ViewModelInject constructor(
    private val wordDao: WordDao,
    private val preferencesManager: PreferencesManager/*, //DOBAVIL 17.04 // Dagger automatically Injected it, 'cause we added @Inject in PM.class
    @Assisted private val state: SavedStateHandle*/
) : ViewModel() {

    fun categoriesFlow() = wordDao.getAllCategories().asLiveData()

    private val categoriesEventChannel = Channel<CategoriesEvent>() // part 11
    val categoriesEvent = categoriesEventChannel.receiveAsFlow() // part 11

    /*private val natLangFlow = preferencesManager.nativeLanguageFlow

    val readNatLang = natLangFlow.asLiveData()*/

    fun onCategoryCheckedChanged(categoryItem: CategoryItem, isChecked: Boolean) = viewModelScope.launch {
        wordDao.updateCategory(categoryItem.copy(categoryShown = isChecked))
    }

    fun onVocabularyClick(categoryItem: CategoryItem) = viewModelScope.launch { // part 11
        categoriesEventChannel.send(CategoriesEvent.NavigateToVocabularyScreen(categoryItem))
        preferencesManager.updateCategoryChosen(categoryItem.categoryNumber) //DOBAVIL 17.04
    }

    fun turnAllCategoriessOn() = viewModelScope.launch {
        wordDao.turnAllCategoriesOn()
    }

    // part 11
    sealed class CategoriesEvent {
        data class NavigateToVocabularyScreen(val categoryItem: CategoryItem) : CategoriesEvent() // part 11
    }

}







