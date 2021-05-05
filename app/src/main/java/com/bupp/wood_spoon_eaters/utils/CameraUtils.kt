//package com.bupp.wood_spoon_eaters.utils
//
//import android.app.Activity
//import android.net.Uri
//import com.github.dhaval2404.imagepicker.ImagePicker
//import java.io.File
//
//object CameraUtils {
//
//    data class CameraUtilResult(
//        val fileUri: Uri? = null,
//        val file: File? = null,
//        val path: String? = null,
//        val error: String? = null
//    )
//
//    interface CameraUtilListener {
//        fun onCameraUtilResult(result: CameraUtilResult)
//    }
//
//    fun openCameraForResult(activity: Activity, listener: CameraUtilListener) {
//        ImagePicker.with(activity)
//            .crop()
//            .compress(1024)  //Final image size will be less than 1 MB(Optional)
//            .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
//            .start { resultCode, data ->
//                if (resultCode == Activity.RESULT_OK) {
//                    val fileUri = data?.data
//                    val file: File? = ImagePicker.getFile(data)
//                    val filePath: String? = ImagePicker.getFilePath(data)
//                    listener.onCameraUtilResult(CameraUtilResult(fileUri, file, filePath))
//                } else {
//                    listener.onCameraUtilResult(CameraUtilResult(error = "Error fetching image"))
//                }
//            }
//    }
//
//}