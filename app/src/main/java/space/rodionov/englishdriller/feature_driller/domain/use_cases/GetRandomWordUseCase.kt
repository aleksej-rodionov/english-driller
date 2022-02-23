package space.rodionov.englishdriller.feature_driller.domain.use_cases

import space.rodionov.englishdriller.feature_driller.domain.models.Word
import space.rodionov.englishdriller.feature_driller.domain.repository.WordRepo

class GetRandomWordUseCase(
    private val repo: WordRepo
) {

    suspend operator fun invoke(activeCatsNames: List<String>) : Word {
        return repo.getRandomWordFromActiveCats(activeCatsNames)
    }
}