package com.bupp.wood_spoon_eaters.features.main

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.androidadvance.topsnackbar.TSnackbar
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.custom_views.orders_bottom_bar.OrdersBottomBar
import com.bupp.wood_spoon_eaters.dialogs.*
import com.bupp.wood_spoon_eaters.dialogs.locationAutoComplete.LocationChooserFragment
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.ActiveOrderTrackerDialog
import com.bupp.wood_spoon_eaters.features.address_and_location.AddressChooserActivity
import com.bupp.wood_spoon_eaters.features.events.EventActivity
import com.bupp.wood_spoon_eaters.features.main.cook_profile.CookProfileDialog
import com.bupp.wood_spoon_eaters.features.main.delivery_details.DeliveryDetailsFragment
import com.bupp.wood_spoon_eaters.features.main.delivery_details.sub_screens.add_new_address.AddAddressFragment
import com.bupp.wood_spoon_eaters.features.main.profile.edit_my_profile.EditMyProfileFragment
import com.bupp.wood_spoon_eaters.features.main.feed.FeedFragment
import com.bupp.wood_spoon_eaters.features.main.profile.my_profile.MyProfileFragment
import com.bupp.wood_spoon_eaters.features.main.order_details.OrderDetailsFragment
import com.bupp.wood_spoon_eaters.features.main.order_history.OrdersHistoryFragment
import com.bupp.wood_spoon_eaters.features.main.report_issue.ReportIssueFragment
import com.bupp.wood_spoon_eaters.features.main.search.SearchFragment
import com.bupp.wood_spoon_eaters.features.main.settings.SettingsFragment
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderActivity
import com.bupp.wood_spoon_eaters.features.support.SupportFragment
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.network.google.models.GoogleAddressResponse
import com.bupp.wood_spoon_eaters.utils.Constants
import com.bupp.wood_spoon_eaters.utils.Utils
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.distribute.Distribute
import com.stripe.android.model.PaymentMethod
import com.stripe.android.view.PaymentMethodsActivity
import com.stripe.android.view.PaymentMethodsActivityStarter
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.android.synthetic.main.single_dish_fragment_dialog_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), HeaderView.HeaderViewListener,
    LocationChooserFragment.LocationChooserFragmentListener,
    NoDeliveryToAddressDialog.NoDeliveryToAddressDialogListener, TipCourierDialog.TipCourierDialogListener,
    StartNewCartDialog.StartNewCartDialogListener, ContactUsDialog.ContactUsDialogListener,
    ShareDialog.ShareDialogListener,
    RateLastOrderDialog.RateDialogListener, ActiveOrderTrackerDialog.ActiveOrderTrackerDialogListener,
    OrdersBottomBar.OrderBottomBatListener, CookProfileDialog.CookProfileDialogListener {


    private var lastFragmentTag: String? = null
    private var currentFragmentTag: String? = null
    val viewModel by viewModel<MainViewModel>()

    private lateinit var selectedGoogleAddress: GoogleAddressResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCenter.start(
            application, "1995d4eb-7e59-44b8-8832-6550bd7752ff",
            Analytics::class.java, Crashes::class.java, Distribute::class.java
        )

        mainActHeaderView.setHeaderViewListener(this, viewModel.getCurrentEater())
        mainActOrdersBB.setOrdersBottomBarListener(this)

        initObservers()
        startLocationUpdates()
        updateAddressTimeView()
        loadFeed()

//        checkForActiveOrder()
        checkForTriggers()
        checkForBranchIntent()

        initFcm()
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
                    handlePb(true)
                    Log.d("wowMain", "brnach intent observing address change")
                    viewModel.waitingForAddressAction = true
                    viewModel.addressUpdateActionEvent.observe(this, Observer { newAddressEvent ->
                        Log.d("wowMain", "brnach intent observing address - ON CHANGE")
                        if (newAddressEvent != null) {
                            handlePb(false)
                            loadNewOrderActivity(menuItemId)
                        }
                    })
                }
            }
        }
    }

    override fun onDishClick(menuItemId: Long) {
        loadNewOrderActivity(menuItemId)
    }

    fun initFcm() {
        viewModel.initFcmListener()
    }

    fun checkForActiveOrder() {
        viewModel.checkForActiveOrder()
    }

    fun checkForTriggers() {
        viewModel.checkForTriggers()
    }

    fun checkCartStatus() {
        viewModel.checkCartStatus()
    }

    private fun initObservers() {
        viewModel.addressUpdateEvent.observe(this, Observer { newAddressEvent ->
            if (newAddressEvent != null) {
                if (newAddressEvent.currentAddress != null) {
                    updateAddressTimeView()
                } else {

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
            feedFragPb.hide()
            if (event.isSuccess) {
                CookProfileDialog(this, event.cook!!).show(supportFragmentManager, Constants.COOK_PROFILE_DIALOG_TAG)
            }
        })
        viewModel.noUserLocationEvent.observe(this, Observer {
            when(it){
                MainViewModel.NoLocationUiEvent.DEVICE_LOCATION_OFF -> {
                    handleDeviceLocationOff()
                }
                MainViewModel.NoLocationUiEvent.NO_LOCATIONS_SAVED -> {
                    NoLocationsDialog().show(supportFragmentManager, Constants.NO_LOCATION_DIALOG)
                }
            }
        })
    }

    private fun handleDeviceLocationOff() {
        val snackbar = TSnackbar.make(
            mainActMainLayout,
            R.string.device_location_off_alerter_body,
            TSnackbar.LENGTH_LONG
        )
        val snackBarView = snackbar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.teal_blue))
        val textView = snackBarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text) as TextView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(R.style.SemiBold13Dark)
        }
        textView.setTextColor(ContextCompat.getColor(this, R.color.white))
        snackBarView.setOnClickListener { openDeviceLocationSettings() }
        snackbar.show()
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
        ActiveOrderTrackerDialog(orders, this).show(supportFragmentManager, Constants.TRACK_ORDER_DIALOG_TAG)
    }

    override fun onContactUsClick() {
        Utils.callPhone(this)
    }

    fun startLocationUpdates() {
        viewModel.startLocationUpdates(this)
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopLocationUpdates()
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

    private fun loadFeed() {
        loadFragment(FeedFragment.newInstance(), Constants.FEED_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_FEED)
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
        loadFragment(DeliveryDetailsFragment.newInstance(), Constants.DELIVERY_DETAILS_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE, "Delivery Details")
    }


    fun loadSettingsFragment() {
        loadFragment(SettingsFragment.newInstance(), Constants.SETTINGS_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE, "Location and Communication settings")
    }

    fun loadOrderHistoryFragment() {
        loadFragment(OrdersHistoryFragment.newInstance(), Constants.ORDER_HISTORY_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE, "Order History")
    }


    override fun onLocationSelected(selected: GoogleAddressResponse?) {
        //on LocationChoosertFragment result
        if (getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) != null && selected != null) {
            this.selectedGoogleAddress = selected
            (getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) as AddAddressFragment).onLocationSelected(selected)
        } else if (getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) != null && selected != null) {
            this.selectedGoogleAddress = selected
//            (getFragmentByTag(Constants.ADDRESS_DIALOG_TAG) as AddressChooserDialog).addAddress(selected)
            Toast.makeText(this, "What should we do here", Toast.LENGTH_SHORT).show()
        }
    }

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
        callPhoneNumber()
    }

    override fun onSmsSupportClick() {
        sendSmsText()
    }

    override fun onShareClick() {
        Toast.makeText(this, "onShareClick", Toast.LENGTH_SHORT).show()
    }


    //load dialogs
    fun openAddressChooser() {
        startActivityForResult(Intent(this, AddressChooserActivity::class.java), Constants.ADDRESS_CHOOSER_REQUEST_CODE)
    }


    fun loadDishOfferedDialog() {
        NewSuggestionSuccessDialog().show(supportFragmentManager, Constants.DISH_OFFERED_TAG)
        if (getFragmentByTag(Constants.SEARCH_TAG) != null) {
            (getFragmentByTag(Constants.SEARCH_TAG) as SearchFragment).onSearchInputChanged("")
        }
    }


    fun setHeaderViewLocationDetails(time: String? = null, location: Address? = null) {
        mainActHeaderView.setLocationTitle(time, location?.streetLine1)
    }


    override
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Constants.LOCATION_PERMISSION_REQUEST_CODE -> {
                Log.d("wowMainVM", "onRequestPermissionsResult: LOCATION_PERMISSION_REQUEST_CODE")
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.startLocationUpdates(this)
                }
            }
            Constants.PHONE_CALL_PERMISSION_REQUEST_CODE -> {
                Log.d("wowMainVM", "onRequestPermissionsResult: LOCATION_PERMISSION_REQUEST_CODE")
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay!
                    Utils.callPhone(this)
                } else {
                    // permission denied, boo! Disable the
                    // functionality
                }
                return
            }
        }
    }

    fun sendSmsText() {
        val smsIntent =
            Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + getString(R.string.default_bupp_phone_number)))
        smsIntent.putExtra("sms_body", getString(R.string.support_frag_sms_sentence))
        startActivity(smsIntent)
    }

    fun callPhoneNumber() {
        val phone = "+16179096185"
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:" + getString(R.string.default_bupp_phone_number))
        startActivity(dialIntent)
    }


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

    override fun onHeaderDoneClick() {

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
            handlePb(true)
            (getFragmentByTag(Constants.EDIT_MY_PROFILE_TAG) as EditMyProfileFragment).saveEaterDetails()
        }
    }

    override fun onHeaderProfileClick() {
        loadMyProfile()
    }

    private fun updateAddressTimeView() {
        setHeaderViewLocationDetails(viewModel.getLastOrderTime(), viewModel.getCurrentAddress())
    }

    override fun onHeaderAddressAndTimeClick() {
        loadDeliveryDetails()
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
                updateAddressTimeView()
                loadFeed()
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
        PaymentMethodsActivityStarter(this).startForResult()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.NEW_ORDER_REQUEST_CODE) {
            loadFeed()
            checkForActiveOrder()
            checkCartStatus()
        }
        if (requestCode == Constants.EVENT_ACTIVITY_REQUEST_CODE) {
//            viewModel.disableEventData()
            checkForActiveOrder()
        }
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    val resultUri = result.uri

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
                            result.paymentMethod?.let{
                                (getFragmentByTag(Constants.MY_PROFILE_TAG) as MyProfileFragment).updateCustomerPaymentMethod(it)
                            }
                        }
                    }
                }
                Constants.ADDRESS_CHOOSER_REQUEST_CODE -> {
                    Log.d("wowMianActivity", "result ADDRESS_CHOOSER_REQUEST_CODE success")
                    handlePb(false)
                    updateAddressTimeView()

                    when (currentFragmentTag) {
                        Constants.DELIVERY_DETAILS_TAG -> {
                            if (getFragmentByTag(Constants.DELIVERY_DETAILS_TAG) as DeliveryDetailsFragment? != null) {
                                (getFragmentByTag(Constants.DELIVERY_DETAILS_TAG) as DeliveryDetailsFragment).onAddressChooserSelected()
                            }
                        }
                        Constants.MY_PROFILE_TAG -> {
                            if (getFragmentByTag(Constants.MY_PROFILE_TAG) as MyProfileFragment? != null) {
                                (getFragmentByTag(Constants.MY_PROFILE_TAG) as MyProfileFragment).onAddressChooserSelected()
                            }
                        }
                    }
                }
            }
        }
        updateAddressTimeView()
    }


    fun updateFilterUi(isFiltered: Boolean) {
        mainActHeaderView.updateFilterUi(isFiltered)
    }


    fun loadNewOrderActivity(id: Long) {
        startActivityForResult(
            Intent(this, NewOrderActivity::class.java).putExtra("menuItemId", id),
            Constants.NEW_ORDER_REQUEST_CODE
        )
    }

    fun loadNewOrderActivityCheckOut() {
        startActivityForResult(
            Intent(this, NewOrderActivity::class.java).putExtra("isCheckout", true),
            Constants.NEW_ORDER_REQUEST_CODE
        )
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
        startActivity(Intent(this, EventActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        checkForActiveOrder()
    }


}