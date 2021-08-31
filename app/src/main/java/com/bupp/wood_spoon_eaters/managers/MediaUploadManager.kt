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
                val resolver = context.contentResolver
                resolver.openInputStream(it).use { stream ->
                    // Perform operations on "stream".
                    stream?.let {
                        val buf = ByteArray(stream.available())
                        while (stream.read(buf) !== -1) {
                        }
                        val requestBody = buf.toRequestBody("application/octet-stream".toMediaTypeOrNull(), 0, buf.size)
                        val result = apiService.uploadAsset(preSignedUrl, requestBody)
                        Log.d(TAG, "result - $result")
                    }
                }
            }
        }
    }

    companion object{
        const val TAG = "wowMediaUploadManager"
    }


}