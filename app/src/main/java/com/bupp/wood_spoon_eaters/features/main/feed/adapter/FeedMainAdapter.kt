package com.bupp.wood_spoon_eaters.features.main.feed.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterCampaignSectionBinding
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterRestaurantItemBinding
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterRestaurantItemSkeletonBinding
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterTitleItemBinding
import com.bupp.wood_spoon_eaters.features.main.feed.adapter.view_holders.FeedAdapterCampaignViewHolder
import com.bupp.wood_spoon_eaters.features.main.feed.adapter.view_holders.FeedAdapterTitleViewHolder
import com.bupp.wood_spoon_eaters.features.main.feed.adapter.view_holders.FeedAdapterRestaurantViewHolder
import com.bupp.wood_spoon_eaters.features.main.feed.adapter.view_holders.FeedAdapterSkeletonViewHolder
import com.bupp.wood_spoon_eaters.model.*

class FeedMainAdapter(val listener: FeedMainAdapterListener) : ListAdapter<FeedAdapterItem, RecyclerView.ViewHolder>(DiffCallback()) {

    interface FeedMainAdapterListener: FeedAdapterRestaurantViewHolder.FeedAdapterRestaurantViewHolderListener{}

    override fun getItemViewType(position: Int): Int = getItem(position).type!!.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            FeedAdapterViewType.TITLE.ordinal -> {
                val binding = FeedAdapterTitleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedAdapterTitleViewHolder(binding)
            }
            FeedAdapterViewType.COUPONS.ordinal -> {
                val binding = FeedAdapterCampaignSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedAdapterCampaignViewHolder(binding)
            }
            FeedAdapterViewType.RESTAURANT.ordinal -> {
                val binding = FeedAdapterRestaurantItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedAdapterRestaurantViewHolder(parent.context, binding)
            }
            else -> {
                val binding = FeedAdapterRestaurantItemSkeletonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedAdapterSkeletonViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("wowFeedAdapter","position: $position")
        val section = getItem(position)
        when (section) {
            is FeedAdapterTitle -> {
                holder as FeedAdapterTitleViewHolder
                holder.bindItems(section)
            }
            is FeedAdapterCoupons -> {
                holder as FeedAdapterCampaignViewHolder
                holder.bindItems(section)
            }
            is FeedAdapterRestaurant -> {
                holder as FeedAdapterRestaurantViewHolder
                holder.bindItems(section,listener)
            }
            is FeedAdapterSkeleton -> {
                holder as FeedAdapterSkeletonViewHolder
                holder.bindItems()
            }

        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<FeedAdapterItem>() {
        override fun areItemsTheSame(
            oldItem: FeedAdapterItem,
            newItem: FeedAdapterItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: FeedAdapterItem,
            newItem: FeedAdapterItem
        ): Boolean {
            return oldItem.type == oldItem.type
        }
    }
}

