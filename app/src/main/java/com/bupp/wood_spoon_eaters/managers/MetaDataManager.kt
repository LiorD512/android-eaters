package com.bupp.wood_spoon_eaters.managers

import android.util.Log
import com.bupp.wood_spoon_eaters.BuildConfig
import com.bupp.wood_spoon_eaters.model.*

class MetaDataManager {

    private var metaDataObject: MetaDataModel = MetaDataModel(null, null)



    fun setMetaDataObject(metaDataObject: MetaDataModel) {
        Log.d("wowMetaData","setMetaDataObject $metaDataObject")
        this.metaDataObject = metaDataObject
    }

    fun getMetaDataObject(): MetaDataModel? {
        return this.metaDataObject
    }

    fun getCuisineList(): ArrayList<CuisineLabel> {
        if (getMetaDataObject()?.cuisines != null) {
            return metaDataObject?.cuisines as ArrayList<CuisineLabel>
        }
        return arrayListOf()
    }

    fun getCuisineListSelectableIcons(): ArrayList<SelectableIcon>{
        if(getMetaDataObject()?.cuisines != null) {
            return metaDataObject.cuisines as ArrayList<SelectableIcon>
        }
        return arrayListOf()
    }
//
    fun getDietaryList(): ArrayList<SelectableIcon> {
        if (getMetaDataObject()?.diets != null) {
            return metaDataObject?.diets as ArrayList<SelectableIcon>
        }
        return arrayListOf()
    }

    fun getMetricsList(): ArrayList<Metrics> {
        if (getMetaDataObject()?.metrics != null) {
            return metaDataObject.metrics as ArrayList<Metrics>
        }
        return arrayListOf()
    }

    fun getReportTopics(): ArrayList<ReportTopic> {
        if (getMetaDataObject()?.reportTopic != null) {
            return metaDataObject.reportTopic as ArrayList<ReportTopic>
        }
        return arrayListOf()
    }

    fun getNotificationsGroup(): ArrayList<NotificationGroup> {
        if (getMetaDataObject()?.notificationsGroup != null) {
            return metaDataObject.notificationsGroup as ArrayList<NotificationGroup>
        }
        return arrayListOf()
    }

    fun getSettings(): ArrayList<AppSetting> {
        if (getMetaDataObject()?.settings != null) {
            return metaDataObject.settings as ArrayList<AppSetting>
        }
        return arrayListOf()
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

    fun getReportsEmailAddress(): String? {
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

//    fun getDeliveryFeeStr(): String {
//        for (settings in getSettings()){
//            if(settings.key == "delivery_fee")
//                return (settings.value!! as Price).formatedValue as String
//        }
//        return ""
//    }
//

    fun getMinOrderFeeStr(): String {
        for (settings in getSettings()){
            if(settings.key == "min_order")
                return (settings.value!! as Price).formatedValue as String
        }
        return ""
    }

    fun getMinAndroidVersion():String?{
        for (settings in getSettings()){
            if(settings.key == "eaters_min_android_version")
                return (settings.value!!) as String
        }
        return ""
    }

    fun checkMinVersionFail(): Boolean {
        val minVersion = getMinAndroidVersion()
        Log.d("wowMetaDataRepo", "minimum version: $minVersion")
        minVersion?.let{
            val versionName = BuildConfig.VERSION_NAME

            val myCurrVersion = getNumberFromStr(versionName)
            val minimumVersion = getNumberFromStr(minVersion)
            Log.d("wowMetaDataRepo", "curVersion: $myCurrVersion, minimum version: $minimumVersion")
            return myCurrVersion < minimumVersion
        }
        return true
    }

    fun getNumberFromStr(str: String): Int {
        var versionNumber = 0
        val numParts = str.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (numParts.size > 0 && numParts.size <= 3) {
            var multiplier = 1
            for (i in numParts.indices.reversed()) {
                versionNumber += Integer.parseInt(numParts[i].replace("\"", "")) * multiplier
                multiplier *= 1000
            }
        }
        return versionNumber
    }


}