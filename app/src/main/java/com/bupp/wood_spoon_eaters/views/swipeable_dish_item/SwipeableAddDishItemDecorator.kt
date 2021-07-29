package com.bupp.wood_spoon_eaters.views.swipeable_dish_item

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.utils.Utils
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.swipeableAdapter.SwipeableAdapter
import java.util.*

class SwipeableAddDishItemDecorator(
    context: Context,
    private val defaultShape: Drawable?,
    private val selectedShape: Drawable?,
    private val decoratedViewType: Int? = null
) :
    RecyclerView.ItemDecoration() {

    private val addIcon = ContextCompat.getDrawable(context, R.drawable.ic_check_add)
    private val intrinsicWidth = addIcon?.intrinsicWidth
    private val intrinsicHeight = addIcon?.intrinsicHeight

    init {
        addIcon?.alpha = 0
    }

    @SuppressLint("LogNotTimber")
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        var right = 10
        val verticalPadding = Utils.toPx(3)
        defaultShape?.let {
            selectedShape?.let {
                parent.adapter?.let {
                    val childCount = Objects.requireNonNull(it).itemCount
                    for (i in 0 until childCount) {
                        if (decoratedViewType == null || it.getItemViewType(i) == decoratedViewType) {
                            val child = parent.getChildAt(i)
                            child?.let {
                                val top = child.top + verticalPadding
                                val bottom = child.bottom - verticalPadding

                                val adapterPosition = parent.getChildAdapterPosition(child)
                                val isInCart = (parent.adapter as SwipeableAdapter<*>).isInCart(adapterPosition)
                                if (isInCart) {
                                    right = 15
                                    selectedShape.setBounds(left, top, right, bottom)
                                    selectedShape.draw(canvas)
                                } else {
                                    right = 10
                                    val defaultAlpha = calcDefaultAlpha(child.translationX)
                                    defaultShape.alpha = defaultAlpha
                                    defaultShape.setBounds(left, top, right, bottom)
                                    defaultShape.draw(canvas)
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    var lastDx = 0f
    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = 0
        var right = 10
        val verticalPadding = Utils.toPx(3)
        selectedShape?.let {
            parent.adapter?.let { adapter ->
                val childCount = Objects.requireNonNull(adapter).itemCount
                for (i in 0 until childCount) {
                    if (decoratedViewType == null || adapter.getItemViewType(i) == decoratedViewType) {
                        val child = parent.getChildAt(i)
                        child?.let {
                            val top = child.top + verticalPadding
                            val bottom = child.bottom - verticalPadding

                            if (child.translationX == 360f) {
                                child.translationX = 0f
                            }

                            val selectedAlpha = calcAlpha(child.translationX)

                            val adapterPosition = parent.getChildAdapterPosition(child)

                            val selectedCopy = selectedShape.constantState?.newDrawable()?.mutate()
                            selectedCopy?.alpha = selectedAlpha

                            val isInCart = (parent.adapter as SwipeableAdapter<*>).isInCart(adapterPosition)
                            if (isInCart) {
                                right = 20
                                selectedCopy?.alpha = 255
                            }

                            if (child.translationX > 0) {
                                right = child.translationX.toInt()
                            }

                            if (lastDx != child.translationX) {
                                lastDx = child.translationX
                            }

                            selectedCopy?.setBounds(left, top, right, bottom)
                            selectedCopy?.draw(canvas)

                            val itemHeight = bottom - top
                            val addIconTop = top + (itemHeight - intrinsicHeight!!) / 2
                            val addIconMargin = (child.translationX.toInt() / 3)
                            val addIconLeft = right - addIconMargin - intrinsicWidth!!
                            val addIconRight = right - addIconMargin
                            val addIconBottom = addIconTop + intrinsicHeight
                            calcIconAlpha(child.translationX - 30)
                            // Draw the delete icon
//                            addIcon?.alpha = selectedAlpha
                            addIcon?.setBounds(addIconLeft, addIconTop, addIconRight, addIconBottom)
                            addIcon?.draw(canvas)
                        }
                    }
                }
            }
        }


        super.onDraw(canvas, parent, state)
    }

    private fun calcAlpha(translationX: Float): Int { //0..225
        var result = ALPHA_THRESHOLD.toFloat()
        if (translationX < result) {
            result = translationX
        }
        return result.toInt()
    }

    private fun calcDefaultAlpha(translationX: Float): Int { //225..0
        var result = 0f
        val current = ALPHA_THRESHOLD - translationX
        if (current > result) {
            result = current
        }
        return result.toInt()
    }

    private fun calcIconAlpha(translationX: Float){ //225..0
        if (translationX > 0) {
            if (translationX > intrinsicWidth!!) {
                addIcon?.alpha = ((translationX - intrinsicWidth) * 3).toInt()
            }
        }
    }


    companion object {
        const val TAG = "wowItemDecoratorAdd"
        const val ALPHA_THRESHOLD = 250
    }

}