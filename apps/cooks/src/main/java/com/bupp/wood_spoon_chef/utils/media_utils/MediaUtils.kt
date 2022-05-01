package com.bupp.wood_spoon_chef.utils.media_utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.bupp.wood_spoon_chef.BuildConfig
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.common.MTLogger
import com.bupp.wood_spoon_chef.presentation.dialogs.MediaChooserDialog
import com.bupp.wood_spoon_chef.presentation.features.main.MainActivity
import com.bupp.wood_spoon_chef.utils.getFilePath
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MediaUtils(private val activity: FragmentActivity, private val listener: MediaUtilListener) :
    MediaChooserDialog.MediaChooserListener {

    enum class MediaUtilsType {
        MEDIA_UTILS_PHOTO,
        MEDIA_UTILS_VIDEO,
    }

    data class MediaUtilResult(
        val type: MediaUtilsType,
        val mediaType: Int,
        val fileUri: Uri? = null,
        val file: File? = null,
        val path: String? = null,
        val error: String? = null,
    )

    interface MediaUtilListener {
        fun onMediaUtilResult(result: MediaUtilResult)
        fun fileToBigError()
    }

    private var mediaType: Int = 0

    private var photoURI: Uri? = null
    private var currentPhotoPath: String? = null
    private var isPhotoMode: Boolean = true

    private val mediaLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                MTLogger.d("camera result ok, URI: $photoURI")
                val file = File(currentPhotoPath)
                listener.onMediaUtilResult(
                    MediaUtilResult(
                        MediaUtilsType.MEDIA_UTILS_PHOTO,
                        mediaType,
                        photoURI,
                        file,
                        currentPhotoPath
                    )
                )
            } else {
                MTLogger.e(result.toString())
            }
        }

    private val mediaGalleryLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                MTLogger.d("gallery result ok, URI: $photoURI")
                val fileUri: Uri? = result.data?.data

                val filePath = fileUri?.let { getFilePath(activity, it) }
                filePath?.let {
                    val file = File(it)
                    listener.onMediaUtilResult(
                        MediaUtilResult(
                            MediaUtilsType.MEDIA_UTILS_PHOTO,
                            mediaType,
                            fileUri,
                            file,
                            it
                        )
                    )
                }
            } else {
                MTLogger.e(result.toString())
            }
        }

    private val requestPermissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            it.forEach { result ->
                if (result.value == false)
                // permission denied
                    return@registerForActivityResult
            }
            onMediaOpen()
        }

    private val openCameraResult =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val fileUri: Uri? = intent?.data
                val filePath = fileUri?.let { getFilePath(activity, it) }
                Log.d(TAG, "openCameraResult: $fileUri")
                fileUri?.let {
                    filePath?.let {
                        val file = File(filePath)
                        val fileSize = file.length() / 1000000
                        if (fileSize > 100) {
                            listener.fileToBigError()
                        } else {
                            listener.onMediaUtilResult(
                                MediaUtilResult(
                                    MediaUtilsType.MEDIA_UTILS_VIDEO,
                                    mediaType,
                                    fileUri = fileUri
                                )
                            )
                        }
                    }
                }
            } else {
                MTLogger.e(result.toString())
            }
        }

    fun startPhotoFetcher(mediaType: Int = 0) {
        activity.let {
            isPhotoMode = true
            this.mediaType = mediaType
            onMediaOpen()
        }
    }

    fun startVideoFetcher(mediaType: Int = 0) {
        activity.let {
            isPhotoMode = false
            this.mediaType = mediaType
            onMediaOpen()
        }
    }

    override fun onMediaChoose(mediaType: Int) {
        when (mediaType) {
            Constants.MEDIA_TYPE_CAMERA -> {
                if (isPhotoMode) {
                    openPhotoCamera()
                } else {
                    openVideoCamera()
                }
            }
            Constants.MEDIA_TYPE_GALLERY -> {
                if (isPhotoMode) {
                    openPhotoGallery()
                } else {
                    openVideoGallery()
                }
            }
        }
    }

    //PHOTOS
    private fun openPhotoCamera() {
        MTLogger.d("user request to open camera - photo")
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            if (activity.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    MTLogger.e(ex.localizedMessage ?: "")
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    photoURI = FileProvider.getUriForFile(
                        activity,
                        BuildConfig.APPLICATION_ID + ".fileprovider", it
                    )

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    mediaLauncher.launch(takePictureIntent)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun openPhotoGallery() {
        MTLogger.d("user request to open gallery - photo")
        val galleryIntent =
            Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        mediaGalleryLauncher.launch(galleryIntent)
    }


    //VIDEOS
    private fun openVideoCamera() {
        MTLogger.d("user request to open camera - video")
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 104857600L)
        openCameraResult.launch(intent)
    }

    private fun openVideoGallery() {
        MTLogger.d("user request to open gallery - video")
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        openCameraResult.launch(intent)
    }


    private fun onMediaOpen() {
        if (checkMediaPermission()) {
            MediaChooserDialog(this).show(
                activity.supportFragmentManager,
                Constants.MEDIA_CHOOSER_TAG
            )
        }
    }

    //PERMISSIONS
    private fun checkMediaPermission(): Boolean {
        val canAccessCamera = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        val canReadExternal = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val permissionsNeeded: MutableList<String> = ArrayList()
        if (!canAccessCamera) {
            permissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (!canReadExternal) {
            permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (permissionsNeeded.isNotEmpty()) {
            if (shouldShowRequestPermissionRationale()) {
                requestPermissionLauncher.launch(permissionsNeeded.toTypedArray())
            } else {
                requestPermissionLauncher.launch(permissionsNeeded.toTypedArray())
            }
            return false
        }
        return true
    }

    private fun shouldShowRequestPermissionRationale() =
        ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.CAMERA
        ) && ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

    companion object {
        const val TAG = "wowMediaUtils"
    }
}