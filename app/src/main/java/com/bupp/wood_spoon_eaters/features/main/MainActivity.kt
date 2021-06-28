package com.bupp.wood_spoon_eaters.features.main

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.campaign_bottom_sheet.CampaignBottomSheet
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.TimePickerBottomSheet
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.MediaUtils
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.databinding.ActivityMainBinding
import com.bupp.wood_spoon_eaters.dialogs.*
import com.bupp.wood_spoon_eaters.dialogs.rate_last_order.RateLastOrderDialog
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.ActiveOrderTrackerDialog
import com.bupp.wood_spoon_eaters.features.base.BaseActivity
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressActivity
import com.bupp.wood_spoon_eaters.features.main.feed.FeedFragment
import com.bupp.wood_spoon_eaters.features.main.feed_loader.FeedLoaderDialog
import com.bupp.wood_spoon_eaters.features.main.order_details.OrderDetailsFragment
import com.bupp.wood_spoon_eaters.features.main.order_history.OrdersHistoryFragment
import com.bupp.wood_spoon_eaters.features.main.profile.edit_my_profile.EditMyProfileFragment
import com.bupp.wood_spoon_eaters.features.main.profile.my_profile.MyProfileFragment
import com.bupp.wood_spoon_eaters.features.main.report_issue.ReportIssueFragment
import com.bupp.wood_spoon_eaters.features.main.search.SearchFragment
import com.bupp.wood_spoon_eaters.features.main.settings.SettingsFragment
import com.bupp.wood_spoon_eaters.features.main.support_center.SupportFragment
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderActivity
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderMainViewModel
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import com.bupp.wood_spoon_eaters.model.CampaignData
import com.bupp.wood_spoon_eaters.model.CampaignViewType
import com.bupp.wood_spoon_eaters.model.UserInteractionStatus
import com.bupp.wood_spoon_eaters.utils.Utils
import com.bupp.wood_spoon_eaters.views.CampaignBanner
import com.bupp.wood_spoon_eaters.views.CartBottomBar
import com.mikhaellopez.ratebottomsheet.AskRateBottomSheet
import com.mikhaellopez.ratebottomsheet.RateBottomSheet
import com.mikhaellopez.ratebottomsheet.RateBottomSheetManager
import com.stripe.android.PaymentSessionData
import com.stripe.android.view.PaymentMethodsActivityStarter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.nio.charset.Charset
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec


class MainActivity : BaseActivity(), HeaderView.HeaderViewListener,
     TipCourierDialog.TipCourierDialogListener,
    ContactUsDialog.ContactUsDialogListener,
    ShareDialog.ShareDialogListener,
    RateLastOrderDialog.RateDialogListener, ActiveOrderTrackerDialog.ActiveOrderTrackerDialogListener,
    CartBottomBar.OrderBottomBatListener, MediaUtils.MediaUtilListener, CampaignBanner.CampaignBannerListener, CampaignBottomSheet.CampaignBottomSheetListener {

    lateinit var binding: ActivityMainBinding
    private val mediaUtil = MediaUtils(this, this)

    private var lastFragmentTag: String? = null
    private var currentFragmentTag: String? = null
    val viewModel by viewModel<MainViewModel>()

    private val updateLocationOnResult = registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
        Log.d("wowMain", "Activity For Result - location")
        binding.mainActHeaderView.enableLocationClick(true)
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            //check if location changed and refresh ui
        }
    }
    private val afterOrderResult = registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
        Log.d("wowMain", "Activity For Result - new order")
//        if (result.resultCode == Activity.RESULT_OK) {
//            val data = result.data
            //check if has order and refresh ui
        viewModel.refreshMainBottomBarUi()
        result.data?.let {
            if (it.getBooleanExtra("isAfterPurchase", false)) {
                showRateTheAppDialog()
                viewModel.checkForActiveOrder()
//                    checkForSharingCampaign()
//                    refreshUser()
            }
        }
    }

    private fun showRateTheAppDialog() {
        Log.d(TAG, "showRateTheAppDialog")
        RateBottomSheetManager(this)
            .setInstallDays(3) // 3 by default
            .setLaunchTimes(2) // 5 by default
            .setRemindInterval(1) // 2 by default
            .setShowAskBottomSheet(true) // True by default
            .setShowLaterButton(true) // True by default
            .setShowCloseButtonIcon(true) // True by default
            .monitor()
        RateBottomSheet.showRateBottomSheetIfMeetsConditions(
            this,
            listener = object : AskRateBottomSheet.ActionListener {
                override fun onDislikeClickListener() {
                    // Will be called when a click on the "I don't like" button is triggered
                    Log.d(TAG, "showRateTheAppDialog - onDislikeClickListener")
                }

                override fun onRateClickListener() {
                    Log.d(TAG, "showRateTheAppDialog - onRateClickListener")
                }
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_main)


        initObservers()
        initUi()

        initUiRelatedProcesses()

        loadFeedProgressBarFragment()
        loadFeed()
    }



    private fun loadFeedProgressBarFragment() {
//        loadFragment(FeedLoaderFragment(), Constants.FEED_LOADER_TAG)
        FeedLoaderDialog().show(supportFragmentManager, Constants.FEED_LOADER_TAG)
    }


    ////////////////////////////////////////////////
    ///////       Ui processes - start       ///////
    ////////////////////////////////////////////////

    private fun initUi() {
        with(binding){
            mainActHeaderView.setHeaderViewListener(this@MainActivity, viewModel.getCurrentEater())
            mainActOrdersBB.setCartBottomBarListener(this@MainActivity)
        }
    }

    private fun initUiRelatedProcesses() {
        checkForBranchIntent()
        viewModel.checkForTriggers()
        viewModel.checkForActiveOrder()
        viewModel.checkIfHaveReferral()
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
//                    loadNewOrderActivity(menuItemId)
                } else {
//                    handlePb(true)
                    Log.d("wowMain", "brnach intent observing address change")
//                    viewModel.waitingForAddressAction = true
                    viewModel.addressUpdateActionEvent.observe(this, Observer { newAddressEvent ->
                        Log.d("wowMain", "brnach intent observing address - ON CHANGE")
                        if (newAddressEvent != null) {
//                            handlePb(false)
//                            loadNewOrderActivity(menuItemId)
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

    private fun initObservers() {
//        viewModel.progressData.observe(this, {
//            handlePb(it)
//        })
        viewModel.mainNavigationEvent.observe(this, {
            handleNavigation(it)
        })

        //header event
        viewModel.getFinalAddressParams().observe(this, {
            binding.mainActHeaderView.setLocationTitle(it?.shortTitle)
        })
        viewModel.getDeliveryTimeLiveData().observe(this, {
            binding.mainActHeaderView.setDeliveryTime(it?.deliveryDateUi)
        })

        viewModel.dishClickEvent.observe(this, Observer {
            val event = it.getContentIfNotHandled()
            event?.let {
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
        viewModel.mainBottomBarEvent.observe(this, {
            handleMainBottomBarUi(it)
        })
        viewModel.getTraceableOrder.observe(this, { traceableOrders ->
            viewModel.refreshMainBottomBarUi()
        })
        viewModel.getTriggers.observe(this, Observer { triggerEvent ->
            triggerEvent?.let {
                it.shouldRateOrder?.id?.let {
                    Log.d(TAG, "found should rate id !: ${triggerEvent.shouldRateOrder}")
                    RateLastOrderDialog(it, this).show(supportFragmentManager, Constants.RATE_LAST_ORDER_DIALOG_TAG)
                }
            }
        })

        viewModel.getShareCampaignEvent.observe(this, Observer {
            it?.let {
                SharingCampaignDialog.newInstance(it).show(supportFragmentManager, Constants.SHARE_CAMPAIGN_DIALOG)
            }
        })
        viewModel.campaignLiveData.observe(this, {
            Log.d(FeedFragment.TAG, "campaign: $it")
            it?.let{
                handleCampaignData(it)
            }
        })

//        viewModel.refreshAppDataEvent.observe(this, Observer {
//            Log.d("wowMainAct", "refreshAppDataEvent !!!!!")
//            startActivity(Intent(this, SplashActivity::class.java))
//            finishAffinity()
//        })

        viewModel.navigationEvent.observe(this, {
            when(it){
                MainViewModel.NavigationEventType.OPEN_CAMERA_UTIL_IMAGE -> {
                    mediaUtil.startPhotoFetcher()
                }
            }
        })

    }

    private fun handleCampaignData(campaignData: CampaignData) {
        val campaign = campaignData.campaign
        if(campaign.status == UserInteractionStatus.IDLE){
            campaign.viewTypes?.forEach {
                when(it){
                    CampaignViewType.BANNER -> {
                        binding.mainActCampaignBanner.initCampaignHeader(campaignData, this)
                    }
                    CampaignViewType.POPUP -> {

                    }
                    CampaignViewType.FEED -> {

                    }
                }
            }
        }
    }

    override fun onCampaignDetailsClick(campaign: CampaignData) {
        CampaignBottomSheet.newInstance(campaign).show(supportFragmentManager, Constants.CAMPAIGN_BOTTOM_SHEET)
    }

    private fun handleMainBottomBarUi(bottomBarEvent: MainViewModel.MainBottomBarEvent?) {
        if(bottomBarEvent?.hasBoth == true){
            val totalPrice = bottomBarEvent.totalPrice
            binding.mainActOrdersBB.updateStatusBottomBarByType(type = CartBottomBar.BottomBarTypes.TRACK_ORDER_OR_CHECKOUT, price = totalPrice)
        }else{
            bottomBarEvent?.activeOrders?.let{
                binding.mainActOrdersBB.updateStatusBottomBarByType(type = CartBottomBar.BottomBarTypes.TRACK_YOUR_ORDER, itemCount = it.size)
            }
            if(bottomBarEvent?.hasPendingOrder == true){
                val totalPrice = bottomBarEvent.totalPrice
                binding.mainActOrdersBB.updateStatusBottomBarByType(type = CartBottomBar.BottomBarTypes.PROCEED_TO_CHECKOUT, price = totalPrice)
            }
        }
    }

    private fun refreshFeedIfNecessary() {
        if (currentFragmentTag == Constants.NO_LOCATIONS_AVAILABLE_TAG || lastFragmentTag == Constants.NO_LOCATIONS_AVAILABLE_TAG) {
            loadFeed()
        }
    }

    override fun onCartBottomBarOrdersClick(type: CartBottomBar.BottomBarTypes) {
        when(type){
            CartBottomBar.BottomBarTypes.TRACK_YOUR_ORDER, CartBottomBar.BottomBarTypes.TRACK_ORDER_OR_CHECKOUT -> {
                //show track your order dialog
                ActiveOrderTrackerDialog().show(supportFragmentManager, Constants.TRACK_ORDER_DIALOG_TAG)
            }
        }
    }

    override fun onBottomBarCheckoutClick() {
        afterOrderResult.launch(Intent(this, NewOrderActivity::class.java).putExtra(Constants.NEW_ORDER_IS_CHECKOUT, true))
    }

    override fun onContactUsClick() {
        val phone = viewModel.getContactUsPhoneNumber()
        Utils.callPhone(this, phone)
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


//    fun handlePb(shouldShow: Boolean) {
//        if (shouldShow) {
//            mainActPb.show()
//        } else {
//            mainActPb.hide()
//        }
//    }


    //fragment and sub features

    private fun handleNavigation(mainNavigationEvent: MainViewModel.MainNavigationEvent) {
        when(mainNavigationEvent){
           MainViewModel.MainNavigationEvent.START_LOCATION_AND_ADDRESS_ACTIVITY -> {
                updateLocationOnResult.launch(Intent(this, LocationAndAddressActivity::class.java))
           }
            MainViewModel.MainNavigationEvent.START_PAYMENT_METHOD_ACTIVITY -> {
                PaymentMethodsActivityStarter(this).startForResult(PaymentMethodsActivityStarter.Args.Builder().build())
            }
            MainViewModel.MainNavigationEvent.INITIALIZE_STRIPE -> {
                viewModel.reInitStripe(this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //change this to ActivityResultStarterCallback when stripe enables.
        if (requestCode == PaymentMethodsActivityStarter.REQUEST_CODE) {
            Log.d(NewOrderActivity.TAG, "Stripe on activity result")
            val result = PaymentMethodsActivityStarter.Result.fromIntent(data)
            val paymentMethod = result?.paymentMethod
            paymentMethod?.let{
                viewModel.updatePaymentsMethod(it)
            }
        }
    }

    private fun loadFeed() {
        loadFragment(FeedFragment.newInstance(), Constants.FEED_TAG)
        binding.mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_FEED)
    }

    private fun loadSearchFragment() {
        loadFragment(SearchFragment.newInstance(), Constants.SEARCH_TAG)
        binding.mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_SEARCH)
    }

    fun loadMyProfile() {
        loadFragment(MyProfileFragment.newInstance(), Constants.MY_PROFILE_TAG)
        binding.mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE_SETTINGS, "My Account")
    }

    fun loadEditMyProfile() {
        loadFragment(EditMyProfileFragment.newInstance(), Constants.EDIT_MY_PROFILE_TAG)
        binding.mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE_SAVE, "Hey ${viewModel.getUserName()}!")
    }

    fun loadSupport() {
        loadFragment(SupportFragment.newInstance(), Constants.SUPPORT_TAG)
        binding.mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE, getString(R.string.support_dialog_title))
    }

    fun loadReport(orderId: Long) {
        loadFragment(ReportIssueFragment.newInstance(orderId), Constants.REPORT_TAG)
        binding.mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE, "Report issue")
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
        binding.mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE, "Order Details")
    }


    fun loadSettingsFragment() {
        loadFragment(SettingsFragment.newInstance(), Constants.SETTINGS_TAG)
        binding.mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE, "Location and Communication settings")
    }

    fun loadOrderHistoryFragment() {
        loadFragment(OrdersHistoryFragment.newInstance(), Constants.ORDER_HISTORY_TAG)
        binding.mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE, "Order History")
    }


    override fun onTipDone(tipAmount: Int) {
        Toast.makeText(this, "onTipDone $tipAmount", Toast.LENGTH_SHORT).show()
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


    fun loadDishOfferedDialog() {
        NewSuggestionSuccessDialog().show(supportFragmentManager, Constants.DISH_OFFERED_TAG)
        if (getFragmentByTag(Constants.SEARCH_TAG) != null) {
            (getFragmentByTag(Constants.SEARCH_TAG) as SearchFragment).onSearchInputChanged("")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
        val timePickerBottomSheet = TimePickerBottomSheet()
        timePickerBottomSheet.show(supportFragmentManager, Constants.TIME_PICKER_BOTTOM_SHEET)
    }

    override fun onHeaderAddressClick() {
        updateLocationOnResult.launch(Intent(this, LocationAndAddressActivity::class.java))
        binding.mainActHeaderView.enableLocationClick(false)
    }


    override fun onHeaderTextChange(str: String) {
        if (getFragmentByTag(Constants.SEARCH_TAG) as SearchFragment? != null) {
            (getFragmentByTag(Constants.SEARCH_TAG) as SearchFragment).onSearchInputChanged(str)
        }
    }

    fun setHeaderViewSaveBtnClickable(isClickable: Boolean) {
        binding.mainActHeaderView.setSaveButtonClickable(isClickable)
    }


    override fun onHeaderSaveClick() {
        if (getFragmentByTag(Constants.EDIT_MY_PROFILE_TAG) != null) {
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
                        binding.mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_FEED)
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
        binding.mainActHeaderView.updateSearchTitle(str)
    }

//    fun startPaymentMethodActivity() {
//        PaymentMethodsActivityStarter(this).startForResult(
//            PaymentMethodsActivityStarter.Args.Builder()
//                .build()
//        )
//    }


    fun updateFilterUi(isFiltered: Boolean) {
        binding.mainActHeaderView.updateFilterUi(isFiltered)
    }


    fun onReportIssueDone() {
        loadFeed()
        ThankYouDialog().show(supportFragmentManager, Constants.THANK_YOU_DIALOG_TAG)
    }

    fun refreshUserUi() {
        binding.mainActHeaderView.refreshUserUi(viewModel.getCurrentEater())
    }

    companion object{
        const val TAG = "wowMainAct"
    }

    override fun onMediaUtilResult(result: MediaUtils.MediaUtilResult) {
        if (getFragmentByTag(Constants.EDIT_MY_PROFILE_TAG) != null) {
            (getFragmentByTag(Constants.EDIT_MY_PROFILE_TAG) as EditMyProfileFragment).onCameraUtilResult(result)
        }
    }



}