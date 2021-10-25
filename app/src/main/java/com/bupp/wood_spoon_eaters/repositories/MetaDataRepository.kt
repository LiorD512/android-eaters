package com.bupp.wood_spoon_eaters.repositories

import android.util.Log
import com.bupp.wood_spoon_eaters.BuildConfig
import com.bupp.wood_spoon_eaters.bottom_sheets.reviews.Metrics
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.base_repos.MetaDataRepositoryImpl
import com.bupp.wood_spoon_eaters.network.result_handler.ResultHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class MetaDataRepository(private val apiService: MetaDataRepositoryImpl) {

    private var metaDataObject: MetaDataModel = MetaDataModel(null, null)

    enum class MetaDataRepoStatus{
        SUCCESS,
        FAILED,
    }
    data class MetaDataRepoResult(val status: MetaDataRepoStatus)

    suspend fun initMetaData(): MetaDataRepoResult {
        val result = withContext(Dispatchers.IO){
            apiService.getMetaData()
        }
        result.let{
            when (result) {
                is ResultHandler.NetworkError -> {
                    Log.d(TAG,"initMetaData - NetworkError")
                    return MetaDataRepoResult(MetaDataRepoStatus.FAILED)
                }
                is ResultHandler.GenericError -> {
                    Log.d(TAG,"initMetaData - GenericError")
                    return MetaDataRepoResult(MetaDataRepoStatus.FAILED)
                }
                is ResultHandler.Success -> {
                    Log.d(TAG,"initMetaData - Success")
                    val metaData = result.value.data?.copy()
                    metaData?.let{
                        this.metaDataObject = it
                    }
                    return MetaDataRepoResult(MetaDataRepoStatus.SUCCESS)
                }
                is ResultHandler.WSCustomError -> {
                    return MetaDataRepoResult(MetaDataRepoStatus.FAILED)
                }
            }
        }

    }

    private fun getMetaDataObject(): MetaDataModel {
        return this.metaDataObject
    }

    fun getCuisineListSelectableIcons(): List<SelectableIcon>{
        if(getMetaDataObject().cuisines != null) {
            return metaDataObject.cuisines as List<SelectableIcon>
        }
        return listOf()
    }

    fun getDietaryList(): List<SelectableIcon> {
        if (getMetaDataObject().diets != null) {
            return metaDataObject.diets as List<SelectableIcon>
        }
        return listOf()
    }

    fun getReportTopics(): List<ReportTopic> {
        if (getMetaDataObject().reportTopic != null) {
            return metaDataObject.reportTopic as List<ReportTopic>
        }
        return listOf()
    }

    fun getNotificationsGroup(): List<NotificationGroup> {
        if (getMetaDataObject().notificationsGroup != null) {
            return metaDataObject.notificationsGroup as List<NotificationGroup>
        }
        return listOf()
    }

    private fun getSettings(): List<AppSetting> {
        if (getMetaDataObject().settings != null) {
            return metaDataObject.settings as List<AppSetting>
        }
        return listOf()
    }


    fun getTermsOfServiceUrl(): String {
        for (settings in getSettings()){
            if(settings.key == "terms_url")
                return settings.value!! as String
        }
        return ""
    }

    fun getPrivacyPolicyUrl(): String {
        for (settings in getSettings()){
            if(settings.key == "privacy_policy_url")
                return settings.value!! as String
        }
        return ""
    }

    fun getStripePublishableKey(): String?{
        for (settings in getSettings()){
            if(settings.key == "stripe_publishable_key")
                return settings.value!! as String
        }
        return null
    }

    fun getReportsEmailAddress(): String {
        for (settings in getSettings()){
            if(settings.key == "reports_email")
                return settings.value!! as String
        }
        return ""
    }

    fun getUpdateDialogTitle(): String {
        for (settings in getSettings()){
            if(settings.key == "android_version_control_title")
                return settings.value!! as String
        }
        return ""
    }

    fun getUpdateDialogBody(): String {
        for (settings in getSettings()){
            if(settings.key == "android_version_control_body")
                return settings.value!! as String
        }
        return ""
    }

    fun getUpdateDialogUrl(): String {
        for (settings in getSettings()){
            if(settings.key == "android_version_control_link")
                return settings.value!! as String
        }
        return ""
    }

    fun getMinOrderFeeStr(nationwide: Boolean): String {
        if(nationwide){
            for (settings in getSettings()){
                if(settings.key == "nationwide_min_order")
                    return (settings.value!! as Price).formatedValue as String
            }
        }else{
            for (settings in getSettings()){
                if(settings.key == "min_order")
                    return (settings.value!! as Price).formatedValue as String
            }
        }
        return ""
    }

    private fun getMinAndroidVersion():String{
        for (settings in getSettings()){
            if(settings.key == "eaters_min_android_version")
                return (settings.value!!) as String
        }
        return ""
    }

    fun checkMinVersionFail(): Boolean {
        val minVersion = getMinAndroidVersion()
        Log.d("wowMetaDataRepo", "minimum version: $minVersion")
        minVersion.let{
            val versionName = BuildConfig.VERSION_NAME

            val myCurrVersion = getNumberFromStr(versionName)
            val minimumVersion = getNumberFromStr(minVersion)
            Log.d("wowMetaDataRepo", "curVersion: $myCurrVersion, minimum version: $minimumVersion")
            return myCurrVersion < minimumVersion
        }
    }

    private fun getNumberFromStr(str: String): Int {
        var versionNumber = 0
        val numParts = str.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (numParts.size in 1..3) {
            var multiplier = 1
            for (i in numParts.indices.reversed()) {
                versionNumber += Integer.parseInt(numParts[i].replace("\"", "")) * multiplier
                multiplier *= 1000
            }
        }
        return versionNumber
    }

    fun getContactUsPhoneNumber(): String {
        for (settings in getSettings()){
            if(settings.key == "contact_us_number")
                return (settings.value!!) as String
        }
        return ""
    }

    fun getContactUsTextNumber(): String {
        for (settings in getSettings()){
            if(settings.key == "text_message_num")
                return (settings.value!!) as String
        }
        return ""
    }

    fun getQaUrl(): String {
        for (settings in getSettings()){
            if(settings.key == "qa_url")
                return (settings.value!!) as String
        }
        return ""
    }

    fun getLocationDistanceThreshold(): Int {
        for (settings in getSettings()){
            if(settings.key == "location_distance_threshold")
                return (settings.value!!) as Int
        }
        return 20
    }

    fun getMinFutureOrderWindow(): Int {
        for (settings in getSettings()){
            if(settings.key == "min_future_order_window")
                return (settings.value!!) as Int
        }
        return 60
    }

    fun getDefaultLat(): Double {
        for (settings in getSettings()){
            if(settings.key == "default_feed_lat")
                return ((settings.value!!) as BigDecimal).toDouble()
        }
        return 0.0
    }

    fun getDefaultLng(): Double {
        for (settings in getSettings()){
            if(settings.key == "default_feed_lng")
                return ((settings.value!!) as BigDecimal).toDouble()
        }
        return 0.0
    }

    fun getDefaultFeedLocationName(): String {
        for (settings in getSettings()){
            if(settings.key == "default_feed_location_name")
                return (settings.value!!) as String
        }
        return ""
    }

    fun getCloudinaryTransformations(): CloudinaryTransformations? {
        for (settings in getSettings()){
            if(settings.key == "cloudinary_transformations"){
                val cloudinaryMap = settings.value as Map<*, *>?
                cloudinaryMap?.let {
                    return CloudinaryTransformations(cloudinaryMap as Map<CloudinaryTransformationsType, String>?)
                }
            }
        }
        return null
    }

    companion object{
        const val TAG = "wowMetaDataRepo"
    }


}