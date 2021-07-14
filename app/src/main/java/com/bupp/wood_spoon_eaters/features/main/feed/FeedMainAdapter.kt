package com.bupp.wood_spoon_eaters.features.main.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FeedCampaignSectionBinding
import com.bupp.wood_spoon_eaters.databinding.TrackOrderDetailsSectionBinding
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.OrderTrackDetails
import com.bupp.wood_spoon_eaters.model.FeedModelsViewType
import com.bupp.wood_spoon_eaters.model.FeedSectionCollectionItem
import mva2.adapter.ItemViewHolder

class FeedMainAdapter() :
    ListAdapter<FeedSectionCollectionItem, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun getItemViewType(position: Int): Int = getItem(position).type!!.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder  {
        return when (viewType) {
            FeedModelsViewType.COUPONS.ordinal -> {
                val binding = FeedCampaignSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return FeedCampaignSectionViewHolder(binding)
            }
            else -> { //FeedModelsViewType.RESTAURANT.ordinal
                RPViewHolderMainInfo(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.restaurant_item_main_info, parent, false)
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val section = getItem(position)
        when (section) {
            is RPSectionDeliveryTiming -> {
                holder as RPViewHolderDeliveryTiming
                holder.bind(section, listener)
            }
            is RPSectionMainInfo -> {
                holder as RPViewHolderMainInfo
                holder.bind(section, listener)
            }
            is RPSectionSearch -> {
            }
            is SPSectionDishes -> {
            }
        }
    }

    abstract class BaseItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(
            section: RestaurantPageSections,
            listener: RestaurantPageMainAdapterListener
        )
    }

    private class DiffCallback : DiffUtil.ItemCallback<FeedSectionCollectionItem>() {
        override fun areItemsTheSame(
            oldItem: FeedSectionCollectionItem,
            newItem: FeedSectionCollectionItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: FeedSectionCollectionItem,
            newItem: FeedSectionCollectionItem
        ): Boolean {
            return oldItem.type == oldItem.type
        }
    }

    inner class FeedCampaignSectionViewHolder(val binding: FeedCampaignSectionBinding) : ItemViewHolder<OrderTrackDetails>(binding.root) {
        fun bindItems(item: OrderTrackDetails) {

        }
    }