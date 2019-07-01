package com.bupp.wood_spoon_eaters.features.main

import android.content.Context
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.managers.PermissionManager
import com.bupp.wood_spoon_eaters.utils.AppSettings

class MainViewModel(val settings: AppSettings, val permissionManager:PermissionManager): ViewModel(){

    fun isFirstTime(): Boolean{
        return settings.isFirstTime()
    }

    fun checkPermission(context: Context, permissions: Array<String>): Boolean {
        for (item in permissions) {
            if (!permissionManager.hasPermission(context, item))
                return false
        }
        return true
    }

    fun requestPermission(activity: FragmentActivity, types: Array<String>, requestName: Int) {
        ActivityCompat.requestPermissions(activity, types, requestName)
    }

}