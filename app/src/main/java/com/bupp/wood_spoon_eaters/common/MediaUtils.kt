package com.bupp.wood_spoon_eaters.common

import android.Manifest
import android.app.Activity
import android.content.ComponentName
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
import com.bupp.wood_spoon_eaters.BuildConfig
import com.bupp.wood_spoon_eaters.dialogs.MediaChooserDialog
import com.bupp.wood_spoon_eaters.utils.getFilePath
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MediaUtils(activity: FragmentActivity? = null, listener: MediaUtilListener) : MediaChooserDialog.MediaChooserListener {

    enum class MediaUtilsType{
        MEDIA_UTILS_PHOTO,
        MEDIA_UTILS_VIDEO,
    }
    data class MediaUtilResult(
            val type: MediaUtilsType,
            val fileUri: Uri? = null,
            val file: File? = null,
            val path: String? = null,
            val error: String? = null
    )

    interface MediaUtilListener {
        fun onMediaUtilResult(result: MediaUtilResult)
    }

    var activity: FragmentActivity? = null
    var listener: MediaUtilListener? = null

    init {
        this.activity = activity
        this.listener = listener
    }

    var photoURI: Uri? = null
    var currentPhotoPath: String? = null
    var isPhotoMode: Boolean = true

    private val mediaLauncher = activity!!.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        Log.d(TAG, "Activity For Result - cameraResult")
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            Log.d("wowFiles", "photoURI: $photoURI")
            val file = File(currentPhotoPath)
            listener.onMediaUtilResult(MediaUtilResult(MediaUtilsType.MEDIA_UTILS_PHOTO, photoURI, file, currentPhotoPath))
        }
    }

    private val mediaGalleryLauncher = activity!!.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        Log.d(TAG, "Activity For Result - mediaGalleryLauncher")
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            var fileUri: Uri? = result.data?.data

            val filePath = fileUri?.let { getFilePath(activity, it) }
            filePath?.let{
                val file = File(it)
                listener.onMediaUtilResult(MediaUtilResult(MediaUtilsType.MEDIA_UTILS_PHOTO, fileUri, file, it))
            }
        }
    }

    private val requestPermissionLauncher = activity!!.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        Log.d(TAG,"requestPermissionLauncher: $it")
        checkMediaPermission()
    }

    private val openCameraResult = activity!!.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            var fileUri: Uri? = intent?.data
            Log.d(TAG, "openCameraResult: $fileUri")
            fileUri?.let{
                listener.onMediaUtilResult(MediaUtilResult(MediaUtilsType.MEDIA_UTILS_VIDEO, fileUri = fileUri))
            }
        }
    }

    fun startPhotoFetcher() {
        activity?.let{
            isPhotoMode = true
            checkMediaPermission()
        }
    }
    fun startVideoFetcher() {
        activity?.let{
            isPhotoMode = false
            checkMediaPermission()
        }
    }

    override fun onMediaChoose(mediaType: Int) {
        when(mediaType){
            Constants.MEDIA_TYPE_CAMERA -> {
                if(isPhotoMode){
                    openPhotoCamera()
                }else{
                    openVideoCamera()
                }
            }
            Constants.MEDIA_TYPE_GALLERY -> {
                if(isPhotoMode){
                    openPhotoGallery()
                }else{
                    openVideoGallery()
                }
            }
        }
    }

    //PHOTOS
    private fun openPhotoCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    photoURI = FileProvider.getUriForFile(activity!!,
                            BuildConfig.APPLICATION_ID + ".fileprovider", it)

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
        val storageDir: File? = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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
        val galleryIntent = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        mediaGalleryLauncher.launch(galleryIntent)
    }

    //VIDEOS

    private fun openVideoCamera() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
        openCameraResult.launch(intent)
    }

    private fun openVideoGallery() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        openCameraResult.launch(intent)
    }


    //PERMISIONS
    private fun checkMediaPermission() {
        when {
            ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d(TAG, "location grated")
                    MediaChooserDialog(this).show(activity!!.supportFragmentManager, Constants.MEDIA_CHOOSER_DIALOG)
            }
            shouldShowRequestPermissionRationale() -> {
                Log.d(TAG,"shouldShowRequestPermissionRationale")
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected.
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE))
            }
            else -> {
                Log.d(TAG,"asking for permission")
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE))
            }
        }
    }

    private fun shouldShowRequestPermissionRationale() =
            ActivityCompat.shouldShowRequestPermissionRationale(
                    activity!!,
                    Manifest.permission.CAMERA
            ) && ActivityCompat.shouldShowRequestPermissionRationale(
                    activity!!,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            )



    companion object{
        const val TAG = "wowMediaUtils"
    }




















}