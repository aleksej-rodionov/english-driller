package space.rodionov.englishdriller.feature_driller.domain.use_cases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import space.rodionov.englishdriller.feature_driller.domain.models.Word
import space.rodionov.englishdriller.feature_driller.domain.repository.WordRepo

class ObserveWordUseCase(
    private val repo: WordRepo
) {

    operator fun invoke(nativ: String?, foreign: String?, categoryName: String?/*word: Word?*/): Flow<Word?> {
        if (nativ==null || foreign==null || categoryName==null/*word == null*/) {
            return flow {  }
        }
        return repo.observeWord(nativ, foreign, categoryName)
    }
}