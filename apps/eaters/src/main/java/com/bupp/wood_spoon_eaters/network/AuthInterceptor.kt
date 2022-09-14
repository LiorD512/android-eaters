package com.bupp.wood_spoon_eaters.network

import android.util.Log
import com.bupp.wood_spoon_eaters.common.FlavorConfigManager
//import com.bupp.wood_spoon_eaters.model.LoginResponse
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Invocation
import java.io.IOException
import java.util.concurrent.locks.ReentrantReadWriteLock

class AuthInterceptor(private val settings: ApiSettings) : Interceptor {

    private val TAG = "wowAuthInterceptor"
    private var storedAuthToken: String? = null

    private val lock = ReentrantReadWriteLock(true)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val authToken: String?

        var request = chain.request()
        lock.readLock().lock()
        try {
            authToken = getStoredAuthToken()
        } finally {
            lock.readLock().unlock()
        }

        val newRequest = buildVersionPath(request)
        val finalRequest = addTokenToRequest(newRequest, authToken)
        val response = chain.proceed(finalRequest)

        if(response.code == 401) {
            updateToken(null)
        }
        if (!response.header("X-Auth-Token").isNullOrEmpty()) {
            val newToken = response.header("X-Auth-Token") as String
            if (newToken.isNotEmpty()) {
                updateToken(newToken)
            }
        }


        return response
    }

    private fun updateToken(newToken: String?) {
//        Log.d("wowAuth", "updateD Token: $newToken")
        storedAuthToken = newToken
        settings.token = newToken
    }

    private fun getStoredAuthToken(): String? {
        return settings.token
    }

    private fun addTokenToRequest(request: Request, authToken: String?): Request {
        var finalRequest = request
        if (authToken != null) {
            Log.d(TAG, "addTokenToRequest: $authToken")
            val requestBuilder = request.newBuilder()//.addHeader("X-Auth-Token", authToken)
                .removeHeader("Authorization")
                .addHeader("Authorization", "Bearer $authToken")
            finalRequest = requestBuilder.build()
        }
        return finalRequest
    }

    private fun buildVersionPath(request: Request): Request {
        Log.d(TAG, "buildVersionPath - VERSION_PLACE_HOLDER")
        val invocation = request.tag(Invocation::class.java)
        val version = invocation?.method()?.getAnnotation(VERSION::class.java)?.version


        var url = request.url.toUrl().toString()

        if (version != null) {
            //Annotation is present
            url = url.replace(FlavorConfigManager.VERSION_PLACE_HOLDER, version)
            Log.d(TAG, "version: ${version} - $url")
        } else {
            //No annotation
            Log.d(TAG, "version: $version - v2")
            url = url.replace(FlavorConfigManager.VERSION_PLACE_HOLDER, "v2")
        }

        Log.d(TAG, "finalUrl: $url")
        val finalRequest = request.newBuilder().url(url).build()
        return finalRequest
    }

}
