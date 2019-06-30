package com.bupp.wood_spoon_eaters.network

import com.bupp.wood_spoon_eaters.model.*
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    //Client
    @GET("eaters/me")
    fun getMe(): Observable<ServerResponse<Client>>

    //General
    @GET("eaters/utils/meta")
    fun getMetaData(): Observable<ServerResponse<MetaDataModel>>

    @GET("eaters/utils/meta")
    fun getMetaDataCall(): Call<ServerResponse<MetaDataModel>>

    @POST("eaters/me")
    fun postMe(@Body client: Client): Call<ServerResponse<Client>>

    @FormUrlEncoded
    @POST("eaters/me/presigned_urls")
    fun postCookPreSignedUrl(@Field("type") type: String): Call<ServerResponse<PreSignedUrl>>

//    @POST("cooks/me/dishes/presigned_urls")
//    fun postDishPreSignedUrl(): Call<ServerResponse<PreSignedUrl>>

    //Login end-points
    @FormUrlEncoded
    @POST("eaters/auth/get_code")
    fun getCode(@Field("phone_number") phone: String): Call<Void>

    @FormUrlEncoded
    @POST("eaters/auth/validate_code")
    fun validateCode(@Field("phone_number") phone: String, @Field("code") code: String): Call<ServerResponse<Client>>


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
