package space.rodionov.englishdriller.feature_driller.presentation.collection

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import space.rodionov.englishdriller.MainActivity
import space.rodionov.englishdriller.R
import space.rodionov.englishdriller.core.redrawViewGroup
import space.rodionov.englishdriller.databinding.FragmentCollectionBinding
import space.rodionov.englishdriller.feature_driller.domain.models.Category
import space.rodionov.englishdriller.feature_driller.utils.Constants.TAG_PETR

@AndroidEntryPoint
class CollectionFragment : Fragment(
    R.layout.fragment_collection
) {

    private val vmCollection: CollectionViewModel by viewModels()
    private var _binding: FragmentCollectionBinding? = null
    private val binding get() = _binding

    private val collectionAdapter: CollectionAdapter by lazy {
        CollectionAdapter(
            onSwitchCatActive = { cat, isChecked ->
                onSwitchActive(cat, isChecked)
            },
            onClickCat = { cat ->
                vmCollection.onCategoryClick(cat)
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCollectionBinding.bind(view)

        binding?.apply {
            rvCats.apply {
                adapter = collectionAdapter
                setHasFixedSize(true)
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }

            tvSearch.setOnClickListener {
                vmCollection.onSearchClick()
            }

            btnBack.setOnClickListener {
                (activity as MainActivity)?.onBackPressed()
            }
        }

        initViewModel()
    }

    private fun initViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vmCollection.categories.collectLatest {
                val cwws = it?.toMutableList() ?: return@collectLatest
//                val cats = cwws.map { cww ->
//                    cww.category
//                }.toMutableList()
                collectionAdapter.submitList(cwws)
                //todo лучше чтобы адаптер брал CWW а не просто Category
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vmCollection.activeCatsFlow.collectLatest {
                val activeCats = it?: return@collectLatest
                vmCollection.refreshActiveCatsAmount(activeCats.size)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vmCollection.eventFlow.collectLatest { event ->
                when (event) {
                    is CollectionViewModel.CollectionEvent.NavigateToWordlistScreen -> {
                        val action =
                            CollectionFragmentDirections.actionCollectionFragmentToWordlistFragment()
                        if (event.cat != null) {
                            action.category = event.cat
                        } else {
                            action.category = null
                        }
                        Log.d(TAG_PETR, "NAVIGATOR: action.category = ${action.category?.name}")
                        findNavController().navigate(action)
                    }
                    is CollectionViewModel.CollectionEvent.RefreshCatSwitch -> {
                        collectionAdapter.refreshCatSwitchState(event.cat)
                    }
                    is CollectionViewModel.CollectionEvent.ShowSnackbar -> {
                        binding?.root?.let {
                            Snackbar.make(it, event.msg, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vmCollection.mode.collectLatest {
                val mode = it ?: return@collectLatest
                (binding?.root as ViewGroup).redrawViewGroup(mode)
                collectionAdapter.updateMode(mode)
            }
        }
    }

    private fun onSwitchActive(cat: Category, isChecked: Boolean) {
        if (isChecked) {
            vmCollection.activateCategory(cat.name)
        } else {
            if (vmCollection.howManyActiveCats() < 2) {
                vmCollection.updateCatSwitchState(cat)
                vmCollection.shoeSnackbar(getString(R.string.cannot_turn_all_cats_off))
            } else {
                vmCollection.inactivateCategory(cat.name)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




