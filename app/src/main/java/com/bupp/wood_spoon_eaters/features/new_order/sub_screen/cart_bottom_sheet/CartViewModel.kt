package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.cart_bottom_sheet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet.CartAdapterItem
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet.CartAdapterSubTotalItem
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet.CartBaseAdapterItem
import com.bupp.wood_spoon_eaters.model.Dish

class CartViewModel : ViewModel() {


    fun initData() {
        cartLiveData.postValue(fetchCartData())
    }

    data class CartData(
        val items: List<CartBaseAdapterItem>
    )

    val cartLiveData = MutableLiveData<CartData>()

    private fun fetchCartData(): CartData {
        val list = mutableListOf<CartBaseAdapterItem>()
        list.add(CartAdapterItem(0, Dish(0, null, "a", null, "d", null, "a", "", null, null, null, null, null, null, null)))
        list.add(CartAdapterItem(1, Dish(0, null, "b", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(CartAdapterItem(0, Dish(0, null, "c", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(CartAdapterItem(10, Dish(0, null, "d", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(CartAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(CartAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(CartAdapterItem(1, Dish(0, null, "z", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(CartAdapterItem(10, Dish(0, null, "d", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(CartAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(CartAdapterItem(1, Dish(0, null, "z", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(CartAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(CartAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(CartAdapterItem(1, Dish(0, null, "z1", null, "d", null, "a", "", null, null, null, null, null, null, null)))
        list.add(CartAdapterSubTotalItem("150"))
        return CartData(list)
    }


}
