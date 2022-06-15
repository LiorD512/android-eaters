package com.bupp.wood_spoon_eaters.features.main.feed.adapters.view_holders

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.databinding.PagerItemHeroBinding
import com.bupp.wood_spoon_eaters.model.FeedHeroItemSection

class FeedHeroSectionPagerAdapter(
    val listener: FeedHeroSectionListener
) : ListAdapter<FeedHeroItemSection, RecyclerView.ViewHolder>(DiffCallback()) {

    interface FeedHeroSectionListener {
        fun onHeroBannerClick(hero: FeedHeroItemSection?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = PagerItemHeroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedCouponViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        val itemViewHolder = holder as FeedCouponViewHolder
        itemViewHolder.bindItem(holder.itemView.context, item, listener)
    }

    class FeedCouponViewHolder(
        val binding: PagerItemHeroBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val thumbnail: ImageView = binding.customBanner
        private val title: TextView = binding.customBannerTitle
        private val subTitle: TextView = binding.customBannerSubTitle

        fun bindItem(
            context: Context,
            hero: FeedHeroItemSection,
            listener: FeedHeroSectionListener
        ) {
            Glide
                .with(context)
                .load(hero.image?.url)
                .into(thumbnail)
            title.text = hero.title
            subTitle.text = hero.text
            binding.root.setOnClickListener {
                listener.onHeroBannerClick(hero)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FeedHeroItemSection>() {

        override fun areItemsTheSame(
            oldItem: FeedHeroItemSection,
            newItem: FeedHeroItemSection
        ) = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: FeedHeroItemSection,
            newItem: FeedHeroItemSection
        ) = oldItem == newItem
    }
}
