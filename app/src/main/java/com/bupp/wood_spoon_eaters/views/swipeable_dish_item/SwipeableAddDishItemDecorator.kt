package com.bupp.wood_spoon_eaters.views.swipeable_dish_item

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.upsale_bottom_sheet.UpSaleAdapter
import com.bupp.wood_spoon_eaters.utils.Utils
import java.util.*

class SwipeableAddDishItemDecorator(context: Context, private val defaultShape: Drawable?, private val selectedShape: Drawable?) :
    RecyclerView.ItemDecoration() {

    private val addIcon = ContextCompat.getDrawable(context, R.drawable.ic_check_add)
    private val intrinsicWidth = addIcon?.intrinsicWidth
    private val intrinsicHeight = addIcon?.intrinsicHeight

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
                        val child = parent.getChildAt(i)
                        child?.let {
                            val top = child.top + verticalPadding
                            val bottom = child.bottom - verticalPadding

                            val isInCart = (parent.adapter as UpSaleAdapter).isInCart(i)
                            if (isInCart) {
                                right = 15
                                selectedShape.setBounds(left, top, right, bottom)
                                selectedShape.draw(canvas)
                            } else {
                                right = 10
                                val defaultAlpha = (ALPHA_THRESHOLD - child.translationX).toInt()
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

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = 0
        var right = 10
        val verticalPadding = Utils.toPx(3)
        defaultShape?.let {
            selectedShape?.let {
                parent.adapter?.let {
                    val childCount = Objects.requireNonNull(it).itemCount
                    for (i in 0 until childCount) {
                        val child = parent.getChildAt(i)
                        child?.let {
                            val top = child.top + verticalPadding
                            val bottom = child.bottom - verticalPadding

                            val defaultAlpha = (ALPHA_THRESHOLD - child.translationX).toInt()
                            val selectedAlpha = (child.translationX - ALPHA_THRESHOLD).toInt()

                            val selectedCopy = selectedShape.constantState?.newDrawable()?.mutate()
                            val defaultCopy = defaultShape.constantState?.newDrawable()?.mutate()
                            defaultCopy?.alpha = defaultAlpha
                            selectedCopy?.alpha = selectedAlpha + 50

                            val isInCart = (parent.adapter as UpSaleAdapter).isInCart(i)
                            if (isInCart) {
                                right = 20
                                selectedCopy?.alpha = 255
                            }

                            if (child.translationX > 0) {
                                right = child.translationX.toInt()
                            }

                            Log.d(TAG, "top: $top")
                            Log.d(TAG, "right: $right")
                            Log.d(TAG, "bottom: $bottom")

                            selectedCopy?.setBounds(left, top, right, bottom)
                            selectedCopy?.draw(canvas)

                            val itemHeight = bottom - top
                            val addIconTop = top + (itemHeight - intrinsicHeight!!) / 2
                            val addIconMargin = child.translationX.toInt() / 3
                            val addIconLeft = right - addIconMargin - intrinsicWidth!!
                            val addIconRight = right - addIconMargin
                            val addIconBottom = addIconTop + intrinsicHeight

                            // Draw the delete icon
                            addIcon?.setBounds(addIconLeft, addIconTop, addIconRight, addIconBottom)
                            addIcon?.draw(canvas)

                        }
                    }
                }
            }
        }


        super.onDraw(canvas, parent, state)
    }

    companion object {
        const val TAG = "wowItemDecorator"
        const val ALPHA_THRESHOLD = 250
    }

}