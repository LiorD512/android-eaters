//package com.bupp.wood_spoon_eaters.utils
//
//import android.net.Uri
//import com.bupp.wood_spoon_eaters.network.ApiService
//import kotlinx.coroutines.CoroutineDispatcher
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.RequestBody.Companion.toRequestBody
//import java.io.File
//import java.io.FileInputStream
//
//class AWSFileHelper(private val apiService: ApiService){
//
//    suspend fun putFileOnAws(fileUri : Uri, preSignedUrl: String){
//        fileUri.path?.let {
//            putFileOnAws(Dispatchers.IO, it , preSignedUrl)
//        }
//    }
//
//    suspend fun putFileOnAws(filePath: String, preSignedUrl: String){
//        putFileOnAws(Dispatchers.IO,filePath, preSignedUrl)
//    }
//
//    private suspend fun putFileOnAws(dispatcher: CoroutineDispatcher = Dispatchers.IO,filePath: String, preSignedUrl: String) =
//        withContext(dispatcher){
//            val file = File(filePath)
//            val inVal = FileInputStream(file)
//            val buf: ByteArray
//            buf = ByteArray(inVal.available())
//            while(inVal.read(buf) !== -1){}
//            val requestBody =
//                buf.toRequestBody("application/octet-stream".toMediaTypeOrNull(), 0, buf.size)
//            apiService.uploadAsset(preSignedUrl, requestBody)
//    }
//}