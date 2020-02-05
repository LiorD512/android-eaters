package com.bupp.wood_spoon_eaters.di.abs

import android.os.Parcelable
import android.util.Log
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.Pagination
import com.bupp.wood_spoon_eaters.model.Search
import com.bupp.wood_spoon_eaters.utils.Constants
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class DeserializerJsonSearch: JsonDeserializer<Search>{
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Search {

        val gson = Gson()
        val jsonObject = json?.asJsonObject

        val search = Search()
        search.id = jsonObject?.get("id")?.asLong
        search.resource = jsonObject?.get("resource")?.asString


        val paginationJson = jsonObject?.getAsJsonObject("pagination")
        search.pagination = gson.fromJson(paginationJson, Pagination::class.java)


        val cookJsonArr = jsonObject?.getAsJsonArray("results")
        when(search.resource){
            Constants.RESOURCE_TYPE_COOK -> {
                val type = object : TypeToken<ArrayList<Cook>>(){}.type
                search.results = gson.fromJson(cookJsonArr, type)

            }
            Constants.RESOURCE_TYPE_DISH -> {
                val type = object : TypeToken<ArrayList<Dish>>(){}.type
                search.results = gson.fromJson(cookJsonArr, type)
            }
        }
        return search
    }

}

//when(search.resource){
//    Constants.RESOURCE_TYPE_COOK -> {
//        cookJsonArr?.let {
//            val list = it.mapNotNull { jsonObj ->
//                Log.d("wowDeserializerSearch", "jsonObj: $jsonObj")
//                gson.fromJson(jsonObj, Cook::class.java)
//            }
//            Log.d("wowDeserializerSearch", "list: $list")
//            val list2 = list as ArrayList<Cook>
//            Log.d("wowDeserializerSearch", "list2: $list2")
//            search.results = list2 as ArrayList<Parcelable>
//        }
//        //val type = object : TypeToken<ArrayList<Cook>>(){}.type
//        //search.results = gson.fromJson<ArrayList<Parcelable>>(cookJsonArr, type)
//
//    }
//    Constants.RESOURCE_TYPE_DISH -> {
//        cookJsonArr?.let {
//            val list = it.mapNotNull { jsonObj ->
//                Log.d("wowDeserializerSearch", "jsonObj: $jsonObj")
//                gson.fromJson(jsonObj, Dish::class.java)
//            }
//            Log.d("wowDeserializerSearch", "list: $list")
//            val list2 = list as ArrayList<Dish>
//            Log.d("wowDeserializerSearch", "list2: $list2")
//            search.results = list2 as ArrayList<Parcelable>
//        }
////                val type = object : TypeToken<ArrayList<Dish>>(){}.type
////                search.results = gson.fromJson(cookJsonArr, type)
//    }
//}