package com.bupp.wood_spoon_eaters.features.main.search

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SingleFeedItemDecoration() : RecyclerView.ItemDecoration() {


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

        val position = parent.getChildAdapterPosition(view)
        when(position){
            0 -> {
//                outRect.left = 15.toPx()
                view.setPadding(0,0, 15.toPx(), 0)
            }
            else -> {
                view.setPadding(15.toPx(),0, 15.toPx(), 0)
//                outRect.left = 15.toPx()
//                outRect.right = 15.toPx()
            }
        }


    }
}