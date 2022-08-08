package com.bupp.wood_spoon_eaters.features.main.profile.my_profile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.BuildConfig
import com.bupp.wood_spoon_eaters.common.FlavorConfigManager
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.*
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.model.EaterRequest
import com.bupp.wood_spoon_eaters.model.ErrorEventType
import com.bupp.wood_spoon_eaters.model.SelectableIcon
import com.bupp.wood_spoon_eaters.repositories.*
import com.eatwoodspoon.analytics.events.AppReviewEvent
import com.eatwoodspoon.analytics.events.EatersFeedEvent
import kotlinx.coroutines.launch

class MyProfileViewModel(
    private val userRepository: UserRepository,
    private val metaDataRepository: MetaDataRepository,
    private val flowEventsManager: FlowEventsManager,
    private val flavorConfigManager: FlavorConfigManager,
    paymentManager: PaymentManager,
    campaignManager: CampaignManager,
    private val appSettingsRepository: AppSettingsRepository,
    private val eatersAnalyticsTracker: EatersAnalyticsTracker
) :
    ViewModel(), EphemeralKeyProvider.EphemeralKeyProviderListener {

    val progressData = ProgressData()
    val errorEvents: MutableLiveData<ErrorEventType> = MutableLiveData()

    val paymentLiveData = paymentManager.getPaymentsLiveData()
    val profileData: SingleLiveEvent<ProfileData> = SingleLiveEvent()
    val versionLiveData = SingleLiveEvent<String>()

    val campaignLiveData = campaignManager.getCampaignLiveData()


    init {
        setVersionData()
        viewModelScope.launch {
            flowEventsManager.fireEvent(FlowEventsManager.FlowEvents.VISIT_PROFILE)
        }
    }

    private fun setVersionData() {
        val buildNumber = if (appSettingsRepository.featureFlag(EatersFeatureFlags.TestMobileShowBuildNumber) != false) {
            " (${BuildConfig.VERSION_CODE})"
        } else ""


        val versionData = "Version: ${BuildConfig.VERSION_NAME}$buildNumber ${flavorConfigManager.getEnvName()}"
        versionLiveData.postValue(versionData)
    }

    data class ProfileData(val eater: Eater?, val dietary: List<SelectableIcon>)

    fun fetchProfileData() {
        viewModelScope.launch {
            Log.d(TAG, "fetchProfileData")
            val eater = userRepository.fetchUser()
            val dietaries = metaDataRepository.getDietaryList()
            profileData.postValue(ProfileData(eater, dietaries))
        }
    }

    fun updateClientAccount(cuisineIcons: List<SelectableIcon>? = null, dietaryIcons: List<SelectableIcon>? = null, forceUpdate: Boolean = false) {
        val eater = EaterRequest()

        var arrayOfCuisinesIds: MutableList<Int>? = null
        var arrayOfDietsIds: MutableList<Int>? = null

        cuisineIcons?.let {
            arrayOfCuisinesIds = mutableListOf()
            for (cuisine in cuisineIcons) {
                arrayOfCuisinesIds!!.add(cuisine.id.toInt())
            }
        }
        dietaryIcons?.let {
            arrayOfDietsIds = mutableListOf()
            for (diet in dietaryIcons) {
                arrayOfDietsIds!!.add(diet.id.toInt())
            }
        }

        eater.cuisineIds = arrayOfCuisinesIds
        eater.dietIds = arrayOfDietsIds
        postClient(eater, forceUpdate)
    }

    private fun postClient(eater: EaterRequest, forceUpdate: Boolean) {
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
                    if (forceUpdate) {
                        fetchProfileData()
                    }
                }
                else -> {
                    Log.d("wowLoginVM", "NetworkError")
                    errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                }
            }
        }
    }

    fun trackProfileScreenEaterRateAppClickedEvent() {
        eatersAnalyticsTracker.logEvent(
            event = AppReviewEvent.ProfileScreenEaterRateAppClickedEvent()
        )
    }

    companion object {
        const val TAG = "wowMyProfileVM"
    }

}

