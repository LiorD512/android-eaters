package com.bupp.wood_spoon_eaters.repositories

import android.content.Context
import android.content.Intent
import android.util.Log
import com.bupp.wood_spoon_eaters.features.splash.SplashActivity
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.AddressRequest
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.model.EaterRequest
import com.bupp.wood_spoon_eaters.network.ApiSettings
import com.bupp.wood_spoon_eaters.network.test.RepositoryImpl
import com.bupp.wood_spoon_eaters.network.test.ResultHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class UserRepository(
    private val apiService: RepositoryImpl,
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
                    Log.d("wowUserRepository","initUserRepo - NetworkError")
                    this.currentUser = null
                }
                is ResultHandler.GenericError -> {
                    Log.d("wowUserRepository","initUserRepo - GenericError")
                    this.currentUser = null
                }
                is ResultHandler.Success -> {
                    Log.d("wowUserRepository","initUserRepo - Success")
                    this.currentUser = result.value.data?.copy()
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
                    Log.d("wowUserRepository","sendPhoneVerification - NetworkError")
                    UserRepoResult(UserRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d("wowUserRepository","sendPhoneVerification - GenericError")
                    UserRepoResult(UserRepoStatus.INVALID_PHONE)
                }
                is ResultHandler.Success -> {
                    Log.d("wowUserRepository","sendPhoneVerification - Success")
                    UserRepoResult(UserRepoStatus.SUCCESS)
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
                    Log.d("wowUserRepository","sendCodeAndPhoneVerification - NetworkError")
                    UserRepoResult(UserRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d("wowUserRepository","sendCodeAndPhoneVerification - GenericError")
                    UserRepoResult(UserRepoStatus.WRONG_PASSWORD)
                }
                is ResultHandler.Success -> {
                    Log.d("wowUserRepository","sendCodeAndPhoneVerification - Success")
                    val eater = result.value.data
                    this.currentUser = eater
                    UserRepoResult(UserRepoStatus.SUCCESS, this.currentUser)
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
                    Log.d("wowUserRepository","updateEater - NetworkError")
                    UserRepoResult(UserRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d("wowUserRepository","updateEater - GenericError")
                    UserRepoResult(UserRepoStatus.SOMETHING_WENT_WRONG)
                }
                is ResultHandler.Success -> {
                    Log.d("wowUserRepository","updateEater - Success")
                    val eater = result.value.data
                    this.currentUser = eater
                    UserRepoResult(UserRepoStatus.SUCCESS, this.currentUser)
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
                    Log.d("wowUserRepository","addNewAddress - NetworkError")
                    UserRepoResult(UserRepoStatus.SERVER_ERROR)
                }
                is ResultHandler.GenericError -> {
                    Log.d("wowUserRepository","addNewAddress - GenericError")
                    UserRepoResult(UserRepoStatus.SOMETHING_WENT_WRONG)
                }
                is ResultHandler.Success -> {
                    Log.d("wowUserRepository","addNewAddress - Success")
                    initUserRepo()
                    UserRepoResult(UserRepoStatus.SUCCESS, this.currentUser)
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
                        Log.d("wowUserRepository", "deleteAddress - NetworkError")
                        UserRepoResult(UserRepoStatus.SERVER_ERROR)
                    }
                    is ResultHandler.GenericError -> {
                        Log.d("wowUserRepository", "deleteAddress - GenericError")
                        UserRepoResult(UserRepoStatus.SOMETHING_WENT_WRONG)
                    }
                    is ResultHandler.Success -> {
                        Log.d("wowUserRepository", "deleteAddress - Success")
                        initUserRepo()
                        UserRepoResult(UserRepoStatus.SUCCESS, this.currentUser)
                    }
                    else -> {
                        Log.d("wowUserRepository", "deleteAddress - addressId is null")
                        UserRepoResult(UserRepoStatus.SOMETHING_WENT_WRONG)
                    }
                }
            }
        }catch (ex: Exception){
            Log.d("wowUserRepository", "deleteAddress - addressId is null")
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




}