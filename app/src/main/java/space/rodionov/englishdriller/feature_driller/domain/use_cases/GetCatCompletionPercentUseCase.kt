package space.rodionov.englishdriller.feature_driller.domain.use_cases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import space.rodionov.englishdriller.feature_driller.data.local.entity.WordEntity
import space.rodionov.englishdriller.feature_driller.domain.models.Category
import space.rodionov.englishdriller.feature_driller.domain.repository.WordRepo

class GetCatCompletionPercentUseCase(
    private val repo: WordRepo
) {

//    operator fun invoke(cat: Category): Flow<Float> = combine(
//        observeActiveWordsByCat(cat.name),
//        observeAllWordsByCat(cat.name)
//    ) { learned, total ->
//        Pair(learned, total)
//    }.flatMapLatest { (learned, total) ->
//        countPercentFlow(learned.size.toFloat(), total.size.toFloat())
//    }
//
//    fun observeActiveWordsByCat(catName: String): Flow<List<WordEntity>> =
//        repo.observeActiveWordsByCat(catName)
//
//    fun observeAllWordsByCat(catName: String): Flow<List<WordEntity>> =
//        repo.observeAllWordsByCat(catName)
//
//    fun countPercentFlow(
//        learned: Float,
//        total: Float
//    ) :Flow<Float> = flow {
//        val percentage = (learned / total) * 100
//        emit(percentage)
//    }
}