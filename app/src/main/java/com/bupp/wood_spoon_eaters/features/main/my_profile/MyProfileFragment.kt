package com.bupp.wood_spoon_eaters.features.main.my_profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.DeliveryDetailsView
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.model.Eater
import kotlinx.android.synthetic.main.my_profile_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class MyProfileFragment : Fragment(), DeliveryDetailsView.DeliveryDetailsViewListener {

    companion object {
        fun newInstance() = MyProfileFragment()
    }

    val viewModel by viewModel<MyProfileViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.my_profile_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        myProfileFragEditLocation.setDeliveryDetailsViewListener(this)
        myProfileFragEditPayment.setDeliveryDetailsViewListener(this)


        initClicks()

        viewModel.userDetails.observe(this, Observer { userDetails -> handleUserDetails(userDetails) })

        viewModel.fetchUserDetails()
    }

    private fun handleUserDetails(userDetails: MyProfileViewModel.UserDetails?) {
        if (userDetails != null) {
            initEaterData(userDetails.eater)
            initDeliveryDetails(userDetails.deliveryAddress)
        }
    }

    private fun initDeliveryDetails(deliveryDetails: String?) {
        if (!deliveryDetails.isNullOrBlank()) {
            myProfileFragEditLocation.updateDeliveryDetails(deliveryDetails)
        }
    }

    private fun initEaterData(eater: Eater) {
        if (!eater.getFullName().isNullOrBlank()) {
            myProfileFragUserName.text = eater.getFullName()
        }
        myProfileFragUserPhoto.setImage(eater.thumbnail)
    }

    private fun initClicks() {
        myProfileFragUserPhoto.setOnClickListener { }
        myProfileFragEditProfileBtn.setOnClickListener { (activity as MainActivity).loadEditMyProfile() }

        myProfileFragPromoCodesBtn.setOnClickListener { }

        myProfileFragLocationSettingsBtn.setOnClickListener { (activity as MainActivity).loadSettings() }
        myProfileFragPrivacyBtn.setOnClickListener { }
        myProfileFragSupportBtn.setOnClickListener { (activity as MainActivity).loadSupport() }

        myProfileFragJoinBtn.setOnClickListener { }
        myProfileFragShareBtnLayout.setOnClickListener { }
    }

    override fun onChangeLocationClick() {
        (activity as MainActivity).loadAddressesDialog()
    }

    override fun onChangePaymentClick() {

    }

    fun onAddressChooserSelected() {
        viewModel.fetchUserDetails()
    }
}
