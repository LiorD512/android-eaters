package com.bupp.wood_spoon_eaters.features.main.edit_my_profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.model.EaterRequest
import com.bupp.wood_spoon_eaters.model.PreSignedUrl
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings
import com.bupp.wood_spoon_eaters.utils.Constants
import com.bupp.wood_spoon_eaters.utils.PutActionManager
import com.bupp.wood_spoon_eaters.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditMyProfileViewModel(val apiService: ApiService, val appSettings: AppSettings, val putActionManager:PutActionManager) : ViewModel(),
    PutActionManager.PutActionListener {


    private val TAG: String? = "wowEditProfileVM"
    val navigationEvent: SingleLiveEvent<NavigationEvent> = SingleLiveEvent()

    data class NavigationEvent(val isSuccess: Boolean = false)

    val userDetails: SingleLiveEvent<Eater> = SingleLiveEvent()

    var eater: EaterRequest = EaterRequest()


    fun getEaterProfile() {
        apiService.getMeCall().enqueue(object : Callback<ServerResponse<Eater>> {
            override fun onResponse(call: Call<ServerResponse<Eater>>, response: Response<ServerResponse<Eater>>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "on success! ")
                    var eater = response.body()?.data!!
                    appSettings.currentEater = eater
                    userDetails.postValue(eater)
                } else {
                    Log.d(TAG, "on Failure! ")
                }
            }

            override fun onFailure(call: Call<ServerResponse<Eater>>, t: Throwable) {
                Log.d(TAG, "on big Failure! " + t.message)
            }
        })
    }

    fun saveEater(fullName: String, email: String, uploadedPhoto: Boolean) {

        var firstAndLast: Pair<String, String> = Utils.getFirstAndLastNames(fullName)
        var firstName = firstAndLast.first
        var lastName = firstAndLast.second


        eater = eater.copy(
            firstName = firstName,
            lastName = lastName,
            email = email
        )

        if(uploadedPhoto){
            startPictureUploading()
        }else{
            postMe()
        }

    }

    private fun postMe() {
        apiService.postMe(eater).enqueue(object : Callback<ServerResponse<Eater>> {
            override fun onResponse(call: Call<ServerResponse<Eater>>, response: Response<ServerResponse<Eater>>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "on success! ")
                    var eater = response.body()?.data!!
                    appSettings.currentEater = eater
                    navigationEvent.postValue(NavigationEvent(true))
                } else {
                    Log.d(TAG, "on Failure! ")
                    navigationEvent.postValue(NavigationEvent(false))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Eater>>, t: Throwable) {
                Log.d(TAG, "on big Failure! " + t.message)
                navigationEvent.postValue(NavigationEvent(false))
            }
        })
    }

    private fun startPictureUploading(){
        apiService.postEaterPreSignedUrl(Constants.PRESIGNED_URL_THUMBNAIL).enqueue(object : Callback<ServerResponse<PreSignedUrl>> {
            override fun onResponse(call: Call<ServerResponse<PreSignedUrl>>, response: Response<ServerResponse<PreSignedUrl>>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "startThumbnailUploading success: ")
                    val preSignedUrl = response.body()?.data
                    eater?.thumbnail = preSignedUrl!!.key
                    uploadMedia(preSignedUrl.url, Constants.PUT_ACTION_COOK_THUMBNAIL)
                } else {
                    Log.d(TAG, "startThumbnailUploading fail")
                }
            }

            override fun onFailure(call: Call<ServerResponse<PreSignedUrl>>, t: Throwable) {
                Log.d(TAG, "startThumbnailUploading big fail")
            }
        })
    }



    private fun uploadMedia(preSingedUrl: String, type: Int) {
        Log.d(TAG, "uploadMedia - start put action: $type")
        var isLast: Boolean = true
        var uri: Uri? = null
        when (type) {
            Constants.PUT_ACTION_COOK_THUMBNAIL -> {
                uri = eater?.tempThumbnail
            }
            Constants.PUT_ACTION_COOK_VIDEO -> {
            }

        }

        putActionManager.initPutAction(
            type,
            preSingedUrl,
            uri!!,
            isLast,
            this
        )
    }

    override fun onPutDone(type: Int, preSignedUrl: Uri, isLast: Boolean) {
        Log.d(TAG, "onPutDone success: $preSignedUrl")
        if (isLast) {
            postMe()
        }
    }

    fun updateTempThumbnail(resultUri: Uri) {
        eater.tempThumbnail = resultUri
    }


}
