package com.eatwoodspoon.network.interceptors

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Invocation

class ApiVersionInterceptor(
    private val defaultVersion: String,
    private val placeholder: String = VERSION_URL_PLACEHOLDER
) : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(buildVersionPath(chain.request()))
    }

    private fun buildVersionPath(request: Request): Request {
        val invocation = request.tag(Invocation::class.java)
        val version = invocation?.method()
            ?.getAnnotation(VERSION::class.java)?.version.takeIf { !it.isNullOrBlank() }
        val url = request.url.toUrl().toString().replace(placeholder, version ?: defaultVersion)
        return request.newBuilder().url(url).build()
    }

}

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class VERSION(val version: String = "")

const val VERSION_URL_PLACEHOLDER = "VERSION"
