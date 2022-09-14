package com.bupp.wood_spoon_eaters.features.login

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.domain.FeatureFlagNewAuthUseCase
import com.bupp.wood_spoon_eaters.fcm.FcmManager
import com.bupp.wood_spoon_eaters.managers.EatersAnalyticsTracker
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import com.bupp.wood_spoon_eaters.model.CountriesISO
import com.bupp.wood_spoon_eaters.model.EaterRequest
import com.bupp.wood_spoon_eaters.model.ErrorEventType
import com.bupp.wood_spoon_eaters.repositories.*
import kotlinx.coroutines.launch

class LoginViewModel(
    private val eatersAnalyticsTracker: EatersAnalyticsTracker,
    private val userRepository: UserRepository,
    private val flowEventsManager: FlowEventsManager,
    private val metaDataRepository: MetaDataRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val deviceDetailsManager: FcmManager,
    private val paymentManager: PaymentManager,
    featureFlagNewAuthUseCase: FeatureFlagNewAuthUseCase
) : ViewModel() {

    private val isNewAuthFlowEnabled = featureFlagNewAuthUseCase.execute(null)

    var phone: String? = null
    var phonePrefix: String? = null
    var code: String? = null

    val navigationEvent: LiveEventData<NavigationEventType> = LiveEventData()
    val countryCodeEvent: MutableLiveData<CountriesISO> = MutableLiveData()
    val errorEvents: MutableLiveData<ErrorEventType> = MutableLiveData()
    val userData: MutableLiveData<String?> = MutableLiveData()
    val progressData = ProgressData()

    val phoneFieldErrorEvent: MutableLiveData<ErrorEventType> = MutableLiveData()

    enum class NavigationEventType {
        OPEN_WEB_FLOW,
        OPEN_PHONE_SCREEN,
        OPEN_CODE_SCREEN,
        OPEN_MAIN_ACT,
        OPEN_SIGNUP_SCREEN,
        CODE_RESENT,
    }

    fun onCountryCodeSelected(selected: CountriesISO) {
        countryCodeEvent.postValue(selected)
    }

    fun setUserPhone(phone: String) {
        this.phone = phone
    }
    fun setUserPhonePrefix(prefix: String) {
        this.phonePrefix = prefix
    }

    fun setUserCode(code: String) {
        this.code = code
    }

    fun getCensoredPhone(): String{
        phonePrefix?.let{
            phone?.let{
                return "$phonePrefix (xxx) xxx - ${it.takeLast(4)}"
            }
        }
        return " "
    }

    fun onStartLoginClicked() {
        eatersAnalyticsTracker.logEvent(Constants.EVENT_CLICK_GET_STARTED)
        if(isNewAuthFlowEnabled) {
            directToWebLogin()
        }else{
            directToPhoneFrag()
        }
    }

    private fun directToWebLogin() {
        navigationEvent.postRawValue(NavigationEventType.OPEN_WEB_FLOW)
    }

    private fun directToPhoneFrag() {
        navigationEvent.postRawValue(NavigationEventType.OPEN_PHONE_SCREEN)
    }

    private fun directToCodeFrag() {
        userData.postValue(getCensoredPhone())
        navigationEvent.postRawValue(NavigationEventType.OPEN_CODE_SCREEN)
    }

    private fun validatePhoneData(): Boolean {
        var isValid = true
        if (phone.isNullOrEmpty() || phonePrefix.isNullOrEmpty()) {
            phoneFieldErrorEvent.postValue(ErrorEventType.PHONE_EMPTY)
            isValid = false
        }
        return isValid
    }

    fun sendPhoneNumber() {
        if (validatePhoneData()) {
            progressData.startProgress()
            viewModelScope.launch {
                val userRepoResult = userRepository.sendPhoneVerification(phonePrefix!!+phone!!)
                when (userRepoResult.type) {
                    UserRepository.UserRepoStatus.SERVER_ERROR -> {
                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                        eatersAnalyticsTracker.logEvent(Constants.EVENT_SEND_OTP, getSendOtpData(false))
                    }
                    UserRepository.UserRepoStatus.INVALID_PHONE -> {
                        errorEvents.postValue(ErrorEventType.INVALID_PHONE)
                        eatersAnalyticsTracker.logEvent(Constants.EVENT_SEND_OTP, getSendOtpData(false))
                    }
                    UserRepository.UserRepoStatus.SUCCESS -> {
                        eatersAnalyticsTracker.logEvent(Constants.EVENT_SEND_OTP, getSendOtpData(true))
                        directToCodeFrag()
                    }
                    else -> {
                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                        eatersAnalyticsTracker.logEvent(Constants.EVENT_SEND_OTP, getSendOtpData(false))
                    }
                }
                progressData.endProgress()
            }
        }
    }

    private fun getSendOtpData(isSuccess: Boolean): Map<String, String> {
        val data = mutableMapOf<String, String>()
        data["number"] = this.phonePrefix+this.phone
        data["success"] = isSuccess.toString()
        return data
    }

    fun resendCode() {
        if (validatePhoneData()) {
            progressData.startProgress()
            viewModelScope.launch {
                MTLogger.d("user resending code to phone:${phonePrefix + phone}}")
                val userRepoResult = userRepository.sendPhoneVerification(phonePrefix!!+phone!!)
                when (userRepoResult.type) {
                    UserRepository.UserRepoStatus.SERVER_ERROR -> {
                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                    }
                    UserRepository.UserRepoStatus.INVALID_PHONE -> {
                        errorEvents.postValue(ErrorEventType.INVALID_PHONE)
                    }
                    UserRepository.UserRepoStatus.SUCCESS -> {
                        navigationEvent.postRawValue(NavigationEventType.CODE_RESENT)
                    }
                    else -> {
                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                    }
                }
                progressData.endProgress()
            }
        }
    }

    fun sendPhoneAndCodeNumber(context: Context) {
        if (!code.isNullOrEmpty()) {
            phone?.let { phone ->
                progressData.startProgress()
                viewModelScope.launch {
                    MTLogger.d("user sending code: phone:${phonePrefix + phone}, code:${code}")
                    val userRepoResult = userRepository.sendCodeAndPhoneVerification(phonePrefix!!+phone, code!!)
                    when (userRepoResult.type) {
                        UserRepository.UserRepoStatus.SERVER_ERROR -> {
                            errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                            eatersAnalyticsTracker.logEvent(Constants.EVENT_VERIFY_OTP, getSendOtpData(false))
                        }
                        UserRepository.UserRepoStatus.WRONG_PASSWORD -> {
                            errorEvents.postValue(ErrorEventType.WRONG_PASSWORD)
                            eatersAnalyticsTracker.logEvent(Constants.EVENT_VERIFY_OTP, getSendOtpData(false))
                        }
                        UserRepository.UserRepoStatus.SUCCESS -> {
                            metaDataRepository.initMetaData()
                            appSettingsRepository.initAppSettings()
                            if (userRepository.isUserSignedUp()) {
                                paymentManager.initPaymentManager(context)
                                navigationEvent.postRawValue(NavigationEventType.OPEN_MAIN_ACT)
                                eatersAnalyticsTracker.logEvent(Constants.EVENT_ON_EXISTING_USER_LOGIN_SUCCESS)
                            } else {
                                navigationEvent.postRawValue(NavigationEventType.OPEN_SIGNUP_SCREEN)
                            }
                            eatersAnalyticsTracker.logEvent(Constants.EVENT_VERIFY_OTP, getSendOtpData(true))
                        }
                        else -> {
                            errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                            eatersAnalyticsTracker.logEvent(Constants.EVENT_VERIFY_OTP, getSendOtpData(false))
                        }
                    }
                    progressData.endProgress()
                }
            }
        } else {
            errorEvents.postValue(ErrorEventType.CODE_EMPTY)
        }
    }

    fun updateClientAccount(
        context: Context,
        firstName: String,
        lastName: String,
        email: String
    ) {
        progressData.startProgress()

        val eater = EaterRequest()
        eater.firstName = firstName
        eater.lastName = lastName
        eater.email = email

        postClient(context, eater)

    }

    private fun postClient(context: Context, eater: EaterRequest) {
        viewModelScope.launch {
            val userRepoResult = userRepository.updateEater(eater)
            when (userRepoResult.type) {
                UserRepository.UserRepoStatus.SERVER_ERROR -> {
                    errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                    eatersAnalyticsTracker.logEvent(Constants.EVENT_CREATE_ACCOUNT, getCreateAccountEventData(false))
                }
                UserRepository.UserRepoStatus.SOMETHING_WENT_WRONG -> {
                    errorEvents.postValue(ErrorEventType.SOMETHING_WENT_WRONG)
                    eatersAnalyticsTracker.logEvent(Constants.EVENT_CREATE_ACCOUNT, getCreateAccountEventData(false))
                }
                UserRepository.UserRepoStatus.SUCCESS -> {
                    val eater = userRepoResult.eater
                    paymentManager.initPaymentManager(context)
                    deviceDetailsManager.refreshPushNotificationToken()
                    navigationEvent.postRawValue(NavigationEventType.OPEN_MAIN_ACT)
                    eatersAnalyticsTracker.logEvent(Constants.EVENT_ON_EXISTING_USER_LOGIN_SUCCESS)
                    eatersAnalyticsTracker.logEvent(Constants.EVENT_CREATE_ACCOUNT, getCreateAccountEventData(true, eater?.id))
                }
                else -> {
                    errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                    eatersAnalyticsTracker.logEvent(Constants.EVENT_CREATE_ACCOUNT, getCreateAccountEventData(false))
                }
            }
            progressData.endProgress()
        }
    }

    private fun getCreateAccountEventData(isSuccess: Boolean, userId: Long? = null): Map<String, String> {
        val data = mutableMapOf("success" to if(isSuccess) "true" else "false")
        data["user_id"] = userId.toString()
        return data
    }

    fun trackPageEvent(eventType: FlowEventsManager.FlowEvents) {
        flowEventsManager.trackPageEvent(eventType)
    }
}