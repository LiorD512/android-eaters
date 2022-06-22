package com.bupp.wood_spoon_eaters.features.main.feed.adapters.view_holders

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterDishSectionBinding
import com.bupp.wood_spoon_eaters.features.main.feed.adapters.FeedDishSectionAdapter
import com.bupp.wood_spoon_eaters.model.FeedAdapterDishSection

class FeedAdapterDishViewHolder(
    val binding: FeedAdapterDishSectionBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bindItems(
        item: FeedAdapterDishSection,
        listener: FeedDishSectionAdapter.FeedDishSectionListener
    ) {
        val adapter = FeedDishSectionAdapter(listener)
        binding.rvFeedDishSection.adapter = adapter
        binding.rvFeedDishSection.layoutManager = object : LinearLayoutManager(
            binding.root.context,
            RecyclerView.HORIZONTAL, false
        ) {
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                if (item.dishSection.size > 1) {
                    lp?.width = width / 100  * 45
                }
                return true
            }
        }
        adapter.submitList(item.dishSection)
    }
}