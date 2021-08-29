package com.bupp.wood_spoon_eaters.views.swipeable_dish_item

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.utils.Utils
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.swipeableAdapter.SwipeableAdapter
import java.util.*
import kotlin.math.abs

class SwipeableRemoveDishItemDecorator(context: Context, private val removeShape: Drawable?) :
    RecyclerView.ItemDecoration() {

    private val removeIcon = ContextCompat.getDrawable(context, R.drawable.ic_trash)
    private val intrinsicWidth = removeIcon?.intrinsicWidth
    private val intrinsicHeight = removeIcon?.intrinsicHeight

    init {
        removeIcon?.alpha = 0
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        var left = parent.width
        var right = 0
        val verticalPadding = Utils.toPx(3)
        removeShape?.let {
            parent.adapter?.let {
                val childCount = Objects.requireNonNull(it).itemCount
                for (i in 0 until childCount) {
                    val child = parent.getChildAt(i)
                    child?.let {
                        val top = child.top + verticalPadding
                        val bottom = child.bottom - verticalPadding

                        val adapterPosition = parent.getChildAdapterPosition(child)
                        if (adapterPosition >= 0) {
                            val isSwipeable = (parent.adapter as SwipeableAdapter<*>).isSwipeable(adapterPosition)
                            if (isSwipeable) {
                                if (child.translationX == -360f) {
                                    child.translationX = 0f
                                }

                                right = child.width
                                if (child.translationX < 0) {
                                    left = right - abs(child.translationX.toInt())
                                }
                                removeShape.setBounds(left, top, right, bottom)
                                removeShape.draw(canvas)

                                val itemHeight = bottom - top
                                val removeIconTop = top + (itemHeight - intrinsicHeight!!) / 2
                                val removeIconMargin = abs((child.translationX.toInt()) / 3)
                                val removeIconLeft = left + removeIconMargin
                                val removeIconRight = left + removeIconMargin + intrinsicWidth!!
                                val removeIconBottom = removeIconTop + intrinsicHeight
                                calcIconAlpha(abs(child.translationX))
                                // Draw the delete icon
                                removeIcon?.setBounds(removeIconLeft, removeIconTop, removeIconRight, removeIconBottom)
                                removeIcon?.draw(canvas)
                            }
                        }
                    }
                }
            }
        }
        super.onDraw(canvas, parent, state)
    }

    private fun calcIconAlpha(translationX: Float) { //225..0
//        Log.d(TAG, translationX.toString())
        if (translationX > 0) {
            removeIcon?.alpha = Utils.lerp(abs(translationX), 0f, SWIPE_THRESHOLD, 0f, ALPHA_THRESHOLD).toInt()
        }
    }

    companion object {
        const val TAG = "wowItemDecoratorRemove"
        const val SWIPE_THRESHOLD = 300f
        const val ALPHA_THRESHOLD = 255f
    }

}