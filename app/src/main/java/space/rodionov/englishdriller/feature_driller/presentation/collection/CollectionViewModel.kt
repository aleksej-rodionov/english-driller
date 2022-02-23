package space.rodionov.englishdriller.feature_driller.presentation.collection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import space.rodionov.englishdriller.feature_driller.utils.Constants.MODE_LIGHT
import space.rodionov.englishdriller.feature_driller.domain.models.Category
import space.rodionov.englishdriller.feature_driller.domain.use_cases.MakeCategoryActiveUseCase
import space.rodionov.englishdriller.feature_driller.domain.use_cases.ObserveAllActiveCatsNamesUseCase
import space.rodionov.englishdriller.feature_driller.domain.use_cases.ObserveAllCatsWithWordsUseCase
import space.rodionov.englishdriller.feature_driller.domain.use_cases.ObserveModeUseCase
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
//    private val observeAllCategories: ObserveAllCategoriesUseCase,
    private val observeAllCatsWithWordsUseCase: ObserveAllCatsWithWordsUseCase,
    private val makeCategoryActiveUseCase: MakeCategoryActiveUseCase,
    private val observeAllActiveCatsNamesUseCase: ObserveAllActiveCatsNamesUseCase,
    private val observeMode: ObserveModeUseCase,
    private val state: SavedStateHandle
) : ViewModel() {
    private var activeCatsAmount = state.get<Int>("activeCatsAmount") ?: 0
        set(value) {
            field = value
            state.set("activeCatsAmount", value)
        }

    private val _mode = observeMode.invoke()
    val mode = _mode.stateIn(viewModelScope, SharingStarted.Lazily, MODE_LIGHT)

    private val _categories = observeAllCatsWithWordsUseCase.invoke()
    val categories = _categories.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val _activeCatsFlow = observeAllActiveCatsNamesUseCase.invoke()
    val activeCatsFlow = _activeCatsFlow.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val _eventFlow = MutableSharedFlow<CollectionEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class CollectionEvent {
        data class NavigateToWordlistScreen(val cat: Category?) : CollectionEvent()
        data class RefreshCatSwitch(val cat: Category) : CollectionEvent()
        data class ShowSnackbar(val msg: String) : CollectionEvent()
    }

//============================METHODS=============================================================

    fun refreshActiveCatsAmount(newValue: Int) {
        activeCatsAmount = newValue
    }

    fun howManyActiveCats() : Int = activeCatsAmount

    fun onCategoryClick(cat: Category) = viewModelScope.launch {
        _eventFlow.emit(CollectionEvent.NavigateToWordlistScreen(cat))
    }

    fun onSearchClick() = viewModelScope.launch {
        _eventFlow.emit(CollectionEvent.NavigateToWordlistScreen(null))
    }

    fun updateCatSwitchState(cat: Category) = viewModelScope.launch {
        _eventFlow.emit(CollectionEvent.RefreshCatSwitch(cat))
    }

    fun shoeSnackbar(msg: String) = viewModelScope.launch {
        _eventFlow.emit(CollectionEvent.ShowSnackbar(msg))
    }

    fun activateCategory(catName: String) = viewModelScope.launch {
        makeCategoryActiveUseCase(catName, true)
    }

    fun inactivateCategory(catName: String) = viewModelScope.launch  {
        makeCategoryActiveUseCase(catName, false)
    }
}
