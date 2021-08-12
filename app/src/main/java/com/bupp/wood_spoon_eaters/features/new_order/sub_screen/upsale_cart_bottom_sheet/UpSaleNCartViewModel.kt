package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.model.Dish

class UpSaleNCartViewModel(
    val cartManager: CartManager
) : ViewModel() {

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
        val shouldShowUpsaleDialog = false
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
        val orderItems = cartManager.getCurrentOrderItems()
        val subTotal = cartManager.getOrderSubTotal()
        orderItems?.forEach {
            val customCartItem = CustomCartItem(
                dishName = it.dish.name,
                quantity = it.quantity,
                price = it.price.formatedValue ?: "0",
                note = it.notes
            )
            list.add(CartAdapterItem(customCartItem = customCartItem))
        }
        subTotal?.let{
            list.add(CartAdapterSubTotalItem(it))
        }
        return CartData(list)
    }




    private fun fetchUpSaleData(): CartData {
        val list = mutableListOf<CartBaseAdapterItem>()

        return CartData(list)
    }


}
