package ru.lonelywh1te.introgym.features.home.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.ui.utils.DiffUtilCallback
import ru.lonelywh1te.introgym.databinding.ItemWorkoutLogBinding
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLogItem
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLogState

class WorkoutLogItemAdapter: RecyclerView.Adapter<WorkoutLogItemViewHolder>() {
    private var onItemClickListener: ((workoutLogItem: WorkoutLogItem) -> Unit)? = null

    private var workoutLogItems: List<WorkoutLogItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutLogItemViewHolder {
        val binding = ItemWorkoutLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkoutLogItemViewHolder(binding)
    }

    override fun getItemCount(): Int = workoutLogItems.size

    override fun onBindViewHolder(holder: WorkoutLogItemViewHolder, position: Int) {
        val item = workoutLogItems[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(item)
        }

    }

    fun update(list: List<WorkoutLogItem>) {
        val diffUtilCallback = DiffUtilCallback(workoutLogItems, list,
            areItemsTheSame = { oldItem, newItem -> oldItem.workoutLogId == newItem.workoutLogId },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem }
        )
        val diffUtilResult = DiffUtil.calculateDiff(diffUtilCallback)

        this.workoutLogItems = list

        diffUtilResult.dispatchUpdatesTo(this)
    }

    fun setOnItemClickListener(listener: (WorkoutLogItem) -> Unit) {
        onItemClickListener = listener
    }

    fun getItem(position: Int): WorkoutLogItem {
        return workoutLogItems[position]
    }
}

class WorkoutLogItemViewHolder(private val binding: ItemWorkoutLogBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: WorkoutLogItem) {
        binding.tvWorkoutName.apply {
            text = item.workoutName

            when(item.state) {
                WorkoutLogState.NotStarted -> {
                    setTextColor(MaterialColors.getColor(binding.root, R.attr.igTextPrimaryColor))
                }
                WorkoutLogState.InProgress -> {
                    setTextColor(MaterialColors.getColor(binding.root, R.attr.igWarningColor))
                }
                WorkoutLogState.Finished -> {
                    setTextColor(MaterialColors.getColor(binding.root, R.attr.igPrimaryColor))
                }
            }
        }

        binding.tvWorkoutDescription.apply {
            text = item.workoutDescription
            visibility = if (text.isBlank()) View.GONE else View.VISIBLE
        }
        binding.tvCountOfExercises.text = binding.root.context.getString(R.string.label_exercise_count, item.countOfExercises.toString())


        binding.tvStateName.apply {
            when(item.state) {
                WorkoutLogState.NotStarted -> {
                    text = binding.root.context.getString(R.string.label_workout_log_state_not_started)
                    setTextColor(MaterialColors.getColor(binding.root, R.attr.igTextSecondaryColor))
                }
                WorkoutLogState.InProgress -> {
                    text = binding.root.context.getString(R.string.label_workout_log_state_in_progress)
                    setTextColor(MaterialColors.getColor(binding.root, R.attr.igWarningColor))
                }
                WorkoutLogState.Finished -> {
                    text = binding.root.context.getString(R.string.label_workout_log_state_finished)
                    setTextColor(MaterialColors.getColor(binding.root, R.attr.igPrimaryColor))
                }
            }
        }

        binding.ivStateIndicator.drawable.apply {
            when(item.state) {
                WorkoutLogState.NotStarted -> {
                    setTint(MaterialColors.getColor(binding.root, R.attr.igTextSecondaryColor))
                }
                WorkoutLogState.InProgress -> {
                    setTint(MaterialColors.getColor(binding.root, R.attr.igWarningColor))
                }
                WorkoutLogState.Finished -> {
                    setTint(MaterialColors.getColor(binding.root, R.attr.igPrimaryColor))
                }
            }
        }

        binding.root.apply {
            strokeColor = when(item.state) {
                WorkoutLogState.NotStarted -> {
                    MaterialColors.getColor(binding.root, R.attr.igCardBackgroundColor)
                }

                WorkoutLogState.InProgress -> {
                    MaterialColors.getColor(binding.root, R.attr.igWarningColor)
                }

                WorkoutLogState.Finished -> {
                    MaterialColors.getColor(binding.root, R.attr.igPrimaryColor)
                }
            }
        }

        binding.layout.apply {
            alpha = when(item.state) {
                WorkoutLogState.Finished -> 0.5f
                else -> 1f
            }
        }
    }
}