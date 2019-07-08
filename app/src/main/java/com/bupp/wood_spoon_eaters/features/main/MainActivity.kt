package com.bupp.wood_spoon_eaters.features.main

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon.dialogs.AddressChooserDialog
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.dialogs.*
import com.bupp.wood_spoon_eaters.features.main.delivery_details.DeliveryDetailsFragment
import com.bupp.wood_spoon_eaters.features.main.delivery_details.sub_screens.add_new_address.AddAddressFragment
import com.bupp.wood_spoon_eaters.features.main.edit_my_profile.EditMyProfileFragment
import com.bupp.wood_spoon_eaters.features.main.feed.FeedFragment
import com.bupp.wood_spoon_eaters.features.main.my_profile.MyProfileFragment
import com.bupp.wood_spoon_eaters.features.main.filter.FilterFragment
import com.bupp.wood_spoon_eaters.features.main.search.SearchFragment
import com.bupp.wood_spoon_eaters.features.main.sub_features.settings.SettingsFragment
import com.bupp.wood_spoon_eaters.features.support.SupportFragment
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.network.google.models.AddressResponse
import com.bupp.wood_spoon_eaters.utils.Constants
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel



class MainActivity : AppCompatActivity(), HeaderView.HeaderViewListener,
    LocationChooserFragment.LocationChooserFragmentListener, AddressChooserDialog.AddressChooserDialogListener,
    NoDeliveryToAddressDialog.NoDeliveryToAddressDialogListener, TipCourierDialog.TipCourierDialogListener,
    StartNewCartDialog.StartNewCartDialogListener{

    private var lastFragmentTag: String? = null
    private var currentFragmentTag: String? = null
    val viewModel by viewModel<MainViewModel>()

    private lateinit var selectedAddress: AddressResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActHeaderView.setHeaderViewListener(this)


        loadFeed()
        updateAddressTimeView()
    }

    private fun loadFragment(fragment: Fragment, tag: String) {
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
        setHeaderViewLocationDetails(viewModel.getLastOrderTime(), viewModel.getLastOrderAddress())
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_FEED)
    }

    private fun loadSearchFragment() {
        loadFragment(SearchFragment.newInstance(), Constants.SEARCH_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_SEARCH)
    }

    fun loadPickFilters() {
        loadFragment(FilterFragment(), Constants.PICK_FILTERS_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE_DONE, "Pick Filters")
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

    //delivery details methods
    fun loadDeliveryDetails() {
        loadFragment(DeliveryDetailsFragment.newInstance(), Constants.DELIVERY_DETAILS_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE_SAVE, "Delivery Details")
    }

    fun loadAddNewAddress() {
        loadFragment(AddAddressFragment(), Constants.ADD_NEW_ADDRESS_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE_SAVE, "Select Your Delivery Address")
    }

    fun loadLocationChooser() {
        LocationChooserFragment(this, null).show(supportFragmentManager, Constants.LOCATION_CHOOSER_TAG)
    }

    override fun onAddressChoose(address: Address) {
        Toast.makeText(this, "Address selected is " + address.streetLine1, Toast.LENGTH_SHORT).show()
        viewModel.setOrderAddress(address)
        setHeaderViewLocationDetails(viewModel.getLastOrderTime(), address.streetLine1)
    }

    override fun onAddAddress() {
        loadAddNewAddress()
    }

    override fun onLocationSelected(selected: AddressResponse?) {
        if (getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) != null && selected != null) {
            this.selectedAddress = selected
            (getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) as AddAddressFragment).onLocationSelected(selected)
        } else if (getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) != null && selected != null) {
            this.selectedAddress = selected
//            (getFragmentByTag(Constants.ADDRESS_DIALOG_TAG) as AddressChooserDialog).addAddress(selected)
            Toast.makeText(this,"What should we do here", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onChangeAddressClick() {
        if(viewModel.getListOfAddresses() != null && viewModel.getListOfAddresses()!!.size > 1){
            loadAddressesDialog()
        }else{
            loadAddNewAddress()
        }
    }

    override fun onTipDone(tipAmount: Int) {
        Toast.makeText(this,"onTipDone $tipAmount", Toast.LENGTH_SHORT).show()
    }

    override fun onNewCartClick() {
        Toast.makeText(this,"onNewCartClick", Toast.LENGTH_SHORT).show()
    }



    //load dialogs
    fun loadAddressesDialog() {
        AddressChooserDialog(this, viewModel.getListOfAddresses(),viewModel.getOrderAddress()).show(
            supportFragmentManager,
            Constants.ADDRESS_DIALOG_TAG
        )
    }


    fun loadNoDeliveryToAddressDialog(){
        NoDeliveryToAddressDialog(this).show(supportFragmentManager,Constants.DELIVERY_TO_ADDRESS_DIALOG_TAG)
    }

    fun loadTipCourierDialog(){
        TipCourierDialog(this).show(supportFragmentManager,Constants.TIP_COURIER_DIALOG_TAG)
    }

    fun loadContactUsDialog(){
        ContactUsDialog().show(supportFragmentManager,Constants.CONTACT_US_DIALOG_TAG)
    }

    fun loadThankYouDialog(){
        ThankYouDialog().show(supportFragmentManager,Constants.THANK_YOU_DIALOG_TAG)
    }

    fun loadStartNewCartDialog(cookInCart: Cook, newCook:Cook){
        StartNewCartDialog(this, cookInCart,newCook).show(supportFragmentManager,Constants.START_NEW_CART_DIALOG_TAG)
    }

    fun loadDishSoldOutDialog(){
        DishSoldOutDialog().show(supportFragmentManager,Constants.DISH_SOLD_OUT_DIALOG_TAG)
    }

    fun loadDishOfferedDialog() {
        NewSuggestionSuccessDialog().show(supportFragmentManager,Constants.DISH_OFFERED_TAG)
    }






    //delivery details methods - end

    fun loadSettings() {
        loadFragment(SettingsFragment(), Constants.SETTINGS_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE, "Location and Communication settings")
    }


    fun setHeaderViewLocationDetails(time: String? = null, location: String? = null) {
        mainActHeaderView.setLocationTitle(time, location)
    }


    override
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Constants.LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadLocationChooser()
                }
            }
        }
    }

    fun sendSmsText() {
        val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + getString(R.string.default_bupp_phone_number)))
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
            handlePb(true)
            (getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) as AddAddressFragment).saveAddressDetails()
        } else if (getFragmentByTag(Constants.DELIVERY_DETAILS_TAG) != null) {
            updateAddressTimeView()
            loadFeed()
        } else if (getFragmentByTag(Constants.EDIT_MY_PROFILE_TAG) != null) {
            handlePb(true)
            (getFragmentByTag(Constants.EDIT_MY_PROFILE_TAG) as EditMyProfileFragment).saveEaterDetails()
        }
    }

    override fun onHeaderProfileClick() {
        loadMyProfile()
    }

    private fun updateAddressTimeView() {
        setHeaderViewLocationDetails(viewModel.getLastOrderTime(), viewModel.getLastOrderAddress())
    }

    override fun onHeaderAddressAndTimeClick() {
        loadDeliveryDetails()
    }

    override fun onBackPressed() {
        if (getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) != null) {
            when (lastFragmentTag) {
                Constants.MY_PROFILE_TAG -> {
                    loadMyProfile()
                }
                Constants.DELIVERY_DETAILS_TAG -> {
                    loadDeliveryDetails()
                }
            }
        } else if (getFragmentByTag(Constants.SETTINGS_TAG) != null) {
            loadMyProfile()
        } else if (getFragmentByTag(Constants.EDIT_MY_PROFILE_TAG) != null) {
            loadMyProfile()
        } else {
            loadFeed()
        }
    }


    fun onNewAddressDone(location: String? = null) {
        handlePb(false)
        if (location != null) {
            mainActHeaderView.setLocationTitle(location = location)
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
            }
        }
    }
}