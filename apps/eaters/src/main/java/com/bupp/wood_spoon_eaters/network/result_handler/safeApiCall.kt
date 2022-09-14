package com.bupp.wood_spoon_eaters.network.result_handler

import android.util.Log
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.model.WSError
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

class ResultManager(private val errorManager: ErrorManger) {

    suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher = Dispatchers.IO, apiCall: suspend () -> T): ResultHandler<T> {
        return withContext(dispatcher) {
            try {
                val result = apiCall.invoke()
                if(result is ServerResponse<*>) {
                    if(result.code !in 200.until(400)) {
                        return@withContext ResultHandler.WSCustomError(errors = result.errors)
                    }
                }
                if(result is Response<*>) {
                    if(!result.isSuccessful) {
                        return@withContext mapResponseToErrorResult(result)
                    }
                }
                ResultHandler.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is IOException -> {
                        val errorMessage = throwable.message
                        Log.d("safeApiCall", "NetworkError: $errorMessage")
                        ResultHandler.NetworkError
                    }
                    is HttpException -> {
                        val code = throwable.code()
                        when (code) {
                            in 400.until(600) -> {
                                mapResponseToErrorResult(throwable.response())
                            }
                            else -> {
                                val errorResponse = convertErrorBody(throwable)
                                errorManager.onError(getCallingFunctionName(apiCall), throwable.localizedMessage ?: "")
                                ResultHandler.GenericError(code, errorResponse)
                            }
                        }
                    }
                    else -> {
                        Timber.e(throwable, "safeApiCall serverResponse [%s]", throwable.message)
                        errorManager.onError(getCallingFunctionName(apiCall), throwable.localizedMessage ?: "")
                        ResultHandler.GenericError(null, null)
                    }
                }
            }
        }
    }

    private fun <T> getCallingFunctionName(apiCall: suspend () -> T): String {
        return apiCall.javaClass.enclosingMethod?.name ?: ""
    }

    private fun <T> mapResponseToErrorResult(response: Response<*>?) : ResultHandler<T> {
        if(response == null) {
            return ResultHandler.GenericError(-1, "missing response object")
        }
        return try {
            val source = response.errorBody()?.source()
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

            val jsonAdapter: JsonAdapter<ServerResponse<Any>> = moshi.adapter(
                Types.newParameterizedType(ServerResponse::class.java, WSError::class.java)
            )
            val serverResponse = jsonAdapter.fromJson(source)
            Log.d("safeApiCall", "errors: $serverResponse")
                ResultHandler.WSCustomError(serverResponse?.errors)
        } catch (ex: Exception) {
            ResultHandler.GenericError(response.code(), response.message())
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
}
