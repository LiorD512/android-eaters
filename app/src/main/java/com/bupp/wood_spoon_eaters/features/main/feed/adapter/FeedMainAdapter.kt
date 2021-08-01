package com.bupp.wood_spoon_eaters.features.main.feed.adapter

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.*
import com.bupp.wood_spoon_eaters.features.main.feed.adapter.view_holders.*
import com.bupp.wood_spoon_eaters.model.*
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper

class FeedMainAdapter(val listener: FeedMainAdapterListener) : ListAdapter<FeedAdapterItem, RecyclerView.ViewHolder>(DiffCallback()),
    FeedCouponSectionPagerAdapter.FeedCouponSectionListener, FeedAdapterRestaurantViewHolder.FeedAdapterRestaurantViewHolderListener,
    FeedAdapterLargeRestaurantViewHolder.FeedAdapterRestaurantViewHolderListener, FeedRestaurantDishPagerAdapter.FeedRestaurantDishPagerAdapterListener {

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
                val snapHelper = GravitySnapHelper(Gravity.START)
                val adapter = FeedRestaurantDishPagerAdapter(this)
                val binding = FeedAdapterRestaurantItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedAdapterRestaurantViewHolder(parent.context, binding, adapter, snapHelper)
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
//                holder.itemView.setOnClickListener {
//                    Log.d("wowFeedAdapter", "itemView setOnClickListener")
//                }
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

    override fun onPageClick() {
        Log.d("wowFeedAdapter","position:")
//        currentList.
    }
}

