package com.bupp.wood_spoon_eaters.features.main.search

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RatingItemDecoration() : RecyclerView.ItemDecoration() {


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
        outRect.bottom = 28.toPx()
    }
}