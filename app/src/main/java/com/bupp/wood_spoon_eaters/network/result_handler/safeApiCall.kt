package com.bupp.wood_spoon_eaters.network.result_handler

import android.util.Log
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.model.WSError
import com.google.gson.Gson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.adapter.rxjava2.Result.response
import java.io.IOException


suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher = Dispatchers.IO, apiCall: suspend () -> T): ResultHandler<T> {
    return withContext(dispatcher) {
        try {
            ResultHandler.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> {
                    val errorMessage = throwable.message
                    Log.d("safeApiCall","NetworkError: $errorMessage")
                    ResultHandler.NetworkError
                }
                is HttpException -> {
                    val code = throwable.code()
                    when (code) {
                        400 -> {
                            val source = throwable.response()?.errorBody()?.source()
                            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

                            val jsonAdapter: JsonAdapter<ServerResponse<Any>> = moshi.adapter(
                                Types.newParameterizedType(ServerResponse::class.java, WSError::class.java)
                            )
                            val serverResponse = jsonAdapter.fromJson(source)
                            Log.d("safeApiCall","wow errors: $serverResponse")
                            if (serverResponse?.errors != null) {
                                    ResultHandler.WSCustomError(serverResponse?.errors)
                            } else {
                                val errorResponse = convertErrorBody(throwable)
                                ResultHandler.GenericError(code, errorResponse)
                            }
                        }
                        else -> {
                            val errorResponse = convertErrorBody(throwable)
                            ResultHandler.GenericError(code, errorResponse)
                        }
                    }
                }
                else -> {
                    Log.d("safeApiCall", "safeApiCall serverResponse: ${throwable.message}")
                    ResultHandler.GenericError(null, null)
                }
            }
        }
    }
}

private fun convertErrorBody(throwable: HttpException): String {
    return "errorrrrrrrrrrrrrrrrrrrrr"
//    return try {
//        throwable.response()?.errorBody()?.source()?.let {
//            val moshiAdapter = Moshi.Builder().build().adapter(ErrorResponse::class.java)
//            moshiAdapter.fromJson(it)
//        }
//    } catch (exception: Exception) {
//        null
//    }
}
