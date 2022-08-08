package com.bupp.wood_spoon_chef.data.remote.network

import com.bupp.wood_spoon_chef.common.MTLogger
import com.bupp.wood_spoon_chef.data.remote.network.base.*
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.UnknownHostException

class ResponseHandler(private val errorManger: ErrorManger) {

    suspend fun <Type> safeApiCall(
        apiCall: suspend () -> ServerResponse<Type>
    ): ResponseResult<Type> {
        return withContext(Dispatchers.IO) {
            try {
                val serverResponse = ResponseSuccess(data = apiCall.invoke().data)
                MTLogger.d(body = "SUCCESS", tag = getTag(apiCall))
                return@withContext serverResponse
            } catch (ex: Exception) {
                val result: ResponseResult<Type> = handleException(ex, getTag(apiCall))
                if (result is ResponseError) {
                    errorManger.onError(getCallingFunctionName(apiCall), result.error)
                }
                return@withContext result
            }
        }
    }

    private fun <T> handleException(ex: Exception, tag: String): ResponseResult<T> {
        when (ex) {
            is HttpException -> {
                try {
                    /** Trying to parse FormattedError **/
                    val source = ex.response()?.errorBody()?.source()
                    source?.let {
                        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

                        val jsonAdapter = moshi.adapter(FormattedError::class.java)
                        val formattedError = jsonAdapter.fromJson(source)
                        formattedError?.let {
                            if (!formattedError.errors.isNullOrEmpty() || formattedError.message.isNotEmpty()) {
                                MTLogger.e(
                                    body = " ERROR!! Formatted Error code: ${formattedError.code}, message: ${formattedError.message}, wsError: ${formattedError.errors}",
                                    tag = tag,
                                    cacheLog = true
                                )
                                return ResponseError(formattedError)
                            }
                        }
                    }
                } catch (ex: Exception) {
                    /** Error in parsing, continue to HTTPError **/
                    MTLogger.e(ex.localizedMessage ?: "")
                }

                val httpError = when (ex.code()) {
                    204 -> ResponseError<T>(
                        HTTPError(
                            code = ex.code(),
                            message = ex.localizedMessage ?: "",
                            type = HTTPErrorType.NO_CONTENT
                        )
                    )
                    400 -> ResponseError(
                        HTTPError(
                            code = ex.code(),
                            message = ex.localizedMessage ?: "",
                            type = HTTPErrorType.BAD_REQUEST
                        )
                    )
                    401 -> ResponseError(
                        HTTPError(
                            code = ex.code(),
                            message = ex.localizedMessage ?: "",
                            type = HTTPErrorType.UNAUTHORIZED
                        )
                    )
                    403 -> ResponseError(
                        HTTPError(
                            code = ex.code(),
                            message = ex.localizedMessage ?: "",
                            type = HTTPErrorType.FORBIDDEN
                        )
                    )
                    404 -> ResponseError(
                        HTTPError(
                            code = ex.code(),
                            message = ex.localizedMessage ?: "",
                            type = HTTPErrorType.NOT_FOUND
                        )
                    )
                    500 -> ResponseError(
                        HTTPError(
                            code = ex.code(),
                            message = ex.localizedMessage ?: "",
                            type = HTTPErrorType.UNDER_MAINTENANCE
                        )
                    )
                    503 -> ResponseError(
                        HTTPError(
                            code = ex.code(),
                            message = ex.localizedMessage ?: "",
                            type = HTTPErrorType.SERVER_DOWN
                        )
                    )
                    else -> ResponseError(
                        HTTPError(
                            code = ex.code(),
                            message = ex.localizedMessage ?: "",
                            type = HTTPErrorType.UNKNOWN_ERROR
                        )
                    )
                }
                (httpError.error as HTTPError).let { error ->
                    MTLogger.e(
                        body = "ERROR!! Http Error code: ${error.code}, type: ${error.type.name}, message:  ${error.message}",
                        tag = tag,
                        cacheLog = true
                    )
                }
                return httpError
            }
            is UnknownHostException -> {
                MTLogger.e(
                    body = "ERROR!! NetworkError ${ex.localizedMessage}",
                    tag = tag,
                    cacheLog = true
                )
                return ResponseError(NetworkError("NetworkError"))
            }
            is KotlinNullPointerException, is NullPointerException -> {
                MTLogger.i(body = "SUCCESS - Empty Response", tag = tag, cacheLog = true)
                return ResponseSuccess(null)
            }
            is JsonDataException -> {
                MTLogger.e(
                    body = "ERROR!! JsonError ${ex.localizedMessage}",
                    tag = tag,
                    cacheLog = true
                )
                return ResponseError(JsonError(ex.localizedMessage ?: "JsonError"))
            }
            else -> {
                MTLogger.e(
                    body = "ERROR!! UnknownError ${ex.localizedMessage}",
                    tag = tag,
                    cacheLog = true
                )
                return ResponseError(UnknownError(ex.localizedMessage ?: "UnknownError"))
            }
        }
    }

    private fun getTag(apiCall: suspend () -> ServerResponse<*>): String {
        val methodName = apiCall.javaClass.enclosingMethod?.name ?: ""
        val className = apiCall.javaClass.enclosingClass?.simpleName ?: ""
        return ".($className.kt:1).$methodName()"
    }

    private fun getCallingFunctionName(apiCall: suspend () -> ServerResponse<*>): String {
        return apiCall.javaClass.enclosingMethod?.name ?: ""
    }
}
