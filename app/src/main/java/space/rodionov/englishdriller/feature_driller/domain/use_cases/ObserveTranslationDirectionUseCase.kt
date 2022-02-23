package space.rodionov.englishdriller.feature_driller.domain.use_cases

import kotlinx.coroutines.flow.Flow
import space.rodionov.englishdriller.feature_driller.domain.repository.WordRepo

class ObserveTranslationDirectionUseCase(
    private val repo: WordRepo
) {

    operator fun invoke(): Flow<Boolean> = repo.getTransDir()
}