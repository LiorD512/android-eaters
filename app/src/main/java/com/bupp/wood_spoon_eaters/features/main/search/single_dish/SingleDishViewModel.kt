package com.bupp.wood_spoon_eaters.features.main.search.single_dish

import android.util.Log
import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.features.main.search.SearchViewModel
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.FullDish
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SingleDishViewModel(val api: ApiService) : ViewModel() {

    data class DishDetailsEvent(val isSuccess: Boolean = false, val dish: FullDish?)
    val dishDetailsEvent: SingleLiveEvent<DishDetailsEvent> = SingleLiveEvent()

    fun getFullDish(menuItemId: Long) {
        api.getMenuItemsDetails(menuItemId).enqueue(object: Callback<ServerResponse<FullDish>> {
            override fun onResponse(call: Call<ServerResponse<FullDish>>, response: Response<ServerResponse<FullDish>>) {
                if(response.isSuccessful){
                    Log.d("wowSearchVM","getMenuItemsDetails success")
                    val dish = response.body()?.data
                    dishDetailsEvent.postValue(DishDetailsEvent(true, dish))
                }else{
                    Log.d("wowSearchVM","getMenuItemsDetails fail")
                    dishDetailsEvent.postValue(DishDetailsEvent(false, null))
                }
            }

            override fun onFailure(call: Call<ServerResponse<FullDish>>, t: Throwable) {
                Log.d("wowSearchVM","getMenuItemsDetails big fail: ${t.message}")
                dishDetailsEvent.postValue(DishDetailsEvent(false, null))
            }
        })
    }
}
