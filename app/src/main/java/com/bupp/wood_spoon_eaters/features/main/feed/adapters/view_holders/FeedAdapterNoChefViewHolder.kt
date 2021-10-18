package com.bupp.wood_spoon_eaters.features.main.feed.adapters.view_holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterEmptyFeedItemBinding
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterEmptySectionItemBinding
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterNoNetworkItemBinding
import com.bupp.wood_spoon_eaters.features.main.feed.adapters.FeedMainAdapter
import com.bupp.wood_spoon_eaters.model.*


class FeedAdapterEmptyFeedViewHolder(val binding: FeedAdapterEmptyFeedItemBinding, val listener: FeedMainAdapter.FeedMainAdapterListener) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems(noChefSection: FeedAdapterEmptyFeed) {
            with(binding) {
                noChefSection.emptyFeedSection.let{
                    binding.emptyFeedTitle.text = it.title
                    binding.emptyFeedSubtitle.text = it.subtitle

                    if(noChefSection.shouldShowBtn){
                        binding.emptyFeedChangeAddress.visibility = View.VISIBLE
                        binding.emptyFeedChangeAddress.setOnClickListener {
                            listener.onChangeAddressClick()
                        }
                    }else{
                        binding.emptyFeedChangeAddress.visibility = View.INVISIBLE
                    }
                }
            }
    }
}

class FeedAdapterEmptySectionViewHolder(val binding: FeedAdapterEmptySectionItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems(noChefSection: FeedAdapterEmptySection) {
            with(binding) {
                noChefSection.emptySection.let{
                    binding.feedNoChefTitle.text = it.title
                    binding.feedNoChefSubtitle.text = it.subtitle
                }
            }
    }
}

class FeedAdapterNoNetworkSectionViewHolder(val binding: FeedAdapterNoNetworkItemBinding, val listener: FeedMainAdapter.FeedMainAdapterListener) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems() {
            with(binding) {
                noNetworkSectionBtn.setOnClickListener {
                    listener.onRefreshFeedClick()
                }
            }
    }
}
