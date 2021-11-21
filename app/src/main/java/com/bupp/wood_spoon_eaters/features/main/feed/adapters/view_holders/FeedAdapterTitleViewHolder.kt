package com.bupp.wood_spoon_eaters.features.main.feed.adapters.view_holders

import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterRestaurantItemSkeletonBinding
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterSearchTitleItemBinding
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterTitleItemBinding
import com.bupp.wood_spoon_eaters.databinding.SearchItemSkeletonBinding
import com.bupp.wood_spoon_eaters.model.FeedAdapterSearchTitle
import com.bupp.wood_spoon_eaters.model.FeedAdapterTitle

class FeedAdapterSearchTitleViewHolder(val binding: FeedAdapterSearchTitleItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems(item: FeedAdapterSearchTitle) {
        binding.feedAdapterSearchTitle.text = item.title
    }
}

class FeedAdapterTitleViewHolder(val binding: FeedAdapterTitleItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems(item: FeedAdapterTitle) {
        binding.feedAdapterTitle.text = item.title
    }
}

class FeedAdapterSkeletonViewHolder(val binding: FeedAdapterRestaurantItemSkeletonBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems() {
    }
}

class FeedAdapterSkeletonSearchViewHolder(val binding: SearchItemSkeletonBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems() {
    }
}