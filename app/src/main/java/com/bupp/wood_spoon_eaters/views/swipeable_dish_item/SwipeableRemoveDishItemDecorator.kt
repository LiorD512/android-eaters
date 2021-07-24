package com.bupp.wood_spoon_eaters.views.swipeable_dish_item

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.utils.Utils
import java.util.*
import kotlin.math.abs

class SwipeableRemoveDishItemDecorator(context: Context, private val removeShape: Drawable?): RecyclerView.ItemDecoration() {

    private val removeIcon = ContextCompat.getDrawable(context, R.drawable.ic_remove)
    private val intrinsicWidth = removeIcon?.intrinsicWidth
    private val intrinsicHeight = removeIcon?.intrinsicHeight

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        var left = parent.width
        var right = 0
        val verticalPadding = Utils.toPx(3)
        removeShape?.let{
            parent.adapter?.let{
                val childCount = Objects.requireNonNull(it).itemCount
                for (i in 0 until childCount) {
                    val child = parent.getChildAt(i)
                    child?.let{
                        val top = child.top + verticalPadding
                        val bottom = child.bottom - verticalPadding

                        if(child.translationX == -360f){
                            child.translationX = 0f
                        }

                        right = child.width.toInt()
                        if(child.translationX < 0){
                            Log.d(TAG, "translationX: ${child.translationX}")
                            left = right - abs(child.translationX.toInt())
                        }

                        removeShape.setBounds(left, top, right, bottom)
                        removeShape.draw(canvas)

                        val itemHeight = bottom - top
                        val removeIconTop = top + (itemHeight - intrinsicHeight!!) / 2
                        val removeIconMargin = abs(child.translationX.toInt() / 3)
                        val removeIconLeft = right - removeIconMargin - intrinsicWidth!!
                        val removeIconRight = right - removeIconMargin
                        val removeIconBottom = removeIconTop + intrinsicHeight

                        // Draw the delete icon
                        removeIcon?.setBounds(removeIconLeft, removeIconTop, removeIconRight, removeIconBottom)
                        removeIcon?.draw(canvas)
                    }
                }
            }
        }
        super.onDraw(canvas, parent, state)
    }

    companion object{
        const val TAG = "wowItemDecorator"
        const val ALPHA_THRESHOLD = 250
    }

}