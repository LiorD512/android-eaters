package com.bupp.wood_spoon_eaters.network

import com.bupp.wood_spoon_eaters.model.*
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
    suspend fun getCode(@Field("phone_number") phone: String): ServerResponse<Void>

    @FormUrlEncoded
    @POST("eaters/auth/validate_code")
    suspend fun validateCode(@Field("phone_number") phone: String, @Field("code") code: String): ServerResponse<Eater>

    //New Order


    @GET("cooks/{cook_id}/reviews")
    suspend fun getDishReview(@Path(value = "cook_id", encoded = true) cookId: Long): ServerResponse<Review>



    //Address
    @DELETE("eaters/me/addresses/{address_id}")
    suspend fun deleteAddress(@Path(value = "address_id", encoded = true) addressId: Long): ServerResponse<Void>



    //Feed
    @GET("eaters/me/feed")
    suspend fun getFeedFlow(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("lat") lat: Double? = null, @Query("lng") lng: Double? = null,
        @Query("address_id") addressId: Long? = null, @Query("timestamp") timestamp: String? = null
    ): ServerResponse<List<FeedFlow>>

    @GET("eaters/me/feed")
    suspend fun getFeed(
        @Query("lat") lat: Double? = null, @Query("lng") lng: Double? = null,
        @Query("address_id") addressId: Long? = null, @Query("timestamp") timestamp: String? = null
    ): ServerResponse<List<Feed>>

    @FormUrlEncoded
    @POST("eaters/me/presigned_urls")
    fun postDishSuggestion(@Field("dish_name") dishName: String, @Field("dish_description") dishDescription: String): Call<ServerResponse<Void>>

    @GET("eaters/me/triggers")
    fun getTriggers(): Call<ServerResponse<Trigger>>

    @GET("eaters/me/stripe/ephemeral_key")
    fun getEphemeralKey(): Observable<ResponseBody>

//    @GET("eaters/me/stripe/ephemeral_key")
//    fun getEphemeralKey2(): Call<ServerResponse<Any>>

    @FormUrlEncoded
    @POST("eaters/me/referrals")
    fun postCampaignReferrals(@Field("sid") sid: String, @Field("cid") cid: String? = null): Observable<ServerResponse<Void>>

    @GET("eaters/me/campaigns/current")
    fun getCurrentShareCampaign(): Call<ServerResponse<Campaign>>


    //Utils
    @FormUrlEncoded
    @POST("eaters/me/presigned_urls")
    fun postEaterPreSignedUrl(@Field("type") type: String): Call<ServerResponse<PreSignedUrl>>


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
    fun postDeviceDetails(@Body device: DeviceDetails): Call<ServerResponse<Void>>

    @FormUrlEncoded
    @POST("eaters/me")
    fun postEaterNotificationGroup(@Field("notification_group_ids") notificationGroupIds: Array<Long>): Call<ServerResponse<Eater>>

    @POST("eaters/me/searches")
    fun search(@Body searchRequest: SearchRequest): Call<ServerResponse<ArrayList<Search>>>

    @GET("eaters/me/searches/{id}")
    fun getNextSearch(@Path(value = "id", encoded = true) searchId: Long, @Field("page") page: String): Call<ServerResponse<ArrayList<Search>>>

    @GET("cooks/{cook_id}")
    fun getCook(
        @Path(value = "cook_id", encoded = true) cookId: Long,
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null,
        @Query("timestamp") timestamp: String? = null,
        @Query("event_id") eventId: Long? = null
    ): Call<ServerResponse<Cook>>

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
    suspend fun checkoutOrder(@Path(value = "order_id", encoded = true) orderId: Long, @Query("source_id") cardId: String? = null): ServerResponse<Void>

//    @POST("eaters/me/orders/{order_id}/finalize")
//    fun finalizeOrder(@Path(value = "order_id", encoded = true) orderId: Long): Call<ServerResponse<Void>>

    @DELETE("eaters/me/orders/{order_id}/")
    fun cancelOrder(@Path(value = "order_id", encoded = true) orderId: Long, @Query("notes") notes: String? = null): Call<ServerResponse<Void>>

    @GET("eaters/me/orders/{order_id}/ups_shipping_rates")
    fun getUpsShippingRates(@Path(value = "order_id", encoded = true) orderId: Long): Call<ServerResponse<ArrayList<ShippingMethod>>>


    //Active Order
    @GET("eaters/me/orders/trackable")
    suspend fun getTraceableOrders(): ServerResponse<List<Order>>

//    @GET("eaters/me/orders/trackable")
//    fun getTrackableOrdersObservable(): Observable<ServerResponse<ArrayList<Order>>>

    @GET("eaters/me/orders")
    fun getOrders(): Call<ServerResponse<ArrayList<Order>>>

    @GET("eaters/me/orders/{order_id}")
    fun getOrderById(@Path(value = "order_id", encoded = true) orderId: Long): Call<ServerResponse<Order>>


    //Profile data
    @GET("eaters/me/dishes/ordered")
    fun getEaterOrdered(
        @Query("lat") lat: Double? = null, @Query("lng") lng: Double? = null,
        @Query("address_id") addressId: Long? = null, @Query("timestamp") timestamp: String? = null
    ): Call<ServerResponse<Search>>

    @GET("eaters/me/favorites")
    fun getEaterFavorites(
        @Query("lat") lat: Double? = null, @Query("lng") lng: Double? = null,
        @Query("address_id") addressId: Long? = null, @Query("timestamp") timestamp: String? = null
    ): Call<ServerResponse<Search>>


//    //Feed
//    @GET("eaters/me/feed")
//    fun getFeed(
//        @Query("lat") lat: Double? = null, @Query("lng") lng: Double? = null,
//        @Query("address_id") addressId: Long? = null, @Query("timestamp") timestamp: String? = null
//    ): Call<ServerResponse<ArrayList<Feed>>>


    //dish likes
    @POST("dishes/{dish_id}/likes")
    fun likeDish(@Path(value = "dish_id", encoded = true) dishId: Long): Call<ServerResponse<Void>>

    @DELETE("dishes/{dish_id}/likes")
    fun unlikeDish(@Path(value = "dish_id", encoded = true) dishId: Long): Call<ServerResponse<Void>>

    //Reports

    //Post Report
    @POST("eaters/me/orders/{order_id}/reports")
    fun postReport(@Path(value = "order_id", encoded = true) orderId: Long, @Body reports: Reports): Call<ServerResponse<Void>>

    //Post Review
    @POST("eaters/me/orders/{order_id}/reviews")
    fun postReview(@Path(value = "order_id", encoded = true) orderId: Long, @Body reviewRequest: ReviewRequest): Call<ServerResponse<Void>>

    @GET("eaters/me/events/{event_id}")
    fun getEventById(@Path(value = "event_id", encoded = true) eventId: String): Call<ServerResponse<Event>>

    @PUT
    suspend fun uploadAsset(@Url uploadUrl: String, @Body photo: RequestBody): ResponseBody

}
