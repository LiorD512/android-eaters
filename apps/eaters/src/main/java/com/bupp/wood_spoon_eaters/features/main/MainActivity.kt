package com.bupp.wood_spoon_eaters.features.main

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.campaign_bottom_sheet.CampaignBottomSheet
import com.bupp.wood_spoon_eaters.common.*
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.databinding.ActivityMainBinding
import com.bupp.wood_spoon_eaters.dialogs.*
import com.bupp.wood_spoon_eaters.features.base.BaseActivity
import com.bupp.wood_spoon_eaters.features.create_profile.EditProfileActivity
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressActivity
import com.bupp.wood_spoon_eaters.features.main.abs.MainActPagerAdapter
import com.bupp.wood_spoon_eaters.features.order_checkout.OrderCheckoutActivity
import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.CustomOrderItem
import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.UpSaleNCartBottomSheet
import com.bupp.wood_spoon_eaters.features.restaurant.RestaurantActivity
import com.bupp.wood_spoon_eaters.features.reviews.review_activity.ReviewActivity
import com.bupp.wood_spoon_eaters.features.splash.SplashActivity
import com.bupp.wood_spoon_eaters.managers.CartManager
import com.bupp.wood_spoon_eaters.managers.FloatingCartEvent
import com.bupp.wood_spoon_eaters.managers.GlobalErrorManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.utils.Utils
import com.bupp.wood_spoon_eaters.views.CampaignBanner
import com.bupp.wood_spoon_eaters.views.MainActivityTabLayout
import com.bupp.wood_spoon_eaters.views.floating_buttons.WSFloatingButton
import com.eatwoodspoon.android_utils.views.setSafeOnClickListener
import com.eatwoodspoon.auth.WoodSpoonAuth
import com.stripe.android.view.PaymentMethodsActivityStarter
import com.uxcam.UXCam
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class MainActivity : BaseActivity(), HeaderView.HeaderViewListener,
    ContactUsDialog.ContactUsDialogListener,
    ShareDialog.ShareDialogListener, MediaUtils.MediaUtilListener, CampaignBanner.CampaignBannerListener,
    CampaignBottomSheet.CampaignBottomSheetListener,
    WSFloatingButton.WSFloatingButtonListener, UpSaleNCartBottomSheet.UpsaleNCartBSListener, MainActivityTabLayout.MainActivityTabLayoutListener {

    lateinit var binding: ActivityMainBinding
    private val mediaUtil = MediaUtils(this, this)
    val viewModel by viewModel<MainViewModel>()

    private val woodSpoonAuth: WoodSpoonAuth by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUi()
        initObservers()
        initMainViewPager()

        initUiRelatedProcesses()


        Log.d(TAG, "density: ${getResources().getDisplayMetrics().density}")
    }

    fun initUi() {
        binding.mainActFloatingCartBtn.setSafeOnClickListener {
            viewModel.openRestaurantFromViewCart()
        }
    }

    private fun initMainViewPager() {
        with(binding) {
            val pagerAdapter = MainActPagerAdapter(this@MainActivity)
            mainActViewPager.adapter = pagerAdapter
            mainActViewPager.offscreenPageLimit = 1
            mainActViewPager.isUserInputEnabled = false

            mainActBottomTabLayout.setViewPager(mainActViewPager, this@MainActivity)
        }
    }

    private val updateLocationOnResult = registerForActivityResult(StartActivityForResult()) { //result: ActivityResult ->
        Log.d(TAG, "Activity For Result - location")
//        if (result.resultCode == Activity.RESULT_OK) {
//            val data = result.data
//        }
    }
    private val afterOrderResult = registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
        Log.d(TAG, "Activity For Result - new order")
        //check if has order and refresh ui
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val isAfterPurchase = data?.getBooleanExtra("isAfterPurchase", false)!!
            val forceFeedRefresh = data.getBooleanExtra("refreshFeed", false)
            if (isAfterPurchase) {
                updateUiAfterOrderSuccess(result.data)
            } else if (forceFeedRefresh) {
                viewModel.forceFeedRefresh()
            }
        }
    }

    private val startCheckoutForResult = registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
        Log.d(TAG, "Activity For Result - startCheckoutForResult")
        if (result.resultCode == Activity.RESULT_OK) {
            updateUiAfterOrderSuccess(result.data)
            val data = result.data
            val editOrderClicked = data?.getBooleanExtra("editOrderClick", false)
            if (editOrderClicked!!) {
                UpSaleNCartBottomSheet().show(supportFragmentManager, Constants.UPSALE_AND_CART_BOTTOM_SHEET)
            }
        }
    }

    private fun updateUiAfterOrderSuccess(data: Intent?) {
        val isAfterPurchase = data?.getBooleanExtra("isAfterPurchase", false)
        if (isAfterPurchase!!) {
            showRateTheAppDialog()
            viewModel.checkForActiveOrder()
            viewModel.forceFeedRefresh()
            refreshActiveCampaigns()
            binding.mainActBottomTabLayout.forceOrdersClick()
        }
    }

    private fun refreshActiveCampaigns() {
        binding.mainActCampaignBanner.hide()
        viewModel.refreshActiveCampaigns()
    }

    private fun showRateTheAppDialog() {
//        Log.d(TAG, "showRateTheAppDialog")
//        RateBottomSheetManager(this)
//            .setInstallDays(3) // 3 by default
//            .setLaunchTimes(2) // 5 by default
//            .setRemindInterval(1) // 2 by default
//            .setShowAskBottomSheet(true) // True by default
//            .setShowLaterButton(true) // True by default
//            .setShowCloseButtonIcon(true) // True by default
//            .monitor()
//        RateBottomSheet.showRateBottomSheetIfMeetsConditions(
//            this,
//            listener = object : AskRateBottomSheet.ActionListener {
//                override fun onDislikeClickListener() {
//                    // Will be called when a click on the "I don't like" button is triggered
//                    Log.d(TAG, "showRateTheAppDialog - onDislikeClickListener")
//                }
//
//                override fun onRateClickListener() {
//                    Log.d(TAG, "showRateTheAppDialog - onRateClickListener")
//                }
//            }
//        )
    }


    ////////////////////////////////////////////////
    ///////       Ui processes - start       ///////
    ////////////////////////////////////////////////


    private fun initUiRelatedProcesses() {
        checkForBranchIntent()
        viewModel.checkForActiveOrder()
    }

    private fun checkForBranchIntent() {
        intent?.let {
            val chefId = intent.getLongExtra("chef_id", -1)
            val menuItemId = intent.getLongExtra("menu_item_id", -1)
            Log.d("wowMain", "branch: cook $chefId, menuItem: $menuItemId")
            if (chefId > 0) {
                viewModel.getRestaurant(chefId)
                viewModel.logDeepLinkEvent(chefId)
            }
        }
    }

    ////////////////////////////////////////////////
    ///////        Ui processes - end        ///////
    ////////////////////////////////////////////////


    private fun initObservers() {
        viewModel.globalErrorLiveData.observe(this) {
            handleError(it)
        }
        viewModel.mainNavigationEvent.observe(this) {
            handleNavigation(it)
        }
        viewModel.floatingCartBtnEvent.observe(this) {
            handleFloatingBtnEvent(it)
        }
        viewModel.startRestaurantActivity.observe(this) {
            startRestaurantActivity(it)
        }
        viewModel.getTraceableOrder.observe(this) { traceableOrders ->
            handleTraceableOrderData(traceableOrders)
        }
        viewModel.getTriggers.observe(this) { triggerEvent ->
            triggerEvent?.getContentIfNotHandled()?.let { trigger ->
                trigger.shouldRateOrder?.let { order ->
                    openReviewActivity(order)
                }
            }
        }
        viewModel.campaignLiveData.observe(this) {
            it?.let {
                handleCampaignData(it)
            }
        }
        viewModel.shareEvent.observe(this) {
            sendShareCampaign(it)
        }
    }

    fun openReviewActivity(order:Order){
        val intent = Intent(this, ReviewActivity::class.java)
        intent.putExtra(Constants.ARG_REVIEW, order)
        startActivity(intent)
    }

    private fun handleTraceableOrderData(traceableOrders: List<Order>?) {
        binding.mainActBottomTabLayout.handleOrdersIndicator(traceableOrders != null && traceableOrders.isNotEmpty())
    }

    private fun startRestaurantActivity(restaurantInitParams: RestaurantInitParams?) {
        afterOrderResult.launch(Intent(this, RestaurantActivity::class.java).putExtra(Constants.ARG_RESTAURANT, restaurantInitParams))
    }

    private fun handleError(errorData: GlobalErrorManager.GlobalError?) {
        errorData?.let {
            when (it.type) {
                GlobalErrorManager.GlobalErrorType.NETWORK_ERROR -> {
//                    Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show()
                }
                GlobalErrorManager.GlobalErrorType.GENERIC_ERROR -> {
//                    Toast.makeText(this, "Server Error", Toast.LENGTH_SHORT).show()
                }
                GlobalErrorManager.GlobalErrorType.WS_ERROR -> {
                    WSErrorDialog(it.wsError?.msg, null).show(
                        supportFragmentManager,
                        Constants.WS_ERROR_DIALOG
                    )
                }
            }
        }
    }

    private fun handleFloatingBtnEvent(event: FloatingCartEvent?) {
        event?.let {
            with(binding) {
                mainActFloatingCartBtn.setWSFloatingBtnListener(this@MainActivity)
                mainActFloatingCartBtn.updateFloatingCartButton(
                    it.restaurantName,
                    it.allOrderItemsQuantity
                )
            }
        }
    }

    override fun onFloatingCartStateChanged(isShowing: Boolean) {
        //this method triggered when Floating cart button is hide or shown - activity related screen need to update their bottom padding.
        viewModel.onFloatingCartStateChanged(isShowing)
    }

    override fun onCartDishCLick(customOrderItem: CustomOrderItem) {
        afterOrderResult.launch(Intent(this, RestaurantActivity::class.java).putExtra(Constants.ARG_DISH, customOrderItem))
    }

    override fun onGoToCheckoutClicked() {
        startCheckoutForResult.launch(Intent(this, OrderCheckoutActivity::class.java))
    }

    private fun handleCampaignData(campaigns: List<Campaign>) {
        campaigns.forEach { campaign ->
            campaign.viewTypes?.forEach { viewType ->
                when (viewType) {
                    CampaignViewType.BANNER -> {
                        binding.mainActCampaignBanner.initCampaignHeader(campaign, this)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun sendShareCampaign(shareText: String) {
        Utils.shareText(this, shareText)
    }

    override fun onCampaignDetailsClick(campaign: Campaign) {
        CampaignBottomSheet.newInstance(campaign).show(supportFragmentManager, Constants.CAMPAIGN_BOTTOM_SHEET)
    }

    override fun handleCampaignAction(campaign: Campaign) {
        when (campaign.buttonAction) {
            CampaignButtonAction.SHARE -> {
                campaign.shareUrl?.let {
                    Utils.shareText(this, it)
                }
            }
            CampaignButtonAction.ACKNOWLEDGE -> {
                //do nothing
            }
            CampaignButtonAction.JUMP_TO_LINK -> {
                //todo = add webView
            }
            else -> {}
        }
        viewModel.updateCampaignStatus(campaign, UserInteractionStatus.ENGAGED)

    }

    fun onContactUsClick() {
        val phone = viewModel.getContactUsPhoneNumber()
        Utils.callPhone(this, phone)
    }


    //fragment and sub features

    private fun handleNavigation(mainNavigationEvent: MainNavigationEvent) {
        when (mainNavigationEvent) {
            MainNavigationEvent.START_LOCATION_AND_ADDRESS_ACTIVITY -> {
                updateLocationOnResult.launch(Intent(this, LocationAndAddressActivity::class.java))
            }
            MainNavigationEvent.START_PAYMENT_METHOD_ACTIVITY -> {
                UXCam.occludeSensitiveScreen(true)
                PaymentMethodsActivityStarter(this).startForResult(
                    PaymentMethodsActivityStarter.Args.Builder().build()
                )
            }
            MainNavigationEvent.LOGOUT -> {
                val intent = Intent(this, SplashActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }
            MainNavigationEvent.LOGOUT_WITH_NEW_AUTH -> {
                val intent = Intent(this, SplashActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_IMMUTABLE)
                woodSpoonAuth.startWebLogout(
                    this,
                    pendingIntent,
                    "account_sign_out"
                )
//                finish()
            }
            MainNavigationEvent.OPEN_CAMERA_UTIL_IMAGE -> {
                mediaUtil.startPhotoFetcher()
            }
            MainNavigationEvent.START_USER_DETAILS_ACTIVITY -> {
                startActivity(EditProfileActivity.createIntent(this,  "Please finish setting up your account details before adding a payment method" ))
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PaymentMethodsActivityStarter.REQUEST_CODE -> {
                MTLogger.c(TAG, "Stripe")
                UXCam.occludeSensitiveScreen(false)
                val result = PaymentMethodsActivityStarter.Result.fromIntent(data)

                result?.let {
                    MTLogger.c(TAG, "payment method success")
                    viewModel.updatePaymentMethod(this, result.paymentMethod)
                }
            }

        }
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


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.LOCATION_PERMISSION_REQUEST_CODE -> {
            }
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


    //HeaderView Listener interface
    override fun onHeaderBackClick() {
        onBackPressed()
    }

    override fun onHeaderCloseClick() {
        onBackPressed()
    }

    override fun onMediaUtilResult(result: MediaUtils.MediaUtilResult) {
        viewModel.onMediaUtilsResultSuccess(result)
    }

    override fun onHomeTabReClicked() {
        viewModel.scrollFeedToTop()
    }

    override fun onSearchTabClicked() {
        viewModel.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_SEARCH)
        viewModel.refreshSearchData()
    }

    override fun onOrdersTabClicked() {
        viewModel.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_ORDERS)
    }

    override fun onProfileTabClicked() {
        viewModel.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_ACCOUNT)
    }

    companion object {
        const val TAG = "wowMainAct"
    }

}