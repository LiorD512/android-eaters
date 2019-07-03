package com.bupp.wood_spoon_eaters.features.main

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.dialogs.LocationChooserFragment
import com.bupp.wood_spoon_eaters.features.main.delivery_details.DeliveryDetailsFragment
import com.bupp.wood_spoon_eaters.features.main.delivery_details.sub_screens.add_new_address.AddAddressFragment
import com.bupp.wood_spoon_eaters.features.main.feed.FeedFragment
import com.bupp.wood_spoon_eaters.features.main.my_profile.MyProfileFragment
import com.bupp.wood_spoon_eaters.features.main.pick_filters.FilterFragment
import com.bupp.wood_spoon_eaters.features.main.sub_features.settings.SettingsFragment
import com.bupp.wood_spoon_eaters.features.support.SupportFragment
import com.bupp.wood_spoon_eaters.utils.Constants
import com.bupp.wood_spoon_eaters.network.google.models.AddressResponse
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity(), HeaderView.HeaderViewListener,
    LocationChooserFragment.LocationChooserFragmentListener {

    private var lastFragmentTag: String? = null
    private var currentFragmentTag: String? = null
    val viewModel by viewModel<MainViewModel>()

    private lateinit var selectedAddress: AddressResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainActHeaderView.setHeaderViewListener(this)

        loadMyProfile()
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

    fun loadPickFilters() {
        loadFragment(FilterFragment(), Constants.PICK_FILTERS_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE_DONE, "Pick Filters")
    }

    fun loadMyProfile() {
        loadFragment(MyProfileFragment.newInstance(), Constants.MY_PROFILE_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE_SETTINGS, "Hey ${viewModel.getUserName()}!")
    }


    fun loadEditMyProfile() {
//        loadFragment(EditMyProfileFragment.newInstance(), Constants.EDIT_MY_PROFILE_TAG)
//        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE_SAVE, "Hey ${viewModel.getUserName()}!")
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

    override fun onLocationSelected(selected: AddressResponse?) {
        if(getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) != null && selected != null) {
            this.selectedAddress = selected
            (getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) as AddAddressFragment).onLocationSelected(selected)
        }
    }
    //delivery details methods - end


    private fun loadSettings() {
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

    override fun onHeaderSaveClick() {
        if (getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) != null) {
            handlePb(true)
            (getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) as AddAddressFragment).saveAddressDetails()
        } else if (getFragmentByTag(Constants.DELIVERY_DETAILS_TAG) != null) {
            updateAddressTimeView()
            loadFeed()
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
            when(lastFragmentTag){
                Constants.MY_PROFILE_TAG ->{
                    loadMyProfile()
                }
                Constants.DELIVERY_DETAILS_TAG ->{
                    loadDeliveryDetails()
                }
            }
        }else{
            loadFeed()
        }
    }


    fun onNewAddressDone(location: String? = null) {
        handlePb(false)
        if(location != null){
            mainActHeaderView.setLocationTitle(location = location)
        }
        when(lastFragmentTag){
            Constants.MY_PROFILE_TAG ->{
                loadMyProfile()
            }
            Constants.DELIVERY_DETAILS_TAG ->{
                loadDeliveryDetails()
            }
        }
    }

}