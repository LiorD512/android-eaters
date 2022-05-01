package com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.edit_account_and_kitchen

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.common.MTLogger
import com.bupp.wood_spoon_chef.data.remote.model.*
import com.bupp.wood_spoon_chef.di.abs.LiveEventData
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account.CreateAccountViewModel
import com.bupp.wood_spoon_chef.managers.MediaUploadManager
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.data.repositories.MetaDataRepository
import com.bupp.wood_spoon_chef.data.repositories.UserRepository
import com.bupp.wood_spoon_chef.utils.media_utils.MediaUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UpdateAccountViewModel(
    val metaDataRepository: MetaDataRepository,
    val userRepository: UserRepository,
    private val mediaUploadManager: MediaUploadManager
) : BaseViewModel(), MediaUploadManager.UploadManagerListener {

    val navigationEvent: MutableLiveData<CreateAccountViewModel.NavigationEventType> =
        MutableLiveData()

    enum class NavigationEventType

    /** Cook Details Update **/
    private var cookRequest: CookRequest? = null
    val cookData = MutableLiveData<Cook?>()
    private var mediaRequests: MediaRequests? = null
    val mediaRequestData = MutableLiveData<MediaRequests?>()

    fun initCookData() {
        val cook = userRepository.getCurrentChef()
        if (cookRequest == null && cook != null) {
            cookData.postValue(cook)
            cookRequest = CookRequest(video = cook.video)
            mediaRequests = MediaRequests()
        }
    }

    fun updateCookAccount(firstName: String?, lastName: String?, email: String?) {
        progressData.startProgress()
        cookRequest?.let { cookRequest ->
            cookRequest.firstName = firstName
            cookRequest.lastName = lastName
            cookRequest.email = email

            val mediaUploadRequests: MutableList<MediaUploadManager.MediaUploadRequest> =
                mutableListOf()
            mediaRequests?.tempThumbnail?.let { media ->
                mediaUploadRequests.add(
                    MediaUploadManager.MediaUploadRequest(
                        Constants.MEDIA_TYPE_COOK_IMAGE,
                        media
                    )
                )
            }
            uploadMediaRequests(mediaUploadRequests)
        }
    }

    fun updateHomeKitchen(
        restaurantName: String?,
        about: String?
    ) {
        progressData.startProgress()
        cookRequest?.let { cookRequest ->
            cookRequest.restaurantName = restaurantName
            cookRequest.about = about

            val mediaUploadRequests: MutableList<MediaUploadManager.MediaUploadRequest> =
                mutableListOf()
            mediaRequests?.tempCover?.let { media ->
                mediaUploadRequests.add(
                    MediaUploadManager.MediaUploadRequest(
                        Constants.MEDIA_TYPE_COVER_IMAGE,
                        media
                    )
                )
            }
            mediaRequests?.tempVideoUri?.let { media ->
                mediaUploadRequests.add(
                    MediaUploadManager.MediaUploadRequest(
                        Constants.MEDIA_TYPE_COOK_STORY,
                        media
                    )
                )
            }
            uploadMediaRequests(mediaUploadRequests)
        }
    }

    fun hasCover(): Boolean {
        return cookRequest?.tempCover != null
    }

    fun deleteKitchenVideo(){
        cookRequest?.video = null
        mediaRequests?.tempVideoUri = null
    }

    private fun uploadMediaRequests(mediaUploadRequests: MutableList<MediaUploadManager.MediaUploadRequest>) {
        viewModelScope.launch(Dispatchers.IO) {
            mediaUploadManager.uploadMedia(mediaUploadRequests, this@UpdateAccountViewModel)
        }
    }

    override fun onMediaUploadCompleted(mediaUploadResult: List<MediaUploadManager.MediaUploadResult>) {
        mediaUploadResult.forEach {
            val result = it
            when (result.uploadType) {
                Constants.MEDIA_TYPE_COOK_IMAGE -> {
                    this.cookRequest?.thumbnail = result.preSignedUrlKey
                }
                Constants.MEDIA_TYPE_COVER_IMAGE -> {
                    this.cookRequest?.restaurantCover = result.preSignedUrlKey
                }
                Constants.MEDIA_TYPE_COOK_STORY -> {
                    this.cookRequest?.video = result.preSignedUrlKey
                }
            }
        }
        accountClient()
    }

    val postAccountSuccessEvent = LiveEventData<Boolean>()
    private fun accountClient() {
        this.cookRequest?.let { it ->
            viewModelScope.launch {
                when (val response = userRepository.updateCook(it)) {
                    is ResponseSuccess -> {
                        postAccountSuccessEvent.postRawValue(true)
                    }
                    is ResponseError -> {
                        errorEvent.postRawValue(response.error)
                    }
                }
                progressData.endProgress()
            }
        }
    }

    /** Start media capture **/

    fun onChangeThumbnailClick() {
        MTLogger.d("chef uploading thumbnail")
        navigationEvent.postValue(CreateAccountViewModel.NavigationEventType.START_IMAGE_CAPTURE)
    }

    fun onChangeCoverClick() {
        MTLogger.d("chef uploading cover")
        navigationEvent.postValue(CreateAccountViewModel.NavigationEventType.START_COVER_CAPTURE)
    }

    fun onChangeVideoClick() {
        MTLogger.d("chef uploading video")
        navigationEvent.postValue(CreateAccountViewModel.NavigationEventType.START_VIDEO_CAPTURE)
    }

    /** Media capture result **/

    //setup details
    fun onThumbnailResult(result: MediaUtils.MediaUtilResult) {
        result.fileUri?.let {
            mediaRequests?.let { mediaRequests ->
                mediaRequests.tempThumbnail = result.fileUri
                mediaRequestData.postValue(mediaRequests)
            }
        }
    }

    //setup details
    fun onCoverResult(result: MediaUtils.MediaUtilResult) {
        result.fileUri?.let {
            mediaRequests?.let { mediaRequests ->
                mediaRequests.tempCover = result.fileUri
                mediaRequestData.postValue(mediaRequests)
            }
        }
    }

    fun onVideoResult(videoUri: Uri) {
        mediaRequests?.let { mediaRequests ->
            mediaRequests.tempVideoUri = videoUri
            mediaRequestData.postValue(mediaRequests)
        }
    }

    fun getCountries(): List<Country> {
        return metaDataRepository.getCountries().filterIsInstance<Country>()
    }

    fun onCountrySelected(chosenItem: SelectableString) {
        this.cookRequest?.countryId = chosenItem.id
    }


    fun resetData() {
        cookRequest = null
        mediaRequests = null
        cookData.postValue(null)
        mediaRequestData.postValue(null)
    }

}