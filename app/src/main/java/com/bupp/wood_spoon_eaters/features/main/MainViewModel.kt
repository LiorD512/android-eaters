package com.bupp.wood_spoon_eaters.features.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.bupp.wood_spoon_eaters.common.*
import com.bupp.wood_spoon_eaters.model.RestaurantInitParams
import com.bupp.wood_spoon_eaters.managers.*
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.repositories.RestaurantRepository
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import com.stripe.android.model.PaymentMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val metaDataRepository: MetaDataRepository,
    val eaterDataManager: EaterDataManager, private val campaignManager: CampaignManager, private val paymentManager: PaymentManager,
    private val userRepository: UserRepository, globalErrorManager: GlobalErrorManager, private var eventsManager: EventsManager,
    private val flowEventsManager: FlowEventsManager, private val cartManager: CartManager, private val restaurantRepository: RestaurantRepository,
) : ViewModel() {

    init {
        eaterDataManager.refreshSegment()
    }

    fun logPageEvent(eventType: FlowEventsManager.FlowEvents) {
        flowEventsManager.logPageEvent(eventType)
    }

    val mainNavigationEvent = MutableLiveData<MainNavigationEvent>()

    enum class MainNavigationEvent {
        START_LOCATION_AND_ADDRESS_ACTIVITY,
        START_PAYMENT_METHOD_ACTIVITY,
        INITIALIZE_STRIPE,
        LOGOUT,
        OPEN_CAMERA_UTIL_IMAGE
    }


    fun handleMainNavigation(type: MainNavigationEvent) {
        mainNavigationEvent.postValue(type)
    }


    fun startLocationAndAddressAct() {
        mainNavigationEvent.postValue(MainNavigationEvent.START_LOCATION_AND_ADDRESS_ACTIVITY)
    }

    val floatingCartBtnEvent = cartManager.getFloatingCartBtnEvent()

    val globalErrorLiveData = globalErrorManager.getGlobalErrorLiveData()
    val campaignLiveData = campaignManager.getCampaignLiveData()

    val startRestaurantActivity = MutableLiveData<RestaurantInitParams>()
    val forceFeedRefresh = MutableLiveData<Boolean>()
    val scrollFeedToTop = MutableLiveData<Boolean>()

    fun getFinalAddressParams() = eaterDataManager.getFinalAddressLiveDataParam()

    //stripe
    val stripeInitializationEvent = paymentManager.getStripeInitializationEvent()
    fun startStripeOrReInit() {
        MTLogger.c(TAG, "startStripeOrReInit")
        if (paymentManager.hasStripeInitialized) {
            Log.d(TAG, "start payment method")
            mainNavigationEvent.postValue(MainNavigationEvent.START_PAYMENT_METHOD_ACTIVITY)
        } else {
            MTLogger.c(TAG, "re init stripe")
            mainNavigationEvent.postValue(MainNavigationEvent.INITIALIZE_STRIPE)
        }
    }

    fun reInitStripe(context: Context) {
        viewModelScope.launch {
            paymentManager.initPaymentManagerWithListener(context)
        }
    }

    private val TAG = "wowMainVM"


    val getTraceableOrder = eaterDataManager.getTraceableOrders()

    fun checkForActiveOrder() {
        viewModelScope.launch {
            eaterDataManager.checkForTraceableOrders()
        }
    }

    fun refreshActiveCampaigns() {
        viewModelScope.launch {
            campaignManager.onFlowEventFired(FlowEventsManager.FlowEvents.VISIT_FEED)
        }
    }

    val getTriggers = eaterDataManager.getTriggers()

    fun getRestaurant(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = restaurantRepository.getRestaurant(id)
            if (result.type == RestaurantRepository.RestaurantRepoStatus.SUCCESS) {
                result.restaurant?.let { restaurant ->
                    val param = RestaurantInitParams(
                        restaurantId = restaurant.id,
                        chefThumbnail = restaurant.thumbnail,
                        coverPhoto = restaurant.cover,
                        rating = restaurant.getAvgRating(),
                        restaurantName = restaurant.restaurantName,
                        chefName = restaurant.getFullName(),
                        isFavorite = restaurant.isFavorite ?: false
                    )
                    startRestaurantActivity(param)
                }
            }
        }
    }

    fun getContactUsPhoneNumber(): String {
        return metaDataRepository.getContactUsPhoneNumber()
    }

    fun getContactUsTextNumber(): String {
        return metaDataRepository.getContactUsTextNumber()
    }


    fun onUserImageClick() {
        mainNavigationEvent.postValue(MainNavigationEvent.OPEN_CAMERA_UTIL_IMAGE)
    }

    fun updateCampaignStatus(campaign: Campaign, status: UserInteractionStatus) {
        viewModelScope.launch {
            campaign.userInteractionId?.let {
                campaignManager.updateCampaignStatus(it, status)
            }
        }
    }

    fun updatePaymentMethod(context: Context, paymentMethod: PaymentMethod?) {
        paymentManager.updateSelectedPaymentMethod(context, paymentMethod)
    }

    val shareEvent = MutableLiveData<String>()
    fun onShareCampaignClick(campaign: Campaign?) {
        val shareUrl = campaign?.shareUrl
        val shareText = campaign?.shareText ?: ""
        shareEvent.postValue("$shareText \n $shareUrl")
        eventsManager.logEvent(Constants.EVENT_CAMPAIGN_INVITE)
    }

    fun deleteAccount() {
        viewModelScope.launch {
            val result = userRepository.deleteAccount()
            if(result.type == UserRepository.UserRepoStatus.SUCCESS){
                logout()
            }
        }
    }

    fun logout() {
        val logoutResult = userRepository.logout()
        if (logoutResult.type == UserRepository.UserRepoStatus.LOGGED_OUT) {
            cartManager.onCartCleared()
            mainNavigationEvent.postValue(MainNavigationEvent.LOGOUT)
        }
    }

    val mediaUtilsResultLiveData = MutableLiveData<MediaUtils.MediaUtilResult>()
    fun onMediaUtilsResultSuccess(result: MediaUtils.MediaUtilResult) {
        //use this liveData when using MediaUtils out side of MainActivity scope (for example - EditProfileBottomSheet)
        mediaUtilsResultLiveData.postValue(result)
    }

    val onFloatingBtnHeightChange = MutableLiveData<Boolean>()
    fun onFloatingCartStateChanged(isShowing: Boolean) {
        onFloatingBtnHeightChange.postValue(isShowing)
    }

    /**
     * Starts Restaurant Activity with the initial params
     * we start it from here, beacuse we need to update stuff when order is successfully done.
     */
    fun startRestaurantActivity(restaurantInitParams: RestaurantInitParams) {
        eventsManager.logEvent(Constants.EVENT_CLICK_RESTAURANT, getRestaurantClicked(restaurantInitParams))
        startRestaurantActivity.postValue(restaurantInitParams)
    }

    private fun getRestaurantClicked(restaurantInitParams: RestaurantInitParams): Map<String, String> {
        val data = mutableMapOf<String, String>()
        data["home_chef_id"] = restaurantInitParams.restaurantId.toString()
        data["home_chef_name"] = restaurantInitParams.chefName.toString()
        data["home_chef_rating"] = restaurantInitParams.rating.toString()
        data["section_title"] = restaurantInitParams.sectionTitle.toString()
        data["section_index"] = restaurantInitParams.sectionOrder.toString()
        data["home_chef_index"] = restaurantInitParams.restaurantOrderInSection.toString()
        data["dish_tapped_index"] = restaurantInitParams.dishIndexInRestaurant.toString()
        return data
    }

    fun forceFeedRefresh() {
        forceFeedRefresh.postValue(true)
    }

    fun scrollFeedToTop() {
        scrollFeedToTop.postValue(true)
    }

    fun logEvent(eventName: String) {
        eventsManager.logEvent(eventName)
    }

    fun logDeepLinkEvent(restaurantId: Long) {
        eventsManager.logEvent(Constants.EVENT_OPEN_DEEP_LINK, mapOf(Pair("home_chef_id", restaurantId)))
    }

}