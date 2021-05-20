package com.bupp.wood_spoon_eaters.repositories

import android.util.Log
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiSettings
import com.bupp.wood_spoon_eaters.network.base_repos.UserRepositoryImpl
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class UserRepository(
    private val apiService: UserRepositoryImpl,
    private val apiSettings: ApiSettings
) {

    private var currentUser: Eater? = null

    data class UserRepoResult(val type: UserRepoStatus, val eater: Eater? = null)
    enum class UserRepoStatus {
        SUCCESS,
        LOGGED_OUT,
        INVALID_PHONE,
        WRONG_PASSWORD,
        SOMETHING_WENT_WRONG,
        SERVER_ERROR
    }

    suspend fun initUserRepo() {
        val result = withContext(Dispatchers.IO){
            apiService.getMe()
        }
        result.let{
            return when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG,"initUserRepo - NetworkError")
                    this.currentUser = null
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG,"initUserRepo - GenericError")
                    this.currentUser = null
                }
                is ResultHandler.Success -> {
                    Log.d(TAG,"initUserRepo - Success")
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
        val result = withContext(Dispatchers.IO){
            apiService.getCode(phone)
        }
        result.let{
            return when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG,"sendPhoneVerification - NetworkError")
                    UserRepoResult(UserRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG,"sendPhoneVerification - GenericError")
                    UserRepoResult(UserRepoStatus.INVALID_PHONE)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG,"sendPhoneVerification - Success")
                    UserRepoResult(UserRepoStatus.SUCCESS)
                }
                is ResultHandler.WSCustomError -> {
                    UserRepoResult(UserRepoStatus.INVALID_PHONE)
                }
            }
        }
    }

    suspend fun sendCodeAndPhoneVerification(phone: String, code: String): UserRepoResult {
        val result = withContext(Dispatchers.IO){
            apiService.validateCode(phone, code)
        }
        result.let{
            return when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG,"sendCodeAndPhoneVerification - NetworkError")
                    UserRepoResult(UserRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG,"sendCodeAndPhoneVerification - GenericError")
                    UserRepoResult(UserRepoStatus.WRONG_PASSWORD)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG,"sendCodeAndPhoneVerification - Success")
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
        val result = withContext(Dispatchers.IO){
            apiService.postMe(eater)
        }
        result.let{
            return when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG,"updateEater - NetworkError")
                    UserRepoResult(UserRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG,"updateEater - GenericError")
                    UserRepoResult(UserRepoStatus.SOMETHING_WENT_WRONG)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG,"updateEater - Success")
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
        val result = withContext(Dispatchers.IO){
            apiService.updateNotificationGroup(notifications)
        }
        result.let{
            return when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG,"updateNotificationsGroup - NetworkError")
                    UserRepoResult(UserRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG,"updateNotificationsGroup - GenericError")
                    UserRepoResult(UserRepoStatus.SOMETHING_WENT_WRONG)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG,"updateNotificationsGroup - Success")
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
    suspend fun addNewAddress(addressRequest: AddressRequest): UserRepoResult {
        val result = withContext(Dispatchers.IO){
            apiService.postNewAddress(addressRequest)
        }
        result.let{
            return when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG,"addNewAddress - NetworkError")
                    UserRepoResult(UserRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG,"addNewAddress - GenericError")
                    UserRepoResult(UserRepoStatus.SOMETHING_WENT_WRONG)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG,"addNewAddress - Success")
                    initUserRepo()
                    UserRepoResult(UserRepoStatus.SUCCESS, this.currentUser)
                }
                is ResultHandler.WSCustomError -> {
                    UserRepoResult(UserRepoStatus.SOMETHING_WENT_WRONG)
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
        }catch (ex: Exception){
            Log.d(TAG, "deleteAddress - addressId is null")
        }
        return UserRepoResult(UserRepoStatus.SOMETHING_WENT_WRONG)
    }

    //General

    fun logout(): UserRepoResult{
        apiSettings.clearSharedPrefs()
        this.currentUser = null
        return UserRepoResult(UserRepoStatus.LOGGED_OUT)
    }

    fun getUser(): Eater? {
        return this.currentUser
    }

    companion object{
        const val TAG = "wowUserRepository"
    }



}