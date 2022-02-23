package space.rodionov.englishdriller.feature_driller.presentation.driller

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import java.util.*

@AndroidEntryPoint
class DrillerFragment : BaseFragment(R.layout.fragment_driller), CardStackListener, TextToSpeech.OnInitListener {

    private val vmDriller: DrillerViewModel by viewModels()
    private var _binding: FragmentDrillerBinding? = null
    private val binding get() = _binding
    private var textToSpeech: TextToSpeech? = null

    private val drillerAdapter = DrillerAdapter(
        onSpeakWord = { word ->
            vmDriller.speakWord(word)
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDrillerBinding.bind(view)
        initViewModel()

        textToSpeech = TextToSpeech(requireContext(), this)

        binding?.apply {
            cardStackView.apply {
                adapter = drillerAdapter
                layoutManager = createLayoutManager()
            }

            btnNewRound.setOnClickListener {
                vmDriller.newRound()
                tvComplete.visibility = View.GONE
                btnNewRound.visibility = View.GONE
            }

            btnFilter.setOnClickListener {
                vmDriller.openFilterBottomSheet()
            }

            btnCollection.setOnClickListener {
                vmDriller.navigateToCollectionScreen()
            }

            btnSettings.setOnClickListener {
                vmDriller.navigateToSettings()
            }
        }
    }

    private fun initViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vmDriller.wordsState.collectLatest { wordsState ->
                binding?.apply {
                    progressBar.isVisible = wordsState.isLoading
                    ivCheck.isVisible = !wordsState.isLoading // а если ошибка то другой iv ?
                    tvItemCount.text = getString(R.string.item_count, wordsState.words.size)
                }

                val list = wordsState.words
                drillerAdapter.submitList(list)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vmDriller.currentPosition.collectLatest { pos ->
                binding?.tvCurrentItem?.text = getString(R.string.current_position, pos)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vmDriller.transDir.collectLatest {
                val transDir = it ?: return@collectLatest

                drillerAdapter.updateTransDir(transDir)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vmDriller.eventFlow.collectLatest { event ->
                when (event) {
                    is DrillerViewModel.DrillerEvent.ShowSnackbar -> {
                        // пусто
                    }
                    is DrillerViewModel.DrillerEvent.ScrollToCurrentPosition -> {
                        binding?.cardStackView?.scrollToPosition(vmDriller.currentPosition.value)
                    }
                    is DrillerViewModel.DrillerEvent.ScrollToSavedPosition -> {
                        binding?.cardStackView?.scrollToPosition(vmDriller.savedPosition)
                        Log.d(TAG_PETR, "initViewModel: called scrolltoPosition ${vmDriller.savedPosition}") // todo этот блок не вызывается почемуто
                        vmDriller.rememberPositionAfterSwitchingFragment = false
                        vmDriller.rememberPositionAfterDestroy = false
                    }
                    is DrillerViewModel.DrillerEvent.NavigateToCollectionScreen -> {
                        vmDriller.rememberPositionAfterSwitchFragment()
                        val navAction = DrillerFragmentDirections.actionDrillerFragmentToCollectionFragment()
                        findNavController().navigate(navAction)
                    }
                    is DrillerViewModel.DrillerEvent.NavigateToSettings -> {
                        val navAction = DrillerFragmentDirections.actionDrillerFragmentToSettingsFragment()
                        findNavController().navigate(navAction)
                    }
                    is DrillerViewModel.DrillerEvent.OpenFilterBottomSheet -> {
                        FilterBottomSheet().show(
                            childFragmentManager,
                            FilterBottomSheet.FILTER_BOTTOM_SHEET
                        )
                    }
                    is DrillerViewModel.DrillerEvent.SpeakWord -> {
                        onSpeakWord(event.word)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vmDriller.mode.collectLatest {
                val mode = it ?: return@collectLatest

                (binding?.root as ViewGroup).redrawViewGroup(mode)
                drillerAdapter.updateMode(mode)
            }
        }

        // other Observers
    }

    override fun onResume() {
        super.onResume()
//        binding?.apply {
//            if (cardStackView.mLayoutManagerSavedState != null) {
//                cardStackView.mStateCardStackLayoutManager?.onRestoreInstanceState(cardStackView.mLayoutManagerSavedState)
//            }
//        }
        vmDriller.scrollToSavedPosIfItIsSaved() // todo переделать это по варианту сверху (сохранять state ресайклера с помощью saveInstantState)
    }

    fun createLayoutManager(): CardStackLayoutManager {
        val drillerLayoutManager = CardStackLayoutManager(requireContext(), this)
        drillerLayoutManager.apply {
            setOverlayInterpolator(LinearInterpolator())
            setStackFrom(StackFrom.Top)
            setVisibleCount(3)
            setTranslationInterval(8.0f)
            setScaleInterval(0.95f)
            setMaxDegree(20.0f)
            setDirections(Direction.FREEDOM)
            setSwipeThreshold(0.3f)
            setCanScrollHorizontal(true)
            setCanScrollVertical(true)
            setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        }
        return drillerLayoutManager
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            try {
                textToSpeech?.language = Locale.ENGLISH
            } catch (e: Exception) {
                Log.d(TAG_PETR, "TTS: Exception: ${e.localizedMessage}")
            }
        } else {
            Log.d(TAG_PETR, "TTS: Language initialization failed")
        }
    }

    private fun onSpeakWord(word: String) {
        textToSpeech?.speak(word, TextToSpeech.QUEUE_FLUSH, null)
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
        // empty
    }

    override fun onCardSwiped(direction: Direction?) {
        if (direction == Direction.Bottom) {
            vmDriller.inactivateCurrentWord()
        }
    }

    override fun onCardRewound() {
        // empty
    }

    override fun onCardCanceled() {
        // empty
    }

    override fun onCardAppeared(view: View?, position: Int) {
        binding?.tvOnCardAppeared?.text = getString(R.string.on_card_appeared, position)
        if (vmDriller.rememberPositionAfterChangingStack) {
            vmDriller.scrollToCurPos()
            vmDriller.updateCurrentPosition(vmDriller.currentPosition.value)
            vmDriller.rememberPositionAfterChangingStack = false
        } else {
            vmDriller.updateCurrentPosition(position)
        }
        if (position == drillerAdapter.itemCount - 3 && position < Constants.MAX_STACK_SIZE - 10) {
            vmDriller.addTenWords()
        }
        if (position == drillerAdapter.itemCount - 1) binding?.tvComplete?.visibility = View.VISIBLE
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        binding?.tvOnCardDisappeared?.text = getString(R.string.on_card_disappeared, position)
        if (position == drillerAdapter.itemCount - 1) binding?.btnNewRound?.visibility =
            View.VISIBLE
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        vmDriller.rememberPositionInCaseOfDestroy()
        textToSpeech?.let {
            it.stop()
            it.shutdown()
        }
    }
}

