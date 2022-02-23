package space.rodionov.englishdriller.feature_driller.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import space.rodionov.englishdriller.feature_driller.domain.models.Category

@Entity
data class CategoryEntity(
    val name: String,
    val isCategoryActive: Boolean = true,
    @PrimaryKey(autoGenerate = true) val id: Int? = null
) {
    fun toCategory(): Category {
        return Category(
            name = name,
            isCategoryActive = isCategoryActive
        )
    }
}
