package com.bupp.wood_spoon_eaters.features.main.feed.adapters

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.*
import com.bupp.wood_spoon_eaters.features.main.feed.adapters.decorators.FeedAdapterDishItemDecorator
import com.bupp.wood_spoon_eaters.features.main.feed.adapters.view_holders.*
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.views.abs.RecyclerHorizontalIndicatorDecoration
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper

class FeedMainAdapter(val listener: FeedMainAdapterListener) : ListAdapter<FeedAdapterItem, RecyclerView.ViewHolder>(DiffCallback()),
    FeedCouponSectionPagerAdapter.FeedCouponSectionListener, FeedAdapterRestaurantViewHolder.FeedAdapterRestaurantViewHolderListener,
    FeedAdapterLargeRestaurantViewHolder.FeedAdapterRestaurantViewHolderListener, FeedRestaurantDishPagerAdapter.FeedRestaurantDishPagerAdapterListener {

    interface FeedMainAdapterListener {
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
                Log.d("wowFeedAdapter", "onCreateViewHolder -> RESTAURANT")
                val snapHelper = GravitySnapHelper(Gravity.START)
                val adapter = FeedRestaurantDishPagerAdapter(this)
                val binding = FeedAdapterRestaurantItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

                binding.feedRestaurantItemList.addItemDecoration(FeedAdapterDishItemDecorator())
                binding.feedRestaurantItemList.addItemDecoration(RecyclerHorizontalIndicatorDecoration())
                binding.feedRestaurantItemList.layoutManager = LinearLayoutManager(parent.context, RecyclerView.HORIZONTAL, false)
                binding.feedRestaurantItemList.adapter = adapter

                FeedAdapterRestaurantViewHolder(parent.context, binding, adapter, snapHelper)
            }
            FeedAdapterViewType.RESTAURANT_LARGE.ordinal -> {
                val snapHelper = GravitySnapHelper(Gravity.START)
                val adapter = FeedRestaurantDishPagerAdapter(this)
                val binding = FeedAdapterBigRestaurantItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

                binding.feedRestaurantItemList.addItemDecoration(FeedAdapterDishItemDecorator())
                binding.feedRestaurantItemList.addItemDecoration(RecyclerHorizontalIndicatorDecoration())
                binding.feedRestaurantItemList.layoutManager = LinearLayoutManager(parent.context, RecyclerView.HORIZONTAL, false)
                binding.feedRestaurantItemList.adapter = adapter

                FeedAdapterLargeRestaurantViewHolder(parent.context, binding, adapter, snapHelper)
            }
            FeedAdapterViewType.EMPTY_FEED_NO_CHEFS.ordinal -> {
                val binding = FeedAdapterNoChefItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedAdapterNoChefViewHolder(binding)
            }
            else -> {
                val binding = FeedAdapterRestaurantItemSkeletonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedAdapterSkeletonViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("wowFeedAdapter", "position: $position")
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
                holder.bindItems(section, this, position)
            }
            is FeedAdapterLargeRestaurant -> {
                holder as FeedAdapterLargeRestaurantViewHolder
                holder.bindItems(section, this, position)
            }
            is FeedAdapterNoChef -> {
                holder as FeedAdapterNoChefViewHolder
                holder.bindItems(section)
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
        campaign?.let {
            listener.onShareBannerClick(campaign)
        }
    }

    override fun onRestaurantClick(cook: Cook) {
        listener.onRestaurantClick(cook)
    }

    override fun onPageClick(position: Int) {
        Log.d("wowFeedAdapter", "position:")
        val section = getItem(position)
        when (section) {
            is FeedAdapterRestaurant -> {
                listener.onRestaurantClick(section.restaurantSection.getCook())
            }
            is FeedAdapterLargeRestaurant  -> {
                listener.onRestaurantClick(section.restaurantSection.getCook())
            }
            else -> {}
        }
    }
}

