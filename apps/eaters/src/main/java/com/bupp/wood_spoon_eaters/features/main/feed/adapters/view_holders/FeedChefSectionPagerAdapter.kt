package com.bupp.wood_spoon_eaters.features.main.feed.adapters.view_holders

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.databinding.PagerItemChefBinding
import com.bupp.wood_spoon_eaters.model.FeedChefItemSection

class FeedChefSectionAdapter(
    val listener: FeedChefSectionListener
) : ListAdapter<FeedChefItemSection, RecyclerView.ViewHolder>(DiffCallback()) {

    interface FeedChefSectionListener {
        fun onChefClick(chef: FeedChefItemSection?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            PagerItemChefBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedChefViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        val itemViewHolder = holder as FeedChefViewHolder
        itemViewHolder.bindItem(holder.itemView.context, item, listener)
    }

    class FeedChefViewHolder(
        val binding: PagerItemChefBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindItem(
            context: Context,
            chef: FeedChefItemSection,
            listener: FeedChefSectionListener
        ) {
            Glide
                .with(context)
                .load(chef.cook?.thumbnail?.url)
                .into(binding.ivChefPhoto)
            binding.tvChefName.text = chef.cook?.getFullName()
            binding.root.setOnClickListener {
                listener.onChefClick(chef)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FeedChefItemSection>() {

        override fun areItemsTheSame(
            oldItem: FeedChefItemSection,
            newItem: FeedChefItemSection
        ) = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: FeedChefItemSection,
            newItem: FeedChefItemSection
        ) = oldItem == newItem
    }
}