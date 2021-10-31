package space.rodionov.englishdriller.ui

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import space.rodionov.englishdriller.feature_words.domain.model.Word
import space.rodionov.englishdriller.databinding.RecyclerItemBinding
import space.rodionov.englishdriller.util.fetchColors
import space.rodionov.englishdriller.util.fetchTheme

class VocabularyAdapter(private val listener: OnVocItemClickListener) :
    ListAdapter<Word, VocabularyAdapter.VocabularyViewHolder>(WordDiff()) {

    private var mode: Int = 0

    fun updateMode(newMode: Int) {
        mode = newMode
        notifyDataSetChanged()
    }

    inner class VocabularyViewHolder(private val binding: RecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val word = getItem(position)
                        listener.onItemClick(word)
                    }
                }
                btnSwitch.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val word = getItem(position)
                        listener.onSwitchClick(word, btnSwitch.isChecked)
                    }
                }
            }
        }

        fun bind(word: Word) {
            binding.apply {
                btnSwitch.isChecked = word.shown
                btnSwitch.text = if (word.shown) "On" else "Off"
                tvUpper.text = word.foreign
                tvDowner.text = /*if (nativeLanguage == NativeLanguage.RUS) */
                    word.rus/* else word.eng*/

                val theme = fetchTheme(mode, itemView.resources)
                val colors = theme.fetchColors()
                rl.background = colors[0].toDrawable()
                btnSwitch.apply {
                    setTextColor(colors[3])
                    thumbTintList = ColorStateList.valueOf(colors[4])
                    trackTintList = ColorStateList.valueOf(colors[4])
                }
                tvDowner.setTextColor(colors[3])
                tvUpper.setTextColor(colors[2])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VocabularyViewHolder {
        val binding =
            RecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VocabularyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VocabularyViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }


    interface OnVocItemClickListener {
        fun onItemClick(word: Word)
        fun onSwitchClick(word: Word, isChecked: Boolean)
    }
}