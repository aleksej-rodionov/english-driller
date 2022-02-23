package space.rodionov.englishdriller.feature_driller.domain.use_cases

import space.rodionov.englishdriller.feature_driller.domain.models.Word
import space.rodionov.englishdriller.feature_driller.domain.repository.WordRepo

class UpdateIsWordActiveUseCase(
    private val repo: WordRepo
) {

    suspend operator fun invoke(
        nativ: String,
        foreign: String,
        catName: String,
        isActive: Boolean
    ) {
        repo.updateIsWordActive(nativ, foreign, catName, isActive)
    }
}