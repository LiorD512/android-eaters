package com.bupp.wood_spoon_eaters.features.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.*
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.common.AppSettings
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.features.main.profile.my_profile.MyProfileViewModel
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderMainViewModel
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import com.stripe.android.model.PaymentMethod
import kotlinx.coroutines.launch

class MainViewModel(
    val api: ApiService, val settings: AppSettings, private val metaDataRepository: MetaDataRepository, private val cartManager: CartManager,
    val eaterDataManager: EaterDataManager, private val campaignManager: CampaignManager, private val paymentManager: PaymentManager,
    private val userRepository: UserRepository, private val globalErrorManager: GlobalErrorManager, private var eventsManager: EventsManager): ViewModel()  {

//    val progressData = ProgressData()

    init {
        eaterDataManager.refreshSegment()
//        fcmManager.initFcmListener()
    }

    val mainNavigationEvent = MutableLiveData<MainNavigationEvent>()
    enum class MainNavigationEvent{
        START_LOCATION_AND_ADDRESS_ACTIVITY,
        START_PAYMENT_METHOD_ACTIVITY,
        INITIALIZE_STRIPE,
        LOGOUT,
        OPEN_CAMERA_UTIL_IMAGE
    }


    fun handleMainNavigation(type: MainNavigationEvent) {
        mainNavigationEvent.postValue(type)
    }


    fun startLocationAndAddressAct(){
        mainNavigationEvent.postValue(MainNavigationEvent.START_LOCATION_AND_ADDRESS_ACTIVITY)
    }


//    val activeCampaignEvent = SingleLiveEvent<ActiveCampaign?>()
//    val campaignUpdateEvent = campaignManager.getCampaignUpdateEvent()
    val globalErrorLiveData = globalErrorManager.getGlobalErrorLiveData()
    val campaignLiveData = campaignManager.getCampaignLiveData()
//    fun checkCampaignForFeed() {
//        campaignManager.checkCampaignFor(FlowEventsManager.FlowEvents.VISIT_FEED)
//    }


//    val bannerEvent = MutableLiveData<Int>()
//    fun showBanner(bannerType: Int) {
//        bannerEvent.postValue(bannerType)
//    }
//
//    fun clearBanner(){
//        bannerEvent.postValue(Constants.NO_BANNER)
//    }


    fun getFinalAddressParams() = eaterDataManager.getFinalAddressLiveDataParam()
    fun getDeliveryTimeLiveData() = eaterDataManager.getDeliveryTimeLiveData()

//    val navigationEvent = MutableLiveData<NavigationEventType>()
//    enum class NavigationEventType{
//        OPEN_CAMERA_UTIL_IMAGE,
//    }

    val dishClickEvent = LiveEventData<Long>()
    fun onDishClick(menuItemId: Long) {
        dishClickEvent.postRawValue(menuItemId)
    }


    //stripe
    val stripeInitializationEvent = paymentManager.getStripeInitializationEvent()
    fun startStripeOrReInit(){
        MTLogger.c(NewOrderMainViewModel.TAG, "startStripeOrReInit")
        if(paymentManager.hasStripeInitialized){
            Log.d(NewOrderMainViewModel.TAG, "start payment method")
            mainNavigationEvent.postValue(MainNavigationEvent.START_PAYMENT_METHOD_ACTIVITY)
        }else{
            MTLogger.c(NewOrderMainViewModel.TAG, "re init stripe")
            mainNavigationEvent.postValue(MainNavigationEvent.INITIALIZE_STRIPE)
        }
    }

    fun reInitStripe(context: Context) {
        viewModelScope.launch {
            paymentManager.initPaymentManagerWithListener(context)
        }
    }
    ///////////////////////////////


    fun getDefaultLocationName(): String{
        return metaDataRepository.getDefaultFeedLocationName()
    }


//    var waitingForAddressAction: Boolean = false
    private var hasPendingOrder: Boolean = false
    private var hasActiveOrder: Boolean = false
    val addressUpdateActionEvent: SingleLiveEvent<AddressUpdateEvent> = SingleLiveEvent()
    val addressUpdateEvent: SingleLiveEvent<AddressUpdateEvent> = SingleLiveEvent()


    data class AddressUpdateEvent(val currentAddress: Address?)

    private val TAG = "wowMainVM"


    fun getUserName(): String {
        return eaterDataManager.currentEater?.firstName!!
    }

    fun getShareText(): String {
//        val inviteUrl = eaterDataManager.currentEater?.shareCampaign?.inviteUrl
//        val text = eaterDataManager.currentEater?.shareCampaign?.shareText
//        return "$text \n $inviteUrl"
        return ""
    }

//    fun startLocationUpdates() {
//        eaterDataManager.startLocationUpdates()
//    }
//
//    fun stopLocationUpdates() {
//        eaterDataManager.stopLocationUpdates()
//    }

    private fun getListOfAddresses(): List<Address>? {
        if (eaterDataManager.currentEater != null) {
            return eaterDataManager.currentEater!!.addresses
        }
        return null
    }

    enum class NoLocationUiEvent {
        DEVICE_LOCATION_OFF,
        NO_LOCATIONS_SAVED
    }

//    val noUserLocationEvent = SingleLiveEvent<NoLocationUiEvent>()
//    override fun onLocationEmpty() {
//        //this method fires when device location services is off
//        if (getListOfAddresses() == null || getListOfAddresses()!!.isEmpty()) {
//            //if user never saved a location -> will show dialog
//            noUserLocationEvent.postValue(NoLocationUiEvent.NO_LOCATIONS_SAVED)
//        }
//    }

//    val locationSettingsEvent = SingleLiveEvent<Boolean>()
//    fun startAndroidLocationSettings(){
//        locationSettingsEvent.postValue(true)
//    }

//    override fun onUsingPreviousLocation() {
//        noUserLocationEvent.postValue(NoLocationUiEvent.DEVICE_LOCATION_OFF)
//    }
//
//
//    override fun onAddressChanged(updatedAddress: Address?) {
//        Log.d("wowMainVM", "onAddressChanged")
//        addressUpdateEvent.postValue(AddressUpdateEvent(updatedAddress))
//        if (waitingForAddressAction) {
//            addressUpdateActionEvent.postValue(AddressUpdateEvent(updatedAddress))
//        }
//    }

    val checkCartStatus: SingleLiveEvent<CheckCartStatusEvent> = SingleLiveEvent()

    data class CheckCartStatusEvent(val hasPendingOrder: Boolean, val totalPrice: Double?)

    fun refreshMainBottomBarUi(){
        val hasPending = !cartManager.isEmpty()
        val totalPrice = cartManager.calcTotalDishesPrice()
        val activeOrders = eaterDataManager.traceableOrdersList
        mainBottomBarEvent.postValue(MainBottomBarEvent(hasPending, totalPrice, activeOrders, !activeOrders.isNullOrEmpty() && hasPending))
    }

    data class MainBottomBarEvent(val hasPendingOrder: Boolean, val totalPrice: Double?, val activeOrders: List<Order>? = null, val hasBoth: Boolean)
    val mainBottomBarEvent = MutableLiveData<MainBottomBarEvent>()

    val getTraceableOrder = eaterDataManager.getTraceableOrders()

//    fun checkCartStatus() {
//        if(!cartManager.isEmpty()){
//            val totalPrice = cartManager.calcTotalDishesPrice()
//            checkCartStatus.postValue(CheckCartStatusEvent(true, totalPrice))
//        }
//    }

    fun checkForActiveOrder() {
        viewModelScope.launch {
            eaterDataManager.checkForTraceableOrders()
        }
    }

    fun refreshActiveCampaigns(){
        viewModelScope.launch {
            campaignManager.onFlowEventFired(FlowEventsManager.FlowEvents.VISIT_FEED)
        }
    }

    val getTriggers = eaterDataManager.getTriggers()
    fun checkForTriggers() {
        viewModelScope.launch {
            eaterDataManager.checkForTriggers()
        }
    }

    val getCookEvent: SingleLiveEvent<CookEvent> = SingleLiveEvent()

    data class CookEvent(val isSuccess: Boolean = false, val cook: Cook?)

    fun getCurrentCook(id: Long) {// todo - nycccc
//        val currentAddress = eaterDataManager.getLastChosenAddress()
//        api.getCook(cookId = id, lat = currentAddress?.lat, lng = currentAddress?.lng).enqueue(object : Callback<ServerResponse<Cook>> {
//            override fun onResponse(call: Call<ServerResponse<Cook>>, response: Response<ServerResponse<Cook>>) {
//                if (response.isSuccessful) {
//                    val cook = response.body()?.data
//                    Log.d("wowFeedVM", "getCurrentCook success: ")
//                    getCookEvent.postValue(CookEvent(true, cook))
//                } else {
//                    Log.d("wowFeedVM", "getCurrentCook fail")
//                    getCookEvent.postValue(CookEvent(false, null))
//                }
//            }
//
//            override fun onFailure(call: Call<ServerResponse<Cook>>, t: Throwable) {
//                Log.d("wowFeedVM", "getCurrentCook big fail")
//                getCookEvent.postValue(CookEvent(false, null))
//            }
//        })
    }


    fun getCurrentEater(): Eater? {
        return eaterDataManager.currentEater
    }

    fun hasAddress(): Boolean {// todo - nyc
//        return eaterDataManager.getLastChosenAddress() != null
        return true
    }

//    fun initLocationFalse() {
////        eaterDataManager.onLocationEmpty()
//    }

    val getUserCampaignDataEvent: SingleLiveEvent<Campaign?> = SingleLiveEvent()
    fun checkForUserCampaignData() {
//        getUserCampaignDataEvent.postValue(eaterDataManager.currentEater)
    }


    //move to eater data manager
    val getShareCampaignEvent: SingleLiveEvent<Campaign?> = SingleLiveEvent()



//    fun checkForCampaignReferrals() {
//        //checks if user have an active campaign coupon prize
//        val sid = eaterDataManager.sid
//        val cid = eaterDataManager.cid
//        if(sid != null){
//            Log.d("wowMainVM", "init start")
//            var serverCallMap = mutableMapOf<Int, Observable<*>>()
//            serverCallMap.put(0, api.postCampaignReferrals(sid, cid))
////            serverCallMap.put(1, api.getMe()) //restore this ny.
//            val requests = ArrayList<Observable<*>>()
//            for (call in serverCallMap) {
//                requests.add(call.value)
//            }
//
//            Observable.zip(requests) { objects ->
//                Log.d("wowMainVM", "Observable success")
//
//                //parse client
//                val eaterServerResponse = objects[1] as ServerResponse<Eater>
//                val eater: Eater? = eaterServerResponse.data
////                eaterDataManager.currentEater = eater // ny delete
//                eater?.activeCampaign?.let{
//                    activeCampaignEvent.postValue(it)
//                    eaterDataManager.sid = null
//                    eaterDataManager.cid = null
//                }
//
//                Log.d("wowMainVM", "eater parsing success: " + eater?.id)
//
//
//                Any()
//            }.timeout(55000, TimeUnit.MILLISECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe({
//                    object : Consumer<Any> {
//                        override fun accept(p0: Any) {
//                            Log.d("wowSplash", "Observable accept success")
//                        }
//                    }
//                }, { result -> Log.d("wowSplash", "wowException $result") })
//        }else{
//            eaterDataManager.currentEater?.activeCampaign.let{
//                activeCampaignEvent.postValue(it)
//            }
//        }
//    }
//
//    fun refreshUserData() {
//        api.getMeCall().enqueue(object : Callback<ServerResponse<Eater>> {
//            override fun onResponse(call: Call<ServerResponse<Eater>>, response: Response<ServerResponse<Eater>>) {
//                if (response.isSuccessful) {
//                    Log.d(TAG, "on success! ")
//                    var eater = response.body()?.data!!
////                    eaterDataManager.currentEater = eater //ny delete
//                    checkForCampaignReferrals()
//                } else {
//                    Log.d(TAG, "on Failure! ")
//                }
//            }
//
//            override fun onFailure(call: Call<ServerResponse<Eater>>, t: Throwable) {
//                Log.d(TAG, "on big Failure! " + t.message)
//            }
//        })
//    }
//
//    fun resetOrderTimeIfNeeded() {
//        deliveryTimeManager.setNewDeliveryTime(null)
//    }
//
//    val refreshAppDataEvent = SingleLiveEvent<Boolean>()
//    fun checkIfMemoryCleaned() {
//        if(eaterDataManager.currentEater == null){
//            refreshAppDataEvent.postValue(true)
//        }
//    }
//
//    fun initGpsStatus(activity: Activity) {
//        eaterDataManager.initGpsStatus(activity)
//    }

    fun getContactUsPhoneNumber(): String {
        return metaDataRepository.getContactUsPhoneNumber()
    }

    fun getContactUsTextNumber(): String {
        return metaDataRepository.getContactUsTextNumber()
    }

    fun test() {
        val cloudinery = metaDataRepository.getCloudinaryTransformations()
        val large = cloudinery?.getByType(CloudinaryTransformationsType.LARGE)
        Log.d(TAG, "coudirery: $cloudinery")
    }

    fun onUserImageClick() {
        mainNavigationEvent.postValue(MainNavigationEvent.OPEN_CAMERA_UTIL_IMAGE)
    }
//
//    fun checkIfHaveReferral() {
//        viewModelScope.launch {
//            campaignManager.validateReferral()
//        }
//    }

    fun updateCampaignStatus(campaign: Campaign, status: UserInteractionStatus) {
        viewModelScope.launch {
            campaign.userInteractionId?.let{
                campaignManager.updateCampaignStatus(it, status)
            }
        }
    }

    fun updatePaymentMethod(paymentMethod: PaymentMethod?) {
        paymentMethod?.let{
            paymentManager.updateSelectedPaymentMethod(it)
        }
    }

    val shareEvent = MutableLiveData<String>()
    fun onShareCampaignClick(campaign: Campaign?) {
        val shareUrl = campaign?.shareUrl
        val shareText = campaign?.shareText ?: ""
        shareEvent.postValue("$shareText \n $shareUrl")
        eventsManager.logEvent(Constants.EVENT_CAMPAIGN_INVITE)
    }

    fun logout() {
        val logoutResult = userRepository.logout()
        if (logoutResult.type == UserRepository.UserRepoStatus.LOGGED_OUT) {
            mainNavigationEvent.postValue(MainNavigationEvent.LOGOUT)
        }
    }


}