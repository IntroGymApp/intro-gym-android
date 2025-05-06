package ru.lonelywh1te.introgym.features.workout.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.ui.AssetPath
import ru.lonelywh1te.introgym.core.ui.AssetType
import ru.lonelywh1te.introgym.core.ui.DiffUtilCallback
import ru.lonelywh1te.introgym.core.ui.ImageLoader
import ru.lonelywh1te.introgym.databinding.ItemExerciseBinding
import ru.lonelywh1te.introgym.databinding.ItemWorkoutExerciseWithProgressBinding
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem

class WorkoutExerciseItemAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onItemClickListener: ((item: WorkoutExerciseItem) -> Unit)? = null

    private var workoutExerciseItems = listOf<WorkoutExerciseItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DEFAULT -> {
                val binding = ItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                WorkoutExerciseItemDefaultViewHolder(binding)
            }
            VIEW_TYPE_WITH_PROGRESS -> {
                val binding = ItemWorkoutExerciseWithProgressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                WorkoutExerciseItemWithProgressViewHolder(binding)
            }
            else -> throw IllegalStateException("Unknown view type: $viewType")
        }
    }

    override fun getItemCount(): Int = workoutExerciseItems.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = workoutExerciseItems[position]

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(item)
        }

        when(holder) {
            is WorkoutExerciseItemDefaultViewHolder -> {
                holder.bind(item as WorkoutExerciseItem.Default)
            }
            is WorkoutExerciseItemWithProgressViewHolder -> {
                holder.bind(item as WorkoutExerciseItem.WithProgress)
            }
        }
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

    fun refresh() {
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: ((item: WorkoutExerciseItem) -> Unit)?) {
        onItemClickListener = listener
    }

    fun getItem(position: Int): WorkoutExerciseItem {
        return workoutExerciseItems[position]
    }

    fun move(from: Int, to: Int) {
        val reorderedWorkoutExerciseItems = workoutExerciseItems.toMutableList()

        val item = reorderedWorkoutExerciseItems.removeAt(from)
        reorderedWorkoutExerciseItems.add(to, item)

        update(reorderedWorkoutExerciseItems)
    }

    override fun getItemViewType(position: Int): Int {
        return when (workoutExerciseItems[position]) {
            is WorkoutExerciseItem.Default -> VIEW_TYPE_DEFAULT
            is WorkoutExerciseItem.WithProgress -> VIEW_TYPE_WITH_PROGRESS
        }
    }

    companion object {
        const val VIEW_TYPE_DEFAULT = 0
        const val VIEW_TYPE_WITH_PROGRESS = 1
    }
}

class WorkoutExerciseItemDefaultViewHolder(
    private val binding: ItemExerciseBinding,
): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: WorkoutExerciseItem.Default) {
        binding.tvCategoryName.text = item.name

        ImageLoader(binding.root.context)
            .load(AssetPath.get(AssetType.EXERCISE_PREVIEW_IMAGE, item.imgFilename), binding.ivExercisePreview)
    }
}

class WorkoutExerciseItemWithProgressViewHolder(
    private val binding: ItemWorkoutExerciseWithProgressBinding,
): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: WorkoutExerciseItem.WithProgress) {
        binding.tvExerciseName.text = item.name

        ImageLoader(binding.root.context)
            .load(AssetPath.get(AssetType.EXERCISE_PREVIEW_IMAGE, item.imgFilename), binding.ivExercisePreview)

        // TODO: fix string
        binding.tvProgress.apply {
            text = "${item.completedSets} / ${item.plannedSets}"
            setTextColor(when {
                item.completedSets == item.plannedSets -> MaterialColors.getColor(binding.root, R.attr.igSuccessColor)
                item.completedSets > item.plannedSets -> MaterialColors.getColor(binding.root, R.attr.igWarningColor)
                else -> MaterialColors.getColor(binding.root, R.attr.igTextPrimaryColor)
            })
        }
    }
}
