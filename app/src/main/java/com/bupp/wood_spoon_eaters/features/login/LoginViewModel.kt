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
import com.bupp.wood_spoon_eaters.managers.location.LocationLiveData
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.test.RepositoryImpl
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.coroutines.launch
import java.util.ArrayList


class LoginViewModel(
    private val applicationContext: Context,
    val apiService: RepositoryImpl,
    val userRepository: UserRepository,
    val eaterDataManager: EaterDataManager,
    val metaDataRepository: MetaDataRepository,
    val deviceDetailsManager: FcmManager,
    val paymentManager: PaymentManager
) : ViewModel() {

    var phone: String? = null
    var code: String? = null
    var privacyPolicyCb: Boolean = false

    val navigationEvent: MutableLiveData<NavigationEventType> = MutableLiveData()
    val errorEvents: MutableLiveData<ErrorEventType> = MutableLiveData()
    val progressData = ProgressData()

    val phoneFieldErrorEvent: MutableLiveData<ErrorEventType> = MutableLiveData()
    val phoneCbFieldErrorEvent: MutableLiveData<ErrorEventType> = MutableLiveData()

    enum class NavigationEventType {
        OPEN_PHONE_SCREEN,
        OPEN_CODE_SCREEN,
        OPEN_MAIN_ACT,
        OPEN_LOCATION_PERMISSION_FROM_CODE,
        OPEN_LOCATION_PERMISSION_FROM_SIGNUP,
        OPEN_SIGNUP_SCREEN,
        CODE_RESENT,
    }

    enum class ErrorEventType {
        PHONE_EMPTY,
        CODE_EMPTY,
        CB_REQUIRED,
        INVALID_PHONE,
        WRONG_PASSWORD,
        SERVER_ERROR,
        SOMETHING_WENT_WRONG
    }

    fun setUserPhone(phone: String) {
        this.phone = phone
    }

    fun setUserCode(code: String) {
        this.code = code
    }

    fun setPhoneCb(isChecked: Boolean) {
        privacyPolicyCb = isChecked
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
        if (phone.isNullOrEmpty()) {
            phoneFieldErrorEvent.postValue(ErrorEventType.PHONE_EMPTY)
            isValid = false
        }
        return isValid
    }

    fun sendPhoneNumber() {
        if (validatePhoneData()) {
            progressData.startProgress()
            viewModelScope.launch {
                val userRepoResult = userRepository.sendPhoneVerification(phone!!)
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
                val userRepoResult = userRepository.sendPhoneVerification(phone!!)
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
                    val userRepoResult = userRepository.sendCodeAndPhoneVerification(phone, code!!)
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
                                navigationEvent.postValue(NavigationEventType.OPEN_LOCATION_PERMISSION_FROM_CODE)
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
        fullName: String,
        email: String,
        cuisineIcons: ArrayList<SelectableIcon>?,
        dietaryIcons: ArrayList<SelectableIcon>?
    ) {
        progressData.startProgress()
        val firstAndLast: Pair<String, String> = Utils.getFirstAndLastNames(fullName)
        val firstName = firstAndLast.first
        val lastName = firstAndLast.second

        val eater = EaterRequest()
        eater.firstName = firstName
        eater.lastName = lastName
        eater.email = email

        val arrayOfCuisinesIds = arrayListOf<Int>()
        val arrayOfDietsIds = arrayListOf<Int>()

        if (!cuisineIcons.isNullOrEmpty()) {
            for (cuisine in cuisineIcons) {
                arrayOfCuisinesIds.add(cuisine.id.toInt())
            }
        }
        if (!dietaryIcons.isNullOrEmpty()) {
            for (diet in dietaryIcons) {
                arrayOfDietsIds.add(diet.id.toInt())
            }
        }

        eater.cuisineIds = arrayOfCuisinesIds
        eater.dietIds = arrayOfDietsIds
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
                    navigationEvent.postValue(NavigationEventType.OPEN_LOCATION_PERMISSION_FROM_SIGNUP)
                }
                else -> {
                    Log.d("wowLoginVM", "NetworkError")
                    errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                }
            }
            progressData.endProgress()
        }
    }

    fun getCuisineList(): ArrayList<SelectableIcon> {
        return metaDataRepository.getCuisineListSelectableIcons()
    }

    fun getDietaryList(): ArrayList<SelectableIcon> {
        return metaDataRepository.getDietaryList()
    }

    //Location Permission methods
//    private val locationData = LocationLiveData(applicationContext)
//    fun getLocationData() = locationData




}