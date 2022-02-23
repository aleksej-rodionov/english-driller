package space.rodionov.englishdriller.feature_driller.presentation

import space.rodionov.englishdriller.feature_driller.domain.models.CatWithWords
import space.rodionov.englishdriller.feature_driller.domain.models.Category
import space.rodionov.englishdriller.feature_driller.domain.models.Word

class WordDiff : DiffUtil.ItemCallback<Word>() {
    override fun areItemsTheSame(oldItem: Word, newItem: Word) =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: Word, newItem: Word) =
        oldItem.foreign == newItem.foreign && oldItem.nativ == newItem.nativ
                && oldItem.categoryName == newItem.categoryName
}


class CatDiff : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category) =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: Category, newItem: Category) =
        oldItem.isCategoryActive == newItem.isCategoryActive && oldItem.name == newItem.name
}

class CatWithWordsDiff : DiffUtil.ItemCallback<CatWithWords>() {
    override fun areItemsTheSame(oldItem: CatWithWords, newItem: CatWithWords) =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: CatWithWords, newItem: CatWithWords) =
        oldItem.category.isCategoryActive == newItem.category.isCategoryActive &&
                oldItem.category.name == newItem.category.name &&
                oldItem.words.countPercentage() == newItem.words.countPercentage()
}