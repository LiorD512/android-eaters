package com.bupp.wood_spoon_eaters.domain

import android.content.Context
import android.net.Uri
import com.bupp.wood_spoon_eaters.domain.comon.UseCase

/**
 *  const val @ONBOARDING_VIDEO_RES add video file to res/raw with id R.raw.onboarding_video
 *  */
const val ONBOARDING_VIDEO_RES = ""
const val ANDROID_RES_ROOT_PATH = "android.resource://"

class GetOnboardingVideoPathUseCase(
    val context: Context
) : UseCase<Uri, Nothing?> {

    override fun execute(params: Nothing?): Uri = Uri.parse(
        ANDROID_RES_ROOT_PATH + context.packageName.toString() + "/" + ONBOARDING_VIDEO_RES
    )
}