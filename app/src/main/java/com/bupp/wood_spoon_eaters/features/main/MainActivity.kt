package com.bupp.wood_spoon_eaters.features.main

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.custom_views.LocationDetailsView
import com.bupp.wood_spoon_eaters.features.main.sub_features.add_address.AddAddressFragment
import com.bupp.wood_spoon_eaters.features.main.sub_features.settings.SettingsFragment
import com.bupp.wood_spoon_eaters.features.support.SupportDialog
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), HeaderView.HeaderViewListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUi()
//        loadSettings()

//        showSupportDialog()
        loadAddAddress()
    }

    private fun initUi() {
        mainActHeaderView.setHeaderViewListener(this)
    }

    private fun loadFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainActContainer, fragment, tag)
            .commit()
    }

    private fun loadFeed() {
        loadFragment(FeedFragment(), Constants.FEED_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_IMAGE_LOCATION_SEARCH, "blablabal doesnt matter")
    }

    private fun loadAddAddress() {
        loadFragment(AddAddressFragment(), Constants.ADD_ADDRESS_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_SAVE_TITLE_BACK, "Add New Address")
    }

    private fun loadSettings() {
        loadFragment(SettingsFragment(), Constants.SETTINGS_TAG)
        mainActHeaderView.setType(Constants.HEADER_VIEW_TYPE_TITLE_BACK, "Location and Communication settings")
    }

    fun showSupportDialog() {
        SupportDialog().show(supportFragmentManager, Constants.SUPPORT_TAG)
    }

    fun setHeaderViewLocationDetails(time: String?, location: String?) {
        mainActHeaderView.setLocationTitle(time, location)
    }

    fun setHeaderViewLocationListener(listener: LocationDetailsView.LocationDetailsViewListener) {
        mainActHeaderView.setLocationDetailsViewListener(listener)
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
        val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + Constants.SMS_NUMBER))
        smsIntent.putExtra("sms_body", getString(R.string.support_frag_sms_sentence))
        this.applicationContext.startActivity(smsIntent)
    }

    fun callPhoneNumber() {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:" + Constants.SMS_NUMBER)
        this.applicationContext.startActivity(dialIntent)
    }


    //HeaderView Listener interface
    override fun onHeaderCloseClick() {
        super.onHeaderCloseClick()
    }

    override fun onHeaderBackClick() {
        onBackPressed()
    }

    override fun onHeaderProfileClick() {
        super.onHeaderProfileClick()
    }

    override fun onHeaderSkipClick() {
        super.onHeaderSkipClick()
    }

    override fun onHeaderSaveClick() {
        super.onHeaderSaveClick()
    }

    override fun onHeaderSearchClick() {
        super.onHeaderSearchClick()
    }

    override fun onHeaderFilterClick() {
        super.onHeaderFilterClick()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }
}