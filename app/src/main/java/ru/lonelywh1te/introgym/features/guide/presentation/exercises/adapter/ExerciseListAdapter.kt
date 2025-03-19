package ru.lonelywh1te.introgym.features.guide.presentation.exercises.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.ui.AssetPath
import ru.lonelywh1te.introgym.core.ui.AssetType
import ru.lonelywh1te.introgym.core.ui.DiffUtilCallback
import ru.lonelywh1te.introgym.databinding.ItemExerciseBinding
import ru.lonelywh1te.introgym.features.guide.domain.model.ExerciseItem

class ExerciseListAdapter(private val isPickMode: Boolean): RecyclerView.Adapter<ExerciseItemViewHolder>() {
    private var onItemClickListener: ((exercise: ExerciseItem) -> Unit)? = null

    private var exerciseList = listOf<ExerciseItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseItemViewHolder {
        val binding = ItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseItemViewHolder(binding, isPickMode)
    }

    override fun getItemCount(): Int = exerciseList.size

    override fun onBindViewHolder(holder: ExerciseItemViewHolder, position: Int) {
        val item = exerciseList[position]

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(item)
        }

        holder.bind(item)
    }

    fun update(list: List<ExerciseItem>) {
        val diffUtilCallback = DiffUtilCallback(exerciseList, list,
            areItemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem }
        )
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)

        exerciseList = list

        diffResult.dispatchUpdatesTo(this)
    }

    fun setOnItemClickListener(listener: ((exercise: ExerciseItem) -> Unit)?) {
        onItemClickListener = listener
    }
}

class ExerciseItemViewHolder(
    private val binding: ItemExerciseBinding,
    private val isPickMode: Boolean
): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ExerciseItem) {
        binding.tvCategoryName.text = item.name

        Glide.with(binding.root)
            .load(AssetPath.get(AssetType.EXERCISE_PREVIEW_IMAGE, item.imgFilename))
            .into(binding.ivExercisePreview)

        binding.ivActionIcon.setImageDrawable(
            ContextCompat.getDrawable(binding.root.context,
                if (isPickMode) R.drawable.ic_plus else R.drawable.ic_right_arrow
            )
        )
    }
}