package com.bupp.wood_spoon_eaters.features.main.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.common.UserSettings
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.model.NotificationGroup
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import kotlinx.coroutines.launch

class SettingsViewModel(private val userSettings: UserSettings, val userRepository: UserRepository, val metaDataRepository: MetaDataRepository, val eaterDataManager: EaterDataManager,
                        private val flowEventsManager: FlowEventsManager) : ViewModel() {

    data class SettingsDetails(
        val enableUserLocation: Boolean = false,
    )

    val settingsDetails: SingleLiveEvent<SettingsDetails> = SingleLiveEvent()

    fun setLocationSetting(isEnabled: Boolean) {
        userSettings.shouldEnabledUserLocation = isEnabled
    }

    fun loadSettings() {
        val useLocation = userSettings.shouldEnabledUserLocation
        settingsDetails.postValue(SettingsDetails(useLocation))
    }

    fun getNotificationsGroup(): List<NotificationGroup> {
        return metaDataRepository.getNotificationsGroup()
    }

    data class PostNotificationGroupEvent(val isSuccessful: Boolean = false)
    val postNotificationGroup: SingleLiveEvent<PostNotificationGroupEvent> = SingleLiveEvent()

    fun updateEaterNotificationGroup(idsList: List<Long>) {

//        val eater = eaterDataManager.currentEater
//        eater?.let{
//            val idsList = eater.getNotificationGroupIds()
//            Log.d("wowSettings","current ids: $idsList")
//            if(idsList.contains(notificationGroupId)){
//                idsList.remove(notificationGroupId)
//            }else{
//                idsList.add(notificationGroupId)
//            }
            viewModelScope.launch {
                val userRepoResult = userRepository.updateNotificationsGroup(idsList)
                when (userRepoResult.type) {
                    UserRepository.UserRepoStatus.SERVER_ERROR -> {
                        Log.d("updateNotifications", "NetworkError")
                        postNotificationGroup.postValue(PostNotificationGroupEvent(false))
                    }
                    UserRepository.UserRepoStatus.SOMETHING_WENT_WRONG -> {
                        Log.d("updateNotifications", "GenericError")
                        postNotificationGroup.postValue(PostNotificationGroupEvent(false))
                    }
                    UserRepository.UserRepoStatus.SUCCESS -> {
                        Log.d("updateNotifications", "Success")
                        postNotificationGroup.postValue(PostNotificationGroupEvent(true))
                    }
                    else -> {
                        Log.d("updateNotifications", "NetworkError")
                        postNotificationGroup.postValue(PostNotificationGroupEvent(false))
                    }
                }
//            }
        }
    }

    fun getEaterNotificationsGroup(): ArrayList<Long>? {
        val eater = eaterDataManager.currentEater
        return if(eater != null){
            eaterDataManager.currentEater?.getNotificationGroupIds()
        }else{
            arrayListOf()
        }
    }

    fun logPageEvent(eventType: FlowEventsManager.FlowEvents) {
        flowEventsManager.trackPageEvent(eventType)
    }
}