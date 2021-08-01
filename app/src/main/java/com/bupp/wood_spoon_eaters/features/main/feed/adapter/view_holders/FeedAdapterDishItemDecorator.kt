package com.bupp.wood_spoon_eaters.features.main.feed.adapter.view_holders

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.BlueBtnCornered
import com.bupp.wood_spoon_eaters.utils.Utils
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.swipeableAdapter.SwipeableAdapter
import java.util.*
import kotlin.math.abs

class FeedAdapterDishItemDecorator: RecyclerView.ItemDecoration() {


    var lastDx = 0f
    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        parent.adapter?.let { adapter ->
            val childCount = Objects.requireNonNull(adapter).itemCount
            for (i in 0 until childCount) {
                val child = parent.getChildAt(i)
                child?.let {
                    val dx = child.x
                    Log.d(TAG, "dx: $dx")
                    child.apply {
                        val titleView: TextView
                        var priceView: TextView? = null
                        var seeMoreView: BlueBtnCornered? = null
                        if (child.id == R.id.feedRestaurantDishItem) {
                            //this is a dish item
                            titleView = child.findViewById(R.id.feedRestaurantItemName)
                            priceView = child.findViewById(R.id.feedRestaurantItemPrice)
                        } else {
                            //this is a see more item
                            titleView = child.findViewById(R.id.feedRestaurantSeeMoreItemQuantityLeft)
                            seeMoreView = child.findViewById(R.id.feedRestaurantSeeMoreItemSeeMore)
                        }

                        priceView?.alpha = 1 - lerp(abs(dx), 0f, 1080f, 0f, 1f)
                        titleView.alpha = 1 - lerp(abs(dx), 0f, 1080f, 0f, 1f)
                        titleView.translationX = 1.0f + (abs(dx) / 3)
                        seeMoreView?.alpha = 1.0f - lerp(abs(dx), 0f, 1080f, 0f, 1f)
                    }
                }
            }
        }
        super.onDraw(canvas, parent, state)
    }


    fun convert(number: Int, original: IntRange, target: IntRange): Int {
        val ratio = number.toFloat() / (original.last - original.first)
        return (ratio * (target.last - target.first)).toInt()
    }

    fun lerp(value: Float, min: Float, max: Float, min2: Float, max2: Float): Float {
        val percentage = (value - min) / (max - min)
        val result = (percentage * (max2 - min2)) + min2
        Log.d(TAG, "lerp: ${result}")
        return result
    }


    companion object {
        const val TAG = "wowFeedDishItemDeco"
        const val ALPHA_THRESHOLD = 255
    }

}