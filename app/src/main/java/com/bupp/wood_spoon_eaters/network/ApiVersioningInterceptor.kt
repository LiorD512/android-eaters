package com.bupp.wood_spoon_eaters.network

import android.util.Log
import com.bupp.wood_spoon_eaters.common.FlavorConfigManager
//import com.bupp.wood_spoon_eaters.model.LoginResponse
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import java.io.IOException

class ApiVersioningInterceptor() : Interceptor {

    private val TAG = "wowApiVersioningInterceptor"

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val invocation = originalRequest.tag(Invocation::class.java)
        val version = invocation?.method()?.getAnnotation(V3::class.java)

        val host = originalRequest.url.toUrl().host
        var path = originalRequest.url.toUrl().path
        if (version != null) {
            //Annotation is present, using new auth method
            path = path.replace(FlavorConfigManager.VERSION_PLACE_HOLDER, "v3")
            Log.d(TAG, "version: $version - v3 - $path")
        } else {
            //No annotation, sticking with legacy auth
            Log.d(TAG, "version: $version - v2")
            path = path.replace(FlavorConfigManager.VERSION_PLACE_HOLDER, "v2")
        }

        val finalUrl = "https://$host$path"
        Log.d(TAG, "finalUrl: $finalUrl")

        originalRequest.newBuilder().url(finalUrl).build()
        return chain.proceed(originalRequest)
    }



}
