package com.bupp.wood_spoon_eaters.di

import `in`.co.ophio.secure.core.KeyStoreKeyGenerator
import `in`.co.ophio.secure.core.ObscuredPreferencesBuilder
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.bupp.wood_spoon_eaters.FlavorConfig
import com.bupp.wood_spoon_eaters.common.FlavorConfigManager
import com.bupp.wood_spoon_eaters.network.abs.DeserializerJsonAppSetting
import com.bupp.wood_spoon_eaters.network.abs.DeserializerJsonSearch
import com.bupp.wood_spoon_eaters.model.AppSetting
import com.bupp.wood_spoon_eaters.model.Search
import com.bupp.wood_spoon_eaters.network.google.client.GoogleRetrofitFactory
import com.bupp.wood_spoon_eaters.network.google.interfaces.GoogleApi
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.ApiSettings
import com.bupp.wood_spoon_eaters.network.AuthInterceptor
import com.bupp.wood_spoon_eaters.managers.PutActionManager
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


val networkModule = module {

    single { PutActionManager(get()) }
    single { FlavorConfigManager(get()) }
    single { provideEncryptedSharedPreferences(get(), androidApplication()) }
    factory { ApiSettings(get()) }
    single { provideGoogleApi() }

    single { provideDefaultOkhttpClient(get()) }
    single { provideRetrofit(get(), get()) }
    single { provideApiService(get()) }

//    factory { TestPresenter(get()) }

}

fun provideGoogleApi(): GoogleApi {
    return GoogleRetrofitFactory.createGoogleRetrofitInstance(GoogleApi.BASE_URL).create(GoogleApi::class.java)
}

fun provideEncryptedSharedPreferences(
    context: Context,
    application: Application
): SharedPreferences {
    val key = KeyStoreKeyGenerator.get(application, context.packageName).loadOrGenerateKeys()
    return ObscuredPreferencesBuilder()
        .setApplication(application)
        .obfuscateValue(true)
        .obfuscateKey(true)
        .setSharePrefFileName("ws_eaters_encrypted_prefs")
        .setSecret(key)
        .createSharedPrefs()
}

fun provideDefaultOkhttpClient(apiSettings: ApiSettings): OkHttpClient {
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY

    val authInterceptor = AuthInterceptor(apiSettings)

    val httpClient = OkHttpClient.Builder().addInterceptor(logging).addInterceptor(authInterceptor)
    return httpClient.build()
}

fun provideRetrofit(client: OkHttpClient, flavorConfig: FlavorConfigManager): Retrofit {

    val gson = GsonBuilder()
        .registerTypeAdapter(Search::class.java, DeserializerJsonSearch())
        .registerTypeAdapter(AppSetting::class.java, DeserializerJsonAppSetting())
        .create()

    return Retrofit.Builder()
        .baseUrl(flavorConfig.getBaseUrl())
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
}

fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
