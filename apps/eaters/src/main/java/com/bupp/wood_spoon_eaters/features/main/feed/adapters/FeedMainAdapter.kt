package com.bupp.wood_spoon_eaters.features.main.feed.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.*
import com.bupp.wood_spoon_eaters.features.main.feed.adapters.decorators.FeedAdapterDishItemDecorator
import com.bupp.wood_spoon_eaters.features.main.feed.adapters.view_holders.*
import com.bupp.wood_spoon_eaters.features.main.search.SearchTagsAdapter
import com.bupp.wood_spoon_eaters.model.RestaurantInitParams
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.views.abs.RecyclerHorizontalIndicatorDecoration
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper

class FeedMainAdapter(
    val listener: FeedMainAdapterListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    FeedCouponSectionPagerAdapter.FeedCouponSectionListener,
    FeedAdapterRestaurantViewHolder.FeedAdapterRestaurantViewHolderListener,
    FeedAdapterLargeRestaurantViewHolder.FeedAdapterRestaurantViewHolderListener,
    FeedRestaurantDishPagerAdapter.FeedRestaurantDishPagerAdapterListener,
    FeedHeroSectionPagerAdapter.FeedHeroSectionListener,
    SearchTagsAdapter.SearchTagsAdapterListener {

    private val dataList: MutableList<FeedAdapterItem> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setDataList(dataList: List<FeedAdapterItem>) {
        this.dataList.clear()
        this.dataList.addAll(dataList)
        notifyDataSetChanged()
    }

    interface FeedMainAdapterListener {
        fun onShareBannerClick(campaign: Campaign)
        fun onHeroBannerClick(hero: FeedHeroItemSection)
        fun onRestaurantClick(restaurantInitParams: RestaurantInitParams)
        fun onChangeAddressClick()
        fun onDishSwiped()
        fun onRefreshFeedClick()
        fun onComingSoonBtnClick(comingSoonData: FeedComingSoonSection)

        //Search
        fun onTagClick(tag: String) {}
    }

    override fun getItemViewType(position: Int): Int = dataList[position].type!!.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            FeedAdapterViewType.SEARCH_TITLE.ordinal -> {
                val binding = FeedAdapterSearchTitleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedAdapterSearchTitleViewHolder(binding)
            }
            FeedAdapterViewType.TITLE.ordinal -> {
                val binding = FeedAdapterTitleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedAdapterTitleViewHolder(binding)
            }
            FeedAdapterViewType.COUPONS.ordinal -> {
                val binding = FeedAdapterCampaignSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedAdapterCampaignViewHolder(binding)
            }
            FeedAdapterViewType.HERO.ordinal -> {
                val binding = FeedAdapterHeroSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedAdapterHeroViewHolder(binding)
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
            FeedAdapterViewType.EMPTY_FEED.ordinal -> {
                val binding = FeedAdapterEmptyFeedItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedAdapterEmptyFeedViewHolder(binding, listener)
            }
            FeedAdapterViewType.EMPTY_SECTION.ordinal -> {
                val binding = FeedAdapterEmptySectionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedAdapterEmptySectionViewHolder(binding)
            }
            FeedAdapterViewType.COMING_SOON.ordinal -> {
                val binding = FeedAdapterComingSoonItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedAdapterComingSoonViewHolder(binding, listener)
            }
            FeedAdapterViewType.EMPTY_SEARCH.ordinal -> {
                val binding = SearchItemEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedAdapterEmptySearchViewHolder(binding)
            }
            FeedAdapterViewType.EMPTY_SEARCH_TAGS.ordinal -> {
                val binding = SearchItemEmptyTagsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedAdapterEmptySearchTagsViewHolder(binding)
            }
            FeedAdapterViewType.NO_NETWORK_SECTION.ordinal -> {
                val binding = FeedAdapterNoNetworkItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedAdapterNoNetworkSectionViewHolder(binding, listener)
            }
            FeedAdapterViewType.SEARCH_TAGS.ordinal -> {
                val binding = SearchItemTagsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedAdapterSearchTagViewHolder(parent.context, binding)
            }
            FeedAdapterViewType.SKELETON_SEARCH.ordinal -> {
                val binding = SearchItemSkeletonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedAdapterSkeletonSearchViewHolder(binding)
            }
            else -> {
                val binding = FeedAdapterRestaurantItemSkeletonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FeedAdapterSkeletonViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("wowProcessFeedData", "onBindViewHolder - position: $position")
        val section = dataList[position]
        when (section) {
            is FeedAdapterTitle -> {
                holder as FeedAdapterTitleViewHolder
                holder.bindItems(section)
            }
            is FeedAdapterSearchTitle -> {
                holder as FeedAdapterSearchTitleViewHolder
                holder.bindItems(section)
            }
            is FeedAdapterCoupons -> {
                holder as FeedAdapterCampaignViewHolder
                holder.bindItems(section, this)
            }
            is FeedAdapterHero -> {
                holder as FeedAdapterHeroViewHolder
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
            is FeedAdapterEmptyFeed -> {
                holder as FeedAdapterEmptyFeedViewHolder
                holder.bindItems(section)
            }
            is FeedAdapterEmptySection -> {
                holder as FeedAdapterEmptySectionViewHolder
                holder.bindItems(section)
            }
            is FeedAdapterComingSoonSection -> {
                holder as FeedAdapterComingSoonViewHolder
                holder.bindItems(section)
            }
            is FeedAdapterNoNetworkSection -> {
                holder as FeedAdapterNoNetworkSectionViewHolder
                holder.bindItems()
            }
            is FeedAdapterSkeleton -> {
                holder as FeedAdapterSkeletonViewHolder
                holder.bindItems()
            }
            is FeedAdapterSearchSkeleton -> {
                holder as FeedAdapterSkeletonSearchViewHolder
                holder.bindItems()
            }
            is FeedAdapterSearchTag -> {
                holder as FeedAdapterSearchTagViewHolder
                val adapter = SearchTagsAdapter(this)
                holder.bindItem(section, adapter)
            }
            is FeedAdapterEmptySearch -> {
                holder as FeedAdapterEmptySearchViewHolder
            }
            is FeedAdapterEmptySearchTags -> {
                holder as FeedAdapterEmptySearchTagsViewHolder
            }
            else -> {}
        }
    }

    override fun onShareBannerClick(campaign: Campaign?) {
        campaign?.let {
            listener.onShareBannerClick(campaign)
        }
    }

    override fun onRestaurantClick(restaurant: FeedRestaurantSection) {
        listener.onRestaurantClick(restaurant.toRestaurantInitParams())
    }

    override fun onDishSwiped() {
        listener.onDishSwiped()
    }

    override fun onPageClick(itemLocalId: Long?, position: Int) {
        Log.d("wowProcessFeedData", "onPageClick position: $position")
        dataList.forEachIndexed { index, feedAdapterItem ->
            if (feedAdapterItem.id == itemLocalId) {
                val section = dataList[index]
                when (section) {
                    is FeedAdapterRestaurant -> {
                        val sectionTitle = section.sectionTitle
                        val sectionOrder = section.sectionOrder
                        val restaurantOrderInSection = section.restaurantOrderInSection
                        val dishIndexInRestaurant = position + 1
                        listener.onRestaurantClick(
                            section.restaurantSection.toRestaurantInitParams(
                                sectionTitle,
                                sectionOrder,
                                restaurantOrderInSection,
                                dishIndexInRestaurant
                            )
                        )
                        return@forEachIndexed
                    }
                    is FeedAdapterLargeRestaurant -> {
                        val sectionTitle = section.sectionTitle
                        val sectionOrder = section.sectionOrder
                        val restaurantOrderInSection = section.restaurantOrderInSection
                        val dishIndexInRestaurant = position + 1
                        listener.onRestaurantClick(
                            section.restaurantSection.toRestaurantInitParams(
                                sectionTitle,
                                sectionOrder,
                                restaurantOrderInSection,
                                dishIndexInRestaurant
                            )
                        )
                        return@forEachIndexed
                    }
                    else -> {}
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onTagClick(tag: String) {
        listener.onTagClick(tag)
    }

    override fun onHeroBannerClick(hero: FeedHeroItemSection?) {
        hero?.url?.contains("woodspoon.app.link/referral_purchase")?.let {
            if (it) {
                hero.let { heroItem ->
                    listener.onHeroBannerClick(heroItem)
                }
            }
        }
    }
}

