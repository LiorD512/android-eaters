package com.bupp.wood_spoon_eaters.features.main.search

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class WSRangeTimeViewItemDecorator : RecyclerView.ItemDecoration() {


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

//        if(parent.childCount == 1){
            when(parent.getChildAdapterPosition(view)){
                0 -> {
                    view.setPadding(0, 50.toPx(), 0, 0)
                }
                parent.childCount -> {
                    view.setPadding(0, 0, 0, 50.toPx())
                }
                else -> {
                    view.setPadding(0,0, 0, 0)
                }
            }
//        }


    }
}