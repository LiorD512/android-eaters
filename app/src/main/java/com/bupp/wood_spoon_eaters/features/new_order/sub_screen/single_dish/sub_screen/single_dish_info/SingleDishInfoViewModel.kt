//package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_info
//
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
//import com.bupp.wood_spoon_eaters.di.abs.ProgressData
//import com.bupp.wood_spoon_eaters.managers.OldCartManager
//import com.bupp.wood_spoon_eaters.managers.EaterDataManager
//import com.bupp.wood_spoon_eaters.model.*
//import java.util.*
//
//class SingleDishInfoViewModel(
//    private val oldCartManager: OldCartManager,
//    private val eaterDataManager: EaterDataManager,
//
//    ) : ViewModel() {
//
//    val timeChangeEvent = LiveEventData<List<MenuItem>>()
//
//    fun updateCurrentOrderItem(quantity: Int? = null, note: String? = null){
//        oldCartManager.updateCurrentOrderItemRequest(OrderItemRequest(quantity = quantity, notes = note))
//    }
//
//    fun getDropOffLocation(): String? {
//        return eaterDataManager.getLastChosenAddress()?.getDropoffLocationStr()
//    }
//
//    fun getTotalPriceForDishQuantity(counter: Int): Double {
//        return oldCartManager.getTotalPriceForDishQuantity(counter)
//    }
//
//    fun onTimeChangeClick() {
//        val menuItems = oldCartManager.currentShowingDish?.availableMenuItems
//        menuItems?.let{
//            timeChangeEvent.postRawValue(it)
//        }
//    }
//
//
//
//
////    fun addNewItemToCart(){
////        cartManager.addNewItemToCart()
////    }
////    val getReviewsEvent: SingleLiveEvent<Review?> = SingleLiveEvent()
////    fun getDishReview() {
////        progressData.startProgress()
////        val currentDishId = cartManager.currentShowingDish?.id
////        currentDishId?.let{
////            viewModelScope.launch {
////                val result = newOrderRepository.getDishReview(it)
////                result?.let{
////                    getReviewsEvent.postValue(it)
////                }
////            }
////        }
////    }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//    var menuItemId: Long = -1
//    var isEvent: Boolean = false
//    val progressData = ProgressData()
//
//
//    fun getCurrentDish(): FullDish? {
//        fullDish.value?.let {
//            return it.fullDish
//        }
//        return null
//    }
//
//
//    data class DishDetailsEvent(val fullDish: FullDish, val newSelectedDate: Date? = null)
//    val fullDish = MutableLiveData<DishDetailsEvent>()
//
//    data class DishAvailability(val isAvailable: Boolean, val startingTime: Date?, val isSoldOut: Boolean = false)
//
//    val availability = MutableLiveData<DishAvailability>()
//
//
//
//
//
//    data class AdditionalDishesEvent(val orderItems: List<OrderItem>?, val moreDishes: List<Dish>?)
//
//
//}
