package com.bupp.wood_spoon_eaters.custom_views.adapters

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import java.util.*

class DividerItemDecorator(val divider: Drawable?): ItemDecoration() {

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width
        parent.adapter?.let{
            val childCount = Objects.requireNonNull(it).itemCount
            for (i in 0 until childCount) {
                val child = parent.getChildAt(i)
                child?.let{
                    val params = child.layoutParams as RecyclerView.LayoutParams
                    val top = child.bottom + params.bottomMargin
                    val bottom = top + divider!!.intrinsicHeight
                    divider.setBounds(left, top, right, bottom)
                    divider.draw(canvas)
                }
            }
        }
    }
}