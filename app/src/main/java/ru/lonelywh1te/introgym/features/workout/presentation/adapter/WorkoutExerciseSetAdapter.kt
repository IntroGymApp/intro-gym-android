package ru.lonelywh1te.introgym.features.workout.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import ru.lonelywh1te.introgym.core.ui.utils.DiffUtilCallback
import ru.lonelywh1te.introgym.databinding.ItemSetBinding
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseSet
import ru.lonelywh1te.introgym.features.workout.presentation.helper.WorkoutExerciseSetHelper
import java.util.UUID

class WorkoutExerciseSetAdapter: RecyclerView.Adapter<WorkoutExerciseSetViewHolder>() {
    private var workoutExerciseSets = emptyList<WorkoutExerciseSet>()
    private var onItemClickListener: ((exerciseSetId: UUID) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutExerciseSetViewHolder {
        val binding = ItemSetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkoutExerciseSetViewHolder(binding)
    }

    override fun getItemCount(): Int = workoutExerciseSets.size

    override fun onBindViewHolder(holder: WorkoutExerciseSetViewHolder, position: Int) {
        val set = workoutExerciseSets[position]
        holder.bind(set, workoutExerciseSets.size - workoutExerciseSets.indexOf(set))

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(set.id)
        }
    }

    fun update(list: List<WorkoutExerciseSet>) {
        workoutExerciseSets = list
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (exerciseSetId: UUID) -> Unit) {
        onItemClickListener = listener
    }
}

class WorkoutExerciseSetViewHolder(private val binding: ItemSetBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(set: WorkoutExerciseSet, index: Int) {
        val effortColor = MaterialColors.getColor(binding.root, WorkoutExerciseSetHelper.getEffortColor(set))

        binding.tvNumberOfSet.text = "$index#"
        binding.cvNumberOfSet.background.setTint(effortColor)
        binding.tvSetInfo.apply {
            text = WorkoutExerciseSetHelper.getStringInfo(set)
            setTextColor(effortColor)
        }
    }
}