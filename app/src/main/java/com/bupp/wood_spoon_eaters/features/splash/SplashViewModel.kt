package com.bupp.wood_spoon_eaters.features.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.fcm.FcmManager
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.features.new_order.service.EphemeralKeyProvider
import com.bupp.wood_spoon_eaters.managers.CampaignManager
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import kotlinx.coroutines.launch


class SplashViewModel(
    val eaterDataManager: EaterDataManager, private val userRepository: UserRepository, val metaDataRepository: MetaDataRepository, private val paymentManager: PaymentManager,
private val deviceDetailsManager: FcmManager, private val campaignManager: CampaignManager
)
    : ViewModel(), EphemeralKeyProvider.EphemeralKeyProviderListener {

    val splashEvent: LiveEventData<SplashEventType> = LiveEventData()

    enum class SplashEventType{
        SHOULD_UPDATE_VERSION,
        GO_TO_WELCOME,
        GO_TO_PHONE_VERIFICATION,
        GO_TO_CREATE_ACCOUNT,
        GOT_TO_MAIN
    }

    val errorEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()

    private var isUserExist = false // true if this is the user's first time in the app
    private var isUserSigned = false // true if users created his account
    private var isUserRegistered = false // true if user had only phone verification
    private var shouldUpdateVersion = false

    fun initAppSplashData(context: Context) {
        viewModelScope.launch {

            userRepository.initUserRepo()
            metaDataRepository.initMetaData()
//            campaignManager.fetchCampaigns()
            paymentManager.initPaymentManager(context)

            isUserExist = userRepository.isUserValid()
            isUserSigned = userRepository.isUserSignedUp()
            isUserRegistered = userRepository.isUserRegistered()
            shouldUpdateVersion = metaDataRepository.checkMinVersionFail()

            if(shouldUpdateVersion){
                //go to update
                splashEvent.postRawValue(SplashEventType.SHOULD_UPDATE_VERSION)
            }else if(isUserExist && isUserRegistered && isUserSigned){
                //go to main
                splashEvent.postRawValue(SplashEventType.GOT_TO_MAIN)
            }else if(isUserExist && isUserRegistered){
                //go to create account
                splashEvent.postRawValue(SplashEventType.GO_TO_CREATE_ACCOUNT)
            }else if(isUserExist){
                //go to phone verification
                splashEvent.postRawValue(SplashEventType.GO_TO_PHONE_VERIFICATION)
            }else{
                //go to welcome
                splashEvent.postRawValue(SplashEventType.GO_TO_WELCOME)
            }
        }
    }

    fun setUserReferralToken(token: String?) {
        campaignManager.setUserReferralToken(token = token)
    }

    fun initFCMAndRefreshToken() {
        deviceDetailsManager.refreshPushNotificationToken()
    }

}