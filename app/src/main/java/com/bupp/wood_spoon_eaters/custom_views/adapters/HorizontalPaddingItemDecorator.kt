package com.bupp.wood_spoon_eaters.custom_views.adapters

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalPaddingItemDecorator(val padding: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val itemPosition = parent.getChildAdapterPosition(view)
        val adapter = parent.adapter
        if (adapter != null && itemPosition != adapter.itemCount - 1) {
            outRect.right = padding
        }
    }
}