package space.rodionov.englishdriller

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import space.rodionov.englishdriller.databinding.RecyclerSimpleItemBinding


class CategoriesAdapter(private val listener: OnItemClickListener/*, var nativeLanguage: NativeLanguage*/) : ListAdapter<CategoryItem, CategoriesAdapter.CategoriesViewHolder>(DiffCallback()) {


    inner class CategoriesViewHolder(private val binding: RecyclerSimpleItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                btnVocabulary.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val categoryItem = getItem(position)
                        listener.onVocabularyClick(categoryItem)
                    }
                }
                btnSwitch.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val categoryItem = getItem(position)
                        listener.onSwitchClick(categoryItem, btnSwitch.isChecked)
                    }
                }
            }
        }

        fun bind(categoryItem: CategoryItem) {
            binding.apply {
                btnSwitch.isChecked = categoryItem.categoryShown
                btnSwitch.text = if (categoryItem.categoryShown) "On" else "Off"
                tvCategoryName.text = categoryItem.categoryNameRus
                // color of Checked category should be coded here
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val binding = RecyclerSimpleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoriesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

//    override fun getItemCount(): Int = categoryList.size

    interface OnItemClickListener {
        fun onVocabularyClick(categoryItem: CategoryItem)
        fun onSwitchClick(categoryItem: CategoryItem, isChecked: Boolean)
    }

    class DiffCallback : DiffUtil.ItemCallback<CategoryItem>() {
        override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem) =
            oldItem.categoryNameRus == newItem.categoryNameRus

    }

}







