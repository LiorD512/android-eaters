package com.eatwoodspoon.analytics.app_attributes.providers

import android.os.Build
import com.eatwoodspoon.analytics.app_attributes.AttributeProvider

internal class OSFullNameAttributeProvider : AttributeProvider<String> {
    override fun get(): String {
        try {
            val userAgent: String? = System.getProperty("http.agent")
            if (userAgent?.isBlank() == false) {
                return userAgent
            }
        } catch (ex: Exception) {
        }
        return "(Linux; Android ${Build.VERSION.RELEASE}; ${Build.BRAND} ${Build.MODEL} Build/${Build.DISPLAY})"
    }
}
