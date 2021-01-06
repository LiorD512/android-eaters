package com.bupp.wood_spoon_eaters.features.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.data.UserRepository
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.features.new_order.service.EphemeralKeyProvider
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.MetaDataRepository
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import com.bupp.wood_spoon_eaters.network.ApiSettings
import com.bupp.wood_spoon_eaters.utils.AppSettings
import kotlinx.coroutines.launch


class SplashViewModel(
    private val apiSettings: ApiSettings, val eaterDataManager: EaterDataManager, val appSettings: AppSettings,
    private val userRepository: UserRepository, val metaDataRepository: MetaDataRepository, private val paymentManager: PaymentManager)
    : ViewModel(), EphemeralKeyProvider.EphemeralKeyProviderListener {

    private var loginTries: Int = 0
    val splashEvent: LiveEventData<SplashEvent> = LiveEventData()

    data class SplashEvent(
        val isUserExist: Boolean = false,
        val isUserRegistered: Boolean = false,
        val shouldUpdateVersion: Boolean = false
    )

    val errorEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()

    private var isUserExist = false
    private var isUserRegistered = false
    private var shouldUpdateVersion = false

    fun initAppSplashData(context: Context) {
        viewModelScope.launch {

            userRepository.initUserRepo()
            metaDataRepository.initMetaData()
            paymentManager.initPaymentManager(context)

            isUserExist = userRepository.isUserValid()
            isUserRegistered = userRepository.isUserRegistered()
            shouldUpdateVersion = metaDataRepository.checkMinVersionFail()

            splashEvent.postRawValue(SplashEvent(isUserExist, isUserRegistered, shouldUpdateVersion))
        }
    }

    fun setUserCampaignParam(sid: String?, cid: String?) {
        sid?.let{
           eaterDataManager.setUserCampaignParam(sid = it)
        }
        cid?.let{
            eaterDataManager.setUserCampaignParam(cid = it)
        }

    }
}