package com.bupp.wood_spoon_eaters.features.main.feed.adapters.view_holders

import android.view.View
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bupp.wood_spoon_eaters.databinding.FeedAdapterHeroSectionBinding
import com.bupp.wood_spoon_eaters.model.FeedAdapterHero
import com.bupp.wood_spoon_eaters.model.FeedHeroItemSection
import kotlinx.coroutines.*

const val AUTO_SCROLL_DELAY: Long = 4000

class FeedAdapterHeroViewHolder(
    val binding: FeedAdapterHeroSectionBinding
) : RecyclerView.ViewHolder(binding.root), LifecycleObserver {

    private lateinit var pagerCallback: ViewPager2.OnPageChangeCallback
    private lateinit var scope: CoroutineScope
    private var job : Job = Job()

    fun bindItems(
        item: FeedAdapterHero,
        listener: FeedHeroSectionPagerAdapter.FeedHeroSectionListener
    ) {
        scope = CoroutineScope(Dispatchers.Default)

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
                            loopScrolling(endlessList)
                        }
                        ViewPager2.SCROLL_STATE_DRAGGING -> {
                            job.cancel()
                        }
                        ViewPager2.SCROLL_STATE_SETTLING -> {
                            restartAutoScroll()
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

    fun restartAutoScroll() {
        job.cancel()
        job = Job()
        startAutoScroll(AUTO_SCROLL_DELAY)
    }

    fun onViewAttachedToWindow() {
        startAutoScroll(AUTO_SCROLL_DELAY)
    }

    fun onViewDetachedFromWindow() {
        job.cancel()
    }

    private fun startAutoScroll(delay: Long) {
        scope.launch(job) {
            repeat(Int.MAX_VALUE) {
                delay(delay)

                withContext(Dispatchers.Main) {
                    binding.feedHeroSectionViewPager.setCurrentItem(
                        binding.feedHeroSectionViewPager.currentItem.inc(),
                        true
                    )
                }
            }
        }
    }

    private fun loopScrolling(endlessList: List<FeedHeroItemSection>) {
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