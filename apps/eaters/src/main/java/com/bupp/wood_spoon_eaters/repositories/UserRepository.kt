package com.bupp.wood_spoon_eaters.repositories

import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import com.bupp.wood_spoon_eaters.managers.GlobalErrorManager
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import com.bupp.wood_spoon_eaters.managers.location.LocationManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiSettings
import com.bupp.wood_spoon_eaters.network.base_repos.UserRepositoryImpl
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import com.eatwoodspoon.android_utils.flows.toImmutable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import kotlin.Exception

class UserRepository(
    private val apiService: UserRepositoryImpl,
    private val apiSettings: ApiSettings,
    private val locationManager: LocationManager,
    private val paymentManager: PaymentManager,
    private val globalErrorManager: GlobalErrorManager,
    private val context: Context
) {

    private val userRepositoryJob: Job = Job()
    private val userRepositoryScope: CoroutineScope = CoroutineScope(Dispatchers.Default + userRepositoryJob)

    private val _currentEaterFlow = MutableStateFlow<Eater?>(null)
    val currentEaterFlow = _currentEaterFlow.toImmutable()

    private var currentUser: Eater?
        set(value) {
            _currentEaterFlow.value = value
        }
        get() = _currentEaterFlow.value

    data class UserRepoResult(
        val type: UserRepoStatus,
        val eater: Eater? = null,
        val errorMessage: String? = null
    )

    @Keep
    enum class UserRepoStatus {
        SUCCESS,
        LOGGED_OUT,
        INVALID_PHONE,
        WRONG_PASSWORD,
        SOMETHING_WENT_WRONG,
        SERVER_ERROR,
        WS_ERROR
    }

    init {
        userRepositoryScope.launch {
            currentEaterFlow.map { it?.id }.distinctUntilChanged().collect {
                onUserChanged()
            }
        }
    }

    suspend fun initUserRepo() {
        val result = withContext(Dispatchers.IO) {
            apiService.getMe()
        }
        result.let {
            when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG, "initUserRepo - NetworkError")
                    this.currentUser = null
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG, "initUserRepo - GenericError")
                    this.currentUser = null
                }
                is ResultHandler.Success -> {
                    Log.d(TAG, "initUserRepo - Success")
                    this.currentUser = result.value.data?.copy()
                }
                is ResultHandler.WSCustomError -> {
                }
            }
        }
    }

    fun isUserValid(): Boolean {
        return this.currentUser != null
    }

    fun isUserRegistered(): Boolean {
        return apiSettings.isRegistered()
    }

    fun isUserSignedUp(): Boolean {
        return isUserValid() && !this.currentUser!!.email.isNullOrEmpty()
    }

    suspend fun sendPhoneVerification(phone: String): UserRepoResult {
        val result = withContext(Dispatchers.IO) {
//            baseApiService.makeParamCall<String>(BaseApiService.EndPoint.GET_CODE, [Pair("phone", phone)])
            apiService.getCode(phone)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG, "sendPhoneVerification - NetworkError")
                    UserRepoResult(UserRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG, "sendPhoneVerification - GenericError")
                    UserRepoResult(UserRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG, "sendPhoneVerification - Success")
                    UserRepoResult(UserRepoStatus.SUCCESS)
                }
                is ResultHandler.WSCustomError -> {
                    UserRepoResult(
                        UserRepoStatus.INVALID_PHONE,
                        errorMessage = result.errors?.firstOrNull().toString()
                    )
                }
            }
        }
    }

    suspend fun sendCodeAndPhoneVerification(phone: String, code: String): UserRepoResult {
        val result = withContext(Dispatchers.IO) {
            apiService.validateCode(phone, code)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG, "sendCodeAndPhoneVerification - NetworkError")
                    UserRepoResult(UserRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG, "sendCodeAndPhoneVerification - GenericError")
                    UserRepoResult(UserRepoStatus.WRONG_PASSWORD)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG, "sendCodeAndPhoneVerification - Success")
                    val eater = result.value.data
                    this.currentUser = eater
                    UserRepoResult(UserRepoStatus.SUCCESS, this.currentUser)
                }
                is ResultHandler.WSCustomError -> {
                    UserRepoResult(UserRepoStatus.WRONG_PASSWORD)
                }
            }
        }
    }

    suspend fun updateEater(eater: EaterRequest): UserRepoResult {
        val result = withContext(Dispatchers.IO) {
            apiService.postMe(eater)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG, "updateEater - NetworkError")
                    UserRepoResult(UserRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG, "updateEater - GenericError")
                    UserRepoResult(UserRepoStatus.SOMETHING_WENT_WRONG)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG, "updateEater - Success")
                    val eater = result.value.data
                    this.currentUser = eater
                    UserRepoResult(UserRepoStatus.SUCCESS, this.currentUser)
                }
                is ResultHandler.WSCustomError -> {
                    UserRepoResult(UserRepoStatus.SOMETHING_WENT_WRONG)
                }
            }
        }
    }

    suspend fun updateNotificationsGroup(notifications: List<Long>): UserRepoResult {
        var notificationsData: List<Long>? = notifications
        if (notifications.isEmpty()) {
            notificationsData = null
        }
        val result = withContext(Dispatchers.IO) {
            apiService.updateNotificationGroup(notificationsData)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG, "updateNotificationsGroup - NetworkError")
                    UserRepoResult(UserRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG, "updateNotificationsGroup - GenericError")
                    UserRepoResult(UserRepoStatus.SOMETHING_WENT_WRONG)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG, "updateNotificationsGroup - Success")
                    val eater = result.value.data
                    this.currentUser = eater
                    UserRepoResult(UserRepoStatus.SUCCESS, this.currentUser)
                }
                is ResultHandler.WSCustomError -> {
                    UserRepoResult(UserRepoStatus.SOMETHING_WENT_WRONG)
                }
            }
        }
    }


    //Address
    suspend fun addNewAddress(addressRequest: AddressRequest): UserRepoResult? {
        val result = withContext(Dispatchers.IO) {
            apiService.postNewAddress(addressRequest)
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG, "addNewAddress - NetworkError")
                    UserRepoResult(UserRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG, "addNewAddress - GenericError")
                    UserRepoResult(UserRepoStatus.SOMETHING_WENT_WRONG)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG, "addNewAddress - Success")
                    initUserRepo()
                    UserRepoResult(UserRepoStatus.SUCCESS, this.currentUser)
                }
                is ResultHandler.WSCustomError -> {
                    result.errors?.let {
                        if (it.isNotEmpty()) {
                            val error = it[0]
                            globalErrorManager.postError(
                                GlobalErrorManager.GlobalErrorType.WS_ERROR,
                                error
                            )

                        }
                    }
                    null
                }
            }
        }
    }

    suspend fun deleteAddress(address: Address): UserRepoResult {
        try {
            val result = withContext(Dispatchers.IO) {
                address.id?.let { apiService.deleteAddress(it) }
            }
            result.let {
                return when (result) {
                    is ResultHandler.NetworkError -> {
                        Log.d(TAG, "deleteAddress - NetworkError")
                        UserRepoResult(UserRepoStatus.SERVER_ERROR)
                    }
                    is ResultHandler.GenericError -> {
                        Log.d(TAG, "deleteAddress - GenericError")
                        UserRepoResult(UserRepoStatus.SOMETHING_WENT_WRONG)
                    }
                    is ResultHandler.Success -> {
                        Log.d(TAG, "deleteAddress - Success")
                        initUserRepo()
                        UserRepoResult(UserRepoStatus.SUCCESS, this.currentUser)
                    }
                    is ResultHandler.WSCustomError -> {
                        UserRepoResult(UserRepoStatus.SOMETHING_WENT_WRONG)
                    }
                    else -> {
                        Log.d(TAG, "deleteAddress - addressId is null")
                        UserRepoResult(UserRepoStatus.SOMETHING_WENT_WRONG)
                    }
                }
            }
        } catch (ex: Exception) {
            Log.d(TAG, "deleteAddress - addressId is null")
        }
        return UserRepoResult(UserRepoStatus.SOMETHING_WENT_WRONG)
    }

    suspend fun deleteAccount(): UserRepoResult {
        val result = withContext(Dispatchers.IO) {
            apiService.deleteMe()
        }
        result.let {
            return when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG, "deleteAccount - NetworkError")
                    UserRepoResult(UserRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG, "deleteAccount - GenericError")
                    UserRepoResult(UserRepoStatus.SOMETHING_WENT_WRONG)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG, "deleteAccount - Success")
                    UserRepoResult(UserRepoStatus.SUCCESS)
                }
                is ResultHandler.WSCustomError -> {
                    result.errors?.let {
                        if (it.isNotEmpty()) {
                            val error = it[0]
                            globalErrorManager.postError(
                                GlobalErrorManager.GlobalErrorType.WS_ERROR,
                                error
                            )

                        }
                    }
                    UserRepoResult(UserRepoStatus.SOMETHING_WENT_WRONG)
                }
            }
        }
    }

    //General

    private suspend fun onUserChanged() {
        try {
            paymentManager.initPaymentManager(context)
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    fun logout(): UserRepoResult {
        locationManager.clearUserAddresses()
        apiSettings.clearSharedPrefs()
        paymentManager.clearPaymentMethods()
        globalErrorManager.clear()
        this.currentUser = null
        return UserRepoResult(UserRepoStatus.LOGGED_OUT)
    }

    fun getUser(): Eater? {
        return this.currentUser
    }

    suspend fun fetchUser(): Eater? {
        initUserRepo()
        return this.currentUser
    }

    companion object {
        const val TAG = "wowUserRepository"
    }


}