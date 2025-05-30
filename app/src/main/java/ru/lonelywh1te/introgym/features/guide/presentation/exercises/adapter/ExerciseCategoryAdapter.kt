package ru.lonelywh1te.introgym.features.guide.presentation.exercises.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.ui.AssetPath
import ru.lonelywh1te.introgym.core.ui.AssetType
import ru.lonelywh1te.introgym.core.ui.utils.DiffUtilCallback
import ru.lonelywh1te.introgym.core.ui.utils.ImageLoader
import ru.lonelywh1te.introgym.databinding.ItemExerciseCategoryBinding
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseCategoryItem

class ExerciseCategoryAdapter: RecyclerView.Adapter<ExerciseCategoryViewHolder>() {
    private var onItemClickListener: ((categoryItem: ExerciseCategoryItem) -> Unit)? = null

    private var categoriesList = listOf<ExerciseCategoryItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseCategoryViewHolder {
        val binding = ItemExerciseCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseCategoryViewHolder(binding)
    }

    override fun getItemCount(): Int = categoriesList.size

    override fun onBindViewHolder(holder: ExerciseCategoryViewHolder, position: Int) {
        val item = categoriesList[position]

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(item)
        }

        holder.bind(item)
    }

    fun setOnItemClickListener(listener: ((categoryItem: ExerciseCategoryItem) -> Unit)?) {
        onItemClickListener = listener
    }

    fun update(list: List<ExerciseCategoryItem>) {
        val diffUtilCallback = DiffUtilCallback(categoriesList, list,
            areItemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
        )
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)

        categoriesList = list

        diffResult.dispatchUpdatesTo(this)
    }
}

class ExerciseCategoryViewHolder(private val binding: ItemExerciseCategoryBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ExerciseCategoryItem) {
        binding.tvCategoryName.text = item.name
        binding.tvExerciseCount.text = binding.root.context.getString(R.string.label_exercise_count, item.countOfExercises.toString())

        ImageLoader(binding.root.context)
            .load(AssetPath.get(AssetType.CATEGORY_IMAGE, item.imgFilename), binding.ivCategoryImage)
    }
}