package com.bupp.wood_spoon_eaters.features.main

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon.dialogs.AddressChooserDialog
import com.bupp.wood_spoon.dialogs.EditAddressDialog
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.dialogs.*
import com.bupp.wood_spoon_eaters.dialogs.locationAutoComplete.LocationChooserFragment
import com.bupp.wood_spoon_eaters.dialogs.rating_dialog.RatingsDialog
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.ActiveOrderTrackerDialog
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.checkout.CheckoutFragment
import com.bupp.wood_spoon_eaters.features.main.delivery_details.DeliveryDetailsFragment
import com.bupp.wood_spoon_eaters.features.main.delivery_details.sub_screens.add_new_address.AddAddressFragment
import com.bupp.wood_spoon_eaters.features.main.profile.edit_my_profile.EditMyProfileFragment
import com.bupp.wood_spoon_eaters.features.main.feed.FeedFragment
import com.bupp.wood_spoon_eaters.features.main.profile.my_profile.MyProfileFragment
import com.bupp.wood_spoon_eaters.features.main.order_details.OrderDetailsFragment
import com.bupp.wood_spoon_eaters.features.main.promo_code.PromoCodeFragment
import com.bupp.wood_spoon_eaters.features.main.report.ReportFragment
import com.bupp.wood_spoon_eaters.features.main.search.SearchFragment
import com.bupp.wood_spoon_eaters.features.main.sub_features.settings.SettingsFragment
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderActivity
import com.bupp.wood_spoon_eaters.features.support.SupportFragment
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.network.google.models.GoogleAddressResponse
import com.bupp.wood_spoon_eaters.utils.Constants
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity(), HeaderView.HeaderViewListener,
    LocationChooserFragment.LocationChooserFragmentListener, AddressChooserDialog.AddressChooserDialogListener,
    NoDeliveryToAddressDialog.NoDeliveryToAddressDialogListener, TipCourierDialog.TipCourierDialogListener,
    StartNewCartDialog.StartNewCartDialogListener, ContactUsDialog.ContactUsDialogListener,
    ShareDialog.ShareDialogListener, RateLastOrderDialog.RateLastOrderDialogListener, EditAddressDialog.EditAddressDialogListener {



    //    private val curShowingDialogs: ArrayList<SingleDishFragment> = arrayListOf()
    private var lastFragmentTag: String? = null
    private var currentFragmentTag: String? = null
    val viewModel by viewModel<MainViewModel>()

    private lateinit var selectedGoogleAddress: GoogleAddressResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActHeaderView.setHeaderViewListener(this)

        initObservers()
        startLocationUpdates()
        updateAddressTimeView()
        loadFeed()

        checkForActiveOrder()
    }

    private fun checkForActiveOrder() {
        viewModel.checkForActiveOrder()
    }

    private fun initObservers() {
        viewModel.addressUpdateEvent.observe(this, Observer { newAddressEvent ->
            if (newAddressEvent != null) {
                if(newAddressEvent.currentAddress != null){
                    updateAddressTimeView()
                }else{

                }
            }
        })

        viewModel.getActiveOrders.observe(this, Observer { ordersEvent ->
            if (ordersEvent.isSuccess && ordersEvent.orders != null) {
                    openActiveOrdersDialog(ordersEvent.orders)
                }else{

                }
            }
        )
    }

    private fun openActiveOrdersDialog(orders: ArrayList<Order>) {
        ActiveOrderTrackerDialog(orders).show(supportFragmentManager, Constants.TRACK_ORDER_DIALOG_TAG)
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
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE_SETTINGS, "Hey ${viewModel.getUserName()}!")
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

    fun loadPromoCode() {
        loadFragment(PromoCodeFragment(), Constants.PROMO_CODE_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE_SAVE, "Add Promo Code")
    }

    fun loadReport() {
        loadFragment(ReportFragment(), Constants.REPORT_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE, "Report issue")
    }

    fun loadOrderDetails() {
        loadFragment(OrderDetailsFragment(), Constants.ORDER_DETAILS_TAG)
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
        AddressChooserDialog(this, viewModel.getListOfAddresses(), viewModel.getChosenAddress()).show(supportFragmentManager, Constants.ADDRESS_DIALOG_TAG)
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

    fun loadStartNewCartDialog(cookInCart: Cook, newCook: Cook) {
        StartNewCartDialog(this, cookInCart, newCook).show(supportFragmentManager, Constants.START_NEW_CART_DIALOG_TAG)
    }

    fun loadDishSoldOutDialog() {
        DishSoldOutDialog().show(supportFragmentManager, Constants.DISH_SOLD_OUT_DIALOG_TAG)
    }

    fun loadDishOfferedDialog() {
        NewSuggestionSuccessDialog().show(supportFragmentManager, Constants.DISH_OFFERED_TAG)
    }

    fun loadShareDialog() {
        ShareDialog(this).show(supportFragmentManager, Constants.SHARE_DIALOG_TAG)
    }

//    fun loadTrackOrder() {
//        TrackOrderFragment(this)
//            .show(supportFragmentManager, Constants.TRACK_ORDER_DIALOG_TAG)
//    }

    fun loadRateLastOrder() {
        RateLastOrderDialog(this).show(supportFragmentManager, Constants.RATE_LAST_ORDER_DIALOG_TAG)
    }

    fun loadRatings() {
        RatingsDialog().show(supportFragmentManager, Constants.RATINGS_DIALOG_TAG)
    }

    override fun onDoneRateClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //delivery details methods - end

    fun loadSettings() {
        loadFragment(SettingsFragment(), Constants.SETTINGS_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE, "Location and Communication settings")
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
        }
    }

    fun sendSmsText() {
        val smsIntent =
            Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + getString(R.string.default_bupp_phone_number)))
        smsIntent.putExtra("sms_body", getString(R.string.support_frag_sms_sentence))
        this.applicationContext.startActivity(smsIntent)
    }

    fun callPhoneNumber() {
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
        loadSettings()
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

//    override fun onDishClick(itemId: Long) {
//        //when user click other dish inside singleDishFragment
////        loadSingleDishDetails(itemId)
//        startActivity(Intent(this, NewOrderActivity::class.java).putExtra("menuItemId",itemId))
//
//    }

    fun loadSingleDishDetails(id: Long) {
        startActivityForResult(Intent(this, NewOrderActivity::class.java).putExtra("menuItemId",id), Constants.NEW_ORDER_REQUEST_CODE)
//        val singleDishDialog = SingleDishFragment.newInstance(id, this)
//        singleDishDialog.show(supportFragmentManager, Constants.SINGLE_DISH_TAG)
//        Log.d("wowDialogs","adding:  ${singleDishDialog.id}")
//        curShowingDialogs.add(singleDishDialog)
    }

//    fun closeAllDialogs(){
//        for(item in curShowingDialogs){
//            Log.d("wowDialogs","removing:  ${item.id}")
////            item.dismiss()
//        }
//        curShowingDialogs.clear()
//    }

//    override fun onCheckout() {
//        loadCheckout()
//    }
}