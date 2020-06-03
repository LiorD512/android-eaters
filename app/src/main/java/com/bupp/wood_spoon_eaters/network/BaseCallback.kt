package com.bupp.wood_spoon_eaters.network

import android.util.Log
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.model.WSError
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

abstract class BaseCallback<T> : Callback<T> {

    val NETWORK_SUCCESS = 200
    val NETWORK_SUCCESS2 = 201

    val UNKNOWN = WSError(0,  "Unknown error")
    val NETWORK = WSError(0,  "Network error")

    override fun onResponse(call: Call<T>, response: Response<T>) {
        try {
            if (response.isSuccessful) {
                val result = response.body()
                onSuccess(result!!)
            } else {
                handleErrorResponse(response)
            }
        } finally {
            onFinally()
        }
    }

    protected fun handleErrorResponse(response: Response<T>) {
        var errorResponse: ServerResponse<*>? = null
        try {
            errorResponse = gson.fromJson(response.errorBody()!!.string(), ServerResponse::class.java)
        } catch (e: Exception) {
            Log.d(TAG, "Can't parse error body", e)
        }

        val error = WSError(
                errorResponse?.code ?: 0,
                errorResponse?.message ?: "Unknown error \uD83D\uDE1E")

        if (response.code() != NETWORK_SUCCESS || response.code() != NETWORK_SUCCESS2) {
            val error = WSError(
                    errorResponse?.code ?: response.code(),
                    errorResponse?.message ?: "Unknown error \uD83D\uDE1E")
            onError(error)
            return
        }
        onError(error)
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        try {
            if (t is IOException) {
                onError(NETWORK)
            } else {
                onError(UNKNOWN)
            }
        } finally {
            onFinally()
        }
    }

    abstract fun onSuccess(result: T)

    abstract fun onError(error: WSError)

    fun onFinally() {}

    companion object {
        val TAG = "wowBaseCallback"
        private val gson = Gson()
    }
}
