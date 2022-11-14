package com.bupp.wood_spoon_chef.presentation.features.new_dish

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.data.remote.model.*
import com.bupp.wood_spoon_chef.di.abs.LiveEvent
import com.bupp.wood_spoon_chef.di.abs.LiveEventData
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.presentation.features.onboarding.login.LoginViewModel
import com.bupp.wood_spoon_chef.managers.MediaUploadManager
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseError
import com.bupp.wood_spoon_chef.data.remote.network.base.ResponseSuccess
import com.bupp.wood_spoon_chef.data.repositories.DishRepository
import com.bupp.wood_spoon_chef.data.repositories.MetaDataRepository
import com.bupp.wood_spoon_chef.utils.media_utils.MediaUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewDishViewModel(
    private val dishRepo: DishRepository,
    private val metaDataRepository: MetaDataRepository,
    private val mediaUploadManager: MediaUploadManager
) : BaseViewModel(), MediaUploadManager.UploadManagerListener {

    //Media Upload Methods
    val navigationEvent: MutableLiveData<NavigationEventType> = MutableLiveData()

    //    val saveDraftEvent: LiveEventData<Boolean> = LiveEventData()
    val saveDraftEvent: MutableLiveData<LiveEvent<Boolean>> = MutableLiveData<LiveEvent<Boolean>>()

    enum class NavigationEventType {
        NAME_TO_DETAILS,
        DETAILS_TO_INSTRUCTIONS,
        INSTRUCTIONS_TO_DIETARY,
        DIETARY_TO_PRICE,
        PRICE_TO_MEDIA,
        OPEN_CAMERA_UTIL,
        OPEN_VIDEO_CHOOSER_DIALOG,
    }

    private var dishRequest: DishRequest = DishRequest()
    val curDishLiveData = MutableLiveData<DishRequest>()

    data class DishUpdateDialogData(val title: String, val body: String? = null)

    val newDishDoneDialogEvent = MutableLiveData<DishUpdateDialogData>()

    data class DishStatus(val isEdit: Boolean = false, val isDraft: Boolean = false)

    val dishStatusEvent = MutableLiveData<DishStatus>()

    fun saveCurrentDraft() {
        saveDraftEvent.postValue(LiveEvent(true))
    }

    fun saveDishName(name: String, description: String) {
        this.dishRequest.name = name
        this.dishRequest.description = description

        curDishLiveData.postValue(this.dishRequest)
        navigationEvent.postValue(NavigationEventType.NAME_TO_DETAILS)
    }

    fun saveDishDetails(ingredients: String?, prepTimeId: Long?, forcePostDraft: Boolean = false) {
        this.dishRequest.ingredients = ingredients
        this.dishRequest.prepTimeRangeId = prepTimeId

        if (forcePostDraft) {
            startDishPublishing(true)
        } else {
            curDishLiveData.postValue(this.dishRequest)
            navigationEvent.postValue(NavigationEventType.DETAILS_TO_INSTRUCTIONS)
        }
    }

    fun saveDishCategory(dishCategory: DishCategory?) {
        this.dishRequest.dishCategory = dishCategory
        this.dishRequest.dishCategoryId = dishCategory?.id
    }

    fun isCategorySelected(): Boolean {
        return this.dishRequest.dishCategoryId != null
    }

    fun getPrepTimeList(): List<PrepTimeRange> {
        return metaDataRepository.getPrepTimeList()
    }

    fun getPrepTimeByStr(selectedPreTimeStr: String): Long? {
        Log.d("wow", "getPrepTimeByStr - $selectedPreTimeStr")
        return metaDataRepository.getPrepTimeList()
            .find { it.getRangeStr() == selectedPreTimeStr }?.id
    }

    fun getDishCategories(): List<DishCategory> =
        metaDataRepository.getDishCategories()

    fun saveDishInstructions(
        instructions: String?,
        portionSize: String?,
        forcePostDraft: Boolean = false
    ) {
        this.dishRequest.instruction = instructions
        this.dishRequest.portionSize = portionSize

        if (forcePostDraft) {
            startDishPublishing(true)
        } else {
            curDishLiveData.postValue(this.dishRequest)
            navigationEvent.postValue(NavigationEventType.INSTRUCTIONS_TO_DIETARY)
        }
    }

    fun saveDishAccommodations(accommodations: String?, forcePostDraft: Boolean = false) {
        this.dishRequest.accommodations = accommodations

        if (forcePostDraft) {
            startDishPublishing(true)
        } else {
            curDishLiveData.postValue(this.dishRequest)
            navigationEvent.postValue(NavigationEventType.DIETARY_TO_PRICE)
        }
    }

    fun saveDishDietary(dietary: List<Long>) {
        this.dishRequest.dietaryIds = dietary.toMutableList()
    }

    fun setSelectedCuisine(cuisine: CuisineIcon) {
        this.dishRequest.cuisineIds = mutableListOf(cuisine.id)
        curDishLiveData.postValue(this.dishRequest)
    }

    data class CuisineSelectData(
        val cuisineIcons: List<CuisineIcon>,
        val selectedCuisine: CuisineIcon?
    )

    val cuisineSelectData = LiveEventData<CuisineSelectData>()
    fun onSelectCuisineClick() {
        cuisineSelectData.postRawValue(CuisineSelectData(getCuisineIcon(), getSelectedCuisine()))
    }

    private fun getSelectedCuisine(): CuisineIcon? {
        this.dishRequest.cuisineIds?.let {
            if (this.dishRequest.cuisineIds!!.isNotEmpty()) {
                return metaDataRepository.getCuisineList()
                    .find { it.id == this.dishRequest.cuisineIds!![0] }
            }

        }
        return null
    }

    fun saveDishPrice(price: Int, forcePostDraft: Boolean = false) {
        this.dishRequest.price = price

        if (forcePostDraft) {
            startDishPublishing(true)
        } else {
            curDishLiveData.postValue(this.dishRequest)
            navigationEvent.postValue(NavigationEventType.PRICE_TO_MEDIA)
        }
    }

    private var currentProcessingMedia: MediaUploadArgs? = null

    enum class MediaUploadArgs {
        IMG_1,
        IMG_2,
        IMG_3,
        VIDEO,
    }

    fun onAddNewMediaClick(mediaArg: MediaUploadArgs) {
        currentProcessingMedia = mediaArg
        if (mediaArg == MediaUploadArgs.VIDEO) {
            navigationEvent.postValue(NavigationEventType.OPEN_VIDEO_CHOOSER_DIALOG)
        } else {
            navigationEvent.postValue(NavigationEventType.OPEN_CAMERA_UTIL)
        }
    }

    fun onCameraUtilResult(result: MediaUtils.MediaUtilResult) {
        result.fileUri?.let {
            when (currentProcessingMedia) {
                MediaUploadArgs.IMG_1 -> {
                    this.dishRequest.tempThumbnail = it
                    if (!this.dishRequest.imageGallery.isNullOrEmpty()) {
                        this.dishRequest.imageGallery!![0] = null
                    }
                }
                MediaUploadArgs.IMG_2 -> {
                    this.dishRequest.tempImageGallery.addOrUpdate(0, it)
                }
                MediaUploadArgs.IMG_3 -> {
                    this.dishRequest.tempImageGallery.addOrUpdate(1, it)
                }

                else -> {}
            }
            curDishLiveData.postValue(this.dishRequest)
        }
    }

    fun onVideoCaptureDone(videoUri: Uri) {
        this.dishRequest.tempVideo = videoUri
        curDishLiveData.postValue(this.dishRequest)
    }

    fun onVideoDelete() {
        this.dishRequest.removeVideo()
        curDishLiveData.postValue(this.dishRequest)
    }

    fun onMediaDelete(uri: Uri?) {
        uri?.let {
            this.dishRequest.removeMedia(it)
            curDishLiveData.postValue(this.dishRequest)
        }
    }

    fun hasMainPhoto(): Boolean {
        return this.dishRequest.hasMainPhoto()
    }

    private var shouldSaveAsDraft: Boolean = false
    fun startDishPublishing(isDraft: Boolean = false) {
        progressData.startProgress()
        this.shouldSaveAsDraft = isDraft
        viewModelScope.launch {
            if (dishRequest.hasTempMedia()) {
                uploadMediaData()
            } else {
                publishDraftOrActivate()
            }
        }
    }

    private fun publishDraftOrActivate() {
        viewModelScope.launch {
            progressData.startProgress()
            when (this@NewDishViewModel.dishRequest.getDishStatus()) {
                NewDishStatus.NEW_DISH -> {
                    if (shouldSaveAsDraft) {
                        saveToDraft()
                    } else {
                        publishNewDish()
                    }
                }
                NewDishStatus.EDIT_DISH -> {
                    updateDish()
                }
                NewDishStatus.EDIT_DRAFT -> {
                    if (shouldSaveAsDraft) {
                        updateDish()
                    } else {
                        publishDraft()
                    }
                }
                else -> {}
            }
            progressData.endProgress()
        }
    }

    private suspend fun saveToDraft() {
        when (val response = dishRepo.postDishDraft(this@NewDishViewModel.dishRequest)) {
            is ResponseSuccess -> {
                newDishDoneDialogEvent.postValue(getDoneDialogData(DishUpdatedDialogStatus.SAVE_TO_DRAFT))
            }
            is ResponseError -> {
                errorEvent.postRawValue(response.error)
            }
        }
    }

    private suspend fun publishNewDish() {
        when (val response = dishRepo.postDishAndPublish(this@NewDishViewModel.dishRequest)) {
            is ResponseSuccess -> {
                newDishDoneDialogEvent.postValue(getDoneDialogData(DishUpdatedDialogStatus.NEW_DISH_PUBLISH))
            }
            is ResponseError -> {
                errorEvent.postRawValue(response.error)
            }
        }
    }

    private suspend fun updateDish() {
        when (val response = dishRepo.updateDish(this@NewDishViewModel.dishRequest)) {
            is ResponseSuccess -> {
                newDishDoneDialogEvent.postValue(getDoneDialogData(DishUpdatedDialogStatus.UPDATE_CHANGES))
            }
            is ResponseError -> {
                errorEvent.postRawValue(response.error)
            }
        }
    }

    private suspend fun publishDraft() {
        when (val response = dishRepo.updateDishAndPublish(this@NewDishViewModel.dishRequest)) {
            is ResponseSuccess -> {
                newDishDoneDialogEvent.postValue(getDoneDialogData(DishUpdatedDialogStatus.DRAFT_PUBLISH))
            }
            is ResponseError -> {
                errorEvent.postRawValue(response.error)
            }
        }
    }


    private fun uploadMediaData() {
        this.dishRequest.let { it ->
            val mediaUploadRequests: MutableList<MediaUploadManager.MediaUploadRequest> =
                mutableListOf()
            it.tempThumbnail?.let {
                mediaUploadRequests.add(
                    MediaUploadManager.MediaUploadRequest(
                        Constants.MEDIA_TYPE_DISH_MAIN_IMAGE,
                        it
                    )
                )
            }
            it.tempImageGallery.forEach { it ->
                it?.let {
                    mediaUploadRequests.add(
                        MediaUploadManager.MediaUploadRequest(
                            Constants.MEDIA_TYPE_DISH_IMAGE,
                            it
                        )
                    )
                }
            }
            it.tempVideo?.let { media ->
                mediaUploadRequests.add(
                    MediaUploadManager.MediaUploadRequest(
                        Constants.MEDIA_TYPE_DISH_VIDEO,
                        media
                    )
                )
            }
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    mediaUploadManager.uploadMedia(mediaUploadRequests, this@NewDishViewModel)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

    override fun onMediaUploadCompleted(mediaUploadResult: List<MediaUploadManager.MediaUploadResult>) {
        mediaUploadResult.forEach {
            val result = it
            when (result.uploadType) {
                Constants.MEDIA_TYPE_DISH_MAIN_IMAGE -> {
                    Log.d(TAG, "onMediaUploadCompleted - MEDIA_TYPE_DISH_MAIN_IMAGE")
                    this.dishRequest.addOrUpdateMainPhoto(result.preSignedUrlKey)
                }
                Constants.MEDIA_TYPE_DISH_IMAGE -> {
                    Log.d(TAG, "onMediaUploadCompleted - MEDIA_TYPE_DISH_IMAGE")
                    if (this.dishRequest.imageGallery == null) {
                        this.dishRequest.imageGallery = mutableListOf()
                    }
                    this.dishRequest.imageGallery!!.add(result.preSignedUrlKey)
                }
                Constants.MEDIA_TYPE_DISH_VIDEO -> {
                    Log.d(TAG, "onMediaUploadCompleted - MEDIA_TYPE_DISH_VIDEO")
                    this.dishRequest.video = result.preSignedUrlKey
                }
            }
        }
        publishDraftOrActivate()
    }

    private fun getDoneDialogData(status: DishUpdatedDialogStatus): DishUpdateDialogData {
        return when (status) {
            DishUpdatedDialogStatus.SAVE_TO_DRAFT -> {
                DishUpdateDialogData(
                    title = "Saved as draft!",
                    body = "You must finalize and publish this dish before adding it to your menu"
                )
            }
            DishUpdatedDialogStatus.DRAFT_PUBLISH -> {
                DishUpdateDialogData(title = "Dish published successfully")
            }
            DishUpdatedDialogStatus.NEW_DISH_PUBLISH -> {
                DishUpdateDialogData(title = "Dish created and published successfully")
            }
            else -> { //DishUpdatedDialogStatus.UPDATE_CHANGES
                DishUpdateDialogData(title = "Changes saved successfully")
            }

        }
    }


    private fun <E> MutableList<E>.addOrUpdate(index: Int, item: E) {
        if (this.isNotEmpty() && this.size > index) {
            this[index]?.let {
                this.removeAt(index)
                this.add(index, item)
            }
        } else {
            this.add(item)
        }
    }

    //data fragments
    fun getDietaryIcons(): List<SelectableIcon> {
        return metaDataRepository.getDietaryList()
    }

    private fun getCuisineIcon(): List<CuisineIcon> {
        return metaDataRepository.getCuisineList()
    }

    fun getSelectedCuisineName(selectedCuisineId: Long?): String {
        selectedCuisineId?.let {
            for (item in getCuisineIcon()) {
                if (item.id == selectedCuisineId) {
                    return item.name
                }
            }
        }
        return ""
    }

    fun getDefaultServiceFee(): Float {
        return metaDataRepository.getBuppServiceFeePercentage()
    }

    fun checkDishStatus(intent: Intent?) {
        val editDishId = intent?.getLongExtra(NewDishActivity.EDIT_DISH_PARAM, -1)
        if (editDishId != null && editDishId > -1) {
            Log.d(TAG, "new Dish in edit mode")
            progressData.startProgress()
            viewModelScope.launch {
                when (val response = dishRepo.getDishById(editDishId)) {
                    is ResponseSuccess -> {
                        val dish = response.data
                        dish?.let {
                            this@NewDishViewModel.dishRequest = it.toDishRequest()
                            curDishLiveData.postValue(this@NewDishViewModel.dishRequest)
                            dishStatusEvent.postValue(
                                DishStatus(
                                    isEdit = true,
                                    isDraft = it.isDraft()
                                )
                            )
                            this@NewDishViewModel.shouldSaveAsDraft = it.isDraft()
                        }
                    }
                    is ResponseError -> {
                        errorEvent.postRawValue(response.error)
                    }
                }
                progressData.endProgress()
            }
        } else {
            dishStatusEvent.postValue(DishStatus(isEdit = false))
        }
    }

    companion object {
        const val TAG = "wowNewDishVM"
    }

}

