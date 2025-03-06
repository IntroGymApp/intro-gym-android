package ru.lonelywh1te.introgym.features.guide.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.lonelywh1te.introgym.databinding.ItemTagBinding
import ru.lonelywh1te.introgym.features.guide.domain.model.Tag

class TagsAdapter: RecyclerView.Adapter<TagViewHolder>() {
    var tags = listOf<Tag>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val binding = ItemTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TagViewHolder(binding)
    }

    override fun getItemCount(): Int = tags.size

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.bind(tags[position])
    }
}

class TagViewHolder(val binding: ItemTagBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Tag) {
        binding.tvTagName.text = item.name
    }
}