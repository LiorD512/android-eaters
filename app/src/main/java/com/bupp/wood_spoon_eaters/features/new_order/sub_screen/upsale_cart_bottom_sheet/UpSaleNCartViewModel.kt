package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.model.CookingSlot
import kotlinx.coroutines.launch

class UpSaleNCartViewModel(
    val cartManager: CartManager
) : ViewModel() {

    var currentPageState = PageState.CART
    val currentOrderData = cartManager.getCurrentOrderData()
    val currentCookingSlot = cartManager.getCurrentCookingSlot()
    val onDishCartClick = LiveEventData<CustomCartItem>()

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
        val items: List<CartBaseAdapterItem>?
    )


    private fun fetchCartData(): CartData? {
        val list = mutableListOf<CartBaseAdapterItem>()
        val orderItems = currentOrderData.value?.orderItems
        if(orderItems?.isEmpty() == true){
            return null
        }
        orderItems?.forEach {
            val customCartItem = CustomCartItem(
                orderItem = it,
                cookingSlot = currentCookingSlot
            )
            list.add(CartAdapterItem(customCartItem = customCartItem))
        }

        val subTotal = currentOrderData.value?.subtotal?.formatedValue
        subTotal?.let {
            list.add(CartAdapterSubTotalItem(it))
        }
        return CartData(list)
    }


    private fun fetchUpSaleData(): CartData {
        val list = mutableListOf<CartBaseAdapterItem>()

        return CartData(list)
    }


    fun addDishToCart(quantity: Int, dishId: Long, note: String? = null) {
        viewModelScope.launch {
            cartManager.addOrUpdateCart(quantity, dishId, note)
        }
    }

    fun removeOrderItemsByDishId(dishId: Long?) {
        dishId?.let {
            viewModelScope.launch {
                cartManager.removeOrderItems(it)
            }
        }
    }

    fun onCartItemClicked(customCartItem: CustomCartItem) {
        onDishCartClick.postRawValue(customCartItem)
    }

//    /**
//     * this function is being called when user swiped out all of his
//     * items from the cart - restaurant page need to update item items counter
//     */
//    fun onCartCleared(){
//        viewModelScope.launch {
//            cartManager.currentCookingSlotId?.let{
//                cartManager.updateCurCookingSlot(it)
//            }
//        }
//    }

}
