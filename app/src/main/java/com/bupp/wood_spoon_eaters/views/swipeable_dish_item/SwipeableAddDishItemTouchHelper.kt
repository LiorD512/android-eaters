package com.bupp.wood_spoon_eaters.views.swipeable_dish_item

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import com.bupp.wood_spoon_eaters.bottom_sheets.upsale_bottom_sheet.UpSaleAdapter


abstract class SwipeableAddDishItemTouchHelper() : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        Log.d(TAG, "onMove")
        return false
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
//        Log.d(TAG, "onChildDraw: $dX state: $actionState, isActive: $isCurrentlyActive")
        val reachedMaxSwipe = dX > SWIPE_THRESHOLD
        if(!reachedMaxSwipe){
            super.onChildDraw(c, recyclerView, viewHolder, dX / 3, dY, actionState, isCurrentlyActive)
        }
    }


    companion object {
        const val TAG = "wowSwipeableItemHelper"
        const val SWIPE_THRESHOLD = 600
    }

}