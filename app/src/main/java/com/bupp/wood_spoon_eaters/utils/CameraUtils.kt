package com.bupp.wood_spoon_eaters.utils

import android.app.Activity
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class CameraUtils {

    companion object{
        fun openCamera(activity: Activity){
            CropImage.activity()
                    .setFixAspectRatio(false)
                    .setAspectRatio(16,9)
//                    .setMinCropWindowSize(1600,900)
                    .setGuidelines(CropImageView.Guidelines.OFF)
                    .start(activity)
        }

        fun openProfileCamera(activity: Activity){
            CropImage.activity()
                .setFixAspectRatio(true)
                .setAspectRatio(1,1)
                .setGuidelines(CropImageView.Guidelines.OFF)
                .start(activity)
        }
    }
}