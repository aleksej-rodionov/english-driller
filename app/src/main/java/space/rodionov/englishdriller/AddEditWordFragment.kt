package space.rodionov.englishdriller

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import space.rodionov.englishdriller.databinding.AddEditWordLayoutBinding

@AndroidEntryPoint
class AddEditWordFragment : Fragment(R.layout.add_edit_word_layout) {

    private val viewModel: AddEditWordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = AddEditWordLayoutBinding.bind(view)

        binding.apply {
            etForeignWord.setText(viewModel.wordForeign)
            etRussianWord.setText(viewModel.wordRus)

            etForeignWord.addTextChangedListener {
                viewModel.wordForeign = it.toString()
            }
            etRussianWord.addTextChangedListener {
                viewModel.wordRus = it.toString()
            }

            fabSaveWord.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditWordEvent.collect { event ->
                when (event) {
                    is AddEditWordViewModel.AddEditWordEvent.NavigateBackWithResult -> {
                        binding.etForeignWord.clearFocus()
//                        binding.etRussianWord.clearFocus() // try without if problem
                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack() // to immediately remove the Fragment from the backstach and go back to the previous one
                    }
                    is AddEditWordViewModel.AddEditWordEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }
    }

}