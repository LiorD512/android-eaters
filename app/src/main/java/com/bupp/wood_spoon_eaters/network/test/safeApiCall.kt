package com.bupp.wood_spoon_eaters.network.test

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher = Dispatchers.IO, apiCall: suspend () -> T): ResultHandler<T> {
    return withContext(dispatcher) {
        try {
            ResultHandler.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> ResultHandler.NetworkError
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = convertErrorBody(throwable)
                    ResultHandler.GenericError(code, errorResponse)
                }
                else -> {
                    ResultHandler.GenericError(null, null)
                }
            }
        }
    }
}

private fun convertErrorBody(throwable: HttpException): String? {
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