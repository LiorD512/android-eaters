//package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet.sub_screens.upsale
//
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.bupp.wood_spoon_eaters.model.Dish
//
//class UpSaleViewModel : ViewModel() {
//
//    var currentPageState = PageState.UPSALE
//
//    fun initData() {
//        when (currentPageState) {
//            PageState.UPSALE -> {
//                upSaleLiveData.postValue(fetchUpSaleData())
//            }
//            PageState.CART -> {
//
//            }
//        }
//    }
//
//
//    enum class PageState {
//        UPSALE,
//        CART
//    }
//
//    data class UpsaleData(
//        val items: List<UpSaleAdapterItem>
//    )
//
//    val upSaleLiveData = MutableLiveData<UpsaleData>()
//
//    data class CartData(
//        val adapter: UpSaleAdapter,
//        val items: List<UpSaleAdapterItem>
//    )
//
//    val cartLiveData = MutableLiveData<CartData>()
//
//
//    private fun fetchUpSaleData(): UpsaleData {
//        val list = mutableListOf<UpSaleAdapterItem>()
//        list.add(UpSaleAdapterItem(0, Dish(0, null, "a", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(UpSaleAdapterItem(1, Dish(0, null, "b", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(UpSaleAdapterItem(0, Dish(0, null, "c", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(UpSaleAdapterItem(10, Dish(0, null, "d", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(UpSaleAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(UpSaleAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(UpSaleAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(UpSaleAdapterItem(1, Dish(0, null, "z", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(UpSaleAdapterItem(10, Dish(0, null, "d", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(UpSaleAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(UpSaleAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(UpSaleAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(UpSaleAdapterItem(1, Dish(0, null, "z", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(UpSaleAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(UpSaleAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(UpSaleAdapterItem(0, Dish(0, null, "e", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        list.add(UpSaleAdapterItem(1, Dish(0, null, "z", null, "d", null, "a", "", null, null, null, null, null, null, null)))
//        return UpsaleData(list)
//    }
//
//
//
//}
