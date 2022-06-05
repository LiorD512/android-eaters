package com.eatwoodspoon.analytics.app_attributes.providers

import android.content.Context
import com.eatwoodspoon.analytics.app_attributes.AttributeProvider
import com.eatwoodspoon.analytics.events.AppAttributesEvent
import com.example.analytics.R

internal class DeviceTypeAttributeProvider(private val context: Context) : AttributeProvider<AppAttributesEvent.DeviceTypeValues> {
    override fun get() = context.getDeviceType()
}

private fun Context.isTablet() = resources.getBoolean(R.bool.isTablet)

private fun Context.getDeviceType() = if (isTablet()) {
    AppAttributesEvent.DeviceTypeValues.tablet
} else {
    AppAttributesEvent.DeviceTypeValues.phone
}

