package com.bupp.wood_spoon_eaters.views.swipeable_dish_item

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


abstract class SwipeableAddDishItemTouchHelper() : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }


//    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
//        /**
//         * To disable "swipe" for specific item return 0 here.
//         * For example:
//         * if (viewHolder?.itemViewType == YourAdapter.SOME_TYPE) return 0
//         * if (viewHolder?.adapterPosition == 0) return 0
//         */
//        if (viewHolder.absoluteAdapterPosition == 10) return 0
//        return super.getMovementFlags(recyclerView, viewHolder)
//    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
//        Log.d(TAG, "onMove")
        return true
    }


    override fun onChildDraw(
            c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val isCanceled = dX > 600

        if (isCanceled && !isCurrentlyActive ) {
            Log.d(TAG, "onChildDraw: $dX")
            clearCanvas(c, 0f, 0f, 0f, 0f)
            super.onChildDraw(c, recyclerView, viewHolder, 0f, dY, actionState, isCurrentlyActive)
            return
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }


    companion object{
        const val TAG = "wowSwipeableItemHelper"
    }
}