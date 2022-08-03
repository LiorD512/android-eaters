package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.network

import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlotRequest
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlotSlim
import com.bupp.wood_spoon_chef.data.remote.model.Order
import com.bupp.wood_spoon_chef.data.remote.network.V3
import com.bupp.wood_spoon_chef.data.remote.network.base.ServerResponse
import retrofit2.http.*

interface CookingSlotApiService {

    @V3
    @GET("cooks/me/cooking_slots")
    suspend fun getCookingSlot(
        @Query("start_time") startTimeMillis: Long,
        @Query("end_time") endTimeMillis: Long
    ): ServerResponse<List<CookingSlotSlim>>

    @POST("cooks/me/cooking_slots")
    suspend fun postCookingSlot(
        @Body cookingSlot: CookingSlotRequest
    ): ServerResponse<CookingSlot>

    @POST("cooks/me/cooking_slots/{slot_id}")
    suspend fun updateCookingSlot(
        @Path(value = "slot_id", encoded = true) slotId: Long,
        @Body cookingSlot: CookingSlotRequest
    ): ServerResponse<CookingSlot>

    @DELETE("cooks/me/cooking_slots/{slot_id}")
    suspend fun cancelCookingSlot(
        @Path(value = "slot_id", encoded = true) slotId: Long,
        @Query(value = "detach") detach: Boolean?
    ): ServerResponse<Any>

    @GET("cooks/me/cooking_slots/trackable")
    suspend fun getTrackableCookingSlot(): ServerResponse<List<CookingSlot>>

    @FormUrlEncoded
    @POST("cooks/me/cooking_slots/{slot_id}")
    suspend fun updateCookingSlotAvailability(
        @Path(value = "slot_id", encoded = true) cookingSlotId: Long,
        @Field("is_active") isActive: Boolean
    ): ServerResponse<CookingSlot>

    @GET("cooks/me/cooking_slots/active")
    suspend fun getActiveCookingSlot(): ServerResponse<CookingSlot>

    @GET("cooks/me/cooking_slots/{slot_id}/orders")
    suspend fun getCookingSlotOrders(
        @Path(value = "slot_id", encoded = true) cookingSlotId: Long
    ): ServerResponse<List<Order>>

    @GET("cooks/me/cooking_slots/{slot_id}")
    suspend fun getCookingSlotById(
        @Path(value = "slot_id", encoded = true) cookingSlotId: Long
    ): ServerResponse<CookingSlot>
}