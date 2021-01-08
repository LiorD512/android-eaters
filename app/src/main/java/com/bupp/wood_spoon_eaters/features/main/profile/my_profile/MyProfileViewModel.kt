package com.bupp.wood_spoon_eaters.features.main.profile.my_profile

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.features.new_order.service.EphemeralKeyProvider
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import com.stripe.android.model.PaymentMethod
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class MyProfileViewModel(val api: ApiService, val userRepository: UserRepository, val eaterDataManager: EaterDataManager, val metaDataRepository: MetaDataRepository
                         , val paymentManager: PaymentManager) :
    ViewModel(), EphemeralKeyProvider.EphemeralKeyProviderListener {


    val myProfileActionEvent = MutableLiveData<MyProfileActionEvent>()
    data class MyProfileActionEvent(val type: MyProfileActionType)
    enum class MyProfileActionType {
        LOGOUT
    }

    val TAG = "wowMyProfileVM"
    data class GetUserDetails(val isSuccess: Boolean, val eater: Eater? = null)
    val getUserDetails: SingleLiveEvent<GetUserDetails> = SingleLiveEvent()

    fun getUserDetails() {
        api.getMeCall().enqueue(object : Callback<ServerResponse<Eater>> {
            override fun onResponse(call: Call<ServerResponse<Eater>>, response: Response<ServerResponse<Eater>>) {
                if (response.isSuccessful) {
                    val eater = response.body()?.data
                    if (eater != null) {
                        getUserDetails.postValue(GetUserDetails(true, eater))
                    } else {
                        getUserDetails.postValue(GetUserDetails(false))
                    }
                } else {
                    getUserDetails.postValue(GetUserDetails(false))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Eater>>, t: Throwable) {
                getUserDetails.postValue(GetUserDetails(false))
            }

        })
    }

    fun updateClientAccount(cuisineIcons: ArrayList<SelectableIcon>? = null, dietaryIcons: ArrayList<SelectableIcon>? = null) {

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
        postClient(eater)
    }

    private fun postClient(eater: EaterRequest) {
        //todo - nyc change
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
    }

    fun getDeliveryAddress(): String {
        val streetLine1 = eaterDataManager.getLastChosenAddress()?.streetLine1
        return if (streetLine1.isNullOrEmpty()) {
            ""
        } else {
            streetLine1
        }
    }

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

    val getStripeCustomerCards: SingleLiveEvent<StripeCustomerCardsEvent> = SingleLiveEvent()
    data class StripeCustomerCardsEvent(val isSuccess: Boolean, val paymentMethods: List<PaymentMethod>? = null)
    fun getStripeCustomerCards(context: Context){
        val paymentMethod = paymentManager.getStripeCustomerCards(context)
        getStripeCustomerCards.postValue(StripeCustomerCardsEvent(true, paymentMethod.value))
    }

    fun updateUserCustomerCard(paymentMethod: PaymentMethod) {
        eaterDataManager.updateCustomerCard(paymentMethod)
    }

    fun getShareText(): String {
        val inviteUrl = eaterDataManager.currentEater?.shareCampaign?.inviteUrl
        val text = eaterDataManager.currentEater?.shareCampaign?.shareText
        return "$text \n $inviteUrl"
    }

    fun getCuisineList(): ArrayList<SelectableIcon> {
        return metaDataRepository.getCuisineListSelectableIcons()
    }

    fun getDietaryList(): ArrayList<SelectableIcon> {
        return metaDataRepository.getDietaryList()
    }



}

