package com.bupp.wood_spoon_chef.presentation.features.main.earnings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.data.remote.model.Earnings
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.data.repositories.UserRepository
import com.bupp.wood_spoon_chef.utils.UserSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EarningsViewModel(val settings: UserSettings, val userRepository: UserRepository) :
    ViewModel() {

    val getEarnings: MutableLiveData<GetEarningsEvent> = MutableLiveData()

    data class GetEarningsEvent(val isSuccess: Boolean = false, val stats: Earnings? = null)

    fun getEarningsStats() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = userRepository.getStats()
            when(result){
                is ResponseError -> {
                    getEarnings.postValue(GetEarningsEvent(false, null))
                }
                is ResponseSuccess -> {
                    val stats = result.data
                    getEarnings.postValue(GetEarningsEvent(true, stats))
                }
            }
        }
    }

    fun getCookAngRating(): String {
        settings.currentCook?.rating?.let {
            return it.toString()
        }
        return "0"
    }

}