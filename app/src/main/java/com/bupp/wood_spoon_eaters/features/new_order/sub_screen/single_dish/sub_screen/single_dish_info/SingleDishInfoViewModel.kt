package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_info

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.NewOrderRepository
import com.bupp.wood_spoon_eaters.common.AppSettings
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.utils.DateUtils
import kotlinx.coroutines.launch
import java.util.*

class SingleDishInfoViewModel(
    val cartManager: CartManager,
    val settings: AppSettings,
    val orderManager: OrderManager,
    val eaterDataManager: EaterDataManager,
    val metaDataRepository: MetaDataRepository,
    val newOrderRepository: NewOrderRepository
) : ViewModel() {


    val getReviewsEvent: SingleLiveEvent<Review?> = SingleLiveEvent()
    fun getDishReview() {
        progressData.startProgress()
        val currentDishId = cartManager.currentShowingDish?.id
        currentDishId?.let{
            viewModelScope.launch {
                val result = newOrderRepository.getDishReview(it)
                result?.let{
                    getReviewsEvent.postValue(it)
                }
            }
        }
    }














    var menuItemId: Long = -1
    var isEvent: Boolean = false
    val progressData = ProgressData()


    fun getCurrentDish(): FullDish? {
        fullDish.value?.let {
            return it.fullDish
        }
        return null
    }


    data class DishDetailsEvent(val fullDish: FullDish, val newSelectedDate: Date? = null)
    val fullDish = MutableLiveData<DishDetailsEvent>()

    data class DishAvailability(val isAvailable: Boolean, val startingTime: Date?, val isSoldOut: Boolean = false)

    val availability = MutableLiveData<DishAvailability>()

//    fun getFullDish(newChosenDate: Date? = null) {
//        progressData.startProgress()
//        val feedRequest = getFeedRequest()
//        api.getSingleDish(
//            menuItemId = menuItemId,
//            lat = feedRequest.lat,
//            lng = feedRequest.lng,
//            addressId = feedRequest.addressId,
//            timestamp = feedRequest.timestamp
//        ).enqueue(object : Callback<ServerResponse<FullDish>> {
//            override fun onResponse(call: Call<ServerResponse<FullDish>>, response: Response<ServerResponse<FullDish>>) {
//                progressData.endProgress()
//                if (response.isSuccessful) {
//                    Log.d("wowSingleDishVM", "getMenuItemsDetails success")
//                    val dish = response.body()?.data
//                    dish?.let {
//                        fullDish.postValue(DishDetailsEvent(it, newChosenDate))
//                        availability.postValue(
//                            DishAvailability(
//                                isAvailable = checkCookingSlotAvailability(it),
//                                startingTime = getStartingDate(it.menuItem?.cookingSlot?.orderFrom),
//                                isSoldOut = checkDishSoldout(it)
//                            )
//                        )
//                    }
//                } else {
//                    Log.d("wowSingleDishVM", "getMenuItemsDetails fail")
////                    dishDetailsEvent.postValue(DishDetailsEvent(false, null))
//                }
//            }
//
//            override fun onFailure(call: Call<ServerResponse<FullDish>>, t: Throwable) {
//                progressData.endProgress()
//                Log.d("wowSingleDishVM", "getMenuItemsDetails big fail: ${t.message}")
////                dishDetailsEvent.postValue(DishDetailsEvent(false, null))
//            }
//        })
//    }
//
//
//    private fun checkDishSoldout(dish: FullDish): Boolean {
//        val quantity = dish.menuItem?.quantity
//        val unitsSold = dish.menuItem?.unitsSold
//        quantity?.let {
//            unitsSold?.let {
//                return ((quantity - unitsSold <= 0))
//            }
//        }
//        return false
//    }
//
//    private fun getStartingDate(startsAt: Date?): Date? {
//        var newDate = Date()
//        startsAt?.let {
//            if (startsAt.after(newDate)) {
//                newDate = startsAt
//            }
//        }
//        return newDate
//    }
//
//
//    private fun checkCookingSlotAvailability(dish: FullDish?): Boolean {
//        val orderFrom: Date? = dish?.menuItem?.cookingSlot?.orderFrom
//        val start: Date? = dish?.menuItem?.cookingSlot?.startsAt
//        val end: Date? = dish?.menuItem?.cookingSlot?.endsAt
//        var userSelection: Date? = eaterDataManager.getLastOrderTime()
//
//        if (start == null || end == null) {
//            return false
//        }
//        if (userSelection == null) {
//            //in this case order is ASAP - then check from starting time and not orderingFrom time
//            userSelection = Date()
//            return (userSelection.equals(start) || userSelection.equals(end)) || (userSelection.after(start) && userSelection.before(end))
//        }
//        return (userSelection.equals(orderFrom) || userSelection.equals(end)) || (userSelection.after(orderFrom) && userSelection.before(end))
//    }
//
//    private fun getFeedRequest(): FeedRequest {
//        var feedRequest = FeedRequest()
//        //address
//        val currentAddress = eaterDataManager.getLastChosenAddress()
//        if (eaterDataManager.isUserChooseSpecificAddress()) {
//            feedRequest.addressId = currentAddress?.id
//        } else {
//            feedRequest.lat = currentAddress?.lat
//            feedRequest.lng = currentAddress?.lng
//        }
//
//        //time
//        feedRequest.timestamp = eaterDataManager.getLastOrderTimeParam()
//
//        return feedRequest
//    }


    fun updateChosenDeliveryDate(selectedMenuItem: MenuItem? = null, newChosenDate: Date?) {
        getCurrentDish()?.let {
            //if new chosen time is approximently now - set null
            var newDate = newChosenDate
            if (DateUtils.isNow(newChosenDate)) {
                newDate = null
            }
            eaterDataManager.orderTime = newDate
            selectedMenuItem?.let {
                getCurrentDish()!!.menuItem = it
            }
        }
    }


//    fun getUserChosenDeliveryDate(): Date? {
//        if (eaterDataManager.getLastOrderTime() != null) {
//            return eaterDataManager.getLastOrderTime()
//        }
//        return Date()
//    }

    fun getDropoffLocation(): String? {
        return eaterDataManager.getDropoffLocation()
    }





//    fun getDeliveryFeeString(): String {
//        return metaDataManager.getDeliveryFeeStr()
//    }

    fun hasValidDeliveryAddress(): Boolean {
        return eaterDataManager.getLastChosenAddress()?.id != null
    }


    fun fetchDishForNewDate(menuItemId: Long, newChosenDate: Date) {
        this.menuItemId = menuItemId
//        getFullDish(newChosenDate)
    }




    data class AdditionalDishesEvent(val orderItems: List<OrderItem>?, val moreDishes: List<Dish>?)


}
