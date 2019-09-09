package com.bupp.wood_spoon_eaters.features.main.profile.my_profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.DeliveryDetailsView
import com.bupp.wood_spoon_eaters.custom_views.feed_view.SingleFeedListView
import com.bupp.wood_spoon_eaters.dialogs.LogoutDialog
import com.bupp.wood_spoon_eaters.dialogs.web_docs.WebDocsDialog
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.features.main.promo_code.PromoCodeFragment
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.my_profile_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyProfileFragment : Fragment(), DeliveryDetailsView.DeliveryDetailsViewListener,
    SingleFeedListView.SingleFeedListViewListener, LogoutDialog.LogoutDialogListener {


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
        initProfileData()
        initObservers()

    }

    private fun initProfileData() {
        myProfileFragPb.show()
        viewModel.getUserDetails()
    }

    private fun initObservers() {
        viewModel.getUserDetails.observe(this, Observer { getUserEvent ->
            myProfileFragPb.hide()
            if (getUserEvent.isSuccess) {
                handleUserDetails(getUserEvent.eater!!)
            }
        })

    }


    private fun handleUserDetails(eater: Eater) {
        initEaterData(eater)
    }


    private fun initEaterData(eater: Eater) {
        myProfileFragUserName.text = eater.getFullName()
        myProfileFragEditLocation.updateDeliveryDetails(viewModel.getDeliveryAddress())
        myProfileFragUserPhoto.setImage(eater.thumbnail)

    }

    override fun onDishClick(dish: Dish) {
        (activity as MainActivity).loadNewOrderActivity(dish.menuItem.id)

    }

    private fun initClicks() {
        myProfileFragUserPhoto.setOnClickListener { }
        myProfileFragEditProfileBtn.setOnClickListener { (activity as MainActivity).loadEditMyProfile() }

        myProfileFragPromoCodesBtn.setOnClickListener { }

        myProfileFragLocationSettingsBtn.setOnClickListener { (activity as MainActivity).loadSettingsFragment() }
        myProfileFragPrivacyBtn.setOnClickListener { WebDocsDialog(Constants.WEB_DOCS_PRIVACY).show(childFragmentManager, Constants.WEB_DOCS_DIALOG_TAG) }
        myProfileFragSupportBtn.setOnClickListener { (activity as MainActivity).loadSupport() }

        myProfileFragJoinBtn.setOnClickListener { }
        myProfileFragShareBtnLayout.setOnClickListener { }

        myProfileFragOrderHistoryBtn.setOnClickListener { openOrderHistoryDialog() }

        profileFragLogout.setOnClickListener {
            LogoutDialog(this).show(childFragmentManager, Constants.LOGOUT_DIALOG_TAG)
        }
    }

    fun loadPromoCodeDialog() {
        PromoCodeFragment()
    }

    override fun logout() {
        viewModel.logout(context!!)
    }

    private fun openOrderHistoryDialog() {
        (activity as MainActivity).loadOrderHistoryFragment()
    }

    override fun onChangeLocationClick() {
        (activity as MainActivity).loadAddressesDialog()
    }

    override fun onChangePaymentClick() {

    }

    fun onAddressChooserSelected() {
        viewModel.getUserDetails()
    }
}
