package com.bupp.wood_spoon_eaters.features.main.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.*
import com.google.android.flexbox.JustifyContent

import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap

import com.google.android.flexbox.FlexboxLayoutManager


class SearchAdapter(val context: Context, val listener: OrdersHistoryAdapterListener) :
    ListAdapter<SearchBaseItem, RecyclerView.ViewHolder>(DiffCallback()), SearchTagsAdapter.SearchTagsAdapterListener {

    interface OrdersHistoryAdapterListener {
        fun onTagClick(tag: String)
    }

    override fun getItemViewType(position: Int): Int = getItem(position).type.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SearchViewType.SEARCH_TAGS.ordinal -> {
                val binding = SearchItemTagsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SearchTagsViewHolder(binding)
            }
//            SearchViewType.RECENT_ORDER.ordinal -> {
//                val binding = OrdersHistoryActiveOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//                ActiveOrderItemViewHolder(binding)
//            }
            SearchViewType.RESTAURANT.ordinal -> {
                val binding = OrderHistoryItemSkeletonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SkeletonItemViewHolder(binding)
            }
            else -> { //SearchViewType.TITLE
                val binding = OrdersHistoryTitleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SearchTitleViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (item) {
            is SearchAdapterTag -> {
                holder as SearchTagsViewHolder
                val adapter = SearchTagsAdapter(this)
                holder.bindItem(item, adapter)
            }
//            is OrderAdapterItemSkeleton -> {
//                holder as SkeletonItemViewHolder
//            }
//            is OrderAdapterItemOrder -> {
//                holder as OrderItemViewHolder
//                holder.bindItem(item)
//            }
            is SearchAdapterTitle -> {
                holder as SearchTitleViewHolder
                holder.bindItem(item)

            }
        }
    }

    inner class SkeletonItemViewHolder(val binding: OrderHistoryItemSkeletonBinding) : RecyclerView.ViewHolder(binding.root)



    inner class SearchTagsViewHolder(val binding: SearchItemTagsBinding) : RecyclerView.ViewHolder(binding.root) {
        private val tagsList = binding.searchTagsItemList
        fun bindItem(tags: SearchAdapterTag, adapter: SearchTagsAdapter) {
            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.FLEX_START
            layoutManager.flexWrap = FlexWrap.WRAP
            tagsList.layoutManager = layoutManager
            tagsList.adapter = adapter
            adapter.submitList(tags.tags)
        }
    }

    inner class SearchTitleViewHolder(val binding: OrdersHistoryTitleItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val title = binding.orderHistoryTitle
        fun bindItem(titleItem: SearchAdapterTitle) {
            title.text = titleItem.title
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SearchBaseItem>() {

        override fun areItemsTheSame(oldItem: SearchBaseItem, newItem: SearchBaseItem): Boolean {
            var isSame = oldItem == newItem
//            if (oldItem is SearchBaseItem && newItem is SearchBaseItem) {
//                isSame = oldItem.order.extendedStatus?.subtitle == newItem.order.extendedStatus?.subtitle
//                Log.d("wowStatus", "isSame: $isSame ${oldItem.order.id}")
//            }
//            Log.d("wowStatus", "adapter - areItemsTheSame $isSame")
            return isSame
        }

        override fun areContentsTheSame(oldItem: SearchBaseItem, newItem: SearchBaseItem): Boolean {
            var isSame = oldItem == newItem
//            if (oldItem is SearchBaseItem && newItem is SearchBaseItem) {
//                isSame = oldItem.order.extendedStatus?.subtitle == newItem.order.extendedStatus?.subtitle
//                Log.d("wowStatus", "isSame: $isSame ${oldItem.order.id}")
//            }
//            Log.d("wowStatus", "adapter - areContentsTheSame $isSame")
            return isSame

        }
    }

    override fun onTagClick(tag: String) {
        listener.onTagClick(tag)
    }

}