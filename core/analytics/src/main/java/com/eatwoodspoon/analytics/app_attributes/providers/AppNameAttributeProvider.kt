package com.eatwoodspoon.analytics.app_attributes.providers

import android.content.Context
import com.eatwoodspoon.analytics.app_attributes.AttributeProvider
import com.eatwoodspoon.analytics.events.AppAttributesEvent

internal class AppNameAttributeProvider(private val context: Context) : AttributeProvider<AppAttributesEvent.MobileFeatureFlagAttributesEvent.AppNameValues> {
    override fun get(): AppAttributesEvent.MobileFeatureFlagAttributesEvent.AppNameValues {
        val appInfo = context.applicationInfo

        return when {
            appInfo.packageName.contains("eaters") -> AppAttributesEvent.MobileFeatureFlagAttributesEvent.AppNameValues.Diners
            appInfo.packageName.contains("chef") -> AppAttributesEvent.MobileFeatureFlagAttributesEvent.AppNameValues.Chefs
            else -> throw IllegalArgumentException("Unsupported app package ${appInfo.packageName}")
        }
    }
}