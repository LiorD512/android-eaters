package com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.payment

import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.di.abs.LiveEventData
import com.bupp.wood_spoon_chef.data.remote.model.Otl
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.data.remote.network.base.WSError
import com.bupp.wood_spoon_chef.data.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PaymentViewModel(val userRepository: UserRepository) : BaseViewModel() {

    val otlData = LiveEventData<Otl?>()

    fun getOtl(){
        viewModelScope.launch(Dispatchers.IO) {
            when (val response = userRepository.getOtl()) {
                is ResponseSuccess -> {
                    otlData.postRawValue(response.data)
                }
                is ResponseError -> {
                    errorEvent.postRawValue(response.error)
                }
            }
        }
    }



}