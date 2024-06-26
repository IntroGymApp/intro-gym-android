package ru.lonelywh1te.introgymapp.presentation.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.lonelywh1te.introgymapp.databinding.WorkoutDateItemBinding
import ru.lonelywh1te.introgymapp.domain.model.Workout

class WorkoutDateAdapter(private val onWorkoutItemClick: OnWorkoutItemClick): RecyclerView.Adapter<WorkoutDateViewHolder>() {
    var workoutList = listOf<Workout>()
        set(newList) {
            val diffCallback = WorkoutCallback(workoutList, newList)
            val diffWorkout = DiffUtil.calculateDiff(diffCallback)

            field = newList

            diffWorkout.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutDateViewHolder {
        val binding = WorkoutDateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkoutDateViewHolder(binding)
    }

    override fun getItemCount(): Int = workoutList.size

    override fun onBindViewHolder(holder: WorkoutDateViewHolder, position: Int) {
        val item = workoutList[position]
        val binding = WorkoutDateItemBinding.bind(holder.itemView)

        binding.workoutDateCard.setOnClickListener {
            onWorkoutItemClick.onClick(item)
        }

        holder.bind(item)
    }
}

class WorkoutDateViewHolder(private val binding: WorkoutDateItemBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Workout) {
        binding.tvWorkoutName.text = item.name
        binding.tvWorkoutDescription.text = item.description
        binding.tvExerciseCount.text = "Упражнений: ${item.exerciseCount}"
    }
}