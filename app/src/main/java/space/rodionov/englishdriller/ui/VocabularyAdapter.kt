package space.rodionov.englishdriller.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import space.rodionov.englishdriller.feature_words.domain.model.Word
import space.rodionov.englishdriller.databinding.RecyclerItemBinding

class VocabularyAdapter(private val listener: OnVocItemClickListener/*, var nativeLanguage: NativeLanguage*/) :
    ListAdapter<Word, VocabularyAdapter.VocabularyViewHolder>(DiffCallback()) {

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
                tvDowner.text = /*if (nativeLanguage == NativeLanguage.RUS) */word.rus/* else word.eng*/
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

    class DiffCallback : DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(oldItem: Word, newItem: Word) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Word, newItem: Word) =
            oldItem.foreign == newItem.foreign && oldItem.rus == newItem.rus
                    && oldItem.category == newItem.category

    }


}