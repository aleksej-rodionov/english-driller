package space.rodionov.englishdriller.ui

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import space.rodionov.englishdriller.feature_words.domain.model.Category
import space.rodionov.englishdriller.databinding.RecyclerSimpleItemBinding
import space.rodionov.englishdriller.util.fetchColors
import space.rodionov.englishdriller.util.fetchTheme


class CategoriesAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Category, CategoriesAdapter.CategoriesViewHolder>(
        DiffCallback()
    ) {

    private var mode: Int = 0

    fun updateMode(newMode: Int) {
        mode = newMode
        notifyDataSetChanged()
    }

    inner class CategoriesViewHolder(private val binding: RecyclerSimpleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

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

                val theme = fetchTheme(mode, itemView.resources)
                val colors = theme.fetchColors()
                rl.background = colors[0].toDrawable()
                btnSwitch.apply {
                    setTextColor(colors[3])
                    thumbTintList = ColorStateList.valueOf(colors[4])
                    trackTintList = ColorStateList.valueOf(colors[4])
                }
                tvCategoryName.setTextColor(colors[2])
                btnVocabulary.drawable?.setTint(colors[2])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val binding =
            RecyclerSimpleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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







