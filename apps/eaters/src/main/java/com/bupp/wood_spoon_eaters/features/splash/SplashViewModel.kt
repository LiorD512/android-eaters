package com.bupp.wood_spoon_eaters.features.splash

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.domain.FeatureFlagNewAuthUseCase
import com.bupp.wood_spoon_eaters.fcm.FcmManager
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EphemeralKeyProvider
import com.bupp.wood_spoon_eaters.managers.CampaignManager
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import com.bupp.wood_spoon_eaters.repositories.*
import kotlinx.coroutines.launch


class SplashViewModel(
    application: Application,
    val eaterDataManager: EaterDataManager, private val userRepository: UserRepository, val metaDataRepository: MetaDataRepository,
    private val appSettingsRepository: AppSettingsRepository, private val paymentManager: PaymentManager,
    private val deviceDetailsManager: FcmManager, private val campaignManager: CampaignManager,
    private val featureFlagNewAuthUseCase: FeatureFlagNewAuthUseCase
) : AndroidViewModel(application), EphemeralKeyProvider.EphemeralKeyProviderListener {

    val splashEvent: LiveEventData<SplashEventType> = LiveEventData()

    enum class SplashEventType {
        SHOULD_UPDATE_VERSION,
        GO_TO_WELCOME,
        GO_TO_PHONE_VERIFICATION,
        GO_TO_CREATE_ACCOUNT,
        GOT_TO_MAIN
    }

    val errorEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()

    fun initAppSplashData() {
        viewModelScope.launch {

            userRepository.initUserRepo()
            metaDataRepository.initMetaData()
            appSettingsRepository.initAppSettings()
            paymentManager.initPaymentManager(getApplication())

            // true if this is the user's first time in the app
            val isUserExist = userRepository.isUserValid()
            // true if users created his account
            val isUserSigned = userRepository.isUserSignedUp()
            // true if user had only phone verification
            val isUserRegistered = userRepository.isUserRegistered()
            val shouldUpdateVersion = appSettingsRepository.checkMinVersionFail()
            val isNewAuthFlowEnabled = featureFlagNewAuthUseCase.execute(null)

            if (shouldUpdateVersion) {
                //go to update
                splashEvent.postRawValue(SplashEventType.SHOULD_UPDATE_VERSION)
            } else if(isNewAuthFlowEnabled && isUserExist) {
                //go to main
                splashEvent.postRawValue(SplashEventType.GOT_TO_MAIN)
            } else if (isUserExist && isUserRegistered && isUserSigned) {
                //go to main
                splashEvent.postRawValue(SplashEventType.GOT_TO_MAIN)
            } else if (isUserExist && isUserRegistered) {
                //go to create account
                splashEvent.postRawValue(SplashEventType.GO_TO_CREATE_ACCOUNT)
            } else if (isUserExist) {
                //go to phone verification
                splashEvent.postRawValue(SplashEventType.GO_TO_PHONE_VERIFICATION)
            } else {
                //go to welcome
                splashEvent.postRawValue(SplashEventType.GO_TO_WELCOME)
            }
        }
    }

    fun setUserReferralToken(token: String?) {
        viewModelScope.launch {
            campaignManager.setUserReferralToken(token = token)
        }
    }

    fun initFCMAndRefreshToken() {
        deviceDetailsManager.refreshPushNotificationToken()
    }

}