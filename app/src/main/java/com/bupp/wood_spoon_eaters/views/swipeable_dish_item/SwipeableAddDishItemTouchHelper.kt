package com.bupp.wood_spoon_eaters.views.swipeable_dish_item

import android.R
import android.graphics.Canvas
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.swipeableAdapter.SwipeableAdapter


class SwipeableAddDishItemTouchHelper(val adapter: SwipeableAdapter<*>, private val decoratedViewType: Int? = null) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.updateItemQuantityAdd(viewHolder.absoluteAdapterPosition)
        adapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        Log.d(TAG, "onMove")
        return false
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        val reachedMaxSwipe = dX > SWIPE_THRESHOLD
        if(!reachedMaxSwipe){
            super.onChildDraw(c, recyclerView, viewHolder, dX / 3, dY, actionState, isCurrentlyActive)
        }
    }

    override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        if(decoratedViewType == null ||viewHolder.itemViewType == decoratedViewType){
            return super.getSwipeDirs(recyclerView, viewHolder)
        }
        return  0
    }


    companion object {
        const val TAG = "wowSwipeableItemHelper"
        const val SWIPE_THRESHOLD = 800
    }

}