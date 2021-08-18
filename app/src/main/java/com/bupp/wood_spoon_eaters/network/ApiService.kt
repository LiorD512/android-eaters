package com.bupp.wood_spoon_eaters.network

import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.RestaurantRepository
import com.bupp.wood_spoon_eaters.repositories.RestaurantRepository.RestaurantResult
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {


    //General
    @GET("eaters/utils/meta")
    suspend fun getMetaData(): ServerResponse<MetaDataModel>

    //Login end-points
    @FormUrlEncoded
    @POST("eaters/auth/get_code")
    suspend fun getCode(@Field("phone_number") phone: String): ServerResponse<Any>

    @FormUrlEncoded
    @POST("eaters/auth/validate_code")
    suspend fun validateCode(@Field("phone_number") phone: String, @Field("code") code: String): ServerResponse<Eater>

    //New Order


    @GET("cooks/{cook_id}/reviews")
    suspend fun getCookReview(@Path(value = "cook_id", encoded = true) cookId: Long): ServerResponse<Review>



    //Address
    @DELETE("eaters/me/addresses/{address_id}")
    suspend fun deleteAddress(@Path(value = "address_id", encoded = true) addressId: Long): ServerResponse<Any>



    //Feed
//    @GET("eaters/me/feed")
//    suspend fun getFeedFlow(
//        @Query("page") page: Int = 1,
//        @Query("limit") limit: Int = 20,
//        @Query("lat") lat: Double? = null, @Query("lng") lng: Double? = null,
//        @Query("address_id") addressId: Long? = null, @Query("timestamp") timestamp: String? = null
//    ): ServerResponse<List<FeedFlow>>

//    @GET("eaters/me/feed")
    @GET
    suspend fun getFeed(
        @Url url: String,
        @Query("lat") lat: Double? = null, @Query("lng") lng: Double? = null,
        @Query("address_id") addressId: Long? = null, @Query("timestamp") timestamp: String? = null
    ): ServerResponse<FeedResult>

    @GET
    suspend fun getHrefCollection(
        @Url url: String,
    ): ServerResponse<List<FeedSectionCollectionItem>>

    @FormUrlEncoded
    @POST("eaters/me/presigned_urls")
    fun postDishSuggestion(@Field("dish_name") dishName: String, @Field("dish_description") dishDescription: String): Call<ServerResponse<Any>>

    @GET("eaters/me/triggers")
    suspend fun getTriggers(): ServerResponse<Trigger>

    @GET("eaters/me/stripe/ephemeral_key")
    fun getEphemeralKey(): Observable<ResponseBody>

    @GET("eaters/me/campaigns/active")
    suspend fun getUserCampaign(): ServerResponse<List<Campaign>>

    @FormUrlEncoded
    @POST("eaters/me/campaigns/interactions/referee")
    suspend fun validateReferralToken(@Field("referral_token") token: String): ServerResponse<Any>

    /** Restaurant **/
    @GET("cooks/{cook_id}")
    suspend fun getRestaurant(
        @Path(value = "cook_id", encoded = true) restaurantId: Long,
        @Query("lat") lat: Double? = null, @Query("lng") lng: Double? = null,
        @Query("address_id") addressId: Long? = null
    ): ServerResponse<Restaurant>

    //cook likes
    @POST("cooks/{cook_id}/likes")
    suspend fun likeCook(@Path(value = "cook_id", encoded = true) cookId: Long): ServerResponse<Any>

    @DELETE("cooks/{cook_id}/likes")
    suspend fun unlikeCook(@Path(value = "cook_id", encoded = true) cookId: Long): ServerResponse<Any>

    @FormUrlEncoded
    @PATCH("eaters/me/campaigns/interactions/{user_interaction_id}")
    suspend fun updateCampaignStatus(@Path(value = "user_interaction_id", encoded = true) userInteractionId: Long, @Field("user_interaction_status") status: String): ServerResponse<Any>

    //Utils
    @POST("eaters/me/presigned_urls")
    suspend fun postEaterPreSignedUrl(): ServerResponse<PreSignedUrl>

    //Eater
    @GET("eaters/me")
    suspend fun getMe(): ServerResponse<Eater>

    @GET("eaters/me")
    fun getMeCall(): Call<ServerResponse<Eater>>

    @POST("eaters/me")
    suspend fun postMe(@Body eater: EaterRequest): ServerResponse<Eater>

    @POST("eaters/me/addresses")
    suspend fun postNewAddress(@Body addressRequest: AddressRequest): ServerResponse<Address>


    @POST("eaters/me")
    suspend fun postDeviceDetails(@Body device: DeviceDetails): ServerResponse<Any>


    @POST("eaters/me")
    suspend fun postEaterNotificationGroup(@Body eater: SettingsRequest): ServerResponse<Eater>

    @DELETE("eaters/me")
    suspend fun deleteMe(): ServerResponse<Any>

    @FormUrlEncoded
    @POST("eaters/me")
    suspend fun postEaterNotificationGroup(@Field("notification_group_ids[]") notificationGroupIds: List<Long>?): ServerResponse<Eater>

    @POST("eaters/me/searches")
    suspend fun search(@Body searchRequest: SearchRequest): ServerResponse<List<Search>>

    @GET("eaters/me/searches/{id}")
    fun getNextSearch(@Path(value = "id", encoded = true) searchId: Long, @Field("page") page: String): Call<ServerResponse<ArrayList<Search>>>

    @GET("cooks/{cook_id}")
    suspend fun getCook(
        @Path(value = "cook_id", encoded = true) cookId: Long,
        @Query("address_id") addressId: Long? = null,
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null,
        @Query("timestamp") timestamp: String? = null,
        @Query("event_id") eventId: Long? = null
    ): ServerResponse<Cook>

    //Single Dish
//    @GET("menu_items/{menu_item_id}/dish")
//    fun getSingleDish(
//        @Path(value = "menu_item_id", encoded = true) menuItemId: Long,
//        @Query("lat") lat: Double? = null,
//        @Query("lng") lng: Double? = null,
//        @Query("address_id") addressId: Long? = null,
//        @Query("timestamp") timestamp: String? = null
//    ): Call<ServerResponse<FullDish>>


    //New Order calls
    @GET("menu_items/{menu_item_id}/dish")
    suspend fun getSingleDish(
        @Path(value = "menu_item_id", encoded = true) menuItemId: Long,
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null,
        @Query("address_id") addressId: Long? = null,
        @Query("timestamp") timestamp: String? = null
    ): ServerResponse<FullDish>

    @POST("eaters/me/orders")
    suspend fun postOrder(@Body orderRequest: OrderRequest): ServerResponse<Order>

    @POST("eaters/me/orders/{order_id}")
    suspend fun updateOrder(@Path(value = "order_id", encoded = true) orderId: Long, @Body orderRequest: OrderRequest): ServerResponse<Order>

    @POST("eaters/me/orders/{order_id}/checkout")
    suspend fun checkoutOrder(@Path(value = "order_id", encoded = true) orderId: Long, @Query("source_id") cardId: String? = null): ServerResponse<Any>

//    @POST("eaters/me/orders/{order_id}/finalize")
//    fun finalizeOrder(@Path(value = "order_id", encoded = true) orderId: Long): Call<ServerResponse<Void>>

    @DELETE("eaters/me/orders/{order_id}/")
    suspend fun cancelOrder(@Path(value = "order_id", encoded = true) orderId: Long, @Query("notes") notes: String? = null): ServerResponse<Any>

    @GET("eaters/me/orders/{order_id}/ups_shipping_rates")
    suspend fun getUpsShippingRates(@Path(value = "order_id", encoded = true) orderId: Long): ServerResponse<List<ShippingMethod>>

    @GET("eaters/me/orders/{order_id}/delivery_times")
    suspend fun getOrderDeliveryTimes(@Path(value = "order_id", encoded = true) orderId: Long): ServerResponse<List<DeliveryDates>>


    //Eater Data
    @GET("eaters/me/orders/trackable")
    suspend fun getTraceableOrders(): ServerResponse<List<Order>>

    @GET("eaters/me/favorites")
    suspend fun getEaterFavorites(
        @Query("lat") lat: Double? = null, @Query("lng") lng: Double? = null,
        @Query("address_id") addressId: Long? = null, @Query("timestamp") timestamp: String? = null
    ): ServerResponse<Search>

//    @GET("eaters/me/orders/trackable")
//    fun getTrackableOrdersObservable(): Observable<ServerResponse<ArrayList<Order>>>

    @GET("eaters/me/orders")
    suspend fun getOrders(): ServerResponse<List<Order>>

    @GET("eaters/me/orders/{order_id}")
    suspend fun getOrderById(@Path(value = "order_id", encoded = true) orderId: Long): ServerResponse<Order>


    //Profile data
//    @GET("eaters/me/dishes/ordered")
//    fun getEaterOrdered(
//        @Query("lat") lat: Double? = null, @Query("lng") lng: Double? = null,
//        @Query("address_id") addressId: Long? = null, @Query("timestamp") timestamp: String? = null
//    ): Call<ServerResponse<Search>>

//    @GET("eaters/me/favorites")
//    fun getEaterFavorites(
//        @Query("lat") lat: Double? = null, @Query("lng") lng: Double? = null,
//        @Query("address_id") addressId: Long? = null, @Query("timestamp") timestamp: String? = null
//    ): Call<ServerResponse<Search>>


//    //Feed
//    @GET("eaters/me/feed")
//    fun getFeed(
//        @Query("lat") lat: Double? = null, @Query("lng") lng: Double? = null,
//        @Query("address_id") addressId: Long? = null, @Query("timestamp") timestamp: String? = null
//    ): Call<ServerResponse<ArrayList<Feed>>>


    //dish likes
    @POST("dishes/{dish_id}/likes")
    fun likeDish(@Path(value = "dish_id", encoded = true) dishId: Long): Call<ServerResponse<Any>>

    @DELETE("dishes/{dish_id}/likes")
    fun unlikeDish(@Path(value = "dish_id", encoded = true) dishId: Long): Call<ServerResponse<Any>>


    //Reports

    //Post Report
    @POST("eaters/me/orders/{order_id}/reports")
    suspend fun postReport(@Path(value = "order_id", encoded = true) orderId: Long, @Body reports: Reports): ServerResponse<Any>

    //Post Review
    @POST("eaters/me/orders/{order_id}/reviews")
    suspend fun postReview(@Path(value = "order_id", encoded = true) orderId: Long, @Body reviewRequest: ReviewRequest): ServerResponse<Any>

    @GET("eaters/me/events/{event_id}")
    fun getEventById(@Path(value = "event_id", encoded = true) eventId: String): Call<ServerResponse<Event>>

    @PUT
    suspend fun uploadAsset(@Url uploadUrl: String, @Body photo: RequestBody): ResponseBody

}
