package space.rodionov.englishdriller.feature_driller.domain.use_cases

import kotlinx.coroutines.flow.Flow
import space.rodionov.englishdriller.feature_driller.domain.models.CatWithWords
import space.rodionov.englishdriller.feature_driller.domain.repository.WordRepo

class ObserveAllCatsWithWordsUseCase(
    private val repo: WordRepo
) {

    operator fun invoke(): Flow<List<CatWithWords>> = repo.observeAllCategoriesWithWords()
}