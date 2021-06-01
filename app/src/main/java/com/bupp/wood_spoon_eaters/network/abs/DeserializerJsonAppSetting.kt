//package com.bupp.wood_spoon_eaters.network.abs
//
//import android.util.Log
//import com.bupp.wood_spoon_eaters.model.*
//import com.google.gson.Gson
//import com.google.gson.JsonDeserializationContext
//import com.google.gson.JsonDeserializer
//import com.google.gson.JsonElement
//import com.google.gson.reflect.TypeToken
//import java.lang.reflect.Type
//
//class DeserializerJsonAppSetting : JsonDeserializer<AppSetting> {
//    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): AppSetting {
//
//        val gson = Gson()
//        val jsonObject = json?.asJsonObject
//
//        val appSetting = AppSetting(null, null, null, null)
//        appSetting.id = jsonObject?.get("id")?.asLong
//        appSetting.key = jsonObject?.get("key")?.asString
//        appSetting.data_type = jsonObject?.get("data_type")?.asString
//
//        when (appSetting.data_type) {
//            "string" -> { //string
//                appSetting.value = jsonObject?.get("value")?.asString
//            }
//            "integer" -> { //integer
//                appSetting.value = jsonObject?.get("value")?.asInt
//            }
//            "decimal" -> { //decimal
//                appSetting.value = jsonObject?.get("value")?.asBigDecimal
//            }
//            "price" -> { //price
//                val type = object : TypeToken<Price>() {}.type
//                appSetting.value = gson.fromJson(jsonObject?.get("value"), type)
////                appSetting.value = jsonObject?.get("value") as Price
//            }
//            "key_value" -> { //key-value
//                var map: Map<String, Any> = HashMap()
//                map = Gson().fromJson(jsonObject?.get("value"), map.javaClass)
//                Log.d("wowDeserializerJson", "map: $map")
//                appSetting.value = map
//            }
//            "boolean" -> { //boolean
//                appSetting.value = jsonObject?.get("value")?.asBoolean
//            }
//            "csv_array" -> { //array
//                appSetting.value = jsonObject?.get("value")
//            }
//        }
//        return appSetting
//    }
//
//}