package com.bupp.wood_spoon_eaters.features.new_order.service

import com.bupp.wood_spoon_eaters.model.EphemeralKey
import com.bupp.wood_spoon_eaters.model.ServerResponse
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * A Retrofit service used to communicate with a server.
 */
interface StripeService {

    @GET("eaters/me/stripe/ephemeral_key")
    fun getEphemeralKey(): Observable<ServerResponse<EphemeralKey>>

//    @FormUrlEncoded
//    @POST("ephemeral_keys")
//    fun createEphemeralKey(@FieldMap apiVersionMap: HashMap<String, String>): Observable<ResponseBody>
//
//    @FormUrlEncoded
//    @POST("create_intent")
//    fun createPaymentIntent(@FieldMap params: HashMap<String, Any>): Observable<ResponseBody>
//
//    @FormUrlEncoded
//    @POST("create_setup_intent")
//    fun createSetupIntent(@FieldMap params: HashMap<String, Any>): Observable<ResponseBody>
}
