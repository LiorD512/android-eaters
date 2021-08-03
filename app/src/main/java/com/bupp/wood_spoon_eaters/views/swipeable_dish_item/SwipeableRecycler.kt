package com.bupp.wood_spoon_eaters.views.swipeable_dish_item

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet.CustomItemAnimator
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.DishesMainAdapter
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.DividerItemDecoratorDish
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.swipeableAdapter.SwipeableAdapter

class SwipeableRecycler @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0):
    RecyclerView(context, attrs, defStyleAttr) {

    fun initSwipeableRecycler(adapter: SwipeableAdapter<*>, divider: Drawable?) {
        //todo - nicole - lets talk about "decoratedViewType"
        this.adapter = adapter

        ItemTouchHelper(SwipeableAddDishItemTouchHelper(adapter)).attachToRecyclerView(this)
        ItemTouchHelper(SwipeableRemoveDishItemTouchHelper(adapter)).attachToRecyclerView(this)

        this.itemAnimator?.changeDuration = 150
        this.itemAnimator?.moveDuration = 0
        this.itemAnimator?.removeDuration = 0
        this.itemAnimator = CustomItemAnimator()

        val removeShape: Drawable? = ContextCompat.getDrawable(context, R.drawable.swipeable_dish_remove_bkg)
        this.addItemDecoration(SwipeableRemoveDishItemDecorator(context, removeShape))
        val defaultShape: Drawable? = ContextCompat.getDrawable(context, R.drawable.grey_white_right_cornered)
        val selectedShape: Drawable? = ContextCompat.getDrawable(context, R.drawable.swipeable_dish_add_bkg)
        this.addItemDecoration(SwipeableAddDishItemDecorator(context, defaultShape, selectedShape))


        this.addItemDecoration(DividerItemDecoratorDish(divider))
    }
}
