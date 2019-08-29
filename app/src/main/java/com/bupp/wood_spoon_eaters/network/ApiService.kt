package com.bupp.wood_spoon_eaters.network

import com.bupp.wood_spoon_eaters.model.*
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*

interface ApiService {


    //Login end-points
    @FormUrlEncoded
    @POST("eaters/auth/get_code")
    fun getCode(@Field("phone_number") phone: String): Call<Void>

    @FormUrlEncoded
    @POST("eaters/auth/validate_code")
    fun validateCode(@Field("phone_number") phone: String, @Field("code") code: String): Call<ServerResponse<Eater>>


    //Address
    @DELETE("eaters/me/addresses/{address_id}")
    fun deleteAddress(@Path(value = "address_id", encoded = true) addressId: Long): Call<ServerResponse<Void>>

    @POST("eaters/me/addresses/{address_id}")
    fun updateAddress(@Path(value = "address_id", encoded = true) addressId: Long, @Body addressRequest: AddressRequest): Call<ServerResponse<Void>>


    //General
    @GET("eaters/utils/meta")
    fun getMetaData(): Observable<ServerResponse<MetaDataModel>>

    @GET("eaters/utils/meta")
    fun getMetaDataCall(): Call<ServerResponse<MetaDataModel>>

    @FormUrlEncoded
    @POST("eaters/me/presigned_urls")
    fun postDishSuggestion(@Field("dish_name") dishName: String,@Field("dish_description") dishDescription: String): Call<ServerResponse<Void>>

    @GET("eaters/me/triggers")
    fun getTriggers(): Call<ServerResponse<Trigger>>




    //Utils
    @FormUrlEncoded
    @POST("eaters/me/presigned_urls")
    fun postEaterPreSignedUrl(@Field("type") type: String): Call<ServerResponse<PreSignedUrl>>




    //Eater
    @GET("eaters/me")
    fun getMe(): Observable<ServerResponse<Eater>>

    @GET("eaters/me")
    fun getMeCall(): Call<ServerResponse<Eater>>

    @POST("eaters/me")
    fun postMe(@Body eater: EaterRequest): Call<ServerResponse<Eater>>

    @POST("eaters/me/searches")
    fun search(@Body searchRequest: SearchRequest): Call<ServerResponse<ArrayList<Search>>>

    @GET("eaters/me/searches/{id}")
    fun getNextSearch(@Path(value = "id", encoded = true) searchId: Long, @Field("page") page: String): Call<ServerResponse<ArrayList<Search>>>


    //Single Dish
    @GET("menu_items/{menu_item_id}/dish")
    fun getMenuItemsDetails(@Path(value = "menu_item_id", encoded = true) menuItemId: Long, @Query("lat") lat: Double? = null, @Query("lng") lng: Double? = null,
                            @Query("address_id") addressId: Long? = null, @Query("timestamp") timestamp: String? = null): Call<ServerResponse<FullDish>>



    //New Order calls
    @POST("eaters/me/orders")
    fun postOrder(@Body orderRequest: OrderRequest): Call<ServerResponse<Order>>

    @POST("eaters/me/orders/{order_id}/checkout")
    fun checkoutOrder(@Path(value = "order_id", encoded = true) orderId: Long): Call<ServerResponse<Void>>

    @POST("eaters/me/orders/{order_id}/finalize")
    fun finalizeOrder(@Path(value = "order_id", encoded = true) orderId: Long): Call<ServerResponse<Void>>

    @DELETE("eaters/me/orders/{order_id}/")
    fun cancelOrder(@Path(value = "order_id", encoded = true) orderId: Long, @Query("notes") notes: String? = null): Call<ServerResponse<Void>>


    //Active Order
    @GET("eaters/me/orders/trackable")
    fun getTrackableOrders(): Call<ServerResponse<ArrayList<Order>>>

    @GET("eaters/me/orders/trackable")
    fun getTrackableOrdersObservable(): Observable<ServerResponse<ArrayList<Order>>>

    @GET("eaters/me/orders")
    fun getOrders(): Call<ServerResponse<ArrayList<Order>>>

    @GET("eaters/me/orders/{order_id}")
    fun getOrderById(@Path(value = "order_id", encoded = true) orderId: Long): Call<ServerResponse<Order>>


    //Profile data
    @GET("eaters/me/dishes/ordered")
    fun getEaterOrdered(@Query("lat") lat: Double? = null, @Query("lng") lng: Double? = null,
                @Query("address_id") addressId: Long? = null, @Query("timestamp") timestamp: String? = null): Call<ServerResponse<Search>>

    @GET("eaters/me/favorites")
    fun getEaterFavorites(@Query("lat") lat: Double? = null, @Query("lng") lng: Double? = null,
                @Query("address_id") addressId: Long? = null, @Query("timestamp") timestamp: String? = null): Call<ServerResponse<Search>>


    //Feed
    @GET("eaters/me/feed")
    fun getFeed(@Query("lat") lat: Double? = null, @Query("lng") lng: Double? = null,
                @Query("address_id") addressId: Long? = null, @Query("timestamp") timestamp: String? = null): Call<ServerResponse<ArrayList<Feed>>>


    //dish likes
    @POST("dishes/{dish_id}/likes")
    fun likeDish(@Path(value = "dish_id", encoded = true) dishId: Long): Call<ServerResponse<Void>>

    @DELETE("dishes/{dish_id}/likes")
    fun unlikeDish(@Path(value = "dish_id", encoded = true) dishId: Long): Call<ServerResponse<Void>>

    //Reports
    @GET("cooks/{cook_id}/reviews")
    fun getDishReview(@Path(value = "cook_id", encoded = true) cookId: Long): Call<ServerResponse<Review>>

    //Post Report
    @POST("eaters/me/orders/{order_id}/reports")
    fun postReport(@Path(value = "order_id", encoded = true) orderId: Long, @Body reports: Reports): Call<ServerResponse<Void>>

    //Post Review
    @POST("eaters/me/orders/{order_id}/reviews")
    fun postReview(@Path(value = "order_id", encoded = true) orderId: Long, @Body reviewRequest: ReviewRequest): Call<ServerResponse<Void>>

}
