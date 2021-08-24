package com.bupp.wood_spoon_eaters.views.swipeable_dish_item

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.swipeableAdapter.SwipeableAdapter

class SwipeableRecycler @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0):
    RecyclerView(context, attrs, defStyleAttr) {

    private val addTouchHelper = SwipeableAddDishItemTouchHelper()
    private val removeTouchHelper = SwipeableRemoveDishItemTouchHelper()

    fun initSwipeableRecycler(adapter: SwipeableAdapter<*>) {
        this.adapter = adapter

//        val addTouchHelper = SwipeableAddDishItemTouchHelper()
//        val removeTouchHelper = SwipeableRemoveDishItemTouchHelper()

        ItemTouchHelper(addTouchHelper).attachToRecyclerView(this)
        ItemTouchHelper(removeTouchHelper).attachToRecyclerView(this)

        addTouchHelper.adapter = adapter
        removeTouchHelper.adapter = adapter

        val removeShape: Drawable? = ContextCompat.getDrawable(context, R.drawable.swipeable_dish_remove_bkg)
        this.addItemDecoration(SwipeableRemoveDishItemDecorator(context, removeShape))
        val defaultShape: Drawable? = ContextCompat.getDrawable(context, R.drawable.grey_white_right_cornered)
        val selectedShape: Drawable? = ContextCompat.getDrawable(context, R.drawable.swipeable_dish_add_bkg)
        this.addItemDecoration(SwipeableAddDishItemDecorator(context, defaultShape, selectedShape))

        this.itemAnimator?.changeDuration = 150
        this.itemAnimator?.moveDuration = 0
        this.itemAnimator?.removeDuration = 0
        this.itemAnimator = CustomItemAnimator()
    }


}
