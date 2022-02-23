package space.rodionov.englishdriller.feature_driller.presentation.wordlist

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import space.rodionov.englishdriller.R
import space.rodionov.englishdriller.core.ModeForAdapter
import space.rodionov.englishdriller.core.fetchColors
import space.rodionov.englishdriller.core.redrawViewGroup
import space.rodionov.englishdriller.databinding.ItemWordHorizontalBinding
import space.rodionov.englishdriller.feature_driller.domain.models.Word
import space.rodionov.englishdriller.feature_driller.presentation.WordDiff

class WordlistAdapter(
    private val onClickLearned: (Word)-> Unit = {},
    private val onSpeakWord: (String) -> Unit = {}
) : ListAdapter<Word, WordlistAdapter.WordlistViewHolder>(WordDiff()),
    ModeForAdapter {

    companion object {
        const val TAG_WORDLIST_ADAPTER = "wordListAdapter"
    }

    private var mode: Int = 0
    override fun updateMode(newMode: Int) { mode = newMode }
    override fun getTag(): String = TAG_WORDLIST_ADAPTER

    inner class WordlistViewHolder(
        private val binding: ItemWordHorizontalBinding,
        private val onLearnedItem: (Int) -> Unit = {},
        private val onSpeakItem: (Int) -> Unit = {}
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(word: Word) {
            binding.apply {
                tvUpper.text = word.foreign
                tvDowner.text = word.nativ
                tvCategory.text = word.categoryName

                if (word.isWordActive) {
                    ivLearned.setImageDrawable(root.context.getDrawable(R.drawable.ic_new_round))
                    ivLearned.imageTintList = null
                    ivLearned.imageTintList = ColorStateList.valueOf(fetchColors(mode, itemView.resources)[3])
                } else {
                    ivLearned.setImageDrawable(root.context.getDrawable(R.drawable.ic_learned))
                }

                (root as ViewGroup).redrawViewGroup(mode)

                ivLearned.setOnClickListener {
                    val pos = adapterPosition
                    if (pos != RecyclerView.NO_POSITION) onLearnedItem(pos)
                }
                ivSpeak.setOnClickListener {
                    val pos = adapterPosition
                    if (pos != RecyclerView.NO_POSITION) onSpeakItem(pos)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordlistViewHolder {
        val binding = ItemWordHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordlistViewHolder(
            binding,
            onLearnedItem = { pos ->
                val word = getItem(pos)
                word?.let { onClickLearned(it) }
            },
            onSpeakItem = { pos ->
                val word = getItem(pos)
                word?.let { onSpeakWord(it.foreign) }
            }
        )
    }

    override fun onBindViewHolder(holder: WordlistViewHolder, position: Int) {
        val curItem = getItem(position)
        holder.bind(curItem)
    }
}