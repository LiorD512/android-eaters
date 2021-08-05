package com.bupp.wood_spoon_eaters.features.main.feed.adapters.view_holders

import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterNoChefItemBinding
import com.bupp.wood_spoon_eaters.model.*


class FeedAdapterNoChefViewHolder(val binding: FeedAdapterNoChefItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindItems(noChefSection: FeedAdapterNoChef) {
            with(binding) {
                noChefSection.noChefSection.let{
                    binding.feedNoChefTitle.text = it.title
                    binding.feedNoChefSubtitle.text = it.subtitle
                }
            }
    }
}
