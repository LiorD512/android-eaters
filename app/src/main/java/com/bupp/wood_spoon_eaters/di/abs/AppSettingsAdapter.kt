package com.bupp.wood_spoon_eaters.di.abs

import android.util.Log
import com.bupp.wood_spoon_eaters.model.AppSetting
import com.bupp.wood_spoon_eaters.model.Price
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

internal class AppSettingAdapter {

    @FromJson
    fun fromJson(appSetting: AppSetting): AppSetting {
//        if (AppSetting.length != 2) throw JsonDataException("Unknown AppSetting: $AppSetting")
//        val rank = card[0]
        val parsedAppSetting = AppSetting(id = appSetting.id, key = appSetting.key, null, null)
        when (appSetting.data_type) {
            "string" -> { //string
                parsedAppSetting.value = appSetting.value as String
            }
            "integer" -> { //integer
                parsedAppSetting.value = (appSetting.value as Double).toInt()
            }
            "decimal" -> { //decimal
                parsedAppSetting.value = (appSetting.value as Double).toBigDecimal()
            }
            "price" -> { //price
                try {
                    val moshi = Moshi.Builder().build()
                    val json = moshi.adapter(Map::class.java).toJson(appSetting.value as Map<*, *>)
                    val jsonAdapter: JsonAdapter<Price> = moshi.adapter(Price::class.java)
                    val price: Price? = jsonAdapter.fromJson(json)
                    parsedAppSetting.value = price
                } catch (ex: Exception) {
                    Log.d("wow", "wow")
                }
            }
            "key_value" -> { //key-value
                try{
                    val moshi = Moshi.Builder().build()
                    val adapter = moshi.adapter(Map::class.java)
                    val jsonStructure = adapter.toJsonValue(appSetting.value as Map<*, *>)
                    val jsonObject = jsonStructure as Map<*, *>?
                    parsedAppSetting.value = jsonObject
                } catch (ex: Exception) {
                    Log.d("wow", "wow")
                }
            }
            "boolean" -> { //boolean
                parsedAppSetting.value = appSetting.value as Boolean
            }
            "csv_array" -> { //array
                parsedAppSetting.value = appSetting.value as MutableList<String>
            }
        }
        return parsedAppSetting
    }
}
