package com.bupp.wood_spoon_eaters.di.abs

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

        val search = Search(null, null, null, null)
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