package space.rodionov.englishdriller.feature_driller.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import space.rodionov.englishdriller.feature_driller.domain.models.Word

@Entity
data class WordEntity(
    val nativ: String,
    val foreign: String,
    val categoryName: String,
    val isWordActive: Boolean = true,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) {
    fun toWord(): Word {
        return Word(
            nativ = nativ,
            foreign = foreign,
            categoryName = categoryName,
            isWordActive = isWordActive
        )
    }
}