package space.rodionov.englishdriller.feature_driller.presentation.wordlist

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WordlistBottomSheet : BottomSheetDialogFragment() {

    companion object {
        const val WORDLIST_BOTTOM_SHEET = "wordlistBottomSheet"
    }

    private val binding: BottomsheetWordlistBinding by lazy {
        BottomsheetWordlistBinding.inflate(layoutInflater)
    }
    private val vmWordlist: WordlistViewModel by viewModels({
        requireParentFragment()
    })

    override fun getTheme(): Int = vmWordlist.mode.value?.let {
        when (it) {
            Constants.MODE_LIGHT -> R.style.Theme_NavBarDay
            Constants.MODE_DARK -> R.style.Theme_NavBarNight
            else -> R.style.Theme_NavBarDay
        }
    } ?: R.style.Theme_NavBarDay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        args?.let {
            Log.d(TAG_PETR, "onCreate: args NOT null")
            val nativ = it.getString("nativ")
            val foreign = it.getString("foreign")
            val categoryName = it.getString("categoryName")
            vmWordlist.nativLivedata.value = nativ
            vmWordlist.foreignLivedata.value = foreign
            vmWordlist.catNameLivedata.value = categoryName
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as View).setBackgroundColor(Color.TRANSPARENT)

        binding.apply {
            this@WordlistBottomSheet.lifecycleScope.launchWhenStarted {
                vmWordlist.word.collectLatest {
                    it?.let {w->
                        Log.d(TAG_PETR, "observeWord BottomSheet New value = $w")
                    }
                    val word = it?: return@collectLatest
                    tvWord.text = resources.getString(R.string.word_in_dialog, word.foreign, word.nativ)
                    tvCategory.text = word.categoryName
                    val learned =
                        if (word.isWordActive) getString(R.string.word_not_learned) else getString(R.string.word_learned)
                    tvDecription.text = resources.getString(R.string.word_description, learned)
                    if (word.isWordActive) {
                        ivLearned.setImageDrawable(resources.getDrawable(R.drawable.ic_new_round))
                    } else {
                        ivLearned.setImageDrawable(resources.getDrawable(R.drawable.ic_learned))
                    }
                    switchLearned.setOnCheckedChangeListener(null)
                    switchLearned.isChecked = !word.isWordActive
                    switchLearned.text = learned
                    switchLearned.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) vmWordlist.inactivateWord() else vmWordlist.activateWord()
                    }
                }
            }

            this@WordlistBottomSheet.lifecycleScope.launchWhenStarted {
                vmWordlist.mode.collectLatest {
                    val mode = it ?: return@collectLatest
                    (root as ViewGroup).redrawViewGroup(mode)
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        vmWordlist.nativLivedata.value = null
        vmWordlist.foreignLivedata.value = null
        vmWordlist.catNameLivedata.value = null
    }
}




