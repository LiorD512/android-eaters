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
import com.bupp.wood_spoon_eaters.utils.Constants
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
        if (!eater.fullName.isNullOrBlank()) {
            myProfileFragUserName.text = eater.fullName
        } else {
            myProfileFragUserName.text = eater.firstName + " " + eater.lastName
        }
        if (!eater.thumbnail.isNullOrBlank()) {
            myProfileFragUserPhoto.setImage(eater.thumbnail)
        }

        //TODO:: Delete this penguin
        myProfileFragUserPhoto.setImage(
            "data:image/jpeg;base64,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"
        )
    }

    private fun initClicks() {
        myProfileFragUserPhoto.setOnClickListener { }
        myProfileFragEditProfileBtn.setOnClickListener { (activity as MainActivity).loadEditMyProfile()}

        myProfileFragPromoCodesBtn.setOnClickListener { }

        myProfileFragLocationSettingsBtn.setOnClickListener { }
        myProfileFragPrivacyBtn.setOnClickListener { }
        myProfileFragSupportBtn.setOnClickListener { (activity as MainActivity).loadSupport() }

        myProfileFragJoinBtn.setOnClickListener { }
        myProfileFragShareBtnLayout.setOnClickListener { }
    }

    override fun onChangeLocationClick() {
//        DeliveryDetailsViewModel.CurrentDataEvent()
        (activity as MainActivity).loadAddNewAddress()
    }

    override fun onChangePaymentClick() {

    }
}
