package com.bupp.wood_spoon_eaters.features.main.feed.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.*
import com.bupp.wood_spoon_eaters.features.main.feed.adapter.view_holders.*
import com.bupp.wood_spoon_eaters.model.*

class FeedMainAdapter(val listener: FeedMainAdapterListener) : ListAdapter<FeedAdapterItem, RecyclerView.ViewHolder>(DiffCallback()),
    FeedCouponSectionPagerAdapter.FeedCouponSectionListener, FeedAdapterRestaurantViewHolder.FeedAdapterRestaurantViewHolderListener,
    FeedAdapterLargeRestaurantViewHolder.FeedAdapterRestaurantViewHolderListener {

    interface FeedMainAdapterListener{
        fun onShareBannerClick(campaign: Campaign)
        fun onRestaurantClick(cook: Cook)
    }

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
            FeedAdapterViewType.RESTAURANT_LARGE.ordinal -> {
                val binding = FeedAdapterBigRestaurantItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedAdapterLargeRestaurantViewHolder(parent.context, binding)
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
                holder.bindItems(section, this)
            }
            is FeedAdapterRestaurant -> {
                holder as FeedAdapterRestaurantViewHolder
                holder.bindItems(section, this)
            }
            is FeedAdapterLargeRestaurant -> {
                holder as FeedAdapterLargeRestaurantViewHolder
                holder.bindItems(section, this)
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

    override fun onShareBannerClick(campaign: Campaign?) {
        campaign?.let{
            listener.onShareBannerClick(campaign)
        }
    }

    override fun onRestaurantClick(cook: Cook) {
        listener.onRestaurantClick(cook)
    }
}

