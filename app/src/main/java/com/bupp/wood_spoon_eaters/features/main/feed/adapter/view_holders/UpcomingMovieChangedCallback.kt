//package com.bupp.wood_spoon_eaters.features.main.feed.adapter.view_holders
//
//import androidx.recyclerview.widget.RecyclerView
//import androidx.viewpager2.widget.ViewPager2
//import kotlin.properties.Delegates
//
//class UpcomingMovieChangedCallback(private val binding: ActivityMainBinding, private val upcomingMoviesAdapter: GenericMoviesAdapter) : ViewPager2.OnPageChangeCallback() {
//
//    var goingLeft: Boolean by Delegates.notNull()
//    private var lastOffset = 0f
//    var progress: Float by Delegates.notNull()
//
//    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//        val realCurrentPosition: Int
//        val nextPosition: Int
//        val realOffset: Float
//        goingLeft = lastOffset > positionOffset
//        if (goingLeft) {
//            realCurrentPosition = position + 1
//            nextPosition = position
//            realOffset = 1 - positionOffset
//        } else {
//            nextPosition = position + 1
//            realCurrentPosition = position
//            realOffset = positionOffset
//        }
//
//        val currentCard = (binding.upcomingMoviesVP[0] as RecyclerView).layoutManager?.findViewByPosition(realCurrentPosition)
//        currentCard?.scaleX = (1 + 0.4 * (1 - realOffset)).toFloat()
//        currentCard?.scaleY = (1 + 0.4 * (1 - realOffset)).toFloat()
//        currentCard?.pivotY = 0f
//
//        val nextCard = (binding.upcomingMoviesVP[0] as RecyclerView).layoutManager?.findViewByPosition(nextPosition)
//        nextCard?.scaleX = (1 + 0.4 * realOffset).toFloat()
//        nextCard?.scaleY = (1 + 0.4 * realOffset).toFloat()
//        nextCard?.pivotY = 0f
//
//        lastOffset = positionOffset
//        progress = when (position) {
//            position -> positionOffset
//            position + 1 -> 1 - positionOffset
//            position - 1 -> 1 - positionOffset
//            else -> 0f
//        }
//    }
//}