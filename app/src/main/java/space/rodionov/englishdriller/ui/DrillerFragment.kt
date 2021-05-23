package space.rodionov.englishdriller.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.yuyakaido.android.cardstackview.*
import dagger.hilt.android.AndroidEntryPoint
import space.rodionov.englishdriller.R
import space.rodionov.englishdriller.data.Word
import space.rodionov.englishdriller.databinding.CardstackLayoutBinding

/**
 * Created by Aleksej Rodionov, march 2021
 *
 * I used very cool external library "CardStackView" from github for building a cool swipable card stack
 */

private const val TAG = "DrillerFragment"

@AndroidEntryPoint
class DrillerFragment : Fragment(R.layout.cardstack_layout), CardStackListener {

    private val viewModel: DrillerViewModel by viewModels()
    private lateinit var currentWord: Word
    //    private var nativToForeign: Boolean = false
    private lateinit var drillerAdapter: JavaDrillerAdapter/*? = null*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = CardstackLayoutBinding.bind(view)
        drillerAdapter = JavaDrillerAdapter(JavaDrillerAdapter.JavaDrillerDiff(), false, requireContext()/*, NativeLanguage.RUS*/)

        viewModel.getLivedataList().observe(viewLifecycleOwner) {
            drillerAdapter.submitList(it)
            Log.d(TAG, "drlr workWorkList.size = " + it.size)
        }

        val drillerLayoutManager = CardStackLayoutManager(requireContext(), this)
        drillerLayoutManager.setOverlayInterpolator(LinearInterpolator())
        drillerLayoutManager.setStackFrom(StackFrom.Top)
        drillerLayoutManager.setVisibleCount(3)
        drillerLayoutManager.setTranslationInterval(8.0f)
        drillerLayoutManager.setScaleInterval(0.95f)
        drillerLayoutManager.setMaxDegree(20.0f)
        drillerLayoutManager.setDirections(Direction.FREEDOM)
        drillerLayoutManager.setSwipeThreshold(0.3f)
        drillerLayoutManager.setCanScrollHorizontal(true)
        drillerLayoutManager.setCanScrollVertical(true)
        drillerLayoutManager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
//        val drillerAdapter = DrillerAdapter()
        binding.apply {
            cardStackView.apply {
                viewModel.readTransDir.observe(viewLifecycleOwner) {
                    drillerAdapter.setNativToForeign(it)
                }
                /*viewModel.readNatLang.observe(viewLifecycleOwner) {
                    drillerAdapter.setNativeLanguage(it)
                }*/
                adapter = drillerAdapter
                layoutManager = drillerLayoutManager
                setHasFixedSize(true)
                itemAnimator = null // ХЗ НАДО ЛИ

            }
        }
        viewModel.get4words()

    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
        // empty
    }

    override fun onCardSwiped(direction: Direction?) {
        if (direction == Direction.Bottom) {
            currentWord.shown = false
            viewModel.update(currentWord)
        }
        viewModel.removeAndAddWord(currentWord)
        Log.d(TAG, currentWord.foreign + " is removed. (drlr)")
    }

    override fun onCardRewound() {
        // empty
    }

    override fun onCardCanceled() {
        // empty
    }

    override fun onCardAppeared(view: View?, position: Int) {
        currentWord = drillerAdapter.getWordAt(position)
        Log.d(
            TAG,
            "onCardAppeared: " + currentWord.foreign + " IS NOW currentWord, position in adapter - " + position + " (drlr)"
        )
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        currentWord = drillerAdapter.getWordAt(position)
        Log.d(
            TAG,
            "onCardDisappeared: " + currentWord.foreign + " IS NOW currentWord, position in adapter - " + position + " (drlr)"
        )
    }

}








