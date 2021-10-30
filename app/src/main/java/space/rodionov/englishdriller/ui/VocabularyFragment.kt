package space.rodionov.englishdriller.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import space.rodionov.englishdriller.*
import space.rodionov.englishdriller.feature_words.domain.model.Word
import space.rodionov.englishdriller.databinding.RecyclerLayoutBinding

private const val TAG = "VocabularyFragment"

@AndroidEntryPoint
class VocabularyFragment : Fragment(R.layout.recycler_layout),
    VocabularyAdapter.OnVocItemClickListener {

    private val viewModel: VocabularyViewModel by viewModels()
    private lateinit var binding: RecyclerLayoutBinding
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*val */binding = RecyclerLayoutBinding.bind(view)
        val vocabularyAdapter = VocabularyAdapter(this/*, NativeLanguage.RUS*/)
        binding.apply {
            recyclerView.apply {
//                viewModel.readNatLang.observe(viewLifecycleOwner) {
//                    vocabularyAdapter.nativeLanguage = it
//                }
                adapter = vocabularyAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
//                itemAnimator = null // ХЗ НАДО ЛИ
            }

//            viewModel.onChooseCategoryClick(viewModel.categoryChosen!!) // HOW I CALL THE NAV ARGUMENT //WHY I NEED THIS? DO I NEED THIS?

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val word = vocabularyAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onWordSwiped(word)
                }
            }).attachToRecyclerView(recyclerView)

            fabNewWord.setOnClickListener {
                viewModel.onNewWordClick()
            }
        }

        setFragmentResultListener("add_edit_request") { _, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        }

        viewModel.words.observe(viewLifecycleOwner) {
            vocabularyAdapter.submitList(it)
        }

//        viewModel.wordsLivedata.observe(viewLifecycleOwner) {
//            vocabularyAdapter.submitList(it)
//        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.vocabularyEvent.collect { event ->
                when (event) {
                    is VocabularyViewModel.VocabularyEvent.NavigateToAddWordScreen -> {
                        val action =
                            VocabularyFragmentDirections.actionVocabularyFragmentToAddEditWordFragment(
                          //null,
                                requireContext().resources.getString(R.string.new_word)
                            )
                        findNavController().navigate(action)
                    }
                    is VocabularyViewModel.VocabularyEvent.NavigateToEditWordScreen -> {
                        val action =
                            VocabularyFragmentDirections.actionVocabularyFragmentToAddEditWordFragment(
                                //event.word,
                                requireContext().resources.getString(R.string.edit_word)
                            )
                        findNavController().navigate(action)
                    }
                    is VocabularyViewModel.VocabularyEvent.ShowWordSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                    is VocabularyViewModel.VocabularyEvent.ShowUndoDeleteWordMessage -> {
                        Snackbar.make(requireView(), requireContext().resources.getString(R.string.note_deleted), Snackbar.LENGTH_LONG)
                            .setAction(requireContext().resources.getString(R.string.undo)) {
                                viewModel.onUndoDeleteClick(event.word)
                            }.show()
                    }
                }.exhaustive
            }
        }

        setHasOptionsMenu(true)

        viewModel.categoryNumber.observe(viewLifecycleOwner) { catNum ->
        viewLifecycleOwner.lifecycleScope.launch {

                (activity as MainActivity).supportActionBar?.title = viewModel.getCategoryName(catNum)
            }
        }

        viewModel.categoryNumber.observe(viewLifecycleOwner) { catNum ->
            viewLifecycleOwner.lifecycleScope.launch {
                if (catNum == 0) {
                    delay(500)
                    binding.recyclerView.scrollToPosition(0)
                }
            }
        }
    }

    override fun onItemClick(word: Word) {
        viewModel.onWordSelected(word)
    }

    override fun onSwitchClick(word: Word, isChecked: Boolean) {
        viewModel.onVocabularyCheckedChanged(word, isChecked)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_vocabulary, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        val pendingQuery = viewModel.searchQuery.value
        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            binding.recyclerView.scrollToPosition(0)
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }

        searchView.onQueryTextChanged {
//            viewModel.searchQuery.value = it
            viewModel.searchSubject.onNext(it)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_show_only_off).isChecked =
                viewModel.catNumOnlyOffFlow.first().onlyOff
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_turn_all_words_on -> {
                viewModel.categoryNumber.observe(viewLifecycleOwner) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.turnAllWordsOn(it)
                    }
                }
                true
            }
            R.id.action_show_only_off -> {
                item.isChecked = !item.isChecked
                viewModel.onShowOnlyOffClick(item.isChecked)
                true
            }
            R.id.action_choose_all_categories -> {
                viewModel.onChooseCategoryClick(0)
//                binding.recyclerView.scrollToPosition(0)
                true
            }
            R.id.action_cat1 -> {
                viewModel.onChooseCategoryClick(1)
                true
            }
            R.id.action_cat2 -> {
                viewModel.onChooseCategoryClick(2)
                true
            }
            R.id.action_cat3 -> {
                viewModel.onChooseCategoryClick(3)
                true
            }
            R.id.action_cat4 -> {
                viewModel.onChooseCategoryClick(4)
                true
            }
            R.id.action_cat5 -> {
                viewModel.onChooseCategoryClick(5)
                true
            }
            R.id.action_cat6 -> {
                viewModel.onChooseCategoryClick(6)
                true
            }
            R.id.action_cat7 -> {
                viewModel.onChooseCategoryClick(7)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }

}







