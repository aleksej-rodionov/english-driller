package space.rodionov.englishdriller.feature_driller.domain.models

import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val name: String,
    val isCategoryActive: Boolean = true
): BaseModel