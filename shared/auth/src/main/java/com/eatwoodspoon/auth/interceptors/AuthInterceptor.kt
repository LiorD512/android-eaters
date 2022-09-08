package com.eatwoodspoon.auth.interceptors

import com.eatwoodspoon.auth.AuthTokenRepository
import okhttp3.Interceptor
import okhttp3.Response

private const val AUTHORIZATION_HEADER = "Authorization"

class AuthInterceptor(private val tokenRepository: AuthTokenRepository) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request().newBuilder()
            .removeHeader(AUTHORIZATION_HEADER).apply {
                tokenRepository.authorizationToken.takeIf { !it.isNullOrBlank() }?.let { token ->
                    addHeader(AUTHORIZATION_HEADER, "Bearer $token")
                }
            }
            .build()
        )
    }
}
