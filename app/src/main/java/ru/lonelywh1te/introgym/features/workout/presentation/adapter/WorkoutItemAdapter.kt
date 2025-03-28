package ru.lonelywh1te.introgym.features.workout.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.ui.DiffUtilCallback
import ru.lonelywh1te.introgym.databinding.ItemWorkoutBinding
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutItem

class WorkoutItemAdapter: RecyclerView.Adapter<WorkoutItemViewHolder>() {
    private var onItemClickListener: ((workout: WorkoutItem) -> Unit)? = null

    private var workoutItems: List<WorkoutItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutItemViewHolder {
        val binding = ItemWorkoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkoutItemViewHolder(binding)
    }

    override fun getItemCount(): Int = workoutItems.size

    override fun onBindViewHolder(holder: WorkoutItemViewHolder, position: Int) {
        holder.bind(workoutItems[position])

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(workoutItems[position])
        }
    }

    fun update(list: List<WorkoutItem>) {
        val diffUtilCallback = DiffUtilCallback(workoutItems, list,
            areItemsTheSame = { oldItem, newItem -> oldItem.workoutId == newItem.workoutId },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem }
        )
        val diffUtilResult = DiffUtil.calculateDiff(diffUtilCallback)

        this.workoutItems = list

        diffUtilResult.dispatchUpdatesTo(this)
    }

    fun move(from: Int, to: Int) {
        val list = this.workoutItems.toMutableList()

        val item = list.removeAt(from)
        list.add(to, item)

        update(list)
    }

    fun getItem(position: Int): WorkoutItem {
        return workoutItems[position]
    }

    fun setOnItemClickListener(listener: ((workout: WorkoutItem) -> Unit)?) {
        this.onItemClickListener = listener
    }
}

class WorkoutItemViewHolder(private val binding: ItemWorkoutBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: WorkoutItem) {
        binding.tvWorkoutName.text = item.name
        binding.tvExerciseCount.text = binding.root.context.getString(R.string.label_exercise_count, item.countOfExercises.toString())
    }
}