package com.bupp.wood_spoon_eaters.di

import `in`.co.ophio.secure.core.KeyStoreKeyGenerator
import `in`.co.ophio.secure.core.ObscuredPreferencesBuilder
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.bupp.wood_spoon_eaters.common.FlavorConfigManager
import com.bupp.wood_spoon_eaters.di.abs.AppSettingAdapter
import com.bupp.wood_spoon_eaters.di.abs.SerializeNulls.Companion.JSON_ADAPTER_FACTORY
import com.bupp.wood_spoon_eaters.di.abs.UriAdapter
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.ApiSettings
import com.bupp.wood_spoon_eaters.network.AuthInterceptor
import com.eatwoodspoon.auth.AuthTokenRepository
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.*
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*


val networkModule = module {

    single { FlavorConfigManager(get()) }
    single { provideEncryptedSharedPreferences(get(), androidApplication()) }
    factory { ApiSettings(get()) }.bind(AuthTokenRepository::class)

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
        .add(FeedModelsViewTypeAdapter())
        .add(FeedRestaurantSectionItemViewTypeAdapter())
        .add(
            PolymorphicJsonAdapterFactory.of(FeedRestaurantSectionItem::class.java, "type")
                .let {
                    var builder = it
                    FeedRestaurantSectionItemViewType.values().forEach { type ->
                        val sectionClazz = when (type) {
                            FeedRestaurantSectionItemViewType.DISH -> FeedRestaurantItemTypeDish::class.java
                            FeedRestaurantSectionItemViewType.SEE_MORE -> FeedRestaurantItemTypeSeeMore::class.java
//                            FeedRestaurantSectionItemViewType.DISH_COVER -> FeedRestaurantItemTypeCover::class.java
                            FeedRestaurantSectionItemViewType.UNKNOWN -> null
                            else -> null
                        }
                        sectionClazz?.let {
                            builder = builder.withSubtype(it, type.value)
                        }
                    }
                    builder
                }
                .withFallbackJsonAdapter(FallbackFeedSectionAdapter { FeedRestaurantUnknownSection(unknownTypeValue = it) })
        )
        .add(
            PolymorphicJsonAdapterFactory.of(FeedSectionCollectionItem::class.java, "type")
                .let {
                    var builder = it
                    FeedModelsViewType.values().forEach { type ->
                        val sectionClazz = when (type) {
                            FeedModelsViewType.COUPONS -> FeedCampaignSection::class.java
                            FeedModelsViewType.RESTAURANT -> FeedRestaurantSection::class.java
                            FeedModelsViewType.EMPTY_FEED -> FeedIsEmptySection::class.java
                            FeedModelsViewType.EMPTY_SECTION -> FeedSingleEmptySection::class.java
                            FeedModelsViewType.EMPTY_SEARCH -> FeedSearchEmptySection::class.java
                            FeedModelsViewType.COMING_SONG -> FeedComingSoonSection::class.java
                            FeedModelsViewType.HERO -> FeedHeroItemSection::class.java
//                            FeedModelsViewType.QUICK_LINK -> null // QuickLinkItem::class.java // TODO - DO we support them??
//                            FeedModelsViewType.REVIEW -> null // ReviewItem::class.java // TODO - DO we support them??
                            FeedModelsViewType.CHEF -> FeedChefItemSection::class.java
                            FeedModelsViewType.DISH -> FeedDishItemSection::class.java
                            FeedModelsViewType.UNKNOWN -> null
                        }
                        sectionClazz?.let {
                            builder = builder.withSubtype(it, type.value)
                        }
                    }
                    builder
                }
                .withFallbackJsonAdapter(FallbackFeedSectionAdapter { FeedUnknownSection(unknownTypeValue = it) })
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

private class FallbackFeedSectionAdapter<T>(private val factory: (type: String?) -> T) :
    JsonAdapter<T>() {

    override fun fromJson(reader: JsonReader): T? {
        var type: String? = null

        reader.beginObject()
        while (reader.hasNext()) {
            if (reader.nextName() == "type") {
                type = reader.nextString()
            } else
                reader.skipValue()
        }
        reader.endObject()

        return factory(null)
    }

    override fun toJson(writer: JsonWriter, value: T?) {
        // Do nothing
    }

}


