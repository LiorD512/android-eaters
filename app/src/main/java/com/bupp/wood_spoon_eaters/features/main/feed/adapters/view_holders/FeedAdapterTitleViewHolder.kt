package com.bupp.wood_spoon_eaters.features.main.feed.adapters.view_holders

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_eaters.databinding.*
import com.bupp.wood_spoon_eaters.features.main.feed.adapters.FeedMainAdapter
import com.bupp.wood_spoon_eaters.model.FeedAdapterComingSoonSection
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

class FeedAdapterComingSoonViewHolder(val binding: FeedAdapterComingSoonItemBinding, val listener: FeedMainAdapter.FeedMainAdapterListener) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems(data: FeedAdapterComingSoonSection) {
        val item = data.comingSoonSection
        binding.comingSoonTitle.text = item.title
        binding.comingSoonSubtitle.text = item.subtitle
        Glide.with(binding.root)
            .load(item.cityPhoto?.url)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
            .into(binding.comingSoonImg)

        binding.comingSoonBtn.setOnClickListener {
            listener.onComingSoonBtnClick(item)
        }
    }
}