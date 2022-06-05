package com.eatwoodspoon.analytics.app_attributes.providers

import android.content.Context
import com.eatwoodspoon.analytics.app_attributes.AttributeProvider

internal class BuildNumAttributeProvider(private val context: Context) : AttributeProvider<Int> {
    override fun get(): Int {
        val appInfo = context.applicationInfo
        val appPackageInfo = context.packageManager.getPackageInfo(appInfo.packageName, 0)
        return appPackageInfo.versionCode
    }
}


internal class VersionNameAttributeProvider(private val context: Context) : AttributeProvider<String> {
    override fun get(): String {
        val appInfo = context.applicationInfo
        val appPackageInfo = context.packageManager.getPackageInfo(appInfo.packageName, 0)
        return appPackageInfo.versionName
    }
}