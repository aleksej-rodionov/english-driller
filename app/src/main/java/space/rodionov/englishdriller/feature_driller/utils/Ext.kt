package space.rodionov.englishdriller.feature_driller.utils

import space.rodionov.englishdriller.feature_driller.domain.models.Word
import kotlin.math.roundToInt

fun List<Word>.countPercentage(): Int {
    val learnedCount = this.filter {
        !it.isWordActive
    }.size
    val totalCount = this.size
    val lch = learnedCount * 100.0f
    val percentage = (lch / totalCount).roundToInt()
    return percentage
}