package space.rodionov.englishdriller.feature_driller.domain.models

data class Word(
    val nativ: String,
    val foreign: String,
    val categoryName: String,
    val isWordActive: Boolean
): BaseModel