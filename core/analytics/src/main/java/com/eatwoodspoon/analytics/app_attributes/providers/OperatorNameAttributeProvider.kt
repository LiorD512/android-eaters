package com.eatwoodspoon.analytics.app_attributes.providers

import android.content.Context
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import com.eatwoodspoon.analytics.app_attributes.AttributeProvider

internal class OperatorNameAttributeProvider(private val context: Context) : AttributeProvider<String?> {
    override fun get() = context.getOperatorName()
}

private fun Context.getOperatorName(): String? {
    try {
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            return (getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkOperatorName
        }
    } catch (ex: Exception) {
    }
    return null
}