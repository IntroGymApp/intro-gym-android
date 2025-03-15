package ru.lonelywh1te.introgym.features.guide.presentation.exercises.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.ui.AssetPath
import ru.lonelywh1te.introgym.core.ui.AssetType
import ru.lonelywh1te.introgym.databinding.ItemExerciseCategoryBinding
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseCategoryItem

// TODO: diffutils

class ExerciseCategoryAdapter: RecyclerView.Adapter<ExerciseCategoryViewHolder>() {
    private var onItemClickListener: ((categoryItem: ExerciseCategoryItem) -> Unit)? = null

    var categoriesList = listOf<ExerciseCategoryItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

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
}

class ExerciseCategoryViewHolder(private val binding: ItemExerciseCategoryBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ExerciseCategoryItem) {
        binding.tvCategoryName.text = item.name
        binding.tvExerciseCount.text = binding.root.context.getString(R.string.label_exercise_count, item.countOfExercises.toString())

        Glide.with(binding.root)
            .load(AssetPath.get(AssetType.CATEGORY_IMAGE, item.imgFilename))
            .into(binding.ivCategoryImage)
    }
}