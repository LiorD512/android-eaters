package com.bupp.wood_spoon_chef.presentation.features.main.single_dish

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.data.remote.model.Dish
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.onboarding.login.LoginViewModel
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.data.repositories.DishRepository
import com.bupp.wood_spoon_chef.data.repositories.MetaDataRepository
import kotlinx.coroutines.launch

class SingleDishViewModel(private val dishRepository: DishRepository, private val metaDataRepository: MetaDataRepository): BaseViewModel() {

    val getDishEvent: MutableLiveData<Dish> = MutableLiveData()

    fun getDish(dishId: Long){
        progressData.startProgress()
        viewModelScope.launch {
            when (val response = dishRepository.getDishById(dishId)) {
                is ResponseSuccess -> {
                    Log.d(LoginViewModel.TAG, "GetDishesEvent - Success")
                    val dish = response.data
                    dish?.let {
                        getDishEvent.postValue(dish)
                    }
                }
                is ResponseError -> {
                    errorEvent.postRawValue(response.error)
                }
            }
            progressData.endProgress()
        }
    }

    fun getDefaultServiceFee(): Float {
        return metaDataRepository.getBuppServiceFeePercentage()
    }
}