package ru.lonelywh1te.introgym.features.guide.presentation.exercises.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.lonelywh1te.introgym.databinding.ItemTagBinding
import ru.lonelywh1te.introgym.features.guide.domain.model.Tag

class TagsAdapter(private val selectedTagIds: MutableList<Int>): RecyclerView.Adapter<TagViewHolder>() {
    private var setOnSelectedItemListener: ((Tag) -> Unit)? = null

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

        holder.itemView.setOnClickListener {
            it.isSelected = !it.isSelected
            setOnSelectedItemListener?.invoke(tags[position])
        }

        holder.itemView.isSelected = selectedTagIds.contains(tags[position].id)
    }

    fun setOnSelectedItemListener(listener: ((Tag) -> Unit)?) {
        setOnSelectedItemListener = listener
    }
}

class TagViewHolder(val binding: ItemTagBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Tag) {
        binding.tvTagName.text = item.name
    }
}