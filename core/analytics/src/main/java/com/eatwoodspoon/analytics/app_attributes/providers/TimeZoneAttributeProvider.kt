package com.eatwoodspoon.analytics.app_attributes.providers

import com.eatwoodspoon.analytics.app_attributes.AttributeProvider
import java.util.*

internal class TimeZoneAttributeProvider : AttributeProvider<String> {
    override fun get(): String {
        return TimeZone.getDefault().id
    }
}