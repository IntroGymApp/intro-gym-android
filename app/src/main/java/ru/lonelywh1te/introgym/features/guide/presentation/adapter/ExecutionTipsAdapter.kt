package ru.lonelywh1te.introgym.features.guide.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.lonelywh1te.introgym.databinding.ItemExecutionTipBinding

class ExecutionTipsAdapter: RecyclerView.Adapter<ExecutionTipItemViewHolder>() {
    var tips = listOf<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExecutionTipItemViewHolder {
        val binding = ItemExecutionTipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExecutionTipItemViewHolder(binding)
    }

    override fun getItemCount(): Int = tips.size

    override fun onBindViewHolder(holder: ExecutionTipItemViewHolder, position: Int) {
        holder.bind(tips[position])
    }
}

class ExecutionTipItemViewHolder(private val binding: ItemExecutionTipBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(text: String) {
        binding.tvTipDescription.text = text
    }
}