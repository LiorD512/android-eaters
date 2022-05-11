package com.bupp.wood_spoon_eaters.repositories

import com.bupp.wood_spoon_eaters.MainCoroutineRule
import com.bupp.wood_spoon_eaters.di.abs.AppSettingAdapter
import com.bupp.wood_spoon_eaters.di.abs.SerializeNulls
import com.bupp.wood_spoon_eaters.managers.EatersAnalyticsTracker
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.result_handler.ErrorManger
import com.bupp.wood_spoon_eaters.network.result_handler.ResultManager
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*


@ExperimentalCoroutinesApi
class AppSettingsRepositoryTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    private val testDispatcher = TestCoroutineDispatcher()

    lateinit var eatersAnalyticsTracker: EatersAnalyticsTracker

    lateinit var appSettingsRepository: AppSettingsRepository

    lateinit var mockWebServer: MockWebServer


    @Before
    fun setUp() {

        Dispatchers.setMain(testDispatcher)

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
        eatersAnalyticsTracker = mock()
        appSettingsRepository = AppSettingsRepositoryImpl(apiService, ResultManager(errorManger), StaticFeatureFlagsListProvider(), eatersAnalyticsTracker)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun testLifecycle() = runBlocking {
        mockWebServer.enqueue(settings = emptyList())
        assert(appSettingsRepository.state.value == AppSettingsRepoState.NotInitialized)
        appSettingsRepository.initAppSettings(testDispatcher)
        assert(appSettingsRepository.state.value is AppSettingsRepoState.Success)
    }

    @Test
    fun testKnownDataTypes() {
        AppSettingKnownTypes.values().forEach {
            when (it) {
                AppSettingKnownTypes.string -> testStringSettingFlag()
                AppSettingKnownTypes.integer -> testIntSettingFlag()
                AppSettingKnownTypes.decimal -> Unit // TODO()
                AppSettingKnownTypes.price -> testPriceSettingFlag()
                AppSettingKnownTypes.key_value -> Unit // TODO()
                AppSettingKnownTypes.boolean -> testBooleanSettingFlag()
                AppSettingKnownTypes.csv_array -> Unit // TODO()
            }
        }
    }

    private fun testPriceSettingFlag() = runBlocking {
        val settingKey = "my_setting"

        val price1 = Price(1050, 10.50, "\$10.50")
        //Manual mapping to correctly mock the data
        mockWebServer.enqueue(
            settings = listOf(
                AppSetting(
                    id = 1, settingKey, data_type = AppSettingKnownTypes.price.name, mapOf(
                        "cents" to price1.cents,
                        "value" to price1.value,
                        "formatted" to price1.formatedValue
                    )
                )
            )
        )
        appSettingsRepository.initAppSettings(testDispatcher)
        assert(appSettingsRepository.priceAppSetting(settingKey) == price1)

        mockWebServer.enqueue(settings = emptyList())
        appSettingsRepository.initAppSettings(testDispatcher)
        assert(appSettingsRepository.priceAppSetting(settingKey) == null)

        mockWebServer.enqueue(settings = listOf(AppSetting(id = 1, settingKey, data_type = AppSettingKnownTypes.price.name, "not_price")))
        appSettingsRepository.initAppSettings(testDispatcher)
        assert(appSettingsRepository.priceAppSetting(settingKey) == null)
    }

    private fun testIntSettingFlag() = runBlocking {
        val settingKey = "my_setting"

        mockWebServer.enqueue(settings = listOf(AppSetting(id = 1, settingKey, data_type = AppSettingKnownTypes.integer.name, 555)))
        appSettingsRepository.initAppSettings(testDispatcher)
        assert(appSettingsRepository.intAppSetting(settingKey) == 555)

        mockWebServer.enqueue(settings = listOf(AppSetting(id = 1, settingKey, data_type = AppSettingKnownTypes.integer.name, 555.0)))
        appSettingsRepository.initAppSettings(testDispatcher)
        assert(appSettingsRepository.intAppSetting(settingKey) == 555)

        mockWebServer.enqueue(settings = emptyList())
        appSettingsRepository.initAppSettings(testDispatcher)
        assert(appSettingsRepository.intAppSetting(settingKey) == null)

        mockWebServer.enqueue(settings = listOf(AppSetting(id = 1, settingKey, data_type = AppSettingKnownTypes.integer.name, "not_integer")))
        appSettingsRepository.initAppSettings(testDispatcher)
        assert(appSettingsRepository.intAppSetting(settingKey) == null)
    }

    private fun testBooleanSettingFlag() = runBlocking {
        val settingKey = "my_setting"

        mockWebServer.enqueue(settings = listOf(AppSetting(id = 1, settingKey, data_type = AppSettingKnownTypes.boolean.name, true)))
        appSettingsRepository.initAppSettings(testDispatcher)
        assert(appSettingsRepository.booleanAppSetting(settingKey) == true)

        mockWebServer.enqueue(settings = emptyList())
        appSettingsRepository.initAppSettings(testDispatcher)
        assert(appSettingsRepository.booleanAppSetting(settingKey) == null)

        mockWebServer.enqueue(settings = listOf(AppSetting(id = 1, settingKey, data_type = AppSettingKnownTypes.boolean.name, "not_bool")))
        appSettingsRepository.initAppSettings(testDispatcher)
        assert(appSettingsRepository.booleanAppSetting(settingKey) == null)
    }

    private fun testStringSettingFlag() = runBlocking {
        val settingKey = "my_setting"

        mockWebServer.enqueue(settings = listOf(AppSetting(id = 1, settingKey, data_type = AppSettingKnownTypes.string.name, "http://example.com")))
        appSettingsRepository.initAppSettings(testDispatcher)
        assert(appSettingsRepository.stringAppSetting(settingKey) == "http://example.com")

        mockWebServer.enqueue(settings = emptyList())
        appSettingsRepository.initAppSettings(testDispatcher)
        assert(appSettingsRepository.stringAppSetting(settingKey) == null)

        mockWebServer.enqueue(settings = listOf(AppSetting(id = 1, settingKey, data_type = AppSettingKnownTypes.string.name, 5)))
        appSettingsRepository.initAppSettings(testDispatcher)
        assert(appSettingsRepository.stringAppSetting(settingKey) == null)
    }

    @Test
    fun testMissingPropertyErrorReporting() = runBlocking {
        val settingKey = "my_setting"

        mockWebServer.enqueue(settings = listOf(AppSetting(id = 1, settingKey, data_type = AppSettingKnownTypes.string.name, "http://example.com")))
        appSettingsRepository.initAppSettings(testDispatcher)
        assert(appSettingsRepository.stringAppSetting("other_key") == null)

//        verify(eventsManager, times(1)).logEvent(MissingKeyErrorEventName, any());

    }

    @Test
    fun testFeatureFlags() = runBlocking {
        mockWebServer.enqueue(
            ff = mapOf(
                "feature_1" to true,
                "feature_2" to false
            )
        )
        appSettingsRepository.initAppSettings(testDispatcher)
        assert(appSettingsRepository.featureFlag("feature_1") == true)
        assert(appSettingsRepository.featureFlag("feature_2") == false)
    }

    @Test
    fun featureFlagsAndSettingsParsingError() = runBlocking {
        mockWebServer.enqueue(
            MockResponse().setBody(
                """
                "bad": "json"
            """.trimIndent()
            )
        )

        appSettingsRepository.initAppSettings(testDispatcher)
        assert(appSettingsRepository.state.value is AppSettingsRepoState.Failed)

        mockWebServer.enqueue(
            MockResponse().setBody(
                """
                "bad": json
            """.trimIndent()
            )
        )

        appSettingsRepository.initAppSettings(testDispatcher)
        assert(appSettingsRepository.state.value is AppSettingsRepoState.Failed)
    }
}

private fun MockWebServer.enqueue(settings: List<AppSetting>? = null, ff: Map<String, Boolean>? = null) {

    val gson = Gson()
    val response = ServerResponse(
        data = AppSettings(settings = settings, ff = ff)
    )

    val json = gson.toJson(response)
    this.enqueue(
        MockResponse().setBody(json)
    )
}