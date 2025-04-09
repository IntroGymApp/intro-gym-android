package ru.lonelywh1te.introgym.features.workout.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.lonelywh1te.introgym.core.ui.AssetPath
import ru.lonelywh1te.introgym.core.ui.AssetType
import ru.lonelywh1te.introgym.core.ui.DiffUtilCallback
import ru.lonelywh1te.introgym.core.ui.ImageLoader
import ru.lonelywh1te.introgym.databinding.ItemExerciseBinding
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem

class WorkoutExerciseItemAdapter: RecyclerView.Adapter<WorkoutExerciseItemViewHolder>() {
    private var onItemClickListener: ((item: WorkoutExerciseItem) -> Unit)? = null

    private var workoutExerciseItems = listOf<WorkoutExerciseItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutExerciseItemViewHolder {
        val binding = ItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkoutExerciseItemViewHolder(binding)
    }

    override fun getItemCount(): Int = workoutExerciseItems.size

    override fun onBindViewHolder(holder: WorkoutExerciseItemViewHolder, position: Int) {
        val item = workoutExerciseItems[position]

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(item)
        }

        holder.bind(item)
    }

    fun update(list: List<WorkoutExerciseItem>) {
        val diffUtilCallback = DiffUtilCallback(workoutExerciseItems, list,
            areItemsTheSame = { oldItem, newItem -> oldItem.workoutExerciseId == newItem.workoutExerciseId },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem }
        )
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)

        workoutExerciseItems = list

        diffResult.dispatchUpdatesTo(this)
    }

    fun setOnItemClickListener(listener: ((item: WorkoutExerciseItem) -> Unit)?) {
        onItemClickListener = listener
    }

    fun getItem(position: Int): WorkoutExerciseItem {
        return workoutExerciseItems[position]
    }
}

class WorkoutExerciseItemViewHolder(
    private val binding: ItemExerciseBinding,
): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: WorkoutExerciseItem) {
        binding.tvCategoryName.text = item.name

        ImageLoader(binding.root.context)
            .load(AssetPath.get(AssetType.EXERCISE_PREVIEW_IMAGE, item.imgFilename), binding.ivExercisePreview)
    }
}