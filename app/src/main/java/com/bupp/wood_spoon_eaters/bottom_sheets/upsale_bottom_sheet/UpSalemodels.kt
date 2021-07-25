package com.bupp.wood_spoon_eaters.bottom_sheets.upsale_bottom_sheet

import com.bupp.wood_spoon_eaters.model.Dish

data class UpSaleAdapterItem(
    var quantity: Int = 0,
    val dish: Dish
){
    fun isInCart(): Boolean{
        return quantity > 0
    }
}