package com.bupp.wood_spoon_eaters.features.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon.dialogs.AddressChooserDialog
import com.bupp.wood_spoon.dialogs.EditAddressDialog
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.dialogs.*
import com.bupp.wood_spoon_eaters.dialogs.locationAutoComplete.LocationChooserFragment
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.ActiveOrderTrackerDialog
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.checkout.CheckoutFragment
import com.bupp.wood_spoon_eaters.features.main.delivery_details.DeliveryDetailsFragment
import com.bupp.wood_spoon_eaters.features.main.delivery_details.sub_screens.add_new_address.AddAddressFragment
import com.bupp.wood_spoon_eaters.features.main.profile.edit_my_profile.EditMyProfileFragment
import com.bupp.wood_spoon_eaters.features.main.feed.FeedFragment
import com.bupp.wood_spoon_eaters.features.main.profile.my_profile.MyProfileFragment
import com.bupp.wood_spoon_eaters.features.main.order_details.OrderDetailsFragment
import com.bupp.wood_spoon_eaters.features.main.order_history.OrdersHistoryFragment
import com.bupp.wood_spoon_eaters.features.main.promo_code.PromoCodeFragment
import com.bupp.wood_spoon_eaters.features.main.report_issue.ReportIssueFragment
import com.bupp.wood_spoon_eaters.features.main.search.SearchFragment
import com.bupp.wood_spoon_eaters.features.main.sub_features.settings.SettingsFragment
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderActivity
import com.bupp.wood_spoon_eaters.features.support.SupportFragment
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.network.google.models.GoogleAddressResponse
import com.bupp.wood_spoon_eaters.utils.Constants
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.distribute.Distribute
import com.stripe.android.model.PaymentMethod
import com.stripe.android.view.PaymentMethodsActivity
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), HeaderView.HeaderViewListener,
    LocationChooserFragment.LocationChooserFragmentListener, AddressChooserDialog.AddressChooserDialogListener,
    NoDeliveryToAddressDialog.NoDeliveryToAddressDialogListener, TipCourierDialog.TipCourierDialogListener,
    StartNewCartDialog.StartNewCartDialogListener, ContactUsDialog.ContactUsDialogListener,
    ShareDialog.ShareDialogListener, EditAddressDialog.EditAddressDialogListener,
    RateLastOrderDialog.RateDialogListener, ActiveOrderTrackerDialog.ActiveOrderTrackerDialogListener {

    //    private val curShowingDialogs: ArrayList<SingleDishFragment> = arrayListOf()
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


        initObservers()
        startLocationUpdates()
        updateAddressTimeView()
        loadFeed()

        checkForActiveOrder()
        checkForTriggers()
    }

    fun checkForActiveOrder() {
        viewModel.checkForActiveOrder()
    }

    fun checkForTriggers() {
        viewModel.checkForTriggers()
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
                handleActiveOrderBottomBar(true)
                openActiveOrdersDialog(ordersEvent.orders!!)
            } else {
                handleActiveOrderBottomBar(false)
            }
        })

        viewModel.getTriggers.observe(this, Observer { triggerEvent ->
            if (triggerEvent.isSuccess) {
                Log.d("wowMain","found should rate id !: ${triggerEvent.trigger?.shouldRateId}")
                RateLastOrderDialog(triggerEvent.trigger?.shouldRateId!!, this)
            }
        })
    }

    private fun handleActiveOrderBottomBar(shouldShow: Boolean) {
        if (shouldShow) {
            Log.d("wowMain", "show bottom bar")
            mainActContainer.setPadding(0,0,0, resources.getDimension(R.dimen.header_view_height).toInt())
            activeOrderBottomBarTitle.visibility = View.VISIBLE
            activeOrderBottomBarTitle.setOnClickListener {
                mainActPb.show()
                viewModel.checkForActiveOrder()
            }
        } else {
            Log.d("wowMain", "hide bottom bar")
            mainActContainer.setPadding(0,0,0,0)
            activeOrderBottomBarTitle.visibility = View.GONE
        }
    }

    private fun openActiveOrdersDialog(orders: ArrayList<Order>) {
        ActiveOrderTrackerDialog(orders, this).show(supportFragmentManager, Constants.TRACK_ORDER_DIALOG_TAG)
    }

    override fun onContactUsClick() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), Constants.PHONE_CALL_PERMISSION_REQUEST_CODE)
            }
        } else {
            callPhone()
        }
    }


    fun callPhone(){
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getString(R.string.default_bupp_phone_number)))
        startActivity(intent)
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
        loadFragment(FeedFragment(), Constants.FEED_TAG)
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
        loadFragment(SupportFragment(), Constants.SUPPORT_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE, getString(R.string.support_dialog_title))
    }

    fun loadCheckout() {
//        currentFragmentTag = Constants.CHECKOUT_TAG
//        CheckoutFragment(this).show(supportFragmentManager, Constants.CHECKOUT_TAG)
    }

//    override fun onCheckoutDone() {
//
//    }

    fun loadPromoCodeDialog() {
        loadFragment(PromoCodeFragment(), Constants.PROMO_CODE_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE_SAVE, "Add Promo Code")
    }

    fun loadReport(orderId: Long) {
        loadFragment(ReportIssueFragment.newInstance(orderId), Constants.REPORT_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE, "Report issue")
    }

    fun loadRateOrder(orderId: Long) {
        RateLastOrderDialog(orderId, this).show(supportFragmentManager, Constants.RATE_LAST_ORDER_DIALOG_TAG)
    }

    override fun onRatingDone() {
        ThankYouDialog().show(supportFragmentManager, Constants.THANK_YOU_DIALOG_TAG)
        if (getFragmentByTag(Constants.ORDER_HISTORY_TAG) != null) {
            (getFragmentByTag(Constants.ORDER_HISTORY_TAG) as OrdersHistoryFragment).onRatingDone()
        }
    }

    fun loadOrderDetails(orderId: Long) {
        loadFragment(OrderDetailsFragment(orderId), Constants.ORDER_DETAILS_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE, "Order Details")
    }

    //delivery details methods
    fun loadDeliveryDetails() {
        loadFragment(DeliveryDetailsFragment.newInstance(), Constants.DELIVERY_DETAILS_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE, "Delivery Details")
    }

    fun loadAddNewAddress() {
        loadFragment(AddAddressFragment(null), Constants.ADD_NEW_ADDRESS_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE_SAVE, "Select Your Delivery Address")
    }

    fun loadLocationChooser(input: String?) {
        LocationChooserFragment(this, input)
            .show(supportFragmentManager, Constants.LOCATION_CHOOSER_TAG)
    }

    fun loadSettingsFragment() {
        loadFragment(SettingsFragment(), Constants.SETTINGS_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE, "Location and Communication settings")
    }

    fun loadOrderHistoryFragment() {
        loadFragment(OrdersHistoryFragment(), Constants.ORDER_HISTORY_TAG)
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

    //address dialog interface
    override fun onAddressChoose(address: Address) {
        Toast.makeText(this, "Address selected is " + address.streetLine1, Toast.LENGTH_SHORT).show()
        viewModel.setChosenAddress(address)
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
            Constants.CHECKOUT_TAG -> {
                if (getFragmentByTag(Constants.CHECKOUT_TAG) as CheckoutFragment? != null) {
                    (getFragmentByTag(Constants.CHECKOUT_TAG) as CheckoutFragment).onAddressChooserSelected()
                }
            }
        }
        setHeaderViewLocationDetails(viewModel.getLastOrderTime(), address)
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
    fun loadAddressesDialog() {
        AddressChooserDialog(this, viewModel.getListOfAddresses(), viewModel.getChosenAddress()).show(
            supportFragmentManager,
            Constants.ADDRESS_DIALOG_TAG
        )
    }

    override fun onAddressMenuClick(address: Address) {
        EditAddressDialog(address, this).show(supportFragmentManager, Constants.EDIT_ADDRESS_DIALOG)
    }

    override fun onEditAddress(address: Address) {
        loadFragment(AddAddressFragment(address), Constants.ADD_NEW_ADDRESS_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE_SAVE, "Select Your Delivery Address")
    }

    override fun onAddAddress() {
        loadAddNewAddress()
    }


    fun loadNoDeliveryToAddressDialog() {
        NoDeliveryToAddressDialog(this).show(supportFragmentManager, Constants.DELIVERY_TO_ADDRESS_DIALOG_TAG)
    }

    fun loadTipCourierDialog() {
        TipCourierDialog(this).show(supportFragmentManager, Constants.TIP_COURIER_DIALOG_TAG)
    }

    fun loadContactUsDialog() {
        ContactUsDialog(this).show(supportFragmentManager, Constants.CONTACT_US_DIALOG_TAG)
    }

    fun loadThankYouDialog() {
        ThankYouDialog().show(supportFragmentManager, Constants.THANK_YOU_DIALOG_TAG)
    }

    fun loadDishSoldOutDialog() {
        DishSoldOutDialog().show(supportFragmentManager, Constants.DISH_SOLD_OUT_DIALOG_TAG)
    }

    fun loadDishOfferedDialog() {
        NewSuggestionSuccessDialog().show(supportFragmentManager, Constants.DISH_OFFERED_TAG)
        if(getFragmentByTag(Constants.SEARCH_TAG) != null){
            (getFragmentByTag(Constants.SEARCH_TAG) as SearchFragment).onSearchInputChanged("")
        }
    }

    fun loadShareDialog() {
        ShareDialog(this).show(supportFragmentManager, Constants.SHARE_DIALOG_TAG)
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
                    callPhone()
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
        this.applicationContext.startActivity(smsIntent)
    }

    fun callPhoneNumber() {
        val phone = "+16179096185"
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:" + getString(R.string.default_bupp_phone_number))
        this.applicationContext.startActivity(dialIntent)
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
        if (getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) != null) {
            (getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) as AddAddressFragment).saveAddressDetails()
        } else if (getFragmentByTag(Constants.EDIT_MY_PROFILE_TAG) != null) {
            handlePb(true)
            (getFragmentByTag(Constants.EDIT_MY_PROFILE_TAG) as EditMyProfileFragment).saveEaterDetails()
        } else if (getFragmentByTag(Constants.PROMO_CODE_TAG) != null) {
            handlePb(true)
            (getFragmentByTag(Constants.PROMO_CODE_TAG) as PromoCodeFragment).savePromoCode()
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
            Constants.SETTINGS_TAG, Constants.EDIT_MY_PROFILE_TAG -> {
                loadMyProfile()
            }
            Constants.DELIVERY_DETAILS_TAG -> {
                updateAddressTimeView()
                loadFeed()
            }

            else -> {
                loadFeed()
            }

        }
    }


    fun onNewAddressDone(location: String? = null) {
        handlePb(false)
        if (location != null) {
            updateAddressTimeView()
        }
        when (lastFragmentTag) {
            Constants.MY_PROFILE_TAG -> {
                loadMyProfile()
            }
            Constants.DELIVERY_DETAILS_TAG -> {
                loadDeliveryDetails()
            }
        }
    }

    fun updateSearchInput(str: String) {
        mainActHeaderView.updateSearchInput(str)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
                Constants.NEW_ORDER_REQUEST_CODE -> {
//                    closeAllDialogs()
                    loadFeed()
                    checkForActiveOrder()
                }
            }
        }
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


    fun onReportIssueDone() {
        loadFeed()
        ThankYouDialog().show(supportFragmentManager, Constants.THANK_YOU_DIALOG_TAG)
    }



}