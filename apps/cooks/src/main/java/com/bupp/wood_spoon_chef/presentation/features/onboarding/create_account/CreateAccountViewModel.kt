package com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.common.MTLogger
import com.bupp.wood_spoon_chef.data.remote.model.AddressRequest
import com.bupp.wood_spoon_chef.data.remote.model.CookRequest
import com.bupp.wood_spoon_chef.data.remote.model.Country
import com.bupp.wood_spoon_chef.data.remote.model.SelectableString
import com.bupp.wood_spoon_chef.fcm.FcmManager
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.managers.MediaUploadManager
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.data.repositories.MetaDataRepository
import com.bupp.wood_spoon_chef.data.repositories.UserRepository
import com.bupp.wood_spoon_chef.utils.GeoCoderUtil
import com.bupp.wood_spoon_chef.utils.GoogleAddressParserUtil
import com.bupp.wood_spoon_chef.utils.LoadDataCallback
import com.bupp.wood_spoon_chef.utils.media_utils.MediaUtils
import com.google.android.libraries.places.api.model.Place
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateAccountViewModel(
    private val userRepository: UserRepository,
    private val deviceDetailsManager: FcmManager,
    private val mediaUploadManager: MediaUploadManager,
    private val metaDataRepository: MetaDataRepository
) : BaseViewModel(), MediaUploadManager.UploadManagerListener {

    //global
    private val currentCookRequest = CookRequest()
    val currentUserLiveData = MutableLiveData<CookRequest>()
    val navigationEvent: MutableLiveData<NavigationEventType> = MutableLiveData()

    //address
    val addressFoundEvent = MutableLiveData<AddressRequest>()

    enum class NavigationEventType {
        OPEN_ADDRESS_AUTO_COMPLETE,
        DETAILS_TO_ADDRESS,
        ADDRESS_TO_PROFILE,
        HELP_TICKET_SEND,
        PROFILE_TO_VIDEO,
        START_IMAGE_CAPTURE,
        START_COVER_CAPTURE,
        START_VIDEO_CAPTURE,
        FINISH
    }

    //setup details
    fun onCameraUtilResult(result: MediaUtils.MediaUtilResult) {
        result.fileUri?.let {
            this.currentCookRequest.tempThumbnail = result.fileUri
            currentUserLiveData.postValue(this.currentCookRequest)
        }
    }

    //setup details
    fun onCoverResult(result: MediaUtils.MediaUtilResult) {
        result.fileUri?.let {
            this.currentCookRequest.tempCover = result.fileUri
            currentUserLiveData.postValue(this.currentCookRequest)
        }
    }

    fun saveDetails(
        firstName: String,
        lastName: String,
        email: String,
        birthdate: String?,
        ssn: String
    ) {
        this.currentCookRequest.firstName = firstName
        this.currentCookRequest.lastName = lastName
        this.currentCookRequest.email = email
        this.currentCookRequest.birthdate = birthdate
        this.currentCookRequest.ssn = ssn

        currentUserLiveData.postValue(this.currentCookRequest)
        navigationEvent.postValue(NavigationEventType.DETAILS_TO_ADDRESS)
    }

    //setup address
    fun onSearchAddressAutoCompleteClick() {
        navigationEvent.postValue(NavigationEventType.OPEN_ADDRESS_AUTO_COMPLETE)
    }

    fun updateAutoCompleteAddressFound(context: Context, place: Place) {
        //called when user select address via auto complete
        val address = GoogleAddressParserUtil.parsePlaceToAddressRequest(place)
        if (validateAddressParams(address)) {
            currentCookRequest.pickupAddress = address
            addressFoundEvent.postValue(address)
        } else {
            geoCodeAddress(context, address)
        }
        currentCookRequest.pickupAddress = address
        addressFoundEvent.postValue(address)
    }

    private fun geoCodeAddress(context: Context, address: AddressRequest) {
        address.lat?.let {
            address.lng?.let {
                GeoCoderUtil.execute(context, address, object :
                    LoadDataCallback<AddressRequest> {
                    override fun onDataLoaded(response: AddressRequest) {
                        if (validateAddressParams(response)) {
                            currentCookRequest.pickupAddress = response
                            addressFoundEvent.postValue(response)
                        } else {
                            val dummyAddress = GeoCoderUtil.parseDummyAddress(address)
                            currentCookRequest.pickupAddress = dummyAddress
                            addressFoundEvent.postValue(dummyAddress)
                        }
                    }

                    override fun onDataNotAvailable(errorCode: Int, reasonMsg: String) {
                        val dummyAddress = GeoCoderUtil.parseDummyAddress(address)
                        currentCookRequest.pickupAddress = dummyAddress
                        addressFoundEvent.postValue(dummyAddress)
                    }
                })
            }
        }
    }

    private fun validateAddressParams(address: AddressRequest): Boolean {
        return address.streetLine1 != null && address.streetNumber != null
    }

    fun saveAddress(apt: String, note: String?) {
        this.currentCookRequest.pickupAddress?.streetLine2 = apt
        this.currentCookRequest.pickupAddress?.notes = note

        currentUserLiveData.postValue(this.currentCookRequest)
        navigationEvent.postValue(NavigationEventType.ADDRESS_TO_PROFILE)
    }

    fun saveProfile(restaurantName: String, about: String) {
        this.currentCookRequest.about = about
        this.currentCookRequest.restaurantName = restaurantName

        currentUserLiveData.postValue(this.currentCookRequest)
        navigationEvent.postValue(NavigationEventType.PROFILE_TO_VIDEO)
    }

    fun onVideoCaptureDone(videoUri: Uri) {
        this.currentCookRequest.tempVideoUri = videoUri
        currentUserLiveData.postValue(this.currentCookRequest)
    }

    fun saveCooksDataAndFinish() {
        this.currentCookRequest.let { it ->
            progressData.startProgress()

            val mediaUploadRequests: MutableList<MediaUploadManager.MediaUploadRequest> =
                mutableListOf()
            it.tempThumbnail?.let { media ->
                mediaUploadRequests.add(
                    MediaUploadManager.MediaUploadRequest(
                        Constants.MEDIA_TYPE_COOK_IMAGE,
                        media
                    )
                )
            }
            it.tempCover?.let { media ->
                mediaUploadRequests.add(
                    MediaUploadManager.MediaUploadRequest(
                        Constants.MEDIA_TYPE_COVER_IMAGE,
                        media
                    )
                )
            }
            it.tempVideoUri?.let { media ->
                mediaUploadRequests.add(
                    MediaUploadManager.MediaUploadRequest(
                        Constants.MEDIA_TYPE_COOK_STORY,
                        media
                    )
                )
            }
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    mediaUploadManager.uploadMedia(mediaUploadRequests, this@CreateAccountViewModel)
                } catch (ex: Exception) {
                    MTLogger.e(ex.localizedMessage ?: "")
                } finally {
//                    progressData.endProgress()
                }
            }
        }
    }

    override fun onMediaUploadCompleted(mediaUploadResult: List<MediaUploadManager.MediaUploadResult>) {
        mediaUploadResult.forEach {
            val result = it
            when (result.uploadType) {
                Constants.MEDIA_TYPE_COOK_IMAGE -> {
                    this.currentCookRequest.thumbnail = result.preSignedUrlKey
                }
                Constants.MEDIA_TYPE_COVER_IMAGE -> {
                    this.currentCookRequest.restaurantCover = result.preSignedUrlKey
                }
                Constants.MEDIA_TYPE_COOK_STORY -> {
                    this.currentCookRequest.video = result.preSignedUrlKey
                }
            }
        }
        postClient()
    }

    private fun postClient() {
        this.currentCookRequest.let {
            viewModelScope.launch {
                when (val response = userRepository.updateCook(it)) {
                    is ResponseSuccess -> {
                        deviceDetailsManager.refreshPushNotificationToken()
                        navigationEvent.postValue(NavigationEventType.FINISH)
                    }
                    is ResponseError -> {
                        errorEvent.postRawValue(response.error)
                    }
                }
                progressData.endProgress()
            }
        }
    }

    fun hasPicture(): Boolean {
        return currentCookRequest.tempThumbnail != null
    }

    fun hasCover(): Boolean {
        return currentCookRequest.tempCover != null
    }

    fun sendHelpMessage(firstName: String, lastName: String, email: String, message: String) {
        viewModelScope.launch {
            progressData.startProgress()
            when (val response = userRepository.postTicket(firstName + lastName, email, message)) {
                is ResponseSuccess -> {
                    navigationEvent.postValue(NavigationEventType.HELP_TICKET_SEND)
                }
                is ResponseError -> {
                    errorEvent.postRawValue(response.error)
                }
            }
            progressData.endProgress()
        }
    }

    fun getCountries(): List<Country> {
        return metaDataRepository.getCountries().filterIsInstance<Country>()
    }

    fun onCountrySelected(chosenItem: SelectableString) {
        this.currentCookRequest.countryId = chosenItem.id
    }

    fun onChangePhotoClick() {
        MTLogger.d("chef uploading thumbnail")
        navigationEvent.postValue(NavigationEventType.START_IMAGE_CAPTURE)
    }

    fun onChangeCoverClick() {
        MTLogger.d("chef uploading cover")
        navigationEvent.postValue(NavigationEventType.START_COVER_CAPTURE)
    }

    fun onChangeVideoClick() {
        MTLogger.d("chef uploading video")
        navigationEvent.postValue(NavigationEventType.START_VIDEO_CAPTURE)
    }

    companion object {
        const val TAG = "wowCreateAccountVM"
    }

}