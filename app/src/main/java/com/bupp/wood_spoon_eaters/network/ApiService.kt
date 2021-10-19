package com.bupp.wood_spoon_eaters.network

import com.bupp.wood_spoon_eaters.bottom_sheets.reviews.Review
import com.bupp.wood_spoon_eaters.bottom_sheets.reviews.ReviewRequest
import com.bupp.wood_spoon_eaters.model.*
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {


    //General
    @GET("v2/eaters/utils/meta")
    suspend fun getMetaData(): ServerResponse<MetaDataModel>

    //Login end-points
    @FormUrlEncoded
    @POST("v2/eaters/auth/get_code")
    suspend fun getCode(@Field("phone_number") phone: String): ServerResponse<Any>

    @FormUrlEncoded
    @POST("v2/eaters/auth/validate_code")
    suspend fun validateCode(@Field("phone_number") phone: String, @Field("code") code: String): ServerResponse<Eater>

    //New Order

    @GET("v2/cooks/{cook_id}/order_reviews")
    suspend fun getCookReview(@Path(value = "cook_id", encoded = true) cookId: Long): ServerResponse<Review>

    //Address
    @DELETE("v2/eaters/me/addresses/{address_id}")
    suspend fun deleteAddress(@Path(value = "address_id", encoded = true) addressId: Long): ServerResponse<Any>

    //Feed
//    @GET("eaters/me/feed")
//    suspend fun getFeedFlow(
//        @Query("page") page: Int = 1,
//        @Query("limit") limit: Int = 20,
//        @Query("lat") lat: Double? = null, @Query("lng") lng: Double? = null,
//        @Query("address_id") addressId: Long? = null, @Query("timestamp") timestamp: String? = null
//    ): ServerResponse<List<FeedFlow>>

    @GET("v2/eaters/me/feed")
    suspend fun getFeed(
        @Query("lat") lat: Double? = null, @Query("lng") lng: Double? = null,
        @Query("address_id") addressId: Long? = null, @Query("timestamp") timestamp: String? = null
    ): ServerResponse<FeedResult>

    @GET()
    suspend fun getHrefCollection(
        @Url url: String,
    ): ServerResponse<List<FeedSectionCollectionItem>>

    @GET("v2/eaters/me/triggers")
    suspend fun getTriggers(): ServerResponse<Trigger>

    @GET("v2/eaters/me/stripe/ephemeral_key")
    fun getEphemeralKey(): Observable<ResponseBody>

    @GET("v2/eaters/me/campaigns/active")
    suspend fun getUserCampaign(): ServerResponse<List<Campaign>>

    @FormUrlEncoded
    @POST("v2/eaters/me/campaigns/interactions/referee")
    suspend fun validateReferralToken(@Field("referral_token") token: String): ServerResponse<Any>

    /** Restaurant **/
    @GET("v2/cooks/{cook_id}")
    suspend fun getRestaurant(
        @Path(value = "cook_id", encoded = true) restaurantId: Long,
        @Query("lat") lat: Double? = null, @Query("lng") lng: Double? = null,
        @Query("address_id") addressId: Long? = null
    ): ServerResponse<Restaurant>

    //cook likes
    @POST("v2/eaters/me/likes/cooks/{id}")
    suspend fun likeCook(@Path(value = "id", encoded = true) cookId: Long): ServerResponse<Any>

    @DELETE("v2/eaters/me/likes/cooks/{id}")
    suspend fun unlikeCook(@Path(value = "id", encoded = true) cookId: Long): ServerResponse<Any>

    @FormUrlEncoded
    @PATCH("v2/eaters/me/campaigns/interactions/{user_interaction_id}")
    suspend fun updateCampaignStatus(@Path(value = "user_interaction_id", encoded = true) userInteractionId: Long, @Field("user_interaction_status") status: String): ServerResponse<Any>

    //Utils
    @POST("v2/eaters/me/presigned_urls")
    suspend fun postEaterPreSignedUrl(): ServerResponse<PreSignedUrl>

    //Eater
    @GET("v2/eaters/me")
    suspend fun getMe(): ServerResponse<Eater>

    @POST("v2/eaters/me")
    suspend fun postMe(@Body eater: EaterRequest): ServerResponse<Eater>

    @POST("v2/eaters/me/addresses")
    suspend fun postNewAddress(@Body addressRequest: AddressRequest): ServerResponse<Address>


    @POST("v2/eaters/me")
    suspend fun postDeviceDetails(@Body device: DeviceDetails): ServerResponse<Any>


    @POST("v2/eaters/me")
    suspend fun postEaterNotificationGroup(@Body eater: SettingsRequest): ServerResponse<Eater>

    @DELETE("v2/eaters/me")
    suspend fun deleteMe(): Response<Unit>

    @POST("v2/eaters/me/searches")
    suspend fun search(@Body searchRequest: SearchRequest): ServerResponse<List<Search>>

    @GET("v2/cooks/{cook_id}")
    suspend fun getCook(
        @Path(value = "cook_id", encoded = true) cookId: Long,
        @Query("address_id") addressId: Long? = null,
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null,
        @Query("timestamp") timestamp: String? = null,
        @Query("event_id") eventId: Long? = null
    ): ServerResponse<Cook>

    //New Order calls
    @GET("v2/menu_items/{menu_item_id}/dish")
    suspend fun getSingleDish(
        @Path(value = "menu_item_id", encoded = true) menuItemId: Long,
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null,
        @Query("address_id") addressId: Long? = null,
        @Query("timestamp") timestamp: String? = null
    ): ServerResponse<FullDish>

    @POST("v2/eaters/me/orders")
    suspend fun postOrder(@Body orderRequest: OrderRequest): ServerResponse<Order>

    @POST("v2/eaters/me/orders/{order_id}")
    suspend fun updateOrder(@Path(value = "order_id", encoded = true) orderId: Long, @Body orderRequest: OrderRequest): ServerResponse<Order>

    @POST("v2/eaters/me/orders/{order_id}/checkout")
    suspend fun checkoutOrder(@Path(value = "order_id", encoded = true) orderId: Long, @Query("source_id") cardId: String? = null): ServerResponse<Any>

    @DELETE("v2/eaters/me/orders/{order_id}/")
    suspend fun cancelOrder(@Path(value = "order_id", encoded = true) orderId: Long, @Query("notes") notes: String? = null): ServerResponse<Any>

    @GET("v2/eaters/me/orders/{order_id}/ups_shipping_rates")
    suspend fun getUpsShippingRates(@Path(value = "order_id", encoded = true) orderId: Long): ServerResponse<List<ShippingMethod>>

    @GET("v2/eaters/me/orders/{order_id}/delivery_times")
    suspend fun getOrderDeliveryTimes(@Path(value = "order_id", encoded = true) orderId: Long): ServerResponse<List<DeliveryDates>>

    //Eater Data
    @GET("v2/eaters/me/orders/trackable")
    suspend fun getTraceableOrders(): ServerResponse<List<Order>>

    @GET("v2/eaters/me/favorites")
    suspend fun getEaterFavorites(
        @Query("lat") lat: Double? = null, @Query("lng") lng: Double? = null,
        @Query("address_id") addressId: Long? = null, @Query("timestamp") timestamp: String? = null
    ): ServerResponse<Search>

    @GET("v2/eaters/me/orders")
    suspend fun getOrders(): ServerResponse<List<Order>>

    @GET("v2/eaters/me/orders/{order_id}")
    suspend fun getOrderById(@Path(value = "order_id", encoded = true) orderId: Long): ServerResponse<Order>

    //Post Report
    @POST("v2/eaters/me/orders/{order_id}/reports")
    suspend fun postReport(@Path(value = "order_id", encoded = true) orderId: Long, @Body reports: Reports): ServerResponse<Any>

    //Post Review
    @POST("v2/eaters/me/orders/{order_id}/order_reviews")
    suspend fun postReview(@Path(value = "order_id", encoded = true) orderId: Long, @Body reviewRequest: ReviewRequest): ServerResponse<Any>

    //Ignore Review
    @POST("v2/eaters/me/orders/{order_id}/reviews/ignore")
    suspend fun ignoreReview(@Path(value = "order_id", encoded = true) orderId: Long): ServerResponse<Any>

    @PUT
    suspend fun uploadAsset(@Url uploadUrl: String, @Body photo: RequestBody): ResponseBody

}
