package space.rodionov.englishdriller.feature_driller.domain.use_cases

import space.rodionov.englishdriller.feature_driller.domain.repository.WordRepo

class IsCategoryActiveUseCase(
    private val repo: WordRepo
) {

    suspend operator fun invoke(name: String): Boolean = repo.isCatActive(name)
}