package com.bupp.wood_spoon_eaters.di

import `in`.co.ophio.secure.core.KeyStoreKeyGenerator
import `in`.co.ophio.secure.core.ObscuredPreferencesBuilder
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlavorConfigManager
import com.bupp.wood_spoon_eaters.di.abs.AppSettingAdapter
import com.bupp.wood_spoon_eaters.di.abs.SerializeNulls.Companion.JSON_ADAPTER_FACTORY
import com.bupp.wood_spoon_eaters.di.abs.UriAdapter
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.ApiSettings
import com.bupp.wood_spoon_eaters.network.AuthInterceptor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
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


val networkModule = module {

    single { FlavorConfigManager(get()) }
    single { provideEncryptedSharedPreferences(get(), androidApplication()) }
    factory { ApiSettings(get()) }

    single { provideDefaultOkhttpClient(get()) }
    single { provideRetrofit(get(), get()) }
    single { provideApiService(get()) }

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

    val moshi = Moshi.Builder()
        .add(
            PolymorphicJsonAdapterFactory.of(FeedRestaurantSectionItem::class.java, "type")
                .withSubtype(FeedRestaurantItemTypeDish::class.java, Constants.RESTAURANT_SECTION_TYPE_DISH)
                .withSubtype(FeedRestaurantItemTypeSeeMore::class.java, Constants.RESTAURANT_SECTION_TYPE_SEE_MORE)
//                .withFallbackJsonAdapter(MoshiNullableSectionAdapter())
        )
        .add(
            PolymorphicJsonAdapterFactory.of(FeedSectionCollectionItem::class.java, "type")
                .withSubtype(FeedCampaignSection::class.java, Constants.FEED_SECTION_TYPE_COUPONS)
                .withSubtype(FeedRestaurantSection::class.java, Constants.FEED_SECTION_TYPE_RESTAURANT)
                .withSubtype(FeedIsEmptySection::class.java, Constants.FEED_EMPTY_NO_CHEF)
                .withSubtype(FeedSingleEmptySection::class.java, Constants.SECTION_EMPTY_NO_CHEF)
                .withSubtype(FeedSearchEmptySection::class.java, Constants.SECTION_EMPTY_SEARCH)
                .withSubtype(FeedComingSoonSection::class.java, Constants.SECTION_COMING_SOON)
//                .withFallbackJsonAdapter(MoshiNullableSectionAdapter())
        )
        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
        .add(JSON_ADAPTER_FACTORY)
        .add(UriAdapter())
        .add(AppSettingAdapter())
//        .add(TestAdapter())
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

