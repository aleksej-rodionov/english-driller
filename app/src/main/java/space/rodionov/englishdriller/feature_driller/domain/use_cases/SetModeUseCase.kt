package space.rodionov.englishdriller.feature_driller.domain.use_cases

import space.rodionov.englishdriller.feature_driller.domain.repository.WordRepo

class SetModeUseCase(
    private val repo: WordRepo
) {

   suspend operator fun invoke(mode: Int) = repo.setMode(mode)
}