package com.eatwoodspoon.analytics.app_attributes.providers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import com.eatwoodspoon.analytics.app_attributes.AttributeProvider

internal class RadioAttributeProvider(private val context: Context) : AttributeProvider<String?> {
    override fun get() = context.getNetworkType()
}

@SuppressLint("MissingPermission")
private fun Context.getNetworkType(): String? {
    val mTelephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val networkType = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                mTelephonyManager.dataNetworkType
            } else {
                TelephonyManager.NETWORK_TYPE_UNKNOWN
            }
        } else {
            mTelephonyManager.networkType
        }
    } catch (ex: Exception) {
        TelephonyManager.NETWORK_TYPE_UNKNOWN
    }
    return when (networkType) {
        TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> {
            "2G"
        }
        TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> {
            "3G"
        }
        TelephonyManager.NETWORK_TYPE_LTE -> {
            "4G"
        }
        TelephonyManager.NETWORK_TYPE_NR -> {
            "5G"
        }
        else -> {
            null
        }
    }
}