//package com.bupp.wood_spoon_eaters.managers
//
//import android.app.Activity
//import android.content.Context
//import android.content.pm.PackageManager
//import androidx.core.app.ActivityCompat
//
//class PermissionManager {
//
//
//    fun hasPermission(context: Context, type: String): Boolean {
//        return (ActivityCompat.checkSelfPermission(context, type) === PackageManager.PERMISSION_GRANTED)
//    }
//
//    fun requestPermission(activity: Activity, types: Array<String>, requestName: Int) {
//        ActivityCompat.requestPermissions(activity, types, requestName)
//    }
//
//}