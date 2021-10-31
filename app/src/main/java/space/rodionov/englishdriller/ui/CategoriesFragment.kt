package space.rodionov.englishdriller.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import space.rodionov.englishdriller.R
import space.rodionov.englishdriller.databinding.RecyclerSimpleLayoutBinding
import space.rodionov.englishdriller.feature_words.domain.model.Category
import space.rodionov.englishdriller.exhaustive
import space.rodionov.englishdriller.util.fetchColors
import space.rodionov.englishdriller.util.fetchTheme


//private const val TAG = "CategoriesFragment"
@AndroidEntryPoint // part 4
class CategoriesFragment : Fragment(R.layout.recycler_simple_layout),
    CategoriesAdapter.OnItemClickListener { // part 3 (created)

    // part 4
    private val viewModel: CategoriesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val binding = RecyclerSimpleLayoutBinding.bind(view)
        val categoriesAdapter = CategoriesAdapter(this/*, NativeLanguage.RUS*/)
        binding.apply {
            recyclerView.apply {
//                viewModel.readNatLang.observe(viewLifecycleOwner) {
//                    categoriesAdapter.nativeLanguage = it
//                }
                adapter = categoriesAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                itemAnimator = null // ХЗ НАДО ЛИ
            }

            viewModel.mode.observe(viewLifecycleOwner) {
                val theme = fetchTheme(it, resources)
                val colors = theme.fetchColors()
//                root.background = colors[9].toDrawable()
                categoriesAdapter.updateMode(it)
            }
        }

        viewModel.cats.observe(viewLifecycleOwner) {
            categoriesAdapter.submitList(it)
        }

        //part 11 (PRIBABLY NEEDED TO CHANGE CATEGORYITEM.CATEGORYNUMBER TO JUST CATEGORYITEM
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.categoriesEvent.collect { event ->
                when (event) {
                    is CategoriesViewModel.CategoriesEvent.NavigateToVocabularyScreen -> {
//                        val action = CategoriesFragmentDirections.actionCategoriesFragmentToVocabularyFragment(event.category.categoryNumber)
//                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onVocabularyClick(category: Category) {
        viewModel.onVocabularyClick(category)
    }

    override fun onSwitchClick(category: Category, isChecked: Boolean) {
        viewModel.onCategoryCheckedChanged(category, isChecked)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_categories, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_turn_all_categories_on -> {
                viewModel.turnAllCategoriessOn()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}




