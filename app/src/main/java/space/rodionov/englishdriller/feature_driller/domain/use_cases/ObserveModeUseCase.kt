package space.rodionov.englishdriller.feature_driller.domain.use_cases

import space.rodionov.englishdriller.feature_driller.domain.repository.WordRepo

class ObserveModeUseCase(
    private val repo: WordRepo
) {

    operator fun invoke() = repo.getMode()
}