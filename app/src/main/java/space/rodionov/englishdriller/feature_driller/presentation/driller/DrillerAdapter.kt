package space.rodionov.englishdriller.feature_driller.presentation.driller

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import space.rodionov.englishdriller.core.ModeForAdapter
import space.rodionov.englishdriller.core.redrawViewGroup
import space.rodionov.englishdriller.databinding.ItemWordCardBinding
import space.rodionov.englishdriller.feature_driller.domain.models.Word
import space.rodionov.englishdriller.feature_driller.presentation.WordDiff

class DrillerAdapter(
    private val onSpeakWord: (String) -> Unit = {}
) : ListAdapter<Word, DrillerAdapter.DrillerViewHolder>(WordDiff()), ModeForAdapter {

    companion object {
        const val TAG_DRILLER_ADAPTER = "drillerAdapter"
    }

    private var mode: Int = 0
    override fun updateMode(newMode: Int) {
        mode = newMode
    }
    override fun getTag(): String = TAG_DRILLER_ADAPTER

    private var mNativeToForeign: Boolean = false
    fun updateTransDir(nativeToForeign: Boolean) { mNativeToForeign = nativeToForeign }

    inner class DrillerViewHolder(
        private val binding: ItemWordCardBinding,
        private val onSpeakItem: (Int) -> Unit = {}
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(word: Word) {
            binding.apply {
                tvDowner.isVisible = false
                btnSpeak.isVisible = !mNativeToForeign

                tvUpper.text = if(mNativeToForeign) word.nativ else word.foreign
                tvDowner.text = if(mNativeToForeign) word.foreign else word.nativ

                (root as ViewGroup).redrawViewGroup(mode)

                root.setOnClickListener {
                    tvDowner.isVisible = true
                    btnSpeak.isVisible = true
                }

                btnSpeak.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onSpeakItem(position)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrillerViewHolder {
        val binding = ItemWordCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DrillerViewHolder(
            binding,
            onSpeakItem = { pos ->
                val word = getItem(pos)
                if (word != null) onSpeakWord(word.foreign)
            }
        )
    }

    override fun onBindViewHolder(holder: DrillerViewHolder, position: Int) {
        val curItem = getItem(position)
        holder.bind(curItem)
    }
}



