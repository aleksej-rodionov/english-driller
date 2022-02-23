package space.rodionov.englishdriller.feature_driller.domain.use_cases

import space.rodionov.englishdriller.feature_driller.domain.repository.WordRepo

class GetAllActiveCatsNamesUseCase(
    private val repo: WordRepo
) {

    suspend operator fun invoke(): List<String> {
        return repo.getAllActiveCatsNames()
    }
}