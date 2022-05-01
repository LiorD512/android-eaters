package com.bupp.wood_spoon_chef.data.remote.network.base

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ServerResponse<T> (
    @Json(name = "code") var code: Int = 0,
    @Json(name = "message") var message: String? = null,
    @Json(name = "data") var data: T? = null,
    @Json(name = "errors") var errors: List<WSError>? = null
)

sealed class ResponseResult<T>

class ResponseSuccess<T>(
    val data: T? = null,
) : ResponseResult<T>()

class ResponseError<T>(
    val error: MTError
) : ResponseResult<T>()



