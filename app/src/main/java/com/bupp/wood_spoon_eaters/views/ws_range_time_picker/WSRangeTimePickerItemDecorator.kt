package com.bupp.wood_spoon_eaters.views.ws_range_time_picker

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class WSRangeTimePickerItemDecorator(private val topHeight: Int) : RecyclerView.ItemDecoration() {


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildLayoutPosition(view)
        when (position) {
            0 -> {
                view.setPadding(0, topHeight, 0, 0)
            }
            else -> {
                view.setPadding(0, 0, 0, 0)
            }
        }


    }

}
