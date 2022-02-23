package space.rodionov.englishdriller.feature_driller.domain.use_cases

import space.rodionov.englishdriller.feature_driller.domain.repository.WordRepo

class GetAllCatsNamesUseCase(
    private val repo: WordRepo
) {

    suspend operator fun invoke(): List<String> {
        return repo.getAllCatsNames()
    }
}