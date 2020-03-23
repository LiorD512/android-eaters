package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.MetaDataManager
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings
import com.bupp.wood_spoon_eaters.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SingleDishViewModel(val api: ApiService, val settings: AppSettings, val orderManager: OrderManager, val eaterDataManager: EaterDataManager, val metaDataManager: MetaDataManager) : ViewModel() {

    var menuItemId: Long = -1
    var isEvent: Boolean = false
    val progressData = ProgressData()


    fun getCurrentDish(): FullDish? {
        fullDish.value?.let {
            return it
        }
        return null
    }

    data class DishDetailsEvent(val isSuccess: Boolean = false, val dish: FullDish?, val isAvailable: Boolean = false)

    val dishDetailsEvent: SingleLiveEvent<DishDetailsEvent> = SingleLiveEvent()
    val fullDish = MutableLiveData<FullDish>()

    data class DishAvailability(val isAvailable: Boolean, val startingTime: Date?, val isSoldOut: Boolean = false)

    val availability = MutableLiveData<DishAvailability>()

    fun getFullDish() {
        progressData.startProgress()
        val feedRequest = getFeedRequest()
        api.getMenuItemsDetails(
            menuItemId = menuItemId,
            lat = feedRequest.lat,
            lng = feedRequest.lng,
            addressId = feedRequest.addressId,
            timestamp = feedRequest.timestamp
        ).enqueue(object : Callback<ServerResponse<FullDish>> {
                override fun onResponse(call: Call<ServerResponse<FullDish>>, response: Response<ServerResponse<FullDish>>) {
                    progressData.endProgress()
                    if (response.isSuccessful) {
                        Log.d("wowSingleDishVM", "getMenuItemsDetails success")
                        val dish = response.body()?.data
                        dish?.let {
                            fullDish.postValue(it)
                            availability.postValue(DishAvailability(checkCookingSlotAvailability(it), getStartingDate(it.menuItem?.cookingSlot?.startsAt), checkDishSoldout(it)))
                        }
//                    val isCookingSlotAvailabilty = checkCookingSlotAvailability(dish)
//                    dishDetailsEvent.postValue(DishDetailsEvent(true, dish, isCookingSlotAvailabilty))
//                    val shouldClearCart = checkForDifferentCook(dish)

                    } else {
                        Log.d("wowSingleDishVM", "getMenuItemsDetails fail")
                        dishDetailsEvent.postValue(DishDetailsEvent(false, null))
                    }
                }

                override fun onFailure(call: Call<ServerResponse<FullDish>>, t: Throwable) {
                    progressData.endProgress()
                    Log.d("wowSingleDishVM", "getMenuItemsDetails big fail: ${t.message}")
                    dishDetailsEvent.postValue(DishDetailsEvent(false, null))
                }
            })
    }

    private fun checkDishSoldout(dish: FullDish): Boolean {
        val quantity = dish.menuItem?.quantity
        val unitsSold = dish.menuItem?.unitsSold
         quantity?.let{
             unitsSold?.let{
                 return ((quantity - unitsSold <= 0))
             }
         }
        return false
    }

    private fun getStartingDate(startsAt: Date?): Date? {
        var newDate = Date()
        startsAt?.let{
            if(startsAt.after(newDate)){
                newDate = startsAt
            }
        }
        return newDate
    }


    private fun checkCookingSlotAvailability(dish: FullDish?): Boolean {
        val start: Date? = dish?.menuItem?.cookingSlot?.startsAt
        val end: Date? = dish?.menuItem?.cookingSlot?.endsAt
        var userSelection: Date? = eaterDataManager.getLastOrderTime()

        if (start == null || end == null) {
            return false
        }
        if (userSelection == null) {
            userSelection = Date()
        }
        return (userSelection.equals(start) || userSelection.equals(end)) || (userSelection.after(start) && userSelection.before(end))
    }

    private fun getFeedRequest(): FeedRequest {
        var feedRequest = FeedRequest()
        //address
        val currentAddress = eaterDataManager.getLastChosenAddress()
        if (eaterDataManager.isUserChooseSpecificAddress()) {
            feedRequest.addressId = currentAddress?.id
        } else {
            feedRequest.lat = currentAddress?.lat
            feedRequest.lng = currentAddress?.lng
        }

        //time
        feedRequest.timestamp = eaterDataManager.getLastOrderTimeParam()

        return feedRequest
    }


    fun updateChosenDeliveryDate(selectedMenuItem: MenuItem? = null, newChosenDate: Date?) {
        getCurrentDish()?.let {
            //if new chosen time is approximently now - set null
            var newDate = newChosenDate
            if(Utils.isNow(newChosenDate)){
                newDate = null
            }
            eaterDataManager.orderTime = newDate
            selectedMenuItem?.let {
                getCurrentDish()!!.menuItem = it
            }
        }
    }


    fun getUserChosenDeliveryDate(): Date? {
        if (eaterDataManager.getLastOrderTime() != null) {
            return eaterDataManager.getLastOrderTime()
        }
        return Date()
    }

    fun getDropoffLocation(): String? {
        return eaterDataManager.getDropoffLocation()
    }


    data class GetReviewsEvent(val isSuccess: Boolean = false, val reviews: Review? = null)

    val getReviewsEvent: SingleLiveEvent<GetReviewsEvent> = SingleLiveEvent()

    fun getDishReview() {
        progressData.startProgress()
        fullDish.value?.let {
            api.getDishReview(it.cook.id).enqueue(object : Callback<ServerResponse<Review>> {
                override fun onResponse(
                    call: Call<ServerResponse<Review>>,
                    response: Response<ServerResponse<Review>>
                ) {
                    progressData.endProgress()
                    if (response.isSuccessful) {
                        val reviews = response.body()?.data
                        Log.d("wowFeedVM", "getDishReview success")
                        getReviewsEvent.postValue(GetReviewsEvent(true, reviews))
                    } else {
                        Log.d("wowFeedVM", "getDishReview fail")
                        getReviewsEvent.postValue(GetReviewsEvent(false))
                    }
                }

                override fun onFailure(call: Call<ServerResponse<Review>>, t: Throwable) {
                    Log.d("wowFeedVM", "getDishReview big fail: ${t.message}")
                    progressData.endProgress()
                    getReviewsEvent.postValue(GetReviewsEvent(false))
                }
            })
        }
    }



    fun getDeliveryFeeString(): String {
        return metaDataManager.getDeliveryFeeStr()
    }

    fun hasValidDeliveryAddress(): Boolean {
        return eaterDataManager.getLastChosenAddress()?.id != null
    }



    fun fetchDishForNewDate(menuItemId: Long) {
        this.menuItemId = menuItemId
        getFullDish()
    }

    data class AdditionalDishesEvent(val orderItems: List<OrderItem>?, val moreDishes: List<Dish>?)




}
