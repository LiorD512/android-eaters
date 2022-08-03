package com.bupp.wood_spoon_chef.data.remote.network.base

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

sealed class MTError(
    open val message: String?
)

@JsonClass(generateAdapter = true)
class FormattedError(
    @Json(name = "code") var code: Int = 0,
    @Json(name = "message") override var message: String = "",
    @Json(name = "errors") var errors: List<WSError>? = null
) : MTError(message) {
    fun hasWsError(): Boolean {
        return !errors.isNullOrEmpty()
    }
}

class JsonError(
    override val message: String,
) : MTError(message)

class NetworkError(
    override val message: String,
) : MTError(message)

class UnknownError(
    override val message: String,
) : MTError(message)

data class HTTPError(
    val code: Int,
    override val message: String,
    val type: HTTPErrorType
) : MTError(message)

data class CustomError(
    override val message: String
) : MTError(message)

enum class HTTPErrorType {
    NOT_FOUND,
    BAD_REQUEST,
    UNAUTHORIZED,
    FORBIDDEN,
    NO_CONTENT,
    UNDER_MAINTENANCE,
    SERVER_DOWN,
    UNKNOWN_ERROR
}

@JsonClass(generateAdapter = true)
data class WSError(
    @Json(name = "code") val code: Int?,
    @Json(name = "message") val msg: String?
)

fun MTError.errorCode() = (this as? FormattedError)?.errors?.firstOrNull()?.code
    ?: (this as? FormattedError)?.code ?: 0
