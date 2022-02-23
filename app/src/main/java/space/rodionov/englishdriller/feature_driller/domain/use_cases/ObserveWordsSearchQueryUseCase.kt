package space.rodionov.englishdriller.feature_driller.domain.use_cases

import kotlinx.coroutines.flow.Flow
import space.rodionov.englishdriller.feature_driller.data.local.entity.WordEntity
import space.rodionov.englishdriller.feature_driller.domain.models.Word
import space.rodionov.englishdriller.feature_driller.domain.repository.WordRepo

class ObserveWordsSearchQueryUseCase(
    private val repo: WordRepo
) {

    operator fun invoke(catName: String, searchQuery: String): Flow<List<Word>> =
        repo.wordsBySearchQuery(catName, searchQuery)
}