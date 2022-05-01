package com.bupp.wood_spoon_chef.data.repositories

import com.bupp.wood_spoon_chef.BuildConfig
import com.bupp.wood_spoon_chef.common.MTLogger
import com.bupp.wood_spoon_chef.data.remote.model.*
import com.bupp.wood_spoon_chef.data.remote.network.ApiService
import com.bupp.wood_spoon_chef.data.remote.network.ResponseHandler
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseResult
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.data.remote.network.getMetaData
import com.bupp.wood_spoon_chef.data.repositories.base_repos.BaseMetaDataRepository
import com.bupp.wood_spoon_chef.utils.parseStringToTime
import java.math.BigDecimal
import java.util.*

class MetaDataRepository(
    private val service: ApiService,
    private val responseHandler: ResponseHandler,
    private val featureListProvider: FeatureFlagsListProvider,
) : BaseMetaDataRepository {

    private var metaDataObject: MetaDataModel? = null

    override suspend fun getMetaData(): ResponseResult<MetaDataModel> {
        return responseHandler.safeApiCall { service.getMetaData(featureListProvider.getFeatureFlagsList()) }
    }

    suspend fun initMetaData() {
        val result = getMetaData()
        if (result is ResponseSuccess) {
            this.metaDataObject = result.data
        }
    }

    fun getMetaDataObject(): MetaDataModel? {
        return this.metaDataObject
    }

    fun getCuisineList(): List<CuisineIcon> {
        getMetaDataObject()?.cuisines?.let {
            return it
        }
        return listOf()
    }

    fun getDietaryList(): List<SelectableIcon> {
        getMetaDataObject()?.diets?.let {
            return it
        }
        return listOf()
    }

    fun getCountries(): List<SelectableString> {
        getMetaDataObject()?.countries?.let {
            return it
        }
        return listOf()
    }

    fun getDishCategories(): List<DishCategory> {
        getMetaDataObject()?.dishCategories?.let {
            return it
        }
        return listOf()
    }

    fun getPrepTimeList(): List<PrepTimeRange> {
        getMetaDataObject()?.prepTimeRanges?.let {
            return it
        }
        return listOf()
    }

    fun getCancellationReasons(): List<CancellationReason> {
        getMetaDataObject()?.cancellationReasons?.let {
            return it
        }
        return listOf()
    }

    fun getBuppServiceFeePercentage(): Float {
        for (setting in getSettings()) {
            if (setting.key.equals("eater_service_fee")) {
                return (setting.value as BigDecimal).toFloat()
            }
        }
        return 14.0f
    }

    private fun getSettings(): List<AppSetting> {
        getMetaDataObject()?.settings?.let {
            return it
        }
        return listOf()
    }

    fun getTermsOfServiceUrl(): String {
        for (settings in getSettings()) {
            if (settings.key == "terms_url")
                return settings.value.toString()
        }
        return ""
    }

    fun getPrivacyPolicyUrl(): String {
        for (settings in getSettings()) {
            if (settings.key == "privacy_policy_url")
                return settings.value.toString()
        }
        return ""
    }

    fun getSupportEmailAddress(): String {
        for (settings in getSettings()) {
            if (settings.key == "client_support_email")
                return settings.value.toString()
        }
        return ""
    }

    fun getQAUrl(): String {
        for (settings in getSettings()) {
            if (settings.key == "qa_url")
                return settings.value.toString()
        }
        return ""
    }

    private fun getMinAndroidVersion(): String {
        for (settings in getSettings()) {
            if (settings.key == "chef_min_android_version")
                return (settings.value).toString()
        }
        return ""
    }

    fun getOfficeHoursStart(): Date? {
        try {
            for (settings in getSettings()) {
                if (settings.key == "ny_office_hours_start_at")
                    return settings.value.toString().parseStringToTime()
            }
        } catch (ex: Exception) {
        }
        return null
    }

    fun getOfficeHoursEnd(): Date? {
        try {
            for (settings in getSettings()) {
                if (settings.key == "ny_office_hours_end_at")
                    return settings.value.toString().parseStringToTime()
            }
        } catch (ex: Exception) {
        }
        return null
    }

    fun checkMinVersion(): Boolean {
        val minVersion = getMinAndroidVersion()
        MTLogger.c("minimum version: $minVersion")
        minVersion.let {
            val versionName = BuildConfig.VERSION_NAME

            val myCurrVersion = getNumberFromStr(versionName)
            val minimumVersion = getNumberFromStr(minVersion)
            MTLogger.c(
                    "curVersion: $myCurrVersion, minimum version: $minimumVersion"
            )
            return myCurrVersion < minimumVersion
        }
    }

    private fun getNumberFromStr(str: String): Int {
        var versionNumber = 0
        val numParts = str.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (numParts.size in 1..4) {
            var multiplier = 1
            for (i in numParts.indices.reversed()) {
                versionNumber += Integer.parseInt(numParts[i].replace("\"", "")) * multiplier
                multiplier *= 1000
            }
        }
        return versionNumber
    }

    fun getContactUsPhoneNumber(): String {
        for (settings in getSettings()) {
            if (settings.key == "contact_us_number")
                return settings.value.toString()
        }
        return ""
    }

    fun getContactUsTextNumber(): String {
        for (settings in getSettings()) {
            if (settings.key == "chef_text_message_num")
                return settings.value.toString()
        }
        return ""
    }

    fun isSupportCancellationEnabled(): Boolean {
        for (settings in getSettings()) {
            if (settings.key == "mobile_orders_call_support_cancellation")
                return settings.value as Boolean
        }
        return false
    }
}