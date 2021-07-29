package com.bupp.wood_spoon_eaters.views.swipeable_dish_item

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.upsale_bottom_sheet.CustomItemAnimator
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.dish_sections.DividerItemDecoratorDish
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSectionsViewType
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.swipeableAdapter.SwipeableAdapter

class SwipeableRecycler @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RecyclerView(context, attrs, defStyleAttr) {

    fun initSwipeableRecycler(adapter: SwipeableAdapter<*>) {
        ItemTouchHelper(SwipeableAddDishItemTouchHelper(adapter, DishSectionsViewType.SINGLE_DISH.ordinal)).attachToRecyclerView(this)
        ItemTouchHelper(SwipeableRemoveDishItemTouchHelper(adapter, DishSectionsViewType.SINGLE_DISH.ordinal)).attachToRecyclerView(this)


        this.itemAnimator?.changeDuration = 150
        this.itemAnimator?.moveDuration = 0
        this.itemAnimator?.removeDuration = 0
        this.itemAnimator = CustomItemAnimator()

        val removeShape: Drawable? = ContextCompat.getDrawable(context, R.drawable.swipeable_dish_remove_bkg)
        this.addItemDecoration(SwipeableRemoveDishItemDecorator(context, removeShape, DishSectionsViewType.SINGLE_DISH.ordinal))
        val defaultShape: Drawable? = ContextCompat.getDrawable(context, R.drawable.grey_white_right_cornered)
        val selectedShape: Drawable? = ContextCompat.getDrawable(context, R.drawable.swipeable_dish_add_bkg)
        this.addItemDecoration(SwipeableAddDishItemDecorator(context, defaultShape, selectedShape, DishSectionsViewType.SINGLE_DISH.ordinal))


        val divider: Drawable? = ContextCompat.getDrawable(context, R.drawable.divider_white_three)
        this.addItemDecoration(DividerItemDecoratorDish(divider))

    }
}
