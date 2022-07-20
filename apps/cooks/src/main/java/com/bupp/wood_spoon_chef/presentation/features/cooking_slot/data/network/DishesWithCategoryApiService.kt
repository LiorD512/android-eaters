package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.network

import com.bupp.wood_spoon_chef.data.remote.model.SectionWithDishes
import com.bupp.wood_spoon_chef.data.remote.network.V3
import com.bupp.wood_spoon_chef.data.remote.network.base.ServerResponse
import retrofit2.http.GET

interface DishesWithCategoryApiService {

    @V3
    @GET("cooks/me/dishes")
    suspend fun getSectionsAndDishes(): ServerResponse<SectionWithDishes>
}