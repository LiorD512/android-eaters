package com.bupp.wood_spoon_eaters.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.features.login.LoginViewModel
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.model.EaterRequest
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiSettings
import com.bupp.wood_spoon_eaters.network.test.RepositoryImpl
import com.bupp.wood_spoon_eaters.network.test.ResultHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(
    private val apiService: RepositoryImpl,
    private val apiSettings: ApiSettings
) {

    private var currentUser: Eater? = null

    data class UserRepoResult(val type: UserRepoStatus, val eater: Eater? = null)
    enum class UserRepoStatus {
        SUCCESS,
        INVALID_PHONE,
        WRONG_PASSWORD,
        SOMETHING_WENT_WRONG,
        SERVER_ERROR
    }

    suspend fun initUserRepo() {
//        if(apiSettings.isRegistered()){
//            val result = withContext(Dispatchers.IO){
//                apiService.getMe().data
//            }
//            result?.let{
//                setCurrentUser(it)
//            }
//        }
    }

    private fun setCurrentUser(user: Eater) {
        this.currentUser = user.copy()
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

}