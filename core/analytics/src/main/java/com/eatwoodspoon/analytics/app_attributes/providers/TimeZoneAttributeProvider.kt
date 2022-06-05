package com.eatwoodspoon.analytics.app_attributes.providers

import com.eatwoodspoon.analytics.app_attributes.AttributeProvider
import java.text.SimpleDateFormat
import java.util.*

internal class TimeZoneAttributeProvider : AttributeProvider<String> {
    override fun get(): String {
        val dateFormat = SimpleDateFormat("ZZZZZ", Locale.getDefault())
        return dateFormat.format(Date(0))
    }
}