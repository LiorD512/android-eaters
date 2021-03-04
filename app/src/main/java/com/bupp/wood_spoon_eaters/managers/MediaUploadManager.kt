package com.bupp.wood_spoon_eaters.managers

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.bupp.wood_spoon_eaters.model.PreSignedUrl
import com.bupp.wood_spoon_eaters.network.ApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileInputStream

class MediaUploadManager(private val context: Context, private val apiService: ApiService) {

    interface UploadManagerListener {
        fun onMediaUploadCompleted(mediaUploadResult: List<MediaUploadResult>)
    }

    private lateinit var listener: UploadManagerListener

    data class MediaUploadResult(
        val preSignedUrlKey: String
    )

    data class MediaUploadRequest(
        val uri: Uri
    )

    suspend fun upload(uploadRequests: List<Uri>, listener: UploadManagerListener) {
        Log.d("wowUploadManager", "uploadRequests -> ${uploadRequests.size}")
//        taskCounter = (images?.size ?: 0)+(videos?.size ?: 0)

        val results = mutableListOf<MediaUploadResult>()

        uploadRequests.forEach { media ->
            var preSignedResult: PreSignedUrl? = null
            preSignedResult = apiService.postEaterPreSignedUrl().data

            preSignedResult?.let { it ->
                putFileOnAws(filePath = getMediaPath(media), preSignedUrl = it.url)
                results.add(MediaUploadResult(preSignedResult.key))
            }
        }
        listener.onMediaUploadCompleted(results)
    }

    private fun getMediaPath(uri: Uri): String? {
        return if ("content".equals(uri.scheme!!, ignoreCase = true)) {
            getDataColumn(uri)
        } else
            uri.path
    }


    private fun getDataColumn(
        uri: Uri?,
        selection: String? = null,
        selectionArgs: Array<String>? = null
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } catch (ex: java.lang.Exception) {
            Log.d("wowImageUtils", "ex: $ex")
        } finally {
            cursor?.close()
        }
        return null
    }


    private suspend fun putFileOnAws(dispatcher: CoroutineDispatcher = Dispatchers.IO, filePath: String?, preSignedUrl: String) =
        filePath?.let {
            withContext(dispatcher) {
                val file = File(filePath)
                val inVal = FileInputStream(file)
                val buf: ByteArray
                buf = ByteArray(inVal.available())
                while (inVal.read(buf) !== -1) {
                }
                val requestBody = buf.toRequestBody("application/octet-stream".toMediaTypeOrNull(), 0, buf.size)
                apiService.uploadAsset(preSignedUrl, requestBody)
            }

        }


}