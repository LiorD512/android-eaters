package com.eatwoodspoon.analytics

import com.eatwoodspoon.analytics.app_attributes.AppAttributesDataSource
import com.eatwoodspoon.analytics.app_attributes.AppAttributesDataSourceImpl
import com.eatwoodspoon.analytics.app_attributes.providers.AppNameAttributeProvider
import com.eatwoodspoon.analytics.app_attributes.providers.BuildNumAttributeProvider
import com.eatwoodspoon.analytics.app_attributes.providers.DeviceIdAttributeProvider
import com.eatwoodspoon.analytics.app_attributes.providers.DeviceTypeAttributeProvider
import com.eatwoodspoon.analytics.app_attributes.providers.OSFullNameAttributeProvider
import com.eatwoodspoon.analytics.app_attributes.providers.OperatorNameAttributeProvider
import com.eatwoodspoon.analytics.app_attributes.providers.RadioAttributeProvider
import com.eatwoodspoon.analytics.app_attributes.providers.ScreenSizeAttributeProvider
import com.eatwoodspoon.analytics.app_attributes.providers.TimeZoneAttributeProvider
import com.eatwoodspoon.analytics.app_attributes.providers.VersionNameAttributeProvider
import com.eatwoodspoon.analytics.app_attributes.providers.WiFiStatusAttributeProvider
import org.koin.dsl.module

private val appAttributesModule = module {
    factory { WiFiStatusAttributeProvider(get()) }
    factory { RadioAttributeProvider(get()) }
    factory { OperatorNameAttributeProvider(get()) }
    factory { DeviceIdAttributeProvider(get()) }
    factory { ScreenSizeAttributeProvider() }
    factory { TimeZoneAttributeProvider() }
    factory { OSFullNameAttributeProvider() }
    factory { DeviceTypeAttributeProvider(get()) }
    factory { AppNameAttributeProvider(get()) }
    factory { BuildNumAttributeProvider(get()) }
    factory { VersionNameAttributeProvider(get()) }
}


val analyticsModule = module {
    single<AppAttributesDataSource> { AppAttributesDataSourceImpl(get(), get(),get(),get(),get(),get(),get(),get(),get(),get(),get()) }
} + appAttributesModule
