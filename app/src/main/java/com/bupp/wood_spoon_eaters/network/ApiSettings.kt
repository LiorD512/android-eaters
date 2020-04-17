package com.bupp.wood_spoon_eaters.network

import android.content.SharedPreferences
import com.bupp.wood_spoon_eaters.FlavorConfig

class ApiSettings(val sharedPreferences: SharedPreferences) {

    val TOKEN_KEY = "token_key"
    val REFRESH_TOKEN_KEY = "refresh_token_key"
    val DEFAULT_TOKEN = FlavorConfig.CONSTANTS.DEFAULT_TOKEN

    var token: String?
        get() = sharedPreferences.getString(TOKEN_KEY, DEFAULT_TOKEN)
        set(token) = sharedPreferences.edit().putString(TOKEN_KEY, token).apply()

    var refreshToken: String?
        get() = sharedPreferences.getString(REFRESH_TOKEN_KEY, null)
        set(token) = sharedPreferences.edit().putString(REFRESH_TOKEN_KEY, token).apply()

    fun isRegistered(): Boolean {
        return token != DEFAULT_TOKEN
    }
}
