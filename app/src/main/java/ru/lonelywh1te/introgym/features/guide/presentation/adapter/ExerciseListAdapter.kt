package ru.lonelywh1te.introgym.features.guide.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.lonelywh1te.introgym.core.ui.AssetPath
import ru.lonelywh1te.introgym.core.ui.AssetType
import ru.lonelywh1te.introgym.databinding.ItemExerciseBinding
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseItem

class ExerciseListAdapter: RecyclerView.Adapter<ExerciseItemViewHolder>() {
    private var onItemClickListener: ((exerciseId: Long) -> Unit)? = null

    var exerciseList = listOf<ExerciseItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseItemViewHolder {
        val binding = ItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseItemViewHolder(binding)
    }

    override fun getItemCount(): Int = exerciseList.size

    override fun onBindViewHolder(holder: ExerciseItemViewHolder, position: Int) {
        val item = exerciseList[position]

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(item.id)
        }

        holder.bind(item)
    }

    fun setOnItemClickListener(listener: ((categoryId: Long) -> Unit)?) {
        onItemClickListener = listener
    }
}

class ExerciseItemViewHolder(private val binding: ItemExerciseBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ExerciseItem) {
        binding.tvCategoryName.text = item.name

        Glide.with(binding.root)
            .load(AssetPath.get(AssetType.EXERCISE_PREVIEW_IMAGE, item.imgFilename))
            .into(binding.ivExercisePreview)
    }
}