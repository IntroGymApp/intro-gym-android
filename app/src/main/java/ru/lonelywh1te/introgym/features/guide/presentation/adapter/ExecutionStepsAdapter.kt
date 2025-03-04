package ru.lonelywh1te.introgym.features.guide.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.databinding.ItemExecutionStepBinding

class ExecutionStepsAdapter: RecyclerView.Adapter<ExecutionStepItemViewHolder>() {
    var steps = listOf<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExecutionStepItemViewHolder {
        val binding = ItemExecutionStepBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExecutionStepItemViewHolder(binding)
    }

    override fun getItemCount(): Int = steps.size

    override fun onBindViewHolder(holder: ExecutionStepItemViewHolder, position: Int) {
        holder.bind((position + 1).toString(), steps[position])
    }
}

class ExecutionStepItemViewHolder(private val binding: ItemExecutionStepBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(number: String, text: String) {
        binding.tvStepNumber.text = binding.root.context.getString(R.string.step_number, number)
        binding.tvStepDescription.text = text
    }
}