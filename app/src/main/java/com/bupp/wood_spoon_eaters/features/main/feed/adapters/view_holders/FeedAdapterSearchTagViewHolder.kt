package com.bupp.wood_spoon_eaters.features.main.feed.adapters.view_holders

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.SearchItemEmptyBinding
import com.bupp.wood_spoon_eaters.databinding.SearchItemEmptyTagsBinding
import com.bupp.wood_spoon_eaters.databinding.SearchItemTagsBinding
import com.bupp.wood_spoon_eaters.features.main.search.SearchAdapterTitle
import com.bupp.wood_spoon_eaters.features.main.search.SearchTagsAdapter
import com.bupp.wood_spoon_eaters.model.FeedAdapterSearchTag
import com.bupp.wood_spoon_eaters.model.FeedSearchTagsEmptySection
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class FeedAdapterSearchTagViewHolder(val context: Context, val binding: SearchItemTagsBinding) : RecyclerView.ViewHolder(binding.root) {
        private val tagsList = binding.searchTagsItemList
        fun bindItem(tags: FeedAdapterSearchTag, adapter: SearchTagsAdapter) {
            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.FLEX_START
            layoutManager.flexWrap = FlexWrap.WRAP
            tagsList.layoutManager = layoutManager
            tagsList.adapter = adapter
            adapter.submitList(tags.tags)
        }
    }

class FeedAdapterEmptySearchViewHolder(val binding: SearchItemEmptyBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItem() {
    }
}

class FeedAdapterEmptySearchTagsViewHolder(val binding: SearchItemEmptyTagsBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItem() {
    }
}