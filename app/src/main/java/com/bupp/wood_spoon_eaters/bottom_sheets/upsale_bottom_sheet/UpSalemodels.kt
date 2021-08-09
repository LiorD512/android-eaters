package com.bupp.wood_spoon_eaters.bottom_sheets.upsale_bottom_sheet

import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.views.swipeable_dish_item.swipeableAdapter.SwipeableAdapterItem

data class UpSaleAdapterItem(
    override var quantity: Int = 0,
    override val menuItem: MenuItem
): SwipeableAdapterItem()