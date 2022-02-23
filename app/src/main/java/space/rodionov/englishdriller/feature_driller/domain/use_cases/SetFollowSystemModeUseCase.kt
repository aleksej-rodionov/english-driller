package space.rodionov.englishdriller.feature_driller.domain.use_cases

import space.rodionov.englishdriller.feature_driller.domain.repository.WordRepo

class SetFollowSystemModeUseCase(
    private val repo: WordRepo
) {

   suspend operator fun invoke(follow: Boolean) = repo.setFollowSystemMode(follow)
}