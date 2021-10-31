package space.rodionov.englishdriller.ui

import androidx.recyclerview.widget.DiffUtil
import space.rodionov.englishdriller.feature_words.domain.model.Word

class WordDiff : DiffUtil.ItemCallback<Word>() {
    override fun areItemsTheSame(oldItem: Word, newItem: Word) =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: Word, newItem: Word) =
        oldItem.foreign == newItem.foreign && oldItem.rus == newItem.rus
                && oldItem.category == newItem.category

}