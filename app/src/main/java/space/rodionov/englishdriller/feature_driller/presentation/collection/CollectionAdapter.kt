package space.rodionov.englishdriller.feature_driller.presentation.collection

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import space.rodionov.englishdriller.core.ModeForAdapter
import space.rodionov.englishdriller.core.redrawViewGroup
import space.rodionov.englishdriller.databinding.ItemCategoryBinding
import space.rodionov.englishdriller.feature_driller.domain.models.CatWithWords
import space.rodionov.englishdriller.feature_driller.domain.models.Category
import space.rodionov.englishdriller.feature_driller.presentation.CatWithWordsDiff
import space.rodionov.englishdriller.feature_driller.utils.Constants.TAG_PETR
import space.rodionov.englishdriller.feature_driller.utils.countPercentage

class CollectionAdapter(
    private val onSwitchCatActive: (Category, Boolean) -> Unit = { _, _ -> },
    private val onClickCat: (Category) -> Unit = { _ -> }
) : ListAdapter<CatWithWords, CollectionAdapter.CollectionViewHolder>(CatWithWordsDiff()),
    ModeForAdapter {

    companion object {
        const val TAG_COLLECTION_ADAPTER = "collectionAdapter"
    }

    private var mode: Int = 0
    override fun updateMode(newMode: Int) {
        mode = newMode
    }
    override fun getTag(): String = TAG_COLLECTION_ADAPTER

    fun refreshCatSwitchState(catToRefresh: Category) {
        val pos = findPosByName(catToRefresh.name)
        pos?.let {
            notifyItemChanged(it)
        }
    }

    private fun findPosByName(catName: String): Int? {
        var pos: Int? = null
        for (i in 0 until currentList.size) {
            if (getItem(i).category.name == catName) {
                pos = i
            }
        }
        Log.d(TAG_PETR, "findPosByName: position to refresh = $pos")
        return pos
    }

//=============================================MAIN ADAPTER STUFF==================================

    inner class CollectionViewHolder(
        private val binding: ItemCategoryBinding,
        private val onCheckSwitch: (Int, Boolean) -> Unit = { _, _ -> },
        private val onClickItem: (Int) -> Unit = {}
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cww: CatWithWords) {
            binding.apply {
                tvCatName.text = cww.category.name

                (root as ViewGroup).redrawViewGroup(mode)

                switchCat.setOnCheckedChangeListener(null)
                switchCat.text = "${cww.words.countPercentage().toString()} %"
                switchCat.isChecked = cww.category.isCategoryActive
                switchCat.setOnCheckedChangeListener { _, isChecked ->
                    val pos = adapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        onCheckSwitch(pos, isChecked)
                    }
                }
                root.setOnClickListener {
                    val pos = adapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        onClickItem(pos)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollectionViewHolder(
            binding,
            onCheckSwitch = { pos, isChecked ->
                val cww = getItem(pos)
                if (cww != null) onSwitchCatActive(cww.category, isChecked)
            },
            onClickItem = { pos ->
                val cww = getItem(pos)
                if (cww != null) onClickCat(cww.category)
            }
        )
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val curItem = getItem(position)
        holder.bind(curItem)
    }
}



