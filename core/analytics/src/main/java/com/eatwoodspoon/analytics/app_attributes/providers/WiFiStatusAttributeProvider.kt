package com.eatwoodspoon.analytics.app_attributes.providers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.eatwoodspoon.analytics.app_attributes.AttributeProvider

internal class WiFiStatusAttributeProvider(private val context: Context) : AttributeProvider<Boolean?> {
    override fun get(): Boolean? = context.isWifiConnected()
}

@SuppressLint("MissingPermission")
private fun Context.isWifiConnected(): Boolean? {
    try {
        if (checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        }
    } catch (ex: Exception) {
    }
    return null
}