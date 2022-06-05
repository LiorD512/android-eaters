package com.eatwoodspoon.analytics.app_attributes

import android.os.Build
import com.eatwoodspoon.analytics.app_attributes.providers.*
import com.eatwoodspoon.analytics.events.AppAttributesEvent
import org.koin.core.parameter.ParametersHolder
import java.util.*

interface AppAttributesDataSource {
    val appAttributes: Map<String, Any>
}

internal class AppAttributesDataSourceImpl(
    private val wifiStatusAttributeProvider: WiFiStatusAttributeProvider,
    private val radioAttributeProvider: RadioAttributeProvider,
    private val operatorNameAttributeProvider: OperatorNameAttributeProvider,
    private val deviceIdAttributeProvider: DeviceIdAttributeProvider,
    private val screenSizeAttributeProvider: ScreenSizeAttributeProvider,
    private val timeZoneAttributeProvider: TimeZoneAttributeProvider,
    private val osFullNameAttributeProvider: OSFullNameAttributeProvider,
    private val deviceTypeAttributeProvider: DeviceTypeAttributeProvider,
    private val appNameAttributeProvider: AppNameAttributeProvider,
    private val buildNumAttributeProvider: BuildNumAttributeProvider,
    private val versionNameAttributeProvider: VersionNameAttributeProvider
) : AppAttributesDataSource {

    override val appAttributes: Map<String, Any>

    init {

        val appAttributesEvent = AppAttributesEvent.MobileFeatureFlagAttributesEvent(
            app_build_number = buildNumAttributeProvider.get(),
            app_version_string = versionNameAttributeProvider.get(),
            app_name = appNameAttributeProvider.get(),
            brand = Build.BRAND,
            carrier = operatorNameAttributeProvider.get(),
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            device_id = deviceIdAttributeProvider.get(),
            radio = radioAttributeProvider.get(),
            wifi = wifiStatusAttributeProvider.get(),
            os = AppAttributesEvent.OsValues.Android,
            os_full = osFullNameAttributeProvider.get(),
            os_version = Build.VERSION.SDK_INT.toString(),
            screen_height = screenSizeAttributeProvider.get().height,
            screen_width = screenSizeAttributeProvider.get().width,
            timezone = timeZoneAttributeProvider.get(),
            device_type = deviceTypeAttributeProvider.get(),
            platform = AppAttributesEvent.PlatformValues.native,
            device_locale = Locale.getDefault().toLanguageTag(),
            device_language = Locale.getDefault().language
        )

        appAttributes = appAttributesEvent.params
    }
}
