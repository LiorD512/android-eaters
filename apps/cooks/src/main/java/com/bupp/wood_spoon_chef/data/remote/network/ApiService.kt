package com.bupp.wood_spoon_chef.data.remote.network


import com.bupp.wood_spoon_chef.data.remote.model.*
import com.bupp.wood_spoon_chef.data.remote.network.base.ServerResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiService {

    /** User Repository **/

    //Login end-points
    @FormUrlEncoded
    @POST("cooks/auth/get_code")
    suspend fun getCode(
        @Field("phone_number", encoded = false) phone: String
    ): ServerResponse<Any>

    @V3
    @FormUrlEncoded
    @POST("cooks/auth/validate_code")
    suspend fun validateCode(
        @Field("phone_number") phone: String,
        @Field("code") code: String
    ): ServerResponse<Cook>

    //Eater
    @V3
    @GET("cooks/me")
    suspend fun getMe(): ServerResponse<Cook>

    @V3
    @POST("cooks/me")
    suspend fun postMe(
        @Body eater: CookRequest
    ): ServerResponse<Cook>


    @DELETE("cooks/me")
    suspend fun deleteAccount(): ServerResponse<Any>

    @FormUrlEncoded
    @POST("web/contact")
    suspend fun postTicket(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("message") message: String
    ): ServerResponse<Any>

    @V3
    @GET("cooks/me/reviews")
    suspend fun getCookReview(): ServerResponse<Review>

    @POST("cooks/me/stripe/otls")
    suspend fun getOtl(): ServerResponse<Otl>

    /** Order Repository **/

    //Orders / cooking slots
    @GET("cooks/me/orders")
    suspend fun getOrders(): ServerResponse<List<Order>>

    //Order status
    @POST("cooks/me/orders/{order_id}/accept")
    suspend fun setStatusAccept(
        @Path(value = "order_id", encoded = true) orderId: Long
    ): ServerResponse<Any>

    @POST("cooks/me/orders/{order_id}/start")
    suspend fun setStatusStart(
        @Path(value = "order_id", encoded = true) orderId: Long
    ): ServerResponse<Any>

    @POST("cooks/me/orders/{order_id}/finish")
    suspend fun setStatusFinish(
        @Path(value = "order_id", encoded = true) orderId: Long
    ): ServerResponse<Any>

    @FormUrlEncoded
    @HTTP(
        method = "DELETE",
        path = "cooks/me/orders/{order_id}",
        hasBody = true
    )
    suspend fun cancelOrder(
        @Path(value = "order_id", encoded = true) orderId: Long,
        @Field("cancellation_reason_id") reasonId: Long?,
        @Field("notes") notes: String?
    ): ServerResponse<Any>

    /** Dish Repository **/

    // New Dish
    @POST("cooks/me/dishes")
    suspend fun postDish(
        @Body dish: DishRequest
    ): ServerResponse<Dish>

    @POST("cooks/me/dishes/{id}")
    suspend fun updateDish(
        @Path(value = "id", encoded = true) dishId: Long,
        @Body dish: DishRequest
    ): ServerResponse<Dish>

    @POST("cooks/me/dishes/{dish_id}/publish")
    suspend fun publishDish(
        @Path(value = "dish_id", encoded = true) dishId: Long
    ): ServerResponse<Dish>

    @POST("cooks/me/dishes/{dish_id}/unpublish")
    suspend fun unPublishDish(
        @Path(value = "dish_id", encoded = true) dishId: Long
    ): ServerResponse<Any>

    @POST("cooks/me/dishes/{dish_id}/duplicate")
    suspend fun duplicateDish(
        @Path(
            value = "dish_id",
            encoded = true
        ) dishId: Long
    ): ServerResponse<Any>

    @DELETE("cooks/me/dishes/{dish_id}")
    suspend fun hideDish(
        @Path(value = "dish_id", encoded = true) dishId: Long
    ): ServerResponse<Any>

    // My Dishes
    @GET("cooks/me/dishes")
    suspend fun getMyDishes(): ServerResponse<List<Dish>>

    @V3
    @GET("cooks/me/dishes")
    suspend fun fetchSectionsWithDishes(): ServerResponse<SectionWithDishes>

    @GET("cooks/me/dishes/{dish_id}")
    suspend fun getDishById(
        @Path(value = "dish_id", encoded = true) dishId: Long
    ): ServerResponse<Dish>

    /** Earning Repository **/


    @V3
    @GET("cooks/me/stats")
    suspend fun getStats(): ServerResponse<Earnings>

    @V3
    @GET("cooks/me/stats/balance")
    suspend fun getBalance(): ServerResponse<Any>

    @V3
    @GET("cooks/me/stats/lifetime")
    suspend fun getLifetime(): ServerResponse<Any>


    /** MetaData Repository **/

    @GET("cooks/utils/meta")
    suspend fun getMetaData(
      @Query("ff") features: String?,
      @Query("user_attrs") userAttributes: String?
    ): ServerResponse<MetaDataModel>

    /** Events Repository **/

    //Events
    @GET("cooks/me/events")
    suspend fun getEvents(): ServerResponse<List<Event>>

    @GET("cooks/me/events/{event_id}/validate_code")
    suspend fun validateCode(
        @Path(value = "event_id", encoded = true) eventId: Long,
        @Query("access_code") code: String
    ): ServerResponse<Event>

    @POST("cooks/me/events/{event_id}/invitations")
    suspend fun requestJoinEvent(
        @Path(value = "event_id", encoded = true) eventId: Long
    ): Any

    /** __________________ **/

    // AWS
    @FormUrlEncoded
    @POST("cooks/me/presigned_urls")
    suspend fun getCookPreSignedUrl(
        @Field("type") type: String
    ): ServerResponse<PreSignedUrl>

    @FormUrlEncoded
    @POST("cooks/me/dishes/presigned_urls")
    suspend fun getDishPreSignedUrl(
        @Field("type") type: String
    ): ServerResponse<PreSignedUrl>

    @PUT
    suspend fun uploadAsset(
        @Url uploadUrl: String,
        @Body photo: RequestBody
    ): ResponseBody

    /** __________________ **/


    @POST("cooks/me")
    suspend fun postDeviceDetails(
        @Body device: DeviceDetails
    ): ServerResponse<Any>
}

suspend fun ApiService.getMetaData(features: List<String>, userAttributes: Map<String, Any>) =
        getMetaData(
            features = features.joinToString(separator = ","),
            userAttributes = userAttributes.entries.joinToString(separator = ",") { "${it.key}=${it.value}" }.ifBlank { null }
        )
