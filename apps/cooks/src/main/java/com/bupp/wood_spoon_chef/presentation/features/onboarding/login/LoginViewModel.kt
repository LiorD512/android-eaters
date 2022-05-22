package com.bupp.wood_spoon_chef.presentation.features.onboarding.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.analytics.ChefAnalyticsTracker
import com.bupp.wood_spoon_chef.analytics.TrackedArea
import com.bupp.wood_spoon_chef.analytics.event.AnalyticsEvent
import com.bupp.wood_spoon_chef.analytics.event.phone_number_verification.PhoneNumberVerificationOnNextClickEvent
import com.bupp.wood_spoon_chef.common.MTLogger
import com.bupp.wood_spoon_chef.di.abs.LiveEventData
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.data.remote.model.CountriesISO
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.data.repositories.MetaDataRepository
import com.bupp.wood_spoon_chef.data.repositories.UserRepository
import kotlinx.coroutines.launch


class LoginViewModel(
    private val userRepository: UserRepository,
    private val metaDataRepository: MetaDataRepository,
    private val chefAnalyticsTracker: ChefAnalyticsTracker
) : BaseViewModel() {

    var phone: String? = null
    private var phonePrefix: String? = null
    var code: String? = null

    val countryCodeEvent: MutableLiveData<CountriesISO> = MutableLiveData()
    val navigationEvent: MutableLiveData<NavigationEventType> = MutableLiveData()

    val phoneFieldErrorEvent: LiveEventData<Boolean> = LiveEventData()

    enum class NavigationEventType {
        OPEN_CODE_SCREEN,
        OPEN_MAIN_ACT,
        START_CREATE_ACCOUNT_ACTIVITY,
        CODE_RESENT,
    }

    fun onCountryCodeSelected(selected: CountriesISO) {
        countryCodeEvent.postValue(selected)
    }

    fun setUserPhone(phone: String) {
        this.phone = phone
    }

    fun getUserPhonePrefix(): String? {
        return this.phonePrefix
    }

    fun setUserPhonePrefix(prefix: String) {
        this.phonePrefix = prefix
    }

    fun setUserCode(code: String) {
        this.code = code
    }

    fun getCensoredPhone(): String {
        phonePrefix?.let {
            phone?.let {
                return "+$phonePrefix (xxx) xxx - ${it.takeLast(4)}"
            }
        }
        return " "
    }

    private fun directToCodeFrag() {
        navigationEvent.postValue(NavigationEventType.OPEN_CODE_SCREEN)
    }

    private fun validatePhoneData(): Boolean {
        var isValid = true
        if (phone.isNullOrEmpty() || phonePrefix.isNullOrEmpty()) {
            phoneFieldErrorEvent.postRawValue(true)
            isValid = false
        }
        return isValid
    }

    fun sendPhoneNumber() {
        if (validatePhoneData()) {
            progressData.startProgress()
            viewModelScope.launch {
                when (val response = userRepository.getCode(phonePrefix + phone)) {
                    is ResponseSuccess -> {
                        directToCodeFrag()
                    }
                    is ResponseError -> {
                        errorEvent.postRawValue(response.error)
                    }
                }
                progressData.endProgress()
            }
        }
    }

    fun resendCode() {
        if (validatePhoneData()) {
            progressData.startProgress()
            viewModelScope.launch {
                MTLogger.d("user resending code to phone:${phonePrefix + phone}}")
                when (val response = userRepository.getCode(phonePrefix + phone)) {
                    is ResponseSuccess -> {
                        navigationEvent.postValue(NavigationEventType.CODE_RESENT)
                    }
                    is ResponseError -> {
                        errorEvent.postRawValue(response.error)
                    }
                }
                progressData.endProgress()
            }
        }
    }

    //code verification methods
    val emptyCodeEvent = LiveEventData<Boolean>()
    fun sendPhoneAndCodeNumber() {
        if (!code.isNullOrEmpty()) {
            phone?.let { phone ->
                code?.let { code ->
                    progressData.startProgress()
                    viewModelScope.launch {
                        MTLogger.d("user sending code: phone:${phonePrefix + phone}, code:${code}")
                        when (val response = userRepository.sendCodeAndPhoneVerification(
                            phonePrefix + phone,
                            code
                        )) {
                            is ResponseSuccess -> {
                                metaDataRepository.initMetaData()
                                if (userRepository.isUserSignedUp()) {
                                    navigationEvent.postValue(NavigationEventType.OPEN_MAIN_ACT)
                                } else {
                                    navigationEvent.postValue(NavigationEventType.START_CREATE_ACCOUNT_ACTIVITY)
                                }
                            }
                            is ResponseError -> {
                                errorEvent.postRawValue(response.error)
                            }
                        }
                        progressData.endProgress()
                    }
                }
            }
        } else {
            emptyCodeEvent.postRawValue(true)
        }
    }

    fun trackAnalyticsEvent(analyticsEvent: AnalyticsEvent) {
        if (analyticsEvent.trackedArea == TrackedArea.PHONE_VERIFICATION) {
            when (analyticsEvent) {
                is PhoneNumberVerificationOnNextClickEvent -> {
                    chefAnalyticsTracker.trackEvent(
                        analyticsEvent.trackedEvent, mapOf(
                            "success" to analyticsEvent.isSuccess
                        )
                    )
                }
            }
        }
    }

}