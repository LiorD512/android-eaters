package com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSectionsViewType.SINGLE_DISH
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSectionsViewType.UNAVAILABLE_HEADER
import java.util.*

class DividerItemDecoratorDish(val divider: Drawable?) : ItemDecoration() {

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width
        parent.adapter?.let { adapter ->
            val childCount = Objects.requireNonNull(adapter).itemCount
            for (i in 1 until childCount-1) {
                val child = parent.getChildAt(i)
                val currentViewType = adapter.getItemViewType(i)
                val nextViewType = adapter.getItemViewType(i + 1)
                if ((currentViewType == SINGLE_DISH.ordinal && nextViewType != UNAVAILABLE_HEADER.ordinal)) {
                        child?.let {
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
}