package com.bupp.wood_spoon_eaters.features.main.feed.adapters.view_holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterHeroSectionBinding
import com.bupp.wood_spoon_eaters.model.FeedAdapterHero

class FeedAdapterHeroViewHolder(
    val binding: FeedAdapterHeroSectionBinding
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var pagerCallback: ViewPager2.OnPageChangeCallback

    fun bindItems(
        item: FeedAdapterHero,
        listener: FeedHeroSectionPagerAdapter.FeedHeroSectionListener
    ) {
        val adapter = FeedHeroSectionPagerAdapter(listener)
        val endlessList =
            listOf(item.heroList.last()) + item.heroList + listOf(item.heroList.first())
        adapter.submitList(endlessList)

        binding.apply {
            feedHeroSectionViewPager.adapter = adapter
            feedHeroSectionViewPager.setCurrentItem(1, false)

            pagerCallback = object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    when (state) {
                        ViewPager2.SCROLL_STATE_IDLE -> {
                            if (state == ViewPager2.SCROLL_STATE_IDLE) {
                                when (binding.feedHeroSectionViewPager.currentItem) {
                                    endlessList.lastIndex ->
                                        binding.feedHeroSectionViewPager.setCurrentItem(1, false)
                                    0 ->
                                        binding.feedHeroSectionViewPager.setCurrentItem(
                                            endlessList.size - 2,
                                            false
                                        )
                                }
                            }
                        }
                    }
                }
            }

            feedHeroSectionViewPager.registerOnPageChangeCallback(pagerCallback)
        }

        if (item.heroList.size > 1) {
            binding.feedHeroSectionIndicator.visibility = View.VISIBLE
            binding.feedHeroSectionIndicator.setViewPager(binding.feedHeroSectionViewPager)
        } else {
            binding.feedHeroSectionIndicator.visibility = View.GONE
        }
    }
}