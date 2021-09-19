package com.bupp.wood_spoon_eaters.features.main.feed.adapters.view_holders

import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterRestaurantItemSkeletonBinding
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterTitleItemBinding
import com.bupp.wood_spoon_eaters.model.FeedAdapterTitle

class FeedAdapterTitleViewHolder(val binding: FeedAdapterTitleItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems(item: FeedAdapterTitle) {
        binding.feedAdapterTitle.text = item.title
    }
}

class FeedAdapterSkeletonViewHolder(val binding: FeedAdapterRestaurantItemSkeletonBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems() {
    }
}