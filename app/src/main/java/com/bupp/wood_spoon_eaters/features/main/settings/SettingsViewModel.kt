package com.bupp.wood_spoon_eaters.features.main.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.model.NotificationGroup
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.common.AppSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class SettingsViewModel(private val appSettings: AppSettings, val api: ApiService, val metaDataRepository: MetaDataRepository, val eaterDataManager: EaterDataManager) : ViewModel() {

    data class SettingsDetails(
        val enableUserLocation: Boolean = false,
        val enableOrderStatusAlert: Boolean = false,
        val enableCommercialEmails: Boolean = false
    )

    val settingsDetails: SingleLiveEvent<SettingsDetails> = SingleLiveEvent()

    fun setLocationSetting(isEnabled: Boolean) {
        appSettings.shouldEnabledUserLocation = isEnabled
    }

    fun setAlertsSetting(isEnabled: Boolean) {
        appSettings.shouldEnabledOrderStatusAlerts = isEnabled
    }

    fun setEmailSetting(isEnabled: Boolean) {
        appSettings.shouldEnabledCommercialEmails = isEnabled
    }

    fun loadSettings() {
        val useLocation = appSettings.shouldEnabledUserLocation
        val sendAlerts = appSettings.shouldEnabledOrderStatusAlerts
        val sendEmails = appSettings.shouldEnabledCommercialEmails

        settingsDetails.postValue(SettingsDetails(useLocation, sendAlerts, sendEmails))
    }

    fun getNotificationsGroup(): ArrayList<NotificationGroup> {
        return metaDataRepository.getNotificationsGroup()
    }

    data class PostNotificationGroupEvent(val isSuccessful: Boolean = false)
    val postNotificationGroup: SingleLiveEvent<PostNotificationGroupEvent> = SingleLiveEvent()

    fun updateEaterNotificationGroup(notificationGroupId: Long) {
        val eater = eaterDataManager.currentEater
        if(eater != null){
            val idsList = eater.getNotificationGroupIds()
            Log.d("wowSettings","current ids: $idsList")
            if(idsList.contains(notificationGroupId)){
                idsList.remove(notificationGroupId)
            }else{
                idsList.add(notificationGroupId)
            }
//            val array = arrayOfNulls<Long>(idsList.size)
//            val array = arrayOf("a", "b")
            val array = arrayOf<Long>()
            val list = idsList.toArray(array)
            Log.d("wowSettings","sending ids: ${list.asList()}")
            api.postEaterNotificationGroup(list).enqueue(object : Callback<ServerResponse<Eater>> {
                override fun onResponse(call: Call<ServerResponse<Eater>>, response: Response<ServerResponse<Eater>>) {
                    if (response.isSuccessful) {
                        Log.d("wowSettingsVM", "postEaterNotificationGroup on success! ")
                        val eater = response.body()?.data!!
//                        eaterDataManager.currentEater = eater // ny delete
                        Log.d("wowSettings","current ids: ${eater.notificationsGroup}")
                        postNotificationGroup.postValue(PostNotificationGroupEvent(true))
                    } else {
                        Log.d("wowSettingsVM", "postEaterNotificationGroupon Failure! ")
                        postNotificationGroup.postValue(PostNotificationGroupEvent(false))
                    }
                }

                override fun onFailure(call: Call<ServerResponse<Eater>>, t: Throwable) {
                    Log.d("wowSettingsVM", "postEaterNotificationGroup on big Failure! " + t.message)
                    postNotificationGroup.postValue(PostNotificationGroupEvent(false))
                }
            })
        }
    }

    fun getEaterNotificationsGroup(): ArrayList<Long>? {
        val eater = eaterDataManager.currentEater
        if(eater != null){
            return  eaterDataManager.currentEater?.getNotificationGroupIds()
        }else{
            return arrayListOf()
        }
    }
}