package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet.UpSaleAdapterItem
import com.bupp.wood_spoon_eaters.model.Dish

class UpSaleNCartViewModel : ViewModel() {

    var currentPageState = PageState.CART


    enum class PageState {
        UPSALE,
        CART
    }

    enum class NavigationEvent{
        GO_TO_CHECKOUT,
        GO_TO_UP_SALE
    }
    val navigationEvent = MutableLiveData<NavigationEvent>()

    fun onCartBtnClick() {
        val shouldShowUpsaleDialog = true
        if(shouldShowUpsaleDialog && currentPageState == PageState.CART){
            navigationEvent.postValue(NavigationEvent.GO_TO_UP_SALE)
            currentPageState = PageState.UPSALE
        }else{
            navigationEvent.postValue(NavigationEvent.GO_TO_CHECKOUT)
            currentPageState = PageState.CART
        }
    }



}
