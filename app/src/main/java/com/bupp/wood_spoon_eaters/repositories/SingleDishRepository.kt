package com.bupp.wood_spoon_eaters.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.SingleDishViewModel
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.model.FeedRequest
import com.bupp.wood_spoon_eaters.model.FullDish
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SingleDishRepository(val apiService: ApiService, val eaterDataManager: EaterDataManager) {

    fun getSingleDish(menuItemId: Long): LiveData<FullDish>{
        val fullDishLiveData = MutableLiveData<FullDish>()
        val feedRequest = getFeedRequest()
        apiService.getSingleDish(
            menuItemId = menuItemId,
            lat = feedRequest.lat,
            lng = feedRequest.lng,
            addressId = feedRequest.addressId,
            timestamp = feedRequest.timestamp
        ).enqueue(object : Callback<ServerResponse<FullDish>> {
            override fun onResponse(call: Call<ServerResponse<FullDish>>, response: Response<ServerResponse<FullDish>>) {
//                progressData.endProgress()
                if (response.isSuccessful) {
                    Log.d("wowSingleDishVM", "getMenuItemsDetails success")
                    val dish = response.body()?.data
                    fullDishLiveData.postValue(dish)
//                    dish?.let {
//                        fullDish.postValue(it)
////                            availability.postValue(DishAvailability(checkCookingSlotAvailability(it), getStartingDate(it.menuItem?.cookingSlot?.startsAt), checkDishSoldout(it)))
//                        availability.postValue(
//                            SingleDishViewModel.DishAvailability(
//                                checkCookingSlotAvailability(it),
//                                getStartingDate(it.menuItem?.cookingSlot?.orderFrom),
//                                checkDishSoldout(it)
//                            )
//                        )
//                    }
//                    val isCookingSlotAvailabilty = checkCookingSlotAvailability(dish)
//                    dishDetailsEvent.postValue(DishDetailsEvent(true, dish, isCookingSlotAvailabilty))
//                    val shouldClearCart = checkForDifferentCook(dish)

                } else {
                    Log.d("wowSingleDishVM", "getMenuItemsDetails fail")
//                    dishDetailsEvent.postValue(SingleDishViewModel.DishDetailsEvent(false, null))
                }
            }

            override fun onFailure(call: Call<ServerResponse<FullDish>>, t: Throwable) {
//                progressData.endProgress()
                Log.d("wowSingleDishVM", "getMenuItemsDetails big fail: ${t.message}")
//                dishDetailsEvent.postValue(SingleDishViewModel.DishDetailsEvent(false, null))
            }
        })
        return fullDishLiveData
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

}