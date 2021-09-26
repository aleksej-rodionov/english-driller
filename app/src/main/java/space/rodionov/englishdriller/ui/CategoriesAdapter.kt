package space.rodionov.englishdriller.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import space.rodionov.englishdriller.feature_words.domain.model.Category
import space.rodionov.englishdriller.databinding.RecyclerSimpleItemBinding


class CategoriesAdapter(private val listener: OnItemClickListener/*, var nativeLanguage: NativeLanguage*/) : ListAdapter<Category, CategoriesAdapter.CategoriesViewHolder>(
    DiffCallback()
) {


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

        fun bind(category: Category) {
            binding.apply {
                btnSwitch.isChecked = category.categoryShown
                btnSwitch.text = if (category.categoryShown) "On" else "Off"
                tvCategoryName.text = category.categoryNameRus
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
        fun onVocabularyClick(category: Category)
        fun onSwitchClick(category: Category, isChecked: Boolean)
    }

    class DiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Category, newItem: Category) =
            oldItem.categoryNameRus == newItem.categoryNameRus

    }

}







