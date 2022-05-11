package com.bupp.wood_spoon_chef.data.repositories

import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.data.local.MemoryDataSource
import com.bupp.wood_spoon_chef.managers.ChefAnalyticsTracker
import com.bupp.wood_spoon_chef.data.remote.model.Cook
import com.bupp.wood_spoon_chef.data.remote.model.CookRequest
import com.bupp.wood_spoon_chef.data.remote.network.ApiService
import com.bupp.wood_spoon_chef.data.remote.network.ResponseHandler
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseResult
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.data.repositories.base_repos.UserRepositoryImp
import com.bupp.wood_spoon_chef.utils.UserSettings
import org.joda.time.DateTime

class UserRepository(
    service: ApiService,
    responseHandler: ResponseHandler,
    private val userSettings: UserSettings,
    private val chefAnalyticsTracker: ChefAnalyticsTracker,
    private val memoryDataSource: MemoryDataSource
) : UserRepositoryImp(service, responseHandler) {

    private var currentUser: Cook? = null

    fun getUserData() = currentUserData
    private var currentUserData = MutableLiveData<Cook?>()

    fun getCurrentChef(): Cook? {
        return currentUser
    }

    private fun saveCurrentChef(cook: Cook?){
        currentUser = cook
        currentUserData.postValue(cook)
    }

    suspend fun getCurrentChef(fetchFromServer: Boolean): Cook? {
        if (fetchFromServer) {
            val result = getMe()
            if (result is ResponseSuccess) {
                val cook = result.data
                saveCurrentChef(cook)
                return cook
            }
        }
        return getUserData().value
    }

    fun getLastSelectedCalendarDateFlow() = memoryDataSource.selectedDateFlow

    suspend fun setMemorySelectedCalendarDate(date: DateTime) {
        memoryDataSource.selectedDateFlow.emit(date.millis)
    }

    suspend fun initUserRepo() {
        val result = getMe()
        if (result is ResponseSuccess) {
            val cook = result.data
            chefAnalyticsTracker.initSegment(cook, cook?.pickupAddress)
            saveCurrentChef(cook)
        }
    }

    fun isUserValid(): Boolean {
        return this.currentUserData.value != null
    }

    fun isUserRegistered(): Boolean {
        return userSettings.isRegistered()
    }

    fun isUserSignedUp(): Boolean {
        return isUserValid() && !this.currentUserData.value?.email.isNullOrEmpty()
    }

    suspend fun sendCodeAndPhoneVerification(phone: String, code: String): ResponseResult<Cook> {
        val result = validateCode(phone, code)
        if (result is ResponseSuccess) {
            val cook = result.data
            saveCurrentChef(cook)
            chefAnalyticsTracker.initSegment(cook, cook?.pickupAddress)
            chefAnalyticsTracker.trackEvent(Constants.EVENTS_CREATED_COOKING_SLOT, getEventsParam())
        }
        return result
    }

    suspend fun updateCook(cook: CookRequest): ResponseResult<Cook> {
        val result = postMe(cook)
        if (result is ResponseSuccess) {
            val cook = result.data
            saveCurrentChef(cook)
        }
        return result
    }


    fun isPendingApproval(): Boolean {
        currentUserData.value?.let {
            return it.accountStatus == "pending_approval"
        }
        return false
    }

    private fun getEventsParam(): Map<String, Any?> {
        val data = mutableMapOf<String, Any?>()
        data["chef_id"] = getCurrentChef()?.id ?: "N/A"
        return data
    }

}