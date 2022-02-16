package com.bupp.wood_spoon_eaters.features.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.fcm.FcmManager
import com.bupp.wood_spoon_eaters.managers.EventsManager
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import com.bupp.wood_spoon_eaters.model.CountriesISO
import com.bupp.wood_spoon_eaters.model.EaterRequest
import com.bupp.wood_spoon_eaters.model.ErrorEventType
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import kotlinx.coroutines.launch


class LoginViewModel(
    private val eventsManager: EventsManager,
    private val userRepository: UserRepository,
    private val flowEventsManager: FlowEventsManager,
    private val metaDataRepository: MetaDataRepository,
    private val deviceDetailsManager: FcmManager,
    private val paymentManager: PaymentManager
) : ViewModel() {

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

    fun directToPhoneFrag() {
        eventsManager.logEvent(Constants.EVENT_CLICK_GET_STARTED)
        navigationEvent.postRawValue(NavigationEventType.OPEN_PHONE_SCREEN)
    }

    private fun directToCodeFrag() {
        userData.postValue(getCensoredPhone())
        navigationEvent.postRawValue(NavigationEventType.OPEN_CODE_SCREEN)
    }

    //phone verification methods
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
                        Log.d("wowLoginVM", "NetworkError")
                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                        eventsManager.logEvent(Constants.EVENT_SEND_OTP, getSendOtpData(false))
                    }
                    UserRepository.UserRepoStatus.INVALID_PHONE -> {
                        Log.d("wowLoginVM", "GenericError")
                        errorEvents.postValue(ErrorEventType.INVALID_PHONE)
                        eventsManager.logEvent(Constants.EVENT_SEND_OTP, getSendOtpData(false))
                    }
                    UserRepository.UserRepoStatus.SUCCESS -> {
                        Log.d("wowLoginVM", "Success")
                        eventsManager.logEvent(Constants.EVENT_SEND_OTP, getSendOtpData(true))
                        directToCodeFrag()
                    }
                    else -> {
                        Log.d("wowLoginVM", "NetworkError")
                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                        eventsManager.logEvent(Constants.EVENT_SEND_OTP, getSendOtpData(false))
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
                        Log.d("wowLoginVM", "NetworkError")
                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                    }
                    UserRepository.UserRepoStatus.INVALID_PHONE -> {
                        Log.d("wowLoginVM", "GenericError")
                        errorEvents.postValue(ErrorEventType.INVALID_PHONE)
                    }
                    UserRepository.UserRepoStatus.SUCCESS -> {
                        Log.d("wowLoginVM", "Success")
                        navigationEvent.postRawValue(NavigationEventType.CODE_RESENT)
                    }
                    else -> {
                        Log.d("wowLoginVM", "NetworkError")
                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                    }
                }
                progressData.endProgress()
            }
        }
    }

    //code verification methods

    fun sendPhoneAndCodeNumber(context: Context) {
        if (!code.isNullOrEmpty()) {
            phone?.let { phone ->
                progressData.startProgress()
                viewModelScope.launch {
                    MTLogger.d("user sending code: phone:${phonePrefix + phone}, code:${code}")
                    val userRepoResult = userRepository.sendCodeAndPhoneVerification(phonePrefix!!+phone, code!!)
                    when (userRepoResult.type) {
                        UserRepository.UserRepoStatus.SERVER_ERROR -> {
                            Log.d("wowLoginVM", "sendPhoneAndCodeNumber - NetworkError")
                            errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                            eventsManager.logEvent(Constants.EVENT_VERIFY_OTP, getSendOtpData(false))
                        }
                        UserRepository.UserRepoStatus.WRONG_PASSWORD -> {
                            Log.d("wowLoginVM", "sendPhoneAndCodeNumber - GenericError")
                            errorEvents.postValue(ErrorEventType.WRONG_PASSWORD)
                            eventsManager.logEvent(Constants.EVENT_VERIFY_OTP, getSendOtpData(false))
                        }
                        UserRepository.UserRepoStatus.SUCCESS -> {
                            Log.d("wowLoginVM", "sendPhoneAndCodeNumber - Success")
                            metaDataRepository.initMetaData()
                            if (userRepository.isUserSignedUp()) {
                                paymentManager.initPaymentManager(context)
                                navigationEvent.postRawValue(NavigationEventType.OPEN_MAIN_ACT)
                                eventsManager.logEvent(Constants.EVENT_ON_EXISTING_USER_LOGIN_SUCCESS)
                            } else {
                                navigationEvent.postRawValue(NavigationEventType.OPEN_SIGNUP_SCREEN)
                            }
                            eventsManager.logEvent(Constants.EVENT_VERIFY_OTP, getSendOtpData(true))
                        }
                        else -> {
                            Log.d("wowLoginVM", "NetworkError")
                            errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                            eventsManager.logEvent(Constants.EVENT_VERIFY_OTP, getSendOtpData(false))
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
                    Log.d("wowLoginVM", "NetworkError")
                    errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                    eventsManager.logEvent(Constants.EVENT_CREATE_ACCOUNT, getCreateAccountEventData(false))
                }
                UserRepository.UserRepoStatus.SOMETHING_WENT_WRONG -> {
                    Log.d("wowLoginVM", "GenericError")
                    errorEvents.postValue(ErrorEventType.SOMETHING_WENT_WRONG)
                    eventsManager.logEvent(Constants.EVENT_CREATE_ACCOUNT, getCreateAccountEventData(false))
                }
                UserRepository.UserRepoStatus.SUCCESS -> {
                    Log.d("wowLoginVM", "Success")
                    val eater = userRepoResult.eater
                    paymentManager.initPaymentManager(context)
                    deviceDetailsManager.refreshPushNotificationToken()
                    navigationEvent.postRawValue(NavigationEventType.OPEN_MAIN_ACT)
                    eventsManager.logEvent(Constants.EVENT_ON_EXISTING_USER_LOGIN_SUCCESS)
//                    eventsManager.sendRegistrationCompletedEvent()
                    eventsManager.logEvent(Constants.EVENT_CREATE_ACCOUNT, getCreateAccountEventData(true, eater?.id))
                }
                else -> {
                    Log.d("wowLoginVM", "NetworkError")
                    errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                    eventsManager.logEvent(Constants.EVENT_CREATE_ACCOUNT, getCreateAccountEventData(false))
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

    fun logPageEvent(eventType: FlowEventsManager.FlowEvents) {
        flowEventsManager.logPageEvent(eventType)
    }


}