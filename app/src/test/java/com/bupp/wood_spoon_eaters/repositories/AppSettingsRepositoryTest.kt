package com.bupp.wood_spoon_eaters.repositories

import com.bupp.wood_spoon_eaters.di.abs.AppSettingAdapter
import com.bupp.wood_spoon_eaters.di.abs.SerializeNulls
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.result_handler.ErrorManger
import com.bupp.wood_spoon_eaters.network.result_handler.ResultManager
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import kotlin.coroutines.CoroutineContext

class AppSettingsRepositoryTest: CoroutineDispatcher() {


    lateinit var appSettingsRepository: AppSettingsRepository
    lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val moshi = Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
            .add(SerializeNulls.JSON_ADAPTER_FACTORY)
            .add(AppSettingAdapter())
            .addLast(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val errorManger: ErrorManger = mock()

        appSettingsRepository = AppSettingsRepositoryImpl(apiService, ResultManager(errorManger))
    }

    @After
    fun tearDown() {
    }

    @Test
    fun getState() {
    }

    @Test
    fun appSetting() {
    }

    @Test
    fun featureFlag() {
        mockWebServer.enqueue(MockResponse().setBody("""
            {}
        """.trimIndent()))

//        appSettingsRepository.initAppSettings()
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        TODO("Not yet implemented")
    }
}