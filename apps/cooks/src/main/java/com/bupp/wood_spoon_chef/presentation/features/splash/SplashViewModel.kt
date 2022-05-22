package com.bupp.wood_spoon_chef.presentation.features.splash

import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.analytics.ChefAnalyticsTracker
import com.bupp.wood_spoon_chef.analytics.TrackedArea
import com.bupp.wood_spoon_chef.analytics.event.AnalyticsEvent
import com.bupp.wood_spoon_chef.di.abs.LiveEventData
import com.bupp.wood_spoon_chef.fcm.FcmManager
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.data.repositories.MetaDataRepository
import com.bupp.wood_spoon_chef.data.repositories.UserRepository
import kotlinx.coroutines.launch

class SplashViewModel(
    private val userRepository: UserRepository,
    private val metaDataRepository: MetaDataRepository,
    private val deviceDetailsManager: FcmManager,
    private val chefAnalyticsTracker: ChefAnalyticsTracker
) : BaseViewModel() {

    val splashEvent: LiveEventData<SplashEventType> = LiveEventData()

    enum class SplashEventType {
        SHOULD_UPDATE_VERSION,
        GO_TO_WELCOME,
        GOT_TO_MAIN,
        START_LOGIN_ACTIVITY,
        START_CREATE_ACCOUNT_ACTIVITY,
    }

    private var isUserExist = false // true if this is the user's first time in the app
    private var isUserSigned = false // true if users created his account
    private var isUserRegistered = false // true if user had only phone verification
    private var shouldUpdateVersion = false

    fun initAppSplashData() {
        viewModelScope.launch {

            userRepository.initUserRepo()
            metaDataRepository.initMetaData()

            isUserExist = userRepository.isUserValid()
            isUserSigned = userRepository.isUserSignedUp()
            isUserRegistered = userRepository.isUserRegistered()
            shouldUpdateVersion = metaDataRepository.checkMinVersion()

            if (shouldUpdateVersion) {
                //go to update
                splashEvent.postRawValue(SplashEventType.SHOULD_UPDATE_VERSION)
            } else if (isUserExist && isUserRegistered && isUserSigned) {
                //go to main
                deviceDetailsManager.refreshPushNotificationToken()
                splashEvent.postRawValue(SplashEventType.GOT_TO_MAIN)
            } else {
                //go to welcome
                splashEvent.postRawValue(SplashEventType.GO_TO_WELCOME)
            }
        }
    }

    fun onWelcomeClick() {
        val isUserExist = userRepository.isUserValid()
        val isUserRegistered = userRepository.isUserRegistered()
        if (isUserExist && isUserRegistered) {
            //go to create account activity
            splashEvent.postRawValue(SplashEventType.START_CREATE_ACCOUNT_ACTIVITY)
        } else {
            //go to welcome screen
            splashEvent.postRawValue(SplashEventType.START_LOGIN_ACTIVITY)
        }
    }

    fun trackAnalyticsEvent(analyticsEvent: AnalyticsEvent) {
        if (analyticsEvent.trackedArea == TrackedArea.ONBOARDING) {
            chefAnalyticsTracker.trackEvent(analyticsEvent.trackedEvent)
        }
    }
}


