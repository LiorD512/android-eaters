package com.bupp.wood_spoon_eaters.features.main.feed.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.databinding.ShareBannerBinding
import com.bupp.wood_spoon_eaters.model.Campaign


class FeedCouponSectionPagerAdapter(val listener: FeedCouponSectionListener) :
    ListAdapter<Campaign, RecyclerView.ViewHolder>(DiffCallback()) {

    interface FeedCouponSectionListener{
        fun onShareBannerClick(campaign: Campaign?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ShareBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedCouponViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        val itemViewHolder = holder as FeedCouponViewHolder
        itemViewHolder.bindItem(holder.itemView.context, item, listener)
    }

    class FeedCouponViewHolder(binding: ShareBannerBinding) : RecyclerView.ViewHolder(binding.root) {
        private val layout: CardView = binding.customBannerLayout
        private val thumbnail: ImageView = binding.customBanner
        private val title: TextView = binding.customBannerTitle
        private val subTitle: TextView = binding.customBannerSubTitle

        fun bindItem(context: Context, coupon: Campaign, listener: FeedCouponSectionListener) {
            Glide.with(context).load(coupon.photoLarge).into(thumbnail)
            title.text = coupon.header
            subTitle.text = coupon.bodyText1

            layout.visibility = View.VISIBLE

            layout.setOnClickListener {
                listener.onShareBannerClick(coupon)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Campaign>() {

        override fun areItemsTheSame(oldItem: Campaign, newItem: Campaign): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Campaign, newItem: Campaign): Boolean {
            return oldItem == newItem
        }
    }
}