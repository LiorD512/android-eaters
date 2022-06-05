package com.eatwoodspoon.analytics.app_attributes.providers

import android.content.Context
import android.preference.PreferenceManager
import android.provider.Settings
import com.eatwoodspoon.analytics.app_attributes.AttributeProvider
import java.util.*

internal class DeviceIdAttributeProvider(private val context: Context) : AttributeProvider<String> {
    override fun get() = context.getDeviceId()
}

private fun Context.getDeviceId(): String {
    val androidId: String? = Settings.System.getString(contentResolver, Settings.Secure.ANDROID_ID)
    return if (androidId != null) {
        UUID.nameUUIDFromBytes(androidId.hexStringToByteArray()).toString()
    } else {
        with(PreferenceManager.getDefaultSharedPreferences(this)) {
            getString("device_id", null) ?: run {
                val newDeviceId = UUID.randomUUID().toString()
                edit().putString("device_id", newDeviceId).apply()
                newDeviceId
            }
        }
    }
}

private fun String.hexStringToByteArray() =
    ByteArray(this.length / 2) { this.substring(it * 2, it * 2 + 2).toInt(16).toByte() }

