package com.bupp.wood_spoon_eaters.features.main

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.main.delivery_details.DeliveryDetailsFragment
import com.bupp.wood_spoon_eaters.features.main.feed.FeedFragment
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.features.main.delivery_details.sub_screens.add_new_address.AddAddressFragment
import com.bupp.wood_spoon_eaters.features.main.sub_features.settings.SettingsFragment
import com.bupp.wood_spoon_eaters.features.support.SupportDialog
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), HeaderView.HeaderViewListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainActHeaderView.setHeaderViewListener(this)
        loadDeliveryDetails()
    }

    private fun loadFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainActContainer, fragment, tag)
            .commit()
    }




    //fragment and sub features

    private fun loadFeed() {
        loadFragment(FeedFragment(), Constants.FEED_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_FEED)
    }

    //delivery details methods
    fun loadDeliveryDetails() {
        loadFragment(DeliveryDetailsFragment.newInstance(), Constants.DELIVERY_DETAILS_TAG)
    }

    fun loadAddNewAddress() {
        loadFragment(AddAddressFragment(), Constants.ADD_NEW_ADDRESS_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE_SAVE, "Select Your Delivery Address")
    }

    fun loadLocationChooser() {
//        loadFragment()
    }
    //delivery details methods - end


    private fun loadSettings() {
        loadFragment(SettingsFragment(), Constants.SETTINGS_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE, "Location and Communication settings")
    }








    fun setHeaderViewLocationDetails(time: String?, location: String?) {
        mainActHeaderView.setLocationTitle(time, location)
    }


    override
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Constants.LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "GRANTED", Toast.LENGTH_LONG).show()
                    //load location chooser?
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
    override fun onHeaderCloseClick() {
        super.onHeaderCloseClick()
    }

    override fun onHeaderBackClick() {
        onBackPressed()
    }


    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }

}