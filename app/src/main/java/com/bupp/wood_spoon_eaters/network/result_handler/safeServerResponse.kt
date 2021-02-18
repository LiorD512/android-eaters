//package com.bupp.wood_spoon_eaters.network.result_handler
//
//import android.util.Log
//import com.bupp.wood_spoon_eaters.model.ServerResponse
//import com.google.gson.Gson
//import kotlinx.coroutines.CoroutineDispatcher
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import retrofit2.HttpException
//import java.io.IOException
//
//suspend fun <T> safeServiceResponse(dispatcher: CoroutineDispatcher = Dispatchers.IO, apiCall: suspend () -> T): ServiceResponseHandler<T> {
//    return withContext(dispatcher) {
//        try {
//            val result = apiCall.invoke()
//            ServiceResponseHandler.Success(result)
//        } catch (throwable: Throwable) {
//            when (throwable) {
//                is IOException -> ServiceResponseHandler.NetworkError
//                is HttpException -> {
//                    val code = throwable.code()
//                    when(code){
//                        400 -> {
//                            val errorsBody = throwable.response()?.errorBody()
//                            var gson = Gson()
//                            val serverResponse = gson.fromJson(errorsBody?.string(), ServerResponse::class.java)
//                            Log.d("safeApiCall", "safeApiCall serverResponse: $serverResponse")
//                            ServiceResponseHandler.WSCustomError(serverResponse.errors)
//                        }
//                        else ->{
//                            val errorResponse = convertErrorBody(throwable)
//                            ServiceResponseHandler.GenericError(code, errorResponse)
//                        }
//                    }
//                }
//                else -> {
//                    ServiceResponseHandler.GenericError(null, null)
//                }
//            }
//        }
//    }
//}
//
//private fun convertErrorBody(throwable: HttpException): String? {
//    return "errorrrrrrrrrrrrrrrrrrrrr"
////    return try {
////        throwable.response()?.errorBody()?.source()?.let {
////            val moshiAdapter = Moshi.Builder().build().adapter(ErrorResponse::class.java)
////            moshiAdapter.fromJson(it)
////        }
////    } catch (exception: Exception) {
////        null
////    }
//}
