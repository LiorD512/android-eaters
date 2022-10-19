package com.eatwoodspoon.analytics.app_attributes.providers

import android.content.Context
import android.preference.PreferenceManager
import android.provider.Settings
import com.eatwoodspoon.analytics.DeviceId
import com.eatwoodspoon.analytics.app_attributes.AttributeProvider
import java.util.*

internal class DeviceIdAttributeProvider(private val context: Context) : AttributeProvider<String> {
    override fun get() = DeviceId.getValue(context)
}