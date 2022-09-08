package com.bupp.wood_spoon_eaters.network

import android.content.SharedPreferences
import com.bupp.wood_spoon_eaters.BuildConfig
import com.eatwoodspoon.auth.AuthTokenRepository

class ApiSettings(private val sharedPreferences: SharedPreferences) : AuthTokenRepository {

    companion object {
        const val TOKEN_KEY = "token_key"
        const val DEFAULT_TOKEN = BuildConfig.DEFAULT_TOKEN
    }

    var token: String?
        get() = sharedPreferences.getString(TOKEN_KEY, "").takeIf { !it.isNullOrBlank() }
            ?: DEFAULT_TOKEN
        set(token) = sharedPreferences.edit().putString(TOKEN_KEY, token).apply()

    fun isRegistered(): Boolean {
        return token != DEFAULT_TOKEN
    }

    fun clearSharedPrefs() {
        sharedPreferences.edit().clear().apply()
    }

    override var authorizationToken: String?
        get() = token
        set(value) {
            token = value
        }
}
