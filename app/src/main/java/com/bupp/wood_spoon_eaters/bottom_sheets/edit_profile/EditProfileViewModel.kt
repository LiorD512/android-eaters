package com.bupp.wood_spoon_eaters.bottom_sheets.edit_profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.model.EaterRequest
import com.bupp.wood_spoon_eaters.managers.MediaUploadManager
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class EditProfileViewModel(private val userRepository: UserRepository, val eaterDataManager: EaterDataManager, private val mediaUploadManager: MediaUploadManager) : ViewModel(),
    MediaUploadManager.UploadManagerListener {

    val progressData = ProgressData()
    private val TAG: String? = "wowEditProfileVM"
    val refreshThumbnailEvent: SingleLiveEvent<NavigationEvent> = SingleLiveEvent()

    data class NavigationEvent(val isSuccess: Boolean = false)

    val userDetails: SingleLiveEvent<Eater> = SingleLiveEvent()

    var eater: EaterRequest = EaterRequest()


    fun getEaterProfile() {
        userDetails.postValue(eaterDataManager.currentEater)
    }

    fun saveEater(firstName: String?, lastName: String?, email: String?, uploadedPhoto: Boolean) {

        eater = eater.copy(
            firstName = firstName,
            lastName = lastName,
            email = email
        )

        if(uploadedPhoto){
            progressData.startProgress()
            uploadMediaData()
        }else{
            updateEater()
        }

    }

    private fun uploadMediaData() {
        this.eater.let { it ->


            val mediaUploadRequests: MutableList<Uri> = mutableListOf()
            it.tempThumbnail?.let { media ->
                mediaUploadRequests.add(media)
            }
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    mediaUploadManager.upload(mediaUploadRequests, this@EditProfileViewModel)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                } finally {
                }
            }
        }
    }

    override fun onMediaUploadCompleted(mediaUploadResult: List<MediaUploadManager.MediaUploadResult>) {
        mediaUploadResult.forEach {
            val result = it
            this.eater.thumbnail = result.preSignedUrlKey
        }
        updateEater()
    }

    private fun updateEater() {
        eater.let {
            viewModelScope.launch {
                val userRepoResult = userRepository.updateEater(it)
                when (userRepoResult.type) {
                    UserRepository.UserRepoStatus.SERVER_ERROR -> {
                        Log.d(TAG, "updateEater - NetworkError")
//                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                    }
                    UserRepository.UserRepoStatus.SOMETHING_WENT_WRONG -> {
                        Log.d(TAG, "updateEater - GenericError")
//                        errorEvents.postValue(ErrorEventType.SOMETHING_WENT_WRONG)
                    }
                    UserRepository.UserRepoStatus.SUCCESS -> {
                        Log.d(TAG, "updateEater - Success")
                        refreshThumbnailEvent.postValue(NavigationEvent(true))
//                        navigationEvent.postValue(NavigationEventType.EDIT_PROFILE_DONE)
                    }
                    else -> {
                        Log.d(TAG, "updateEater - NetworkError")
//                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                    }
                }
                progressData.endProgress()
            }
        }
    }



    fun updateTempThumbnail(resultUri: Uri) {
        eater.tempThumbnail = resultUri
    }


}
