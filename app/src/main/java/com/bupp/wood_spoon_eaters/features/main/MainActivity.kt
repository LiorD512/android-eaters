package com.bupp.wood_spoon_eaters.features.main

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.custom_views.orders_bottom_bar.OrdersBottomBar
import com.bupp.wood_spoon_eaters.dialogs.*
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.ActiveOrderTrackerDialog
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.TimePickerBottomSheet
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressActivity
import com.bupp.wood_spoon_eaters.features.main.cook_profile.CookProfileDialog
import com.bupp.wood_spoon_eaters.features.main.feed.FeedFragment
import com.bupp.wood_spoon_eaters.features.main.no_locations.NoLocationsAvailableFragment
import com.bupp.wood_spoon_eaters.features.main.order_details.OrderDetailsFragment
import com.bupp.wood_spoon_eaters.features.main.order_history.OrdersHistoryFragment
import com.bupp.wood_spoon_eaters.features.main.profile.edit_my_profile.EditMyProfileFragment
import com.bupp.wood_spoon_eaters.features.main.profile.my_profile.MyProfileFragment
import com.bupp.wood_spoon_eaters.features.main.report_issue.ReportIssueFragment
import com.bupp.wood_spoon_eaters.features.main.search.SearchFragment
import com.bupp.wood_spoon_eaters.features.main.settings.SettingsFragment
import com.bupp.wood_spoon_eaters.features.main.support_center.SupportFragment
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderActivity
import com.bupp.wood_spoon_eaters.features.splash.SplashActivity
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.utils.Utils
import com.canhub.cropper.CropImage
import com.stripe.android.view.PaymentMethodsActivityStarter
import it.sephiroth.android.library.xtooltip.ClosePolicy
import it.sephiroth.android.library.xtooltip.Tooltip
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_feed.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity(), HeaderView.HeaderViewListener,
    NoDeliveryToAddressDialog.NoDeliveryToAddressDialogListener, TipCourierDialog.TipCourierDialogListener,
    StartNewCartDialog.StartNewCartDialogListener, ContactUsDialog.ContactUsDialogListener,
    ShareDialog.ShareDialogListener,
    RateLastOrderDialog.RateDialogListener, ActiveOrderTrackerDialog.ActiveOrderTrackerDialogListener,
    OrdersBottomBar.OrderBottomBatListener, CookProfileDialog.CookProfileDialogListener {

    private var tooltip: Tooltip? = null

    //    private lateinit var gpsBroadcastReceiver: GPSBroadcastReceiver
    private var lastFragmentTag: String? = null
    private var currentFragmentTag: String? = null
    val viewModel by viewModel<MainViewModel>()

    private val updateLocationOnResult = registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
        Log.d("wowMain","Activity For Result")
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            //check if location changed and refresh ui
        }
    }
    private val afterOrderResult = registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
        Log.d("wowMain","Activity For Result")
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            //check if has order and refresh ui
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initObservers()
        initUi()

        initUiRelatedProcesses()

        loadFeed()
    }




    ////////////////////////////////////////////////
    ///////       Ui processes - start       ///////
    ////////////////////////////////////////////////

    private fun initUi() {
        mainActHeaderView.setHeaderViewListener(this, viewModel.getCurrentEater())
        mainActOrdersBB.setOrdersBottomBarListener(this)
    }

    private fun initUiRelatedProcesses() {
        checkForBranchIntent()
        viewModel.checkForCampaignReferrals()
        viewModel.checkForTriggers()
    }

    private fun checkForBranchIntent() {
        intent?.let {
            val cookId = intent.getLongExtra("cook_id", -1)
            val menuItemId = intent.getLongExtra("menu_item_id", -1)
            Log.d("wowMain", "branch: cook $cookId, menuItem: $menuItemId")
            if (cookId > 0) {
                viewModel.getCurrentCook(cookId)
            } else if (menuItemId > 0) {
                if (viewModel.hasAddress()) {
                    loadNewOrderActivity(menuItemId)
                } else {
//                    handlePb(true)
                    Log.d("wowMain", "brnach intent observing address change")
//                    viewModel.waitingForAddressAction = true
                    viewModel.addressUpdateActionEvent.observe(this, Observer { newAddressEvent ->
                        Log.d("wowMain", "brnach intent observing address - ON CHANGE")
                        if (newAddressEvent != null) {
//                            handlePb(false)
                            loadNewOrderActivity(menuItemId)
                        }
                    })
                }
            }
        }
    }

    ////////////////////////////////////////////////
    ///////        Ui processes - end        ///////
    ////////////////////////////////////////////////



    private fun checkForCampaignReferrals() {
    }



    override fun onDishClick(menuItemId: Long) {
        loadNewOrderActivity(menuItemId)
    }

    fun checkForActiveOrder() {
        viewModel.checkForActiveOrder()
    }

    private fun checkForTriggers() {
    }

    private fun checkCartStatus() {
        viewModel.checkCartStatus()
    }

    private fun initObservers() {
        viewModel.progressData.observe(this, {
            handlePb(it)
        })
        viewModel.mainNavigationEvent.observe(this, {
            startLocationAndAddresAct()
        })

        viewModel.bannerEvent.observe(this, {
            handleBannerEvent(it)
        })


        //header event
        viewModel.getFinalAddressParams().observe(this, {
            mainActHeaderView.setLocationTitle(it?.locationTitle)
        })
        viewModel.getDeliveryTimeLiveData().observe(this, {
            mainActHeaderView.setDeliveryTime(it?.deliveryDateUi)
        })


        viewModel.dishClickEvent.observe(this, Observer{
            val event = it.getContentIfNotHandled()
            event?.let{
                afterOrderResult.launch(Intent(this, NewOrderActivity::class.java).putExtra(Constants.NEW_ORDER_MENU_ITEM_ID, event))
            }
        })





        viewModel.addressUpdateEvent.observe(this, Observer { newAddressEvent ->
            if (newAddressEvent != null) {
                if (newAddressEvent.currentAddress != null) {
                    refreshFeedIfNecessary()
                }
            }
        })

        viewModel.getActiveOrders.observe(this, Observer { ordersEvent ->
            mainActPb.hide()
            if (ordersEvent.isSuccess) {
                mainActOrdersBB.handleBottomBar(showActiveOrders = true)
                if (ordersEvent.showDialog) {
                    openActiveOrdersDialog(ordersEvent.orders!!)
                }
            } else {
                mainActOrdersBB.handleBottomBar(showActiveOrders = false)
            }
        })

        viewModel.getTriggers.observe(this, Observer { triggerEvent ->
            if (triggerEvent.isSuccess) {
                Log.d("wowMain", "found should rate id !: ${triggerEvent.trigger?.shouldRateOrder}")
                RateLastOrderDialog(triggerEvent.trigger?.shouldRateOrder!!.id, this).show(supportFragmentManager, Constants.RATE_LAST_ORDER_DIALOG_TAG)
            }
        })

        viewModel.checkCartStatus.observe(this, Observer { pendingOrderEvent ->
            if (pendingOrderEvent.hasPendingOrder) {
                mainActOrdersBB.handleBottomBar(showCheckout = true)
                pendingOrderEvent.totalPrice?.let {
                    mainActOrdersBB.updateStatusBottomBar(type = Constants.STATUS_BAR_TYPE_CHECKOUT, checkoutPrice = it)
                }
            } else {
                mainActOrdersBB.handleBottomBar(showCheckout = false)
            }
        })
        viewModel.getCookEvent.observe(this, Observer { event ->
//            feedFragPb.hide()
            if (event.isSuccess) {
                CookProfileDialog(this, event.cook!!).show(supportFragmentManager, Constants.COOK_PROFILE_DIALOG_TAG)
            }
        })
//        viewModel.noUserLocationEvent.observe(this, Observer {
//            when (it) {
//                MainViewModel.NoLocationUiEvent.DEVICE_LOCATION_OFF -> {
//                    handleDeviceLocationOff()
//                }
//                MainViewModel.NoLocationUiEvent.NO_LOCATIONS_SAVED -> {
//                    loadFragment(NoLocationsAvailableFragment(), Constants.NO_LOCATIONS_AVAILABLE_TAG)
//                }
//                else -> {
//                }
//            }
//        })
        viewModel.locationSettingsEvent.observe(this, Observer {
            startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), Constants.ANDROID_SETTINGS_REQUEST_CODE)
        })
        viewModel.getShareCampaignEvent.observe(this, Observer {
            it?.let {
                SharingCampaignDialog.newInstance(it).show(supportFragmentManager, Constants.SHARE_CAMPAIGN_DIALOG)
            }
        })
        viewModel.activeCampaignEvent.observe(this, Observer {
            val activeCampaign = it
            if (activeCampaign != null) {
                mainActCampaignHeader.visibility = View.VISIBLE
                mainActCampaignTitle.text = it.title
                mainActCampaignSubTitle.text = it.terms
                it.image?.let {
                    Glide.with(this).load(it).into(mainActCampaignImg)
                }

                it.color?.let {
                    mainActCampaignHeader.setBackgroundColor(Color.parseColor(it))
                }

                mainActCampaignHeader.setOnClickListener {
                    if (mainActCampaignImg.visibility == View.VISIBLE) {
                        mainActCampaignImg.visibility = View.GONE
                        mainActCampaignSubTitle.visibility = View.GONE
                    } else {
                        mainActCampaignImg.visibility = View.VISIBLE
                        mainActCampaignSubTitle.visibility = View.VISIBLE
                    }
                }
            } else {
                mainActCampaignHeader.visibility = View.GONE
            }
        })

        viewModel.refreshAppDataEvent.observe(this, Observer {
            Log.d("wowMainAct", "refreshAppDataEvent !!!!!")
            startActivity(Intent(this, SplashActivity::class.java))
            finishAffinity()
        })
    }

    private fun handleBannerEvent(bannerType: Int) {
        bannerType.let{
            when(bannerType){
                Constants.NO_BANNER -> {
                    tooltip?.dismiss()
                }
                Constants.BANNER_KNOWN_ADDRESS -> {
                    mainActHeaderView.post {
                        showBanner(getString(R.string.banner_known_address))
                    }
                }
                Constants.BANNER_MY_LOCATION -> {
                    mainActHeaderView.post {
                        val city = viewModel.getDefaultLocationName()
                        showBanner(getString(R.string.banner_my_location, city))
                    }
                }
                Constants.BANNER_NO_GPS -> {
                    mainActHeaderView.post {
                        showBanner(getString(R.string.banner_no_gps))
                    }
                }
                else -> {}
            }
        }
    }

    private fun showBanner(text: String) {
        tooltip = Tooltip.Builder(this)
            .anchor(mainActHeaderView, 0, 0, true)
//            .anchor(150, 150)
            .text(text)
//                        .styleId(Int)
//                        .typeface(Typeface)
//                        .maxWidth(Int)
            .arrow(true)
            .floatingAnimation(Tooltip.Animation.SLOW)
            .closePolicy(ClosePolicy.TOUCH_ANYWHERE_CONSUME)
//            .showDuration(1500)
//            .fadeDuration(5000)
            .overlay(false)
            .maxWidth(750)
            .create()

        tooltip!!
            .doOnHidden { }
            .doOnFailure { }
            .doOnShown { }
            .show(mainActHeaderView, Tooltip.Gravity.BOTTOM, false)
    }

    private fun refreshFeedIfNecessary() {
        if (currentFragmentTag == Constants.NO_LOCATIONS_AVAILABLE_TAG || lastFragmentTag == Constants.NO_LOCATIONS_AVAILABLE_TAG) {
            loadFeed()
        }
    }

    private fun handleDeviceLocationOff() {
        mainActLocationDisabledText.visibility = View.VISIBLE
        mainActLocationDisabledText.setOnClickListener {
            mainActLocationDisabledText.visibility = View.GONE
            openDeviceLocationSettings()
        }

    }

    private fun openDeviceLocationSettings() {
        startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    override fun onBottomBarOrdersClick(type: Int) {
        viewModel.checkForActiveOrder(true)
    }

    override fun onBottomBarCheckoutClick() {
        startActivityForResult(
            Intent(this, NewOrderActivity::class.java).putExtra("isCheckout", true),
            Constants.NEW_ORDER_REQUEST_CODE
        )
    }

    private fun openActiveOrdersDialog(orders: ArrayList<Order>) {
        ActiveOrderTrackerDialog.newInstance(orders).show(supportFragmentManager, Constants.TRACK_ORDER_DIALOG_TAG)
    }

    override fun onContactUsClick() {
        val phone = viewModel.getContactUsPhoneNumber()
        Utils.callPhone(this, phone)
    }



    override fun onPause() {
        super.onPause()
//        viewModel.stopLocationUpdates()
    }

    private fun loadFragment(fragment: Fragment, tag: String) {
        // todo - check status bar status??
        lastFragmentTag = currentFragmentTag
        currentFragmentTag = tag
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainActContainer, fragment, tag)
            .commit()
    }

    private fun getFragmentByTag(tag: String): Fragment? {
        val fragmentManager = this@MainActivity.supportFragmentManager
        val fragments = fragmentManager.fragments
        for (fragment in fragments) {
            if (fragment.tag == tag)
                return fragment
        }
        return null
    }


    fun handlePb(shouldShow: Boolean) {
        if (shouldShow) {
            mainActPb.show()
        } else {
            mainActPb.hide()
        }
    }


    //fragment and sub features

    private fun startLocationAndAddresAct() {
        updateLocationOnResult.launch(Intent(this, LocationAndAddressActivity::class.java))
    }

    private fun loadFeed() {
        loadFragment(FeedFragment.newInstance(), Constants.FEED_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_FEED)

//        viewModel.checkForShareCampaign()
//        setHeaderViewLocationDetails(viewModel.getLastOrderTime(), viewModel.getLastOrderAddress())
    }

    private fun loadSearchFragment() {
        loadFragment(SearchFragment.newInstance(), Constants.SEARCH_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_SEARCH)
    }

    fun loadMyProfile() {
        loadFragment(MyProfileFragment.newInstance(), Constants.MY_PROFILE_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE_SETTINGS, "My Account")
    }

    fun loadEditMyProfile() {
        loadFragment(EditMyProfileFragment.newInstance(), Constants.EDIT_MY_PROFILE_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE_SAVE, "Hey ${viewModel.getUserName()}!")
    }

    fun loadSupport() {
        loadFragment(SupportFragment.newInstance(), Constants.SUPPORT_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE, getString(R.string.support_dialog_title))
    }

    fun loadReport(orderId: Long) {
        loadFragment(ReportIssueFragment.newInstance(orderId), Constants.REPORT_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE, "Report issue")
    }

    fun loadRateOrder(orderId: Long) {
        RateLastOrderDialog(orderId, this).show(supportFragmentManager, Constants.RATE_LAST_ORDER_DIALOG_TAG)
    }

    override fun onRatingDone() {
//        ThankYouDialog().show(supportFragmentManager, Constants.THANK_YOU_DIALOG_TAG)
        if (getFragmentByTag(Constants.ORDER_HISTORY_TAG) != null) {
            (getFragmentByTag(Constants.ORDER_HISTORY_TAG) as OrdersHistoryFragment).onRatingDone()
        }
    }

    fun loadOrderDetails(orderId: Long) {
        loadFragment(OrderDetailsFragment.newInstance(orderId), Constants.ORDER_DETAILS_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE, "Order Details")
    }

    //delivery details methods



    fun loadDeliveryDetails() {
//        loadFragment(DeliveryDetailsFragment.newInstance(), Constants.DELIVERY_DETAILS_TAG)
//        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE, "Delivery Details")
    }


    fun loadSettingsFragment() {
        loadFragment(SettingsFragment.newInstance(), Constants.SETTINGS_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE, "Location and Communication settings")
    }

    fun loadOrderHistoryFragment() {
        loadFragment(OrdersHistoryFragment.newInstance(), Constants.ORDER_HISTORY_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE, "Order History")
    }


//    override fun onLocationSelected(selected: GoogleAddressResponse?) {
//        //on LocationChoosertFragment result
//        if (getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) != null && selected != null) {
////            this.selectedGoogleAddress = selected
////            (getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) as AddOrEditAddressFragment).onLocationSelected(selected) //todo - ny !
//        } else if (getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) != null && selected != null) {
////            this.selectedGoogleAddress = selected
////            (getFragmentByTag(Constants.ADDRESS_DIALOG_TAG) as AddressChooserDialog).addAddress(selected)
//            Toast.makeText(this, "What should we do here", Toast.LENGTH_SHORT).show()
//        }
//    }

    override fun onChangeAddressClick() {
        loadDeliveryDetails()
    }


    override fun onTipDone(tipAmount: Int) {
        Toast.makeText(this, "onTipDone $tipAmount", Toast.LENGTH_SHORT).show()
    }

    override fun onNewCartClick() {
        Toast.makeText(this, "onNewCartClick", Toast.LENGTH_SHORT).show()
    }

    override fun onCallSupportClick() {
        onContactUsClick()
    }

    override fun onSmsSupportClick() {
        sendSmsText()
    }

    override fun onShareClick() {
        Toast.makeText(this, "onShareClick", Toast.LENGTH_SHORT).show()
    }

    //load dialogs
//    fun openAddressChooser() {
//        startActivityForResult(Intent(this, AddressChooserActivity::class.java), Constants.ADDRESS_CHOOSER_REQUEST_CODE)
//    }

    fun loadDishOfferedDialog() {
        NewSuggestionSuccessDialog().show(supportFragmentManager, Constants.DISH_OFFERED_TAG)
        if (getFragmentByTag(Constants.SEARCH_TAG) != null) {
            (getFragmentByTag(Constants.SEARCH_TAG) as SearchFragment).onSearchInputChanged("")
        }
    }

    private fun setHeaderViewLocationDetails(time: String? = null, location: Address? = null) {
//        mainActHeaderView.setLocationTitle(time, location?.streetLine1)
    }

//    // Request multiple permissions contract
//    private val requestMultiplePermissions =
//        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions : Map<String, Boolean> ->
//            // Do something if some permissions granted or denied
//            permissions.entries.forEach {
//                // Do checking here
//            }
//        }
//
//    request_multiple_permission.setOnClickListener {
//        requestMultiplePermissions.launch(
//            arrayOf(
//                permission.BLUETOOTH,
//                permission.ACCESS_FINE_LOCATION
//            )
//        )
//    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {

    }
    when (requestCode) {
        Constants.LOCATION_PERMISSION_REQUEST_CODE -> {
//            invokeLocationAction()
        }
//            Constants.LOCATION_PERMISSION_REQUEST_CODE -> {
//                Log.d("wowMainVM", "onRequestPermissionsResult: LOCATION_PERMISSION_REQUEST_CODE")
//                if(grantResults.isNotEmpty()){
//                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                        viewModel.startLocationUpdates()
//                    } else {
//                        viewModel.initLocationFalse()
//                    }
//                }
//            }
        Constants.PHONE_CALL_PERMISSION_REQUEST_CODE -> {
            Log.d("wowMainVM", "onRequestPermissionsResult: LOCATION_PERMISSION_REQUEST_CODE")
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission was granted, yay!
                val phone = viewModel.getContactUsPhoneNumber()
                Utils.callPhone(this, phone)
            } else {
                // permission denied, boo! Disable the
                // functionality
            }
            return
        }
        }
    }

    fun sendSmsText() {
        val phone = viewModel.getContactUsTextNumber()
        val smsIntent =
            Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone))
        smsIntent.putExtra("sms_body", getString(R.string.support_frag_sms_sentence))
        startActivity(smsIntent)
    }

//


    //HeaderView Listener interface
    override fun onHeaderBackClick() {
        onBackPressed()
    }

    override fun onHeaderSearchClick() {
        loadSearchFragment()
    }

    override fun onHeaderSettingsClick() {
        loadSettingsFragment()
    }

    override fun onHeaderFilterClick() {
        if (getFragmentByTag(Constants.SEARCH_TAG) as SearchFragment? != null) {
            (getFragmentByTag(Constants.SEARCH_TAG) as SearchFragment).openFilterDialog()
        }
    }

    override fun onHeaderTimeClick() {
//        loadFragment(TimePickerBottomSheet(), "tag")
        val timePickerBottomSheet = TimePickerBottomSheet()
        timePickerBottomSheet.show(supportFragmentManager, Constants.TIME_PICKER_BOTTOM_SHEET)
    }

    override fun onHeaderAddressClick() {
        startLocationAndAddresAct()
    }


    override fun onHeaderTextChange(str: String) {
        if (getFragmentByTag(Constants.SEARCH_TAG) as SearchFragment? != null) {
            (getFragmentByTag(Constants.SEARCH_TAG) as SearchFragment).onSearchInputChanged(str)
        }
    }

    fun setHeaderViewSaveBtnClickable(isClickable: Boolean) {
        mainActHeaderView.setSaveButtonClickable(isClickable)
    }


    override fun onHeaderSaveClick() {
//        if (getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) != null) {
//            (getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) as AddAddressFragment).saveAddressDetails()
//        } else
        if (getFragmentByTag(Constants.EDIT_MY_PROFILE_TAG) != null) {
//            handlePb(true)
            (getFragmentByTag(Constants.EDIT_MY_PROFILE_TAG) as EditMyProfileFragment).saveEaterDetails()
        }
    }

    override fun onHeaderProfileClick() {
        loadMyProfile()
    }


    override fun onBackPressed() {
        when (currentFragmentTag) {
            Constants.ADD_NEW_ADDRESS_TAG -> {
                when (lastFragmentTag) {
                    Constants.MY_PROFILE_TAG -> {
                        loadMyProfile()
                    }
                    Constants.DELIVERY_DETAILS_TAG -> {
                        loadDeliveryDetails()
                    }
                }
            }
            Constants.SETTINGS_TAG, Constants.EDIT_MY_PROFILE_TAG,
            Constants.SUPPORT_TAG -> {
                loadMyProfile()
            }
            Constants.REPORT_TAG -> {
                loadOrderHistoryFragment()
            }
            Constants.DELIVERY_DETAILS_TAG -> {
                when (lastFragmentTag) {
                    Constants.NO_LOCATIONS_AVAILABLE_TAG -> {
                        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_FEED)
                    }
                    else -> {
//                        updateAddressTimeView()
                        loadFeed()
                    }
                }
            }
            Constants.ORDER_HISTORY_TAG -> {
                loadMyProfile()
            }
            Constants.ORDER_DETAILS_TAG -> {
                loadOrderHistoryFragment()
            }
            else -> {
                loadFeed()
            }
        }
    }

    fun updateSearchBarTitle(str: String) {
        mainActHeaderView.updateSearchTitle(str)
    }

    fun startPaymentMethodActivity() {
        PaymentMethodsActivityStarter(this).startForResult(
            PaymentMethodsActivityStarter.Args.Builder()
                .build()
        )
    }

//    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.NEW_ORDER_REQUEST_CODE) {
            resetOrderTimeIfNeeded()
            loadOrRefreshFeed()
            checkForActiveOrder()
            checkCartStatus()
            data?.let {
                if (it.hasExtra("isAfterPurchase") && it.getBooleanExtra("isAfterPurchase", false)) {
                    if (getFragmentByTag(Constants.FEED_TAG) is FeedFragment) {
                        (getFragmentByTag(Constants.FEED_TAG) as FeedFragment).silentRefresh()
                        checkForSharingCampaign()
                        refreshUser()
                    }
                }
            }
        }
        if (requestCode == Constants.EVENT_ACTIVITY_REQUEST_CODE) {
//            viewModel.disableEventData()
            checkForActiveOrder()
        }
        if (requestCode == Constants.ANDROID_SETTINGS_REQUEST_CODE) {
            Log.d("wowMainActivity", "BACK FROM SETTINGS")
        }
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    val resultUri = result?.uri

                    if (getFragmentByTag(Constants.EDIT_MY_PROFILE_TAG) as EditMyProfileFragment? != null) {
                        (getFragmentByTag(Constants.EDIT_MY_PROFILE_TAG) as EditMyProfileFragment).onMediaCaptureResult(
                            resultUri
                        )
                    }
                }
                PaymentMethodsActivityStarter.REQUEST_CODE -> {
                    Log.d("wowMainActivity", "Stripe")
//                    val paymentMethod: PaymentMethod = (data?.getParcelableExtra(PaymentMethodsActivityStarter.REQUEST_CODE) as PaymentMethod)
//                    if (paymentMethod != null && paymentMethod.card != null) {
                    val result = PaymentMethodsActivityStarter.Result.fromIntent(data)
                    result?.let {
                        Log.d("wowNewOrder", "payment method success")
                        if (getFragmentByTag(Constants.MY_PROFILE_TAG) != null) {
                            result.paymentMethod?.let {
                                (getFragmentByTag(Constants.MY_PROFILE_TAG) as MyProfileFragment).updateCustomerPaymentMethod(it)
                            }
                        }
                    }
                }
                Constants.ADDRESS_CHOOSER_REQUEST_CODE -> {
                    Log.d("wowMianActivity", "result ADDRESS_CHOOSER_REQUEST_CODE success")
//                    handlePb(false)
//                    updateAddressTimeView()

                    when (currentFragmentTag) {
//                        Constants.DELIVERY_DETAILS_TAG -> {
//                            if (getFragmentByTag(Constants.DELIVERY_DETAILS_TAG) as DeliveryDetailsFragment? != null) {
//                                (getFragmentByTag(Constants.DELIVERY_DETAILS_TAG) as DeliveryDetailsFragment).onAddressChooserSelected()
//                            }
//                        }
                        Constants.MY_PROFILE_TAG -> {
                            if (getFragmentByTag(Constants.MY_PROFILE_TAG) as MyProfileFragment? != null) {
                                (getFragmentByTag(Constants.MY_PROFILE_TAG) as MyProfileFragment).onAddressChooserSelected()
                            }
                        }
                    }
                }
            }
        }
//        updateAddressTimeView()
    }

    private fun loadOrRefreshFeed() {
        if (!currentFragmentTag.equals(Constants.FEED_TAG)) {
            loadFeed()
        } else {
            //change feeditemlist adapter to ListAdapter (singleItemSetChanged) and then remove comment from below
//            (getFragmentByTag(Constants.FEED_TAG) as FeedFragment).silentRefresh()
        }
    }

    private fun resetOrderTimeIfNeeded() {
        viewModel.resetOrderTimeIfNeeded()
    }

    private fun refreshUser() {
        //refresh user data for changes made after checkout (used campaign coupon)
        viewModel.refreshUserData()
    }

    private fun checkForSharingCampaign() {
        //check for active banner campaign after checkout
        viewModel.checkForShareCampaign()
    }

    fun updateFilterUi(isFiltered: Boolean) {
        mainActHeaderView.updateFilterUi(isFiltered)
    }

    fun loadNewOrderActivity(id: Long) {
//        startActivityForResult(
//            Intent(this, NewOrderActivity::class.java).putExtra("menuItemId", id),
//            Constants.NEW_ORDER_REQUEST_CODE
//        )
    }

    fun onReportIssueDone() {
        loadFeed()
        ThankYouDialog().show(supportFragmentManager, Constants.THANK_YOU_DIALOG_TAG)
    }

    fun refreshUserUi() {
        mainActHeaderView.refreshUserUi(viewModel.getCurrentEater())
    }

    fun startEventActivity() {
//        startActivityForResult(Intent(this, EventActivity::class.java), Constants.EVENT_ACTIVITY_REQUEST_CODE)
//        startActivity(Intent(this, EventActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        checkForActiveOrder()
//        checkIfMemoryCleaned() //check if this is nesseracy. ny code
    }

    private fun checkIfMemoryCleaned() {
        //this method belongs to Android 7 and below.. when garbadge collector cleaned the apps memory
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.LOLLIPOP){
            Log.d("wowMainAct", "checkIfMemoryCleaned()")
            viewModel.checkIfMemoryCleaned()
        }
    }

    override fun onStart() {
        super.onStart()

    }


    override fun onStop() {
        super.onStop()
//        unregisterReceiver(gpsBroadcastReceiver)
    }




}