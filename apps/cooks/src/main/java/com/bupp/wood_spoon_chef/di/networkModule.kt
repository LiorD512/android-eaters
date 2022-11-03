package com.bupp.wood_spoon_chef.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.bupp.wood_spoon_chef.common.FlavorConfigManager
import com.bupp.wood_spoon_chef.di.abs.SerializeNulls.Companion.JSON_ADAPTER_FACTORY
import com.bupp.wood_spoon_chef.di.abs.UriAdapter
import com.bupp.wood_spoon_chef.data.remote.network.ApiService
import com.bupp.wood_spoon_chef.data.remote.network.AuthInterceptor
import com.bupp.wood_spoon_chef.data.remote.network.google.client.GoogleRetrofitFactory
import com.bupp.wood_spoon_chef.data.remote.network.google.interfaces.GoogleApi
import com.bupp.wood_spoon_chef.utils.UserSettings
import com.eatwoodspoon.obscured_sharedprefrences.core.KeyStoreKeyGenerator
import com.eatwoodspoon.obscured_sharedprefrences.core.ObscuredPreferencesBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

val networkModule = module {


    single { provideEncryptedSharedPreferences(get(), androidApplication()) }
    single { FlavorConfigManager(get()) }
    factory { UserSettings(get(),get()) }
    single { provideGoogleApi() }

    single { provideDefaultOkhttpClient(get()) }
    single { provideRetrofit(get(), get()) }
    single { provideApiService(get()) }

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
            .setSharePrefFileName("ws_encrypted_prefs")
            .setSecret(key)
            .createSharedPrefs()
}

fun provideDefaultOkhttpClient(userSettings: UserSettings): OkHttpClient {
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY

    val authInterceptor = AuthInterceptor(userSettings)

    val httpClient = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(logging).addInterceptor(authInterceptor)

    return httpClient.build()
}



fun provideRetrofit(client: OkHttpClient, flavorConfig: FlavorConfigManager): Retrofit {
    val moshi = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
        .add(JSON_ADAPTER_FACTORY)
        .add(UriAdapter())
        .add(AppSettingAdapter())
        .addLast(KotlinJsonAdapterFactory())
        .build()

    return Retrofit.Builder()
        .baseUrl(flavorConfig.getBaseUrl())
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
}

fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
