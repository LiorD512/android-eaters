package com.bupp.wood_spoon_eaters.features.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import com.bupp.wood_spoon_eaters.fcm.FcmManager
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.base_repos.UserRepositoryImpl
import kotlinx.coroutines.launch


class LoginViewModel(
    val apiService: UserRepositoryImpl,
    val userRepository: UserRepository,
    val eaterDataManager: EaterDataManager,
    val metaDataRepository: MetaDataRepository,
    val deviceDetailsManager: FcmManager,
    val paymentManager: PaymentManager
) : ViewModel() {

    var phone: String? = null
    var phonePrefix: String? = null
    var code: String? = null
    var privacyPolicyCb: Boolean = false

    val navigationEvent: MutableLiveData<NavigationEventType> = MutableLiveData()
    val countryCodeEvent: MutableLiveData<CountriesISO> = MutableLiveData()
    val errorEvents: MutableLiveData<ErrorEventType> = MutableLiveData()
    val progressData = ProgressData()

    val phoneFieldErrorEvent: MutableLiveData<ErrorEventType> = MutableLiveData()
    val phoneCbFieldErrorEvent: MutableLiveData<ErrorEventType> = MutableLiveData()

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
        return "error on getting phone"
    }


    fun directToPhoneFrag() {
        navigationEvent.postValue(NavigationEventType.OPEN_PHONE_SCREEN)
    }

    private fun directToCodeFrag() {
        navigationEvent.postValue(NavigationEventType.OPEN_CODE_SCREEN)
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
                    }
                    UserRepository.UserRepoStatus.INVALID_PHONE -> {
                        Log.d("wowLoginVM", "GenericError")
                        errorEvents.postValue(ErrorEventType.INVALID_PHONE)
                    }
                    UserRepository.UserRepoStatus.SUCCESS -> {
                        Log.d("wowLoginVM", "Success")
                        directToCodeFrag()
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

    fun resendCode() {
        if (validatePhoneData()) {
            progressData.startProgress()
            viewModelScope.launch {
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
                        navigationEvent.postValue(NavigationEventType.CODE_RESENT)
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

    fun sendPhoneAndCodeNumber() {
        if (!code.isNullOrEmpty()) {
            phone?.let { phone ->
                progressData.startProgress()
                viewModelScope.launch {
                    val userRepoResult = userRepository.sendCodeAndPhoneVerification(phonePrefix!!+phone, code!!)
                    when (userRepoResult.type) {
                        UserRepository.UserRepoStatus.SERVER_ERROR -> {
                            Log.d("wowLoginVM", "sendPhoneAndCodeNumber - NetworkError")
                            errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                        }
                        UserRepository.UserRepoStatus.WRONG_PASSWORD -> {
                            Log.d("wowLoginVM", "sendPhoneAndCodeNumber - GenericError")
                            errorEvents.postValue(ErrorEventType.WRONG_PASSWORD)
                        }
                        UserRepository.UserRepoStatus.SUCCESS -> {
                            Log.d("wowLoginVM", "sendPhoneAndCodeNumber - Success")
                            metaDataRepository.initMetaData()
                            if (userRepository.isUserSignedUp()) {
                                navigationEvent.postValue(NavigationEventType.OPEN_MAIN_ACT)
                            } else {
                                navigationEvent.postValue(NavigationEventType.OPEN_SIGNUP_SCREEN)
                            }
                        }
                        else -> {
                            Log.d("wowLoginVM", "NetworkError")
                            errorEvents.postValue(ErrorEventType.SERVER_ERROR)
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
                }
                UserRepository.UserRepoStatus.SOMETHING_WENT_WRONG -> {
                    Log.d("wowLoginVM", "GenericError")
                    errorEvents.postValue(ErrorEventType.SOMETHING_WENT_WRONG)
                }
                UserRepository.UserRepoStatus.SUCCESS -> {
                    Log.d("wowLoginVM", "Success")
                    paymentManager.initPaymentManager(context)
                    deviceDetailsManager.refreshPushNotificationToken()
                    navigationEvent.postValue(NavigationEventType.OPEN_MAIN_ACT)
                }
                else -> {
                    Log.d("wowLoginVM", "NetworkError")
                    errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                }
            }
            progressData.endProgress()
        }
    }

//    fun getCuisineList(): ArrayList<SelectableIcon> {
//        return metaDataRepository.getCuisineListSelectableIcons()
//    }
//
//    fun getDietaryList(): ArrayList<SelectableIcon> {
//        return metaDataRepository.getDietaryList()
//    }



    //Location Permission methods
//    private val locationData = LocationLiveData(applicationContext)
//    fun getLocationData() = locationData




}