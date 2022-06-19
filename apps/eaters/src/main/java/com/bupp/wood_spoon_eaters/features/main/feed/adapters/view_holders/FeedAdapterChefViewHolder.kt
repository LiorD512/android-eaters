package com.bupp.wood_spoon_eaters.features.main.feed.adapters.view_holders

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterChefSectionBinding
import com.bupp.wood_spoon_eaters.model.FeedAdapterChefSection

class FeedAdapterChefViewHolder(
    val binding: FeedAdapterChefSectionBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bindItems(
        item: FeedAdapterChefSection,
        listener: FeedChefSectionAdapter.FeedChefSectionListener
    ) {
        val adapter = FeedChefSectionAdapter(listener)
        binding.rvFeedChefSection.adapter = adapter
        binding.rvFeedChefSection.layoutManager = object : LinearLayoutManager(
            binding.root.context,
            RecyclerView.HORIZONTAL, false
        ) {
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                if (item.chefSection.size > 1) {
                    lp?.width = (width / 100) * 75
                }
                return true
            }
        }
        adapter.submitList(item.chefSection)
    }
}