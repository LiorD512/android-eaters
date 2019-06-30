package com.bupp.wood_spoon_eaters.di

import android.content.Context
import android.content.SharedPreferences
import com.bupp.wood_spoon_eaters.network.google.client.GoogleRetrofitFactory
import com.bupp.wood_spoon_eaters.network.google.interfaces.GoogleApi
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.ApiSettings
import com.bupp.wood_spoon_eaters.network.AuthInterceptor
import com.bupp.wood_spoon_eaters.utils.PutActionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


val networkModule = module {

    single { PutActionManager(get()) }

    single { provideSharedPreferences(get()) }
    factory { ApiSettings(get()) }
    single { provideGoogleApi() }

    single { provideDefaultOkhttpClient(get()) }
    single { provideRetrofit(get()) }
    single { provideApiService(get()) }

//    factory { TestPresenter(get()) }

}

fun provideGoogleApi(): GoogleApi {
    return GoogleRetrofitFactory.createGoogleRetrofitInstance(GoogleApi.BASE_URL).create(GoogleApi::class.java)
}

const val DEFAULT_NAMESPACE = "default"
const val SERVER_BASE_URL = "https://woodspoon-staging.herokuapp.com/api/v1/"

fun provideSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences("User", android.content.Context.MODE_PRIVATE)
}

fun provideDefaultOkhttpClient(apiSettings: ApiSettings): OkHttpClient {
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY

    val authInterceptor = AuthInterceptor(apiSettings)

    val httpClient = OkHttpClient.Builder().addInterceptor(logging).addInterceptor(authInterceptor)
    return httpClient.build()
}

fun provideRetrofit(client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(SERVER_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
}

fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
