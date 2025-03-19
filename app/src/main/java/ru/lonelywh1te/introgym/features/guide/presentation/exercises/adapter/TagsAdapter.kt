package ru.lonelywh1te.introgym.features.guide.presentation.exercises.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.lonelywh1te.introgym.core.ui.DiffUtilCallback
import ru.lonelywh1te.introgym.databinding.ItemTagBinding
import ru.lonelywh1te.introgym.features.guide.domain.model.Tag

class TagsAdapter(private val selectedTagIds: MutableList<Int>): RecyclerView.Adapter<TagViewHolder>() {
    private var setOnSelectedItemListener: ((Tag) -> Unit)? = null

    private var tags = listOf<Tag>()

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

    fun update(list: List<Tag>) {
        val diffUtilCallback = DiffUtilCallback(tags, list,
            areItemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id},
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
        )

        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)

        tags = list

        diffResult.dispatchUpdatesTo(this)
    }
}

class TagViewHolder(val binding: ItemTagBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Tag) {
        binding.tvTagName.text = item.name
    }
}