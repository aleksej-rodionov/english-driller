package space.rodionov.englishdriller.feature_driller.presentation.wordlist

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import space.rodionov.englishdriller.MainActivity
import space.rodionov.englishdriller.R
import space.rodionov.englishdriller.core.redrawViewGroup
import space.rodionov.englishdriller.core.showKeyboard
import space.rodionov.englishdriller.databinding.FragmentWordlistBinding
import space.rodionov.englishdriller.feature_driller.domain.models.Word
import space.rodionov.englishdriller.feature_driller.utils.Constants.EMPTY_STRING
import space.rodionov.englishdriller.feature_driller.utils.Constants.TAG_PETR
import java.lang.Exception
import java.util.*

@AndroidEntryPoint
class WordlistFragment : Fragment(R.layout.fragment_wordlist), TextToSpeech.OnInitListener {

    private val vmWordlist: WordlistViewModel by viewModels()
    private var _binding: FragmentWordlistBinding? = null
    val binding get() = _binding
    private var textToSpeech: TextToSpeech? = null

    private val wordlistAdapter: WordlistAdapter by lazy {
        WordlistAdapter(
            onClickLearned = {
                openWordBottomSheet(it)
            },
            onSpeakWord = {
                vmWordlist.speakWord(it)
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWordlistBinding.bind(view)

        textToSpeech = TextToSpeech(requireContext(), this)

        binding?.apply {
            rvWords.apply {
                adapter = wordlistAdapter
                setHasFixedSize(true)
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }
            ivClearText.setOnClickListener {
                etSearch.text.clear()
            }
            etSearch.showKeyboard()
            etSearch.addTextChangedListener {
                ivClearText.isVisible = !it.toString().isEmpty()

                vmWordlist.onSearch(it.toString())
            }

            btnBack.setOnClickListener {
                (activity as MainActivity).onBackPressed()
            }
        }

        initViewModel()
    }

    private fun initViewModel() {
        vmWordlist.catToSearchIn.observe(viewLifecycleOwner) {
            binding?.apply {
                if (it == null) tvTitle.text = getString(R.string.search_among_all_words)
                vmWordlist.updateCatStorage(EMPTY_STRING)
                val cat = it ?: return@observe
                tvTitle.text = getString(R.string.search_in, cat.name)
                vmWordlist.updateCatStorage(cat.name)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vmWordlist.words.collectLatest {
                val words = it ?: return@collectLatest
                Log.d(TAG_PETR, "initViewModel: size = ${words.size} words")
                wordlistAdapter.submitList(words)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vmWordlist.eventFlow.collectLatest { event ->
                when (event) {
                    is WordlistViewModel.WordlistEvent.OpenWordBottomSheet -> {
                        showWordBottomSheet(event.word)
                    }
                    is WordlistViewModel.WordlistEvent.SpeakWord -> {
                        onSpeakWord(event.word)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vmWordlist.mode.collectLatest {
                val mode = it ?: return@collectLatest
                (binding?.root as ViewGroup).redrawViewGroup(mode)
                wordlistAdapter.updateMode(mode)
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            try {
                textToSpeech?.language = Locale.ENGLISH
            } catch (e: Exception) {
                Log.d(TAG_PETR, "TTS: Exception: ${e.localizedMessage}")
            }
        } else {
            Log.d(TAG_PETR, "TTS Language initialization failed")
        }
    }

    private fun onSpeakWord(word: String) { // todo переменстить во вьюмодель надо?
        textToSpeech?.speak(word, TextToSpeech.QUEUE_FLUSH, null)
    }

    private fun openWordBottomSheet(word: Word) {
        vmWordlist.openWordBottomSheet(word)
    }

    private fun showWordBottomSheet(word: Word) {
        val args = Bundle()
        args.putString("nativ", word.nativ)
        args.putString("foreign", word.foreign)
        args.putString("categoryName", word.categoryName)

        val wordBottomSheet = WordlistBottomSheet()
        wordBottomSheet.arguments = args
        wordBottomSheet.show(
            childFragmentManager,
            WordlistBottomSheet.WORDLIST_BOTTOM_SHEET
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        textToSpeech?.let {
            it.stop()
            it.shutdown()
        }
    }
}