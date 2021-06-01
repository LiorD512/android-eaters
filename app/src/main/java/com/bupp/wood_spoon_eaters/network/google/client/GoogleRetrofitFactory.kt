package com.bupp.wood_spoon_eaters.network.google.client;

import com.google.gson.GsonBuilder
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

object GoogleRetrofitFactory {

    fun createGoogleRetrofitInstance(baseUrl: String): Retrofit {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        val moshi = Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe()).build()
//        val gson = GsonBuilder()
//                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//                .create()

        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(httpClient.build())
                .build()
    }
}
