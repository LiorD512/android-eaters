package com.bupp.wood_spoon_chef.managers

import android.content.Context
import android.net.Uri
import android.util.Log
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.common.MTLogger
import com.bupp.wood_spoon_chef.data.remote.model.PreSignedUrl
import com.bupp.wood_spoon_chef.data.remote.network.ApiService
import com.bupp.wood_spoon_chef.data.remote.network.ErrorManger
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody


class MediaUploadManager(
    private val context: Context,
    private val apiService: ApiService,
    private val errorManger: ErrorManger
) {

    interface UploadManagerListener {
        fun onMediaUploadCompleted(mediaUploadResult: List<MediaUploadResult>)
    }

    data class MediaUploadResult(
        val uploadType: Int,
        val preSignedUrlKey: String
    )

    data class MediaUploadRequest(
        val type: Int,
        val uri: Uri
    )

    suspend fun uploadMedia(
        uploadRequests: List<MediaUploadRequest>,
        listener: UploadManagerListener
    ) {
        Log.d("wowUploadManager", "uploadRequests -> ${uploadRequests.size}")
        try {
            val results = mutableListOf<MediaUploadResult>()
            uploadRequests.forEach { media ->
                var preSignedResult: PreSignedUrl? = null
                when (media.type) {
                    Constants.MEDIA_TYPE_COOK_IMAGE -> {
                        //get cook image preSignedUrl
                        preSignedResult =
                            apiService.getCookPreSignedUrl(Constants.PRESIGNED_URL_THUMBNAIL).data
                    }
                    Constants.MEDIA_TYPE_COVER_IMAGE -> {
                        //get cover image preSignedUrl
                        preSignedResult =
                            apiService.getCookPreSignedUrl(Constants.PRESIGNED_URL_COVER).data
                    }
                    Constants.MEDIA_TYPE_COOK_STORY -> {
                        //get cook video preSignedUrl
                        preSignedResult =
                            apiService.getCookPreSignedUrl(Constants.PRESIGNED_URL_VIDEO).data
                    }
                    Constants.MEDIA_TYPE_DISH_MAIN_IMAGE -> {
                        //get dish main image preSignedUrl
                        preSignedResult =
                            apiService.getDishPreSignedUrl(Constants.PRESIGNED_URL_THUMBNAIL).data
                    }
                    Constants.MEDIA_TYPE_DISH_IMAGE -> {
                        //get dish image preSignedUrl
                        preSignedResult =
                            apiService.getDishPreSignedUrl(Constants.PRESIGNED_URL_THUMBNAIL).data
                    }
                    Constants.MEDIA_TYPE_DISH_VIDEO -> {
                        //get dish image preSignedUrl
                        preSignedResult =
                            apiService.getDishPreSignedUrl(Constants.PRESIGNED_URL_VIDEO).data
                    }
                }
                preSignedResult?.let { it ->
//                val filePath = getMediaPath(media.uri)
                    putFileOnAws(uri = media.uri, preSignedUrl = it.url)
                    results.add(MediaUploadResult(media.type, preSignedResult.key))
                }
            }
            listener.onMediaUploadCompleted(results)
        } catch (ex: Exception) {
            MTLogger.e("uploadMedia", "Exception: " + ex.localizedMessage)
        }
    }

    private suspend fun putFileOnAws(
        uri: Uri?,
        preSignedUrl: String
    ) {
        try {
            uri?.let {
                val resolver = context.contentResolver
                resolver.openInputStream(it)?.use { stream ->
                    // Perform operations on "stream".
                    val buf: ByteArray = stream.readBytes()
                    MTLogger.d("trying to upload media: fileSize = ${buf.size}, preSignedUrl= $preSignedUrl")
                    val requestBody = buf.toRequestBody(
                        "application/octet-stream".toMediaTypeOrNull(),
                        0,
                        buf.size
                    )
                    val result = apiService.uploadAsset(preSignedUrl, requestBody)
                    MTLogger.d("uploading media finished successfully")
                }
            }
        } catch (ex: Exception) {
            MTLogger.e("uploading media failed. Exception: " + ex.localizedMessage)
        }
    }
    companion object {
        const val TAG = "wowMediaUploadMgr"
    }
}