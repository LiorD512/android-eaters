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




    //General
    @GET("eaters/utils/meta")
    fun getMetaData(): Observable<ServerResponse<MetaDataModel>>

    @GET("eaters/utils/meta")
    fun getMetaDataCall(): Call<ServerResponse<MetaDataModel>>

    @FormUrlEncoded
    @POST("eaters/me/presigned_urls")
    fun postDishSuggestion(@Field("dish_name") dishName: String,@Field("dish_description") dishDescription: String): Call<ServerResponse<Void>>



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
    fun getMenuItemsDetails(@Path(value = "menu_item_id", encoded = true) searchId: Long): Call<ServerResponse<FullDish>>



    //Feed
    @GET("eaters/me/feed")
    fun getFeed(@Query("lat") lat: Double? = null, @Query("lng") lng: Double? = null,
                @Query("address_id") addressId: Long? = null, @Query("timestamp") timestamp: Int? = null): Call<ServerResponse<ArrayList<Feed>>>


    @POST("eaters/me/orders")
    fun postOrder(@Body orderRequest: OrderRequest): Call<ServerResponse<Search>>

//    @POST("cooks/me/dishes/presigned_urls")
//    fun postDishPreSignedUrl(): Call<ServerResponse<PreSignedUrl>>
//    //NewDish
//    @POST("cooks/me/dishes")
//    fun postNewDish(@Body dish: Dish): Call<ServerResponse<Dish>>
//
//    @FormUrlEncoded
//    @POST("getCode")
//    fun getCode(@Field("phone") phone: String, @Field("validation_code") code: String): Call<User>
//
//    @GET("app_settings")
//    fun getSettings(): Observable<List<Settings>>
//
//    @POST("getCode/auth")
//    fun getCode(@Body request: LoginRequest): Call<LoginResponse>
//
//    @POST("UserDevice")
//    fun registerUserDevice(@Body request: RegisterUserDeviceRequest): Call<Void>
//
//    @POST("UserDevice/BiometricToken/status")
//    fun updateBiometricToken(@Body request: UpdateBiometricToken): Call<UpdateBiometricResponse>
//
//    @POST("getCode/biometric")
//    fun loginBiometric(@Body request: BiometricLoginRequest): Call<LoginResponse>
//
//    @POST("getCode/refresh")
//    fun refreshToken(@Header("Authorization") authorization: String, @Body request: RefreshTokenRequest): Call<LoginResponse>
}
