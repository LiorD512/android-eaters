package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.MenuItem

class UpSaleNCartViewModel : ViewModel() {

    var currentPageState = PageState.CART


    enum class PageState {
        UPSALE,
        CART
    }

    enum class NavigationEvent {
        GO_TO_CHECKOUT,
        GO_TO_UP_SALE
    }

    val navigationEvent = MutableLiveData<NavigationEvent>()

    fun onCartBtnClick() {
        val shouldShowUpsaleDialog = true
        if (shouldShowUpsaleDialog && currentPageState == PageState.CART) {
            currentPageState = PageState.UPSALE
            navigationEvent.postValue(NavigationEvent.GO_TO_UP_SALE)
        } else {
            currentPageState = PageState.CART
            navigationEvent.postValue(NavigationEvent.GO_TO_CHECKOUT)
        }
    }

    fun initData() {
        when (currentPageState) {
            PageState.CART -> {
                upsaleNCartLiveData.postValue(fetchCartData())
            }
            else -> {
                upsaleNCartLiveData.postValue(fetchUpSaleData())
            }
        }
    }


    val upsaleNCartLiveData = MutableLiveData<CartData>()
    data class CartData(
        val items: List<CartBaseAdapterItem>
    )

//    val upsaleLiveData = MutableLiveData<UpsaleData>()
//    data class UpsaleData(
//        val items: List<UpSaleAdapterItem>
//    )

    private fun fetchCartData(): CartData {
        val list = mutableListOf<CartBaseAdapterItem>()
//        list.add(CartAdapterItem(0, Dish(0, null, "a", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(CartAdapterItem(1, Dish(0, null, "b", null, "d", null, "a", "", null, null, null, null, null, null, null)))
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
//        list.add(CartAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(CartAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(CartAdapterItem(1, Dish(0, null, "z1", null, "d", null, "a", "", null, null, null, null, null, null, null)))
        list.add(CartAdapterItem(0, MenuItem(id = 0, price = null, quantity = 0, unitsSold = 0, orderAt = null, dishId = 0, tags = listOf(), cookingSlot = null,
            dish = Dish(0, null, "a", null, "d", null, "a", "", null, null, null, null, null, null, null),
            availableLater = null
        )))
        list.add(CartAdapterItem(1, MenuItem(id = 0, price = null, quantity = 0, unitsSold = 0, orderAt = null, dishId = 0, tags = listOf(), cookingSlot = null,
            dish = Dish(0, null, "b", null, "d", null, "a", "", null, null, null, null, null, null, null),
            availableLater = null
        )))
        list.add(CartAdapterItem(0, MenuItem(id = 0, price = null, quantity = 0, unitsSold = 0, orderAt = null, dishId = 0, tags = listOf(), cookingSlot = null,
            dish = Dish(0, null, "c", null, "d", null, "a", "", null, null, null, null, null, null, null),
            availableLater = null
        )))
        list.add(CartAdapterItem(0, MenuItem(id = 0, price = null, quantity = 0, unitsSold = 0, orderAt = null, dishId = 0, tags = listOf(), cookingSlot = null,
            dish = Dish(0, null, "d", null, "d", null, "a", "", null, null, null, null, null, null, null),
            availableLater = null
        )))
        list.add(CartAdapterItem(1, MenuItem(id = 0, price = null, quantity = 0, unitsSold = 0, orderAt = null, dishId = 0, tags = listOf(), cookingSlot = null,
            dish = Dish(0, null, "e", null, "d", null, "a", "", null, null, null, null, null, null, null),
            availableLater = null
        )))
        list.add(CartAdapterItem(0, MenuItem(id = 0, price = null, quantity = 0, unitsSold = 0, orderAt = null, dishId = 0, tags = listOf(), cookingSlot = null,
            dish = Dish(0, null, "f", null, "d", null, "a", "", null, null, null, null, null, null, null),
            availableLater = null
        )))
        list.add(CartAdapterItem(10, MenuItem(id = 0, price = null, quantity = 0, unitsSold = 0, orderAt = null, dishId = 0, tags = listOf(), cookingSlot = null,
            dish = Dish(0, null, "g", null, "d", null, "a", "", null, null, null, null, null, null, null),
            availableLater = null
        )))
        list.add(CartAdapterSubTotalItem("150"))
        return CartData(list)
    }




    private fun fetchUpSaleData(): CartData {
        val list = mutableListOf<CartBaseAdapterItem>()
        list.add(UpsaleAdapterItem(0, MenuItem(id = 0, price = null, quantity = 0, unitsSold = 0, orderAt = null, dishId = 0, tags = listOf(), cookingSlot = null,
            dish = Dish(0, null, "a", null, "d", null, "a", "", null, null, null, null, null, null, null),
            availableLater = null
        )))
        list.add(UpsaleAdapterItem(1, MenuItem(id = 0, price = null, quantity = 0, unitsSold = 0, orderAt = null, dishId = 0, tags = listOf(), cookingSlot = null,
            dish = Dish(0, null, "b", null, "d", null, "a", "", null, null, null, null, null, null, null),
            availableLater = null
        )))
        list.add(UpsaleAdapterItem(0, MenuItem(id = 0, price = null, quantity = 0, unitsSold = 0, orderAt = null, dishId = 0, tags = listOf(), cookingSlot = null,
            dish = Dish(0, null, "c", null, "d", null, "a", "", null, null, null, null, null, null, null),
            availableLater = null
        )))
        list.add(UpsaleAdapterItem(0, MenuItem(id = 0, price = null, quantity = 0, unitsSold = 0, orderAt = null, dishId = 0, tags = listOf(), cookingSlot = null,
            dish = Dish(0, null, "d", null, "d", null, "a", "", null, null, null, null, null, null, null),
            availableLater = null
        )))
        list.add(UpsaleAdapterItem(1, MenuItem(id = 0, price = null, quantity = 0, unitsSold = 0, orderAt = null, dishId = 0, tags = listOf(), cookingSlot = null,
            dish = Dish(0, null, "e", null, "d", null, "a", "", null, null, null, null, null, null, null),
            availableLater = null
        )))
        list.add(UpsaleAdapterItem(0, MenuItem(id = 0, price = null, quantity = 0, unitsSold = 0, orderAt = null, dishId = 0, tags = listOf(), cookingSlot = null,
            dish = Dish(0, null, "f", null, "d", null, "a", "", null, null, null, null, null, null, null),
            availableLater = null
        )))
        list.add(UpsaleAdapterItem(10, MenuItem(id = 0, price = null, quantity = 0, unitsSold = 0, orderAt = null, dishId = 0, tags = listOf(), cookingSlot = null,
            dish = Dish(0, null, "g", null, "d", null, "a", "", null, null, null, null, null, null, null),
            availableLater = null
        )))
        return CartData(list)
    }


}
