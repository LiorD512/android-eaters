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

class MyProfileViewModel(
    val api: ApiService,
    private val userRepository: UserRepository,
    val eaterDataManager: EaterDataManager,
    val metaDataRepository: MetaDataRepository,
    private val paymentManager: PaymentManager
) :
    ViewModel(), EphemeralKeyProvider.EphemeralKeyProviderListener {

    val progressData = ProgressData()
    val errorEvents: MutableLiveData<ErrorEventType> = MutableLiveData()

    val paymentLiveData = paymentManager.getPaymentsLiveData()
    val favoritesLiveData = eaterDataManager.getFavoritesLiveData()
    val profileData: SingleLiveEvent<ProfileData> = SingleLiveEvent()

    val myProfileActionEvent = MutableLiveData<MyProfileActionEvent>()

    data class MyProfileActionEvent(val type: MyProfileActionType)
    enum class MyProfileActionType {
        LOGOUT
    }

    init {
        fetchProfileData()
        refreshFavorites()
    }

    data class ProfileData(val eater: Eater?, val dietary: List<SelectableIcon>, val bannerUrl: String?)

    fun fetchProfileData() {
        val eater = getUserDetails()
        val dietaries = metaDataRepository.getDietaryList()
        val bannerUrl = metaDataRepository.getProfileBannerUrl()
        profileData.postValue(ProfileData(eater, dietaries, bannerUrl))
    }

    private fun refreshFavorites() {
        viewModelScope.launch {
            eaterDataManager.refreshMyFavorites()
        }
    }

    //    val getUserDetails: SingleLiveEvent<Eater> = SingleLiveEvent()
    fun getUserDetails(): Eater? {
        if (userRepository.getUser() == null) {
            viewModelScope.launch {
                userRepository.initUserRepo()
            }
        }
        return userRepository.getUser()
    }

    fun updateClientAccount(cuisineIcons: List<SelectableIcon>? = null, dietaryIcons: List<SelectableIcon>? = null, forceUpdate: Boolean = false) {
        progressData.startProgress()
        val eater = EaterRequest()

        var arrayOfCuisinesIds: MutableList<Int>? = null
        var arrayOfDietsIds: MutableList<Int>? = null

        cuisineIcons?.let {
            arrayOfCuisinesIds = mutableListOf()
            for (cuisine in cuisineIcons) {
                arrayOfCuisinesIds!!.add(cuisine.id.toInt())
            }
        }
        dietaryIcons?.let {
            arrayOfDietsIds = mutableListOf()
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
                    if (forceUpdate) {
                        fetchProfileData()
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

    fun logout() {
        val logoutResult = userRepository.logout()
        if (logoutResult.type == UserRepository.UserRepoStatus.LOGGED_OUT) {
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

//    fun getDietaryList(): List<SelectableIcon> {
//        return metaDataRepository.getDietaryList()
//    }

    companion object {
        const val TAG = "wowMyProfileVM"
    }


}

