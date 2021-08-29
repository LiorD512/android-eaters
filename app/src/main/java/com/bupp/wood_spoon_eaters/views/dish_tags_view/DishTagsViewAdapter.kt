package com.bupp.wood_spoon_eaters.views.dish_tags_view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.TagViewItemBinding

class DishTagsViewAdapter: ListAdapter<String, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = TagViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TagItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val tag = getItem(position)
        val itemViewHolder = holder as TagItemViewHolder

        itemViewHolder.bindItem(tag)

    }


    class TagItemViewHolder(view: TagViewItemBinding) : RecyclerView.ViewHolder(view.root) {
        private val tagView: TextView = view.tagViewItem

        fun bindItem(tag: String){
            tagView.text = tag
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}