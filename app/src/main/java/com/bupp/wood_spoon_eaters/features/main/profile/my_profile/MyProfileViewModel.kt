package com.bupp.wood_spoon_eaters.features.main.profile.my_profile

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.features.new_order.service.EphemeralKeyProvider
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import com.stripe.android.model.PaymentMethod
import kotlinx.coroutines.launch
import java.util.ArrayList

class MyProfileViewModel(val api: ApiService, private val userRepository: UserRepository, val eaterDataManager: EaterDataManager, val metaDataRepository: MetaDataRepository
                         , private val paymentManager: PaymentManager) :
    ViewModel(), EphemeralKeyProvider.EphemeralKeyProviderListener {

    val progressData = ProgressData()
    val errorEvents: MutableLiveData<ErrorEventType> = MutableLiveData()

    val paymentLiveData = paymentManager.getPaymentsLiveData()
    val favoritesLiveData = eaterDataManager.getFavoritesLiveData()

    val myProfileActionEvent = MutableLiveData<MyProfileActionEvent>()
    data class MyProfileActionEvent(val type: MyProfileActionType)
    enum class MyProfileActionType {
        LOGOUT
    }

    init{
        refreshFavorites()
    }

    private fun refreshFavorites() {
        viewModelScope.launch {
            eaterDataManager.refreshMyFavorites()
        }
    }

    val TAG = "wowMyProfileVM"
    data class GetUserDetails(val isSuccess: Boolean, val eater: Eater? = null)
    val getUserDetails: SingleLiveEvent<Eater> = SingleLiveEvent()

    fun getUserDetails() {
        if(userRepository.getUser() != null){
            getUserDetails.postValue(userRepository.getUser())
        }else{
            viewModelScope.launch {
                userRepository.initUserRepo()
                getUserDetails.postValue(userRepository.getUser())
            }
        }
//        api.getMeCall().enqueue(object : Callback<ServerResponse<Eater>> {
//            override fun onResponse(call: Call<ServerResponse<Eater>>, response: Response<ServerResponse<Eater>>) {
//                if (response.isSuccessful) {
//                    val eater = response.body()?.data
//                    if (eater != null) {
//                        getUserDetails.postValue(GetUserDetails(true, eater))
//                    } else {
//                        getUserDetails.postValue(GetUserDetails(false))
//                    }
//                } else {
//                    getUserDetails.postValue(GetUserDetails(false))
//                }
//            }
//
//            override fun onFailure(call: Call<ServerResponse<Eater>>, t: Throwable) {
//                getUserDetails.postValue(GetUserDetails(false))
//            }
//
//        })
    }

    fun updateClientAccount(cuisineIcons: List<SelectableIcon>? = null, dietaryIcons: List<SelectableIcon>? = null, forceUpdate: Boolean = false) {
        progressData.startProgress()
        val eater = EaterRequest()

        var arrayOfCuisinesIds: ArrayList<Int>? = null
        var arrayOfDietsIds: ArrayList<Int>? = null

        cuisineIcons?.let{
            arrayOfCuisinesIds = arrayListOf()
            for (cuisine in cuisineIcons) {
                arrayOfCuisinesIds!!.add(cuisine.id.toInt())
            }
        }
        dietaryIcons?.let{
            arrayOfDietsIds = arrayListOf()
            for (diet in dietaryIcons) {
                arrayOfDietsIds!!.add(diet.id.toInt())
            }
        }

        eater.cuisineIds = arrayOfCuisinesIds
        eater.dietIds = arrayOfDietsIds
        postClient(eater, forceUpdate)
    }

    private fun postClient(eater: EaterRequest, forceUpdate: Boolean) {
        viewModelScope.launch {
                val userRepoResult = userRepository.updateEater(eater)
                when (userRepoResult.type) {
                    UserRepository.UserRepoStatus.SERVER_ERROR -> {
                        Log.d("wowLoginVM", "NetworkError")
                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                    }
                    UserRepository.UserRepoStatus.SOMETHING_WENT_WRONG -> {
                        Log.d("wowLoginVM", "GenericError")
                        errorEvents.postValue(ErrorEventType.SOMETHING_WENT_WRONG)
                    }
                    UserRepository.UserRepoStatus.SUCCESS -> {
                        Log.d("wowLoginVM", "Success")
                        if(forceUpdate){
                            getUserDetails()
                        }
                    }
                    else -> {
                        Log.d("wowLoginVM", "NetworkError")
                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                    }
                }
                progressData.endProgress()
            }
        }
//        api.postMe(eater).enqueue(object : Callback<ServerResponse<Eater>> {
//            override fun onResponse(call: Call<ServerResponse<Eater>>, response: Response<ServerResponse<Eater>>) {
//                if (response.isSuccessful) {
//                    Log.d("wowCreateAccountVM", "on success! ")
//                    eaterDataManager.currentEater = response.body()?.data!!
//                } else {
//                    Log.d("wowCreateAccountVM", "on Failure! ")
//                }
//
//            }
//
//            override fun onFailure(call: Call<ServerResponse<Eater>>, t: Throwable) {
//                Log.d("wowCreateAccountVM", "on big Failure! " + t.message)
//            }
//        })


//    fun getDeliveryAddress(): String {//todo - nyc
//        val streetLine1 = eaterDataManager.getLastChosenAddress()?.streetLine1
//        return if (streetLine1.isNullOrEmpty()) {
//            ""
//        } else {
//            streetLine1
//        }
//    }

    fun logout() {
        val logoutResult = userRepository.logout()
        if(logoutResult.type == UserRepository.UserRepoStatus.LOGGED_OUT){
            myProfileActionEvent.postValue(MyProfileActionEvent(MyProfileActionType.LOGOUT))
        }
    }

    fun initStripe(activity: Activity) {
        //todo - fix this
//        PaymentConfiguration.init(activity, metaDataManager.getStripePublishableKey())
//        CustomerSession.initCustomerSession(activity, EphemeralKeyProvider(this), false)
    }

//    private val getStripeCustomerCards: SingleLiveEvent<StripeCustomerCardsEvent> = SingleLiveEvent()
//    data class StripeCustomerCardsEvent(val isSuccess: Boolean, val paymentMethods: List<PaymentMethod>? = null)
//    fun getStripeCustomerCards(context: Context){
//        val paymentMethod = paymentManager.getStripeCustomerCards(context)
//        getStripeCustomerCards.postValue(StripeCustomerCardsEvent(true, paymentMethod.value))
//    }

    fun updateUserCustomerCard(paymentMethod: PaymentMethod) {
//        eaterDataManager.updateCustomerCard(paymentMethod)//todo - nyyyyy
    }

    fun getShareText(): String {
//        val inviteUrl = eaterDataManager.currentEater?.shareCampaign?.inviteUrl
        val text = eaterDataManager.currentEater?.shareCampaign?.shareText
        return "$text \n"
    }

    fun getCuisineList(): List<SelectableIcon> {
        return metaDataRepository.getCuisineListSelectableIcons()
    }

    fun getDietaryList(): List<SelectableIcon> {
        return metaDataRepository.getDietaryList()
    }



}

