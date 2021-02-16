package com.bupp.wood_spoon_eaters.network.result_handler

import com.bupp.wood_spoon_eaters.model.WSError


sealed class ResultHandler<out T> {
    data class Success<out T>(val value: T): ResultHandler<T>()
    data class GenericError(val code: Int? = null, val error: String? = null): ResultHandler<Nothing>()
    data class WSCustomError(val errors: List<WSError>? = null): ResultHandler<Nothing>()
    object NetworkError: ResultHandler<Nothing>()
}