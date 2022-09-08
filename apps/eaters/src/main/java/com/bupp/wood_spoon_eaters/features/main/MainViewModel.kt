package com.bupp.wood_spoon_eaters.features.main

import android.content.Context
import androidx.lifecycle.*
import com.bupp.wood_spoon_eaters.common.*
import com.bupp.wood_spoon_eaters.model.RestaurantInitParams
import com.bupp.wood_spoon_eaters.managers.*
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.repositories.*
import com.stripe.android.model.PaymentMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


enum class MainNavigationEvent {
    START_LOCATION_AND_ADDRESS_ACTIVITY,
    START_PAYMENT_METHOD_ACTIVITY,
    INITIALIZE_STRIPE,
    LOGOUT_WITH_NEW_AUTH,
    LOGOUT,
    OPEN_CAMERA_UTIL_IMAGE
}

class MainViewModel(
    private val appSettingsRepository: AppSettingsRepository,
    private val feedDataManager: FeedDataManager,
    val eaterDataManager: EaterDataManager,
    private val campaignManager: CampaignManager,
    private val paymentManager: PaymentManager,
    private val userRepository: UserRepository,
    globalErrorManager: GlobalErrorManager,
    private var eatersAnalyticsTracker: EatersAnalyticsTracker,
    private val flowEventsManager: FlowEventsManager,
    private val cartManager: CartManager,
    private val restaurantRepository: RestaurantRepository,
) : ViewModel() {

    val shareEvent = MutableLiveData<String>()
    val mainNavigationEvent = MutableLiveData<MainNavigationEvent>()
    val floatingCartBtnEvent = cartManager.getFloatingCartBtnEvent()

    val globalErrorLiveData = globalErrorManager.getGlobalErrorLiveData()
    val campaignLiveData = campaignManager.getCampaignLiveData()

    val startRestaurantActivity = MutableLiveData<RestaurantInitParams>()
    val forceFeedRefresh = MutableLiveData<Boolean>()
    val scrollFeedToTop = MutableLiveData<Boolean>()
    val stripeInitializationEvent = paymentManager.getStripeInitializationEvent()
    val getTraceableOrder = eaterDataManager.getTraceableOrders()
    val getTriggers = eaterDataManager.getTriggers()
    val mediaUtilsResultLiveData = MutableLiveData<MediaUtils.MediaUtilResult>()
    val onFloatingBtnHeightChange = MutableLiveData<Boolean>()
    val refreshSearchData = MutableLiveData<Boolean>()

    init {
        eaterDataManager.refreshSegment()
    }

    fun logPageEvent(eventType: FlowEventsManager.FlowEvents) {
        flowEventsManager.trackPageEvent(eventType)
    }

    fun handleMainNavigation(type: MainNavigationEvent) {
        mainNavigationEvent.postValue(type)
    }

    fun startLocationAndAddressAct() {
        mainNavigationEvent.postValue(MainNavigationEvent.START_LOCATION_AND_ADDRESS_ACTIVITY)
    }

    fun getFinalAddressParams() = eaterDataManager.getFinalAddressLiveDataParam()

    fun startStripeOrReInit() {
        if (paymentManager.hasStripeInitialized) {
            mainNavigationEvent.postValue(MainNavigationEvent.START_PAYMENT_METHOD_ACTIVITY)
        } else {
            mainNavigationEvent.postValue(MainNavigationEvent.INITIALIZE_STRIPE)
        }
    }

    fun reInitStripe(context: Context) {
        viewModelScope.launch {
            paymentManager.initPaymentManagerWithListener(context)
        }
    }

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

    fun getRestaurant(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val lastFeedRequest = feedDataManager.getLastFeedRequest()
            val result = restaurantRepository.getRestaurant(id, lastFeedRequest)
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
        return appSettingsRepository.getContactUsPhoneNumber()
    }

    fun getContactUsTextNumber(): String {
        return appSettingsRepository.getContactUsTextNumber()
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

    fun onShareCampaignClick(shareUrl: String?, shareText: String?) {
        shareEvent.postValue("$shareText \n $shareUrl")
        eatersAnalyticsTracker.logEvent(Constants.EVENT_CAMPAIGN_INVITE)
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
            if(appSettingsRepository.featureFlag(EatersFeatureFlags.NewAuth) == true) {
                mainNavigationEvent.postValue(MainNavigationEvent.LOGOUT_WITH_NEW_AUTH)
            }else{
                mainNavigationEvent.postValue(MainNavigationEvent.LOGOUT)
            }
        }
    }

    fun onMediaUtilsResultSuccess(result: MediaUtils.MediaUtilResult) {
        //use this liveData when using MediaUtils out side of MainActivity scope (for example - EditProfileBottomSheet)
        mediaUtilsResultLiveData.postValue(result)
    }

    fun onFloatingCartStateChanged(isShowing: Boolean) {
        onFloatingBtnHeightChange.postValue(isShowing)
    }

    /**
     * Starts Restaurant Activity with the initial params
     * we start it from here, beacuse we need to update stuff when order is successfully done.
     */
    fun startRestaurantActivity(restaurantInitParams: RestaurantInitParams) {
        startRestaurantActivity.postValue(restaurantInitParams)
    }

    fun logRestaurantClick(restaurantInitParams: RestaurantInitParams){
        val data = mutableMapOf<String, String>()
        data["home_chef_id"] = restaurantInitParams.restaurantId.toString()
        data["kitchen_name"] = restaurantInitParams.restaurantName.toString()
        data["home_chef_availability"] = restaurantInitParams.cookingSlot?.getAvailabilityString().toString()
        data["home_chef_name"] = restaurantInitParams.chefName.toString()
        data["home_chef_rating"] = restaurantInitParams.rating.toString()
        data["section_title"] = restaurantInitParams.sectionTitle.toString()
        data["section_index"] = restaurantInitParams.sectionOrder.toString()
        data["home_chef_index"] = restaurantInitParams.restaurantOrderInSection.toString()
        data["dish_tapped_index"] = restaurantInitParams.dishIndexInRestaurant.toString()
        data["item_clicked"] = restaurantInitParams.itemClicked.toString()
        eatersAnalyticsTracker.logEvent(Constants.EVENT_CLICK_RESTAURANT, data)
    }

    fun forceFeedRefresh() {
        forceFeedRefresh.postValue(true)
    }

    fun scrollFeedToTop() {
        scrollFeedToTop.postValue(true)
    }

    fun logEvent(eventName: String) {
        eatersAnalyticsTracker.logEvent(eventName)
    }

    fun logDeepLinkEvent(restaurantId: Long) {
        eatersAnalyticsTracker.logEvent(Constants.EVENT_OPEN_DEEP_LINK, mapOf(Pair("home_chef_id", restaurantId)))
    }

    fun refreshSearchData() {
        refreshSearchData.postValue(true)
    }

}