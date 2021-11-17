package com.bupp.wood_spoon_eaters.managers

import android.content.Context
import android.net.Uri
import android.util.Log
import com.bupp.wood_spoon_eaters.model.PreSignedUrl
import com.bupp.wood_spoon_eaters.network.ApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import android.graphics.Bitmap

import android.graphics.BitmapFactory
import java.io.*
import java.lang.Exception


class MediaUploadManager(private val context: Context, private val apiService: ApiService) {

    interface UploadManagerListener {
        fun onMediaUploadCompleted(mediaUploadResult: List<MediaUploadResult>)
    }

    data class MediaUploadResult(
        val preSignedUrlKey: String
    )

    suspend fun upload(uploadRequests: List<Uri>, listener: UploadManagerListener) {
        Log.d("wowUploadManager", "uploadRequests -> ${uploadRequests.size}")

        val results = mutableListOf<MediaUploadResult>()

        uploadRequests.forEach { media ->
            val preSignedResult: PreSignedUrl? = apiService.postEaterPreSignedUrl().data

            preSignedResult?.let { it ->
                putFileOnAws(uri = media, preSignedUrl = it.url)
                results.add(MediaUploadResult(preSignedResult.key))
            }
        }
        listener.onMediaUploadCompleted(results)
    }

    private suspend fun putFileOnAws(dispatcher: CoroutineDispatcher = Dispatchers.IO, uri: Uri?, preSignedUrl: String) {
        uri?.let {
            withContext(dispatcher) {
                val bitmap = decodeUri(context, uri, 200)
                val stream = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.PNG, 90, stream)
                val image = stream.toByteArray()
                val requestBody = image.toRequestBody("application/octet-stream".toMediaTypeOrNull(), 0, image.size)
                val result = apiService.uploadAsset(preSignedUrl, requestBody)
                Log.d(TAG, "result - $result")
            }
//            withContext(dispatcher) { //todo - this func uses uri - we replaced it with bitmap
//                val resolver = context.contentResolver
//                resolver.openInputStream(it).use { stream ->
//                    // Perform operations on "stream".
//                    stream?.let {
//                        val buf = ByteArray(stream.available())
//                        while (stream.read(buf) !== -1) {
//                        }
//                        val requestBody = buf.toRequestBody("application/octet-stream".toMediaTypeOrNull(), 0, buf.size)
//                        val result = apiService.uploadAsset(preSignedUrl, requestBody)
//                        Log.d(TAG, "result - $result")
//                    }
//                }
//            }
        }
    }

    @Throws(FileNotFoundException::class)
    fun decodeUri(c: Context, uri: Uri?, requiredSize: Int): Bitmap? {
        val o = BitmapFactory.Options()
        o.inJustDecodeBounds = true
        BitmapFactory.decodeStream(c.contentResolver.openInputStream(uri!!), null, o)
        var width_tmp = o.outWidth
        var height_tmp = o.outHeight
        var scale = 1
        while (true) {
            if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize) break
            width_tmp /= 2
            height_tmp /= 2
            scale *= 2
        }
        val o2 = BitmapFactory.Options()
        o2.inSampleSize = scale
        return BitmapFactory.decodeStream(c.contentResolver.openInputStream(uri), null, o2)
    }


    companion object{
        const val TAG = "wowMediaUploadManager"
    }


}