package com.bupp.wood_spoon_chef.di

import com.bupp.wood_spoon_chef.data.remote.model.AppSetting
import com.bupp.wood_spoon_chef.data.remote.model.AppSettingKnownTypes
import com.bupp.wood_spoon_chef.data.remote.model.Price
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import timber.log.Timber

internal class AppSettingAdapter {

    private val moshi = Moshi.Builder().build()

    @FromJson
    fun fromJson(appSetting: AppSetting): AppSetting {
        val convertedValue = try {
            when (AppSettingKnownTypes.values().firstOrNull { it.name == appSetting.dataType }) {


                AppSettingKnownTypes.string -> appSetting.value as String
                AppSettingKnownTypes.integer -> (appSetting.value as Double).toInt()
                AppSettingKnownTypes.decimal -> (appSetting.value as Double).toBigDecimal()
                AppSettingKnownTypes.price -> {
                    val json = moshi.adapter(Map::class.java).toJson(appSetting.value as Map<*, *>)
                    val jsonAdapter: JsonAdapter<Price> = moshi.adapter(Price::class.java)
                    val price: Price? = jsonAdapter.fromJson(json)
                    price
                }
                AppSettingKnownTypes.key_value -> {
                    val adapter = moshi.adapter(Map::class.java)
                    val jsonStructure = adapter.toJsonValue(appSetting.value as Map<*, *>)
                    val jsonObject = jsonStructure as Map<*, *>?
                    jsonObject
                }
                AppSettingKnownTypes.boolean -> appSetting.value as Boolean
                AppSettingKnownTypes.csv_array -> appSetting.value as List<String>
                null -> appSetting.value
            }
        } catch (ex: Exception) {
            Timber.e("AppSetting parsing error: data_type=[%s] value=[%s]", appSetting.dataType, appSetting.value)
            appSetting.value
        }
        return appSetting.copy(
                value = convertedValue
        )
    }
}
