package com.bupp.wood_spoon_eaters.features.main.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.SearchTagItemBinding

class SearchTagsAdapter(val listener: SearchTagsAdapterListener) :
    ListAdapter<String, RecyclerView.ViewHolder>(DiffCallback()) {

    interface SearchTagsAdapterListener {
        fun onTagClick(tag: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = SearchTagItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchTagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        holder as SearchTagViewHolder
        holder.bindItem(item)
    }

    inner class SearchTagViewHolder(val binding: SearchTagItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val title = binding.searchTagItem
        fun bindItem(titleItem: String) {
            title.text = titleItem

            title.setOnClickListener {
                listener.onTagClick(titleItem)
            }
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