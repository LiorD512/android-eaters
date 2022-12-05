package com.bupp.wood_spoon_eaters.network

import android.util.Log
import com.bupp.wood_spoon_eaters.common.FlavorConfigManager
import com.eatwoodspoon.analytics.SessionId
import com.eatwoodspoon.auth.AuthTokenRepository
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Invocation
import java.io.IOException

// Workaround to avoid logging out when some of the endpoints return 401
const val DoNotLogoutOnUnauthorizedHeader = "X-Do-Not-Logout-On-Unauthorized"

class AuthInterceptor(private val tokenRepository: AuthTokenRepository) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()

        val doNotLogoutOnUnauthorized = request.header(DoNotLogoutOnUnauthorizedHeader)?.toBoolean() == true
        if(doNotLogoutOnUnauthorized) {
            request = request.newBuilder().removeHeader(DoNotLogoutOnUnauthorizedHeader).build()
        }

        val authToken: String? = tokenRepository.authorizationToken

        val newRequest = buildVersionPath(request)
        val finalRequest = addTokenToRequest(newRequest, authToken)
        val response = chain.proceed(finalRequest)

        if(response.code == 401 && !doNotLogoutOnUnauthorized) {
            tokenRepository.authorizationToken = null
        }
        if (!response.header("X-Auth-Token").isNullOrEmpty()) {
            val newToken = response.header("X-Auth-Token") as String
            if (newToken.isNotEmpty()) {
                tokenRepository.authorizationToken = newToken
            }
        }


        return response
    }

    private fun addTokenToRequest(request: Request, authToken: String?): Request {
        var finalRequest = request
        if (authToken != null) {
            Log.d(Companion.TAG, "addTokenToRequest: $authToken")
            val requestBuilder = request.newBuilder()//.addHeader("X-Auth-Token", authToken)
                .removeHeader("Authorization")
                .addHeader("Authorization", "Bearer $authToken")
                .removeHeader("X-Session-Id")
                .addHeader("X-Session-Id", SessionId.value)
            finalRequest = requestBuilder.build()
        }
        return finalRequest
    }

    private fun buildVersionPath(request: Request): Request {
        Log.d(Companion.TAG, "buildVersionPath - VERSION_PLACE_HOLDER")
        val invocation = request.tag(Invocation::class.java)
        val version = invocation?.method()?.getAnnotation(VERSION::class.java)?.version


        var url = request.url.toUrl().toString()

        if (version != null) {
            //Annotation is present
            url = url.replace(FlavorConfigManager.VERSION_PLACE_HOLDER, version)
            Log.d(Companion.TAG, "version: ${version} - $url")
        } else {
            //No annotation
            Log.d(Companion.TAG, "version: $version - v2")
            url = url.replace(FlavorConfigManager.VERSION_PLACE_HOLDER, "v2")
        }

        Log.d(Companion.TAG, "finalUrl: $url")
        val finalRequest = request.newBuilder().url(url).build()
        return finalRequest
    }

    companion object {
        private val TAG = "AuthInterceptor"
    }

}
