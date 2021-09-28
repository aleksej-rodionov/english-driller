package space.rodionov.englishdriller.data

import androidx.room.Embedded
import androidx.room.Relation
import space.rodionov.englishdriller.feature_words.domain.model.Category
import space.rodionov.englishdriller.feature_words.domain.model.Word
import space.rodionov.englishdriller.feature_words.domain.model.Word1

data class CategoryWithWords(
    @Embedded
    val category: Category,
    @Relation(
        parentColumn = "categoryNameRus",
        entityColumn = "catName"
    )
    val words: List<Word1>
)
