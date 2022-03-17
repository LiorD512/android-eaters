package com.bupp.wood_spoon_eaters.network

import com.bupp.wood_spoon_eaters.bottom_sheets.reviews.Review
import com.bupp.wood_spoon_eaters.bottom_sheets.reviews.ReviewRequest
import com.bupp.wood_spoon_eaters.model.*
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    //AppConfig
    @VERSION("v3")
    @GET("eaters/utils/config")
    suspend fun getAppSettings(): ServerResponse<AppSettings>

    //MetaData
    @VERSION("v3")
    @GET("eaters/utils/meta")
    suspend fun getMetaData(): ServerResponse<MetaDataModel>

    //Login end-points
    @FormUrlEncoded
    @POST("eaters/auth/get_code")
    suspend fun getCode(
        @Field("phone_number") phone: String
    ): ServerResponse<Any>

    @FormUrlEncoded
    @POST("eaters/auth/validate_code")
    suspend fun validateCode(
        @Field("phone_number") phone: String,
        @Field("code") code: String
    ): ServerResponse<Eater>

    //New Order
    @VERSION("v3")
    @GET("cooks/{cook_id}/reviews")
    suspend fun getCookReview(
        @Path(value = "cook_id", encoded = true) cookId: Long
    ): ServerResponse<Review>

    //Address
    @DELETE("eaters/me/addresses/{address_id}")
    suspend fun deleteAddress(
        @Path(value = "address_id", encoded = true) addressId: Long
    ): ServerResponse<Any>

    @VERSION("v4")
    @GET("eaters/me/feed/order_again")
    suspend fun getRecentOrders(
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null,
        @Query("address_id") addressId: Long? = null,
        @Query("timestamp") timestamp: String? = null,
    ): ServerResponse<List<FeedRestaurantSection>>

    @VERSION("v3")
    @GET("eaters/me/search/tags")
    suspend fun getSearchTags(
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null,
        @Query("address_id") addressId: Long? = null
    ): ServerResponse<List<String>>

    @VERSION("v4")
    @GET("eaters/me/search")
    suspend fun search(
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null,
        @Query("address_id") addressId: Long? = null,
        @Query("timestamp") timestamp: String? = null,
        @Query("q") q: String? = null
    ): ServerResponse<FeedResult>

    @VERSION("v4")
    @GET("eaters/me/feed")
    suspend fun getFeed(
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null,
        @Query("address_id") addressId: Long? = null,
        @Query("timestamp") timestamp: String? = null
    ): ServerResponse<FeedResult>

    @GET()
    suspend fun getHrefCollection(
        @Url url: String,
    ): ServerResponse<List<FeedSectionCollectionItem>>

    @GET("eaters/me/triggers")
    suspend fun getTriggers(): ServerResponse<Trigger>

    @GET("eaters/me/stripe/ephemeral_key")
    fun getEphemeralKey(): Observable<ResponseBody>

    @GET("eaters/me/campaigns/active")
    suspend fun getUserCampaign(): ServerResponse<List<Campaign>>

    @FormUrlEncoded
    @POST("eaters/me/campaigns/interactions/referee")
    suspend fun validateReferralToken(
        @Field("referral_token") token: String
    ): ServerResponse<Any>

    /** Restaurant **/
    @VERSION("v3")
    @GET("cooks/{cook_id}")
    suspend fun getRestaurant(
        @Path(value = "cook_id", encoded = true) restaurantId: Long,
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null,
        @Query("address_id") addressId: Long? = null,
        @Query("q") query: String? = null
    ): ServerResponse<Restaurant>

    //cook likes
    @POST("eaters/me/likes/cooks/{id}")
    suspend fun likeCook(
        @Path(value = "id", encoded = true) cookId: Long
    ): ServerResponse<Any>

    @DELETE("eaters/me/likes/cooks/{id}")
    suspend fun unlikeCook(
        @Path(value = "id", encoded = true) cookId: Long
    ): ServerResponse<Any>

    @FormUrlEncoded
    @PATCH("eaters/me/campaigns/interactions/{user_interaction_id}")
    suspend fun updateCampaignStatus(
        @Path(value = "user_interaction_id", encoded = true) userInteractionId: Long,
        @Field("user_interaction_status") status: String
    ): ServerResponse<Any>

    //Utils
    @POST("eaters/me/presigned_urls")
    suspend fun postEaterPreSignedUrl(): ServerResponse<PreSignedUrl>

    //Eater
    @GET("eaters/me")
    suspend fun getMe(): ServerResponse<Eater>

    @POST("eaters/me")
    suspend fun postMe(
        @Body eater: EaterRequest
    ): ServerResponse<Eater>

    @POST("eaters/me/addresses")
    suspend fun postNewAddress(
        @Body addressRequest: AddressRequest
    ): ServerResponse<Address>

    @POST("eaters/me")
    suspend fun postDeviceDetails(
        @Body device: DeviceDetails
    ): ServerResponse<Any>

    @POST("eaters/me")
    suspend fun postEaterNotificationGroup(
        @Body eater: SettingsRequest
    ): ServerResponse<Eater>

    @DELETE("eaters/me")
    suspend fun deleteMe(): Response<Unit>

    @VERSION("v3")
    @GET("cooks/{cook_id}")
    suspend fun getCook(
        @Path(value = "cook_id", encoded = true) cookId: Long,
        @Query("address_id") addressId: Long? = null,
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null,
        @Query("timestamp") timestamp: String? = null,
        @Query("event_id") eventId: Long? = null
    ): ServerResponse<Cook>

    //New Order calls

    @VERSION("v3")
    @GET("dishes/{dish_id}")
    suspend fun getSingleDish(
        @Path(value = "dish_id", encoded = true) dishId: Long,
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null,
        @Query("address_id") addressId: Long? = null,
        @Query("timestamp") timestamp: String? = null
    ): ServerResponse<FullDish>

    @VERSION("v3")
    @GET("menu_items/{menu_item_id}/dish")
    suspend fun getSingleMenuItem(
        @Path(value = "menu_item_id", encoded = true) menuItemId: Long,
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null,
        @Query("address_id") addressId: Long? = null,
        @Query("timestamp") timestamp: String? = null
    ): ServerResponse<FullDish>

    @POST("eaters/me/orders")
    suspend fun postOrder(
        @Body orderRequest: OrderRequest
    ): ServerResponse<Order>

    @POST("eaters/me/orders/{order_id}")
    suspend fun updateOrder(
        @Path(value = "order_id", encoded = true) orderId: Long,
        @Body orderRequest: OrderRequest
    ): ServerResponse<Order>

    @POST("eaters/me/orders/{order_id}/checkout")
    suspend fun checkoutOrder(
        @Path(value = "order_id", encoded = true) orderId: Long,
        @Query("source_id") cardId: String? = null
    ): ServerResponse<Any>

    @DELETE("eaters/me/orders/{order_id}/")
    suspend fun cancelOrder(
        @Path(value = "order_id", encoded = true) orderId: Long,
        @Query("notes") notes: String? = null
    ): ServerResponse<Any>

    @GET("eaters/me/orders/{order_id}/ups_shipping_rates")
    suspend fun getUpsShippingRates(
        @Path(value = "order_id", encoded = true) orderId: Long
    ): ServerResponse<List<ShippingMethod>>

    @GET("eaters/me/orders/{order_id}/delivery_times")
    suspend fun getOrderDeliveryTimes(
        @Path(value = "order_id", encoded = true) orderId: Long
    ): ServerResponse<List<DeliveryDates>>

    //Eater Data
    @GET("eaters/me/orders/trackable")
    suspend fun getTraceableOrders(): ServerResponse<List<Order>>

    @GET("eaters/me/orders")
    suspend fun getOrders(): ServerResponse<List<Order>>

    @GET("eaters/me/orders/{order_id}")
    suspend fun getOrderById(
        @Path(value = "order_id", encoded = true) orderId: Long
    ): ServerResponse<Order>

    //Post Report
    @POST("eaters/me/orders/{order_id}/reports")
    suspend fun postReport(
        @Path(value = "order_id", encoded = true) orderId: Long,
        @Body reports: Reports
    ): ServerResponse<Any>

    //Post Review
    @VERSION("v3")
    @POST("eaters/me/orders/{order_id}/reviews")
    suspend fun postReview(
        @Path(value = "order_id", encoded = true) orderId: Long,
        @Body reviewRequest: ReviewRequest
    ): ServerResponse<Any>

    //Ignore Review
    @POST("eaters/me/orders/{order_id}/reviews/ignore")
    suspend fun ignoreReview(
        @Path(value = "order_id", encoded = true) orderId: Long
    ): ServerResponse<Any>

    @PUT
    suspend fun uploadAsset(
        @Url uploadUrl: String,
        @Body photo: RequestBody
    ): ResponseBody
}
