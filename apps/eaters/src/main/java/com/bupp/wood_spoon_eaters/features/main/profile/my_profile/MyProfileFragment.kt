package com.bupp.wood_spoon_eaters.features.main.profile.my_profile

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.delete_account.DeleteAccountBottomSheet
import com.bupp.wood_spoon_eaters.bottom_sheets.edit_profile.EditProfileBottomSheet
import com.bupp.wood_spoon_eaters.bottom_sheets.join_as_chef.JoinAsChefBottomSheet
import com.bupp.wood_spoon_eaters.bottom_sheets.settings.SettingsBottomSheet
import com.bupp.wood_spoon_eaters.bottom_sheets.support_center.SupportCenterBottomSheet
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.CustomDetailsView
import com.bupp.wood_spoon_eaters.custom_views.cuisine_chooser.CuisinesChooserDialog
import com.bupp.wood_spoon_eaters.databinding.MyProfileFragmentBinding
import com.bupp.wood_spoon_eaters.dialogs.LogoutDialog
import com.bupp.wood_spoon_eaters.dialogs.web_docs.WebDocsDialog
import com.bupp.wood_spoon_eaters.features.main.MainNavigationEvent
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.managers.StripeInitializationStatus
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.views.ShareBanner
import com.bupp.wood_spoon_eaters.views.UserImageVideoView
import com.bupp.wood_spoon_eaters.views.WSEditText
import com.bupp.wood_spoon_eaters.views.horizontal_dietary_view.HorizontalDietaryView
import com.stripe.android.model.PaymentMethod
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyProfileFragment : Fragment(R.layout.my_profile_fragment),
    CustomDetailsView.CustomDetailsViewListener,
    LogoutDialog.LogoutDialogListener,
    CuisinesChooserDialog.CuisinesChooserListener,
    HorizontalDietaryView.HorizontalDietaryViewListener,
    ShareBanner.WSCustomBannerListener,
    UserImageVideoView.UserImageVideoViewListener {

    var binding: MyProfileFragmentBinding? = null
    private val viewModel by viewModel<MyProfileViewModel>()
    private val mainViewModel by sharedViewModel<MainViewModel>()

    private lateinit var editProfileBS: EditProfileBottomSheet

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MyProfileFragmentBinding.bind(view)

        initClicks()
        initObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchProfileData()
    }

    private fun initProfileData(profileData: MyProfileViewModel.ProfileData) {
        with(binding!!) {
            profileData.dietary.let {
                myProfileFragDietary.initHorizontalDietaryView(it, this@MyProfileFragment)
            }

            profileData.eater?.let { eater ->

                myProfileFragUserHey.text = "Hey, ${eater.firstName}"
                myProfileFragUserName.text = eater.getFullName()

                if (eater.ordersCount > 0) {
                    if (eater.ordersCount == 1) {
                        myProfileFragOrderCount.text = "1 Order"
                    } else {
                        myProfileFragOrderCount.text = "${eater.ordersCount} Orders"
                    }
                }

                myProfileFragUserImageView.setImage(eater.thumbnail)
                myProfileFragUserImageView.setUserImageVideoViewListener(this@MyProfileFragment)


                //load selected cuisines and dietary
                eater.cuisines?.let {
                    myProfileFragCuisine.setTextFromList(it as List<SelectableIcon>)
                }
                eater.diets?.let {
                    myProfileFragDietary.setSelectedDietary(it as List<SelectableIcon>)
                }

                myProfileFragCuisine.setIsEditable(true, object : WSEditText.WSEditTextListener {
                    override fun onWSEditUnEditableClick() {
                        val cuisineFragment = CuisinesChooserDialog(this@MyProfileFragment)
                        cuisineFragment.show(childFragmentManager, "CookingCuisine")
                    }
                })
            }


        }
    }

    private fun initObservers() {
        viewModel.profileData.observe(viewLifecycleOwner) { profileData ->
            initProfileData(profileData)
        }
        viewModel.paymentLiveData.observe(viewLifecycleOwner) { cardsEvent ->
            handleCustomerCards(cardsEvent)
        }
        viewModel.progressData.observe(viewLifecycleOwner) {
            if (it) {
                binding!!.myProfileFragPb.show()
            } else {
                binding!!.myProfileFragPb.hide()
            }
        }
        viewModel.campaignLiveData.observe(viewLifecycleOwner) {
            handleCampaign(it)
        }
        viewModel.versionLiveData.observe(viewLifecycleOwner) {
            binding!!.myProfileFragVersion.text = it
        }
        mainViewModel.stripeInitializationEvent.observe(viewLifecycleOwner) {
            with(binding!!) {
                when (it) {
                    StripeInitializationStatus.START -> {
                        myProfileFragPb.show()
                    }
                    StripeInitializationStatus.SUCCESS -> {
                        myProfileFragPb.hide()
                    }
                    StripeInitializationStatus.FAIL -> {
                        myProfileFragPb.hide()
                    }
                    else -> {}
                }
            }
        }
        mainViewModel.getFinalAddressParams().observe(viewLifecycleOwner) {
            binding!!.myProfileFragAddress.updateDeliveryAddressFullDetails(it.address)
        }
        mainViewModel.onFloatingBtnHeightChange.observe(viewLifecycleOwner) {
            binding!!.myProfileFragHeightCorrection.isVisible = it
        }
    }

    private fun handleCampaign(campaigns: List<Campaign>?) {
        campaigns?.forEach { campaign ->
            campaign.viewTypes?.forEach { viewType ->
                when (viewType) {
                    CampaignViewType.PROFILE -> {
                        binding!!.myProfileFragShareBanner.initCustomBannerByCampaign(campaign, this)
                    }
                }
            }
        }
    }


    private fun initClicks() {
        with(binding!!) {
            myProfileFragAddress.setOnClickListener {
                mainViewModel.handleMainNavigation(
                    MainNavigationEvent.START_LOCATION_AND_ADDRESS_ACTIVITY
                )
            }
            myProfileFragSettings.setOnClickListener {
                SettingsBottomSheet()
                    .show(childFragmentManager, Constants.SETTINGS_BOTTOM_SHEET)
            }
            myProfileFragPrivacy.setOnClickListener {
                WebDocsDialog(Constants.WEB_DOCS_PRIVACY)
                    .show(childFragmentManager, Constants.WEB_DOCS_DIALOG_TAG)
            }
            myProfileFragSupport.setOnClickListener {
                SupportCenterBottomSheet()
                    .show(childFragmentManager, Constants.SUPPORT_CENTER_BOTTOM_SHEET)
            }
            myProfileFragJoinAsChef.setOnClickListener {
                JoinAsChefBottomSheet()
                    .show(childFragmentManager, Constants.JOIN_AS_CHEF_BOTTOM_SHEET)
            }
            myProfileFragLogout.setOnClickListener {
                LogoutDialog(this@MyProfileFragment)
                    .show(childFragmentManager, Constants.LOGOUT_DIALOG_TAG)
                mainViewModel.logEvent(Constants.EVENT_CLICK_SIGN_OUT)
            }
            myProfileFragDeleteAccount.setOnClickListener {
                DeleteAccountBottomSheet()
                    .show(childFragmentManager, Constants.DELETE_ACCOUNT_BOTTOM_SHEET)
            }
            myProfileFragEditAccount.setOnClickListener {
                onUserImageClick(null)
            }
            myProfileFragAddress.setDeliveryDetailsViewListener(this@MyProfileFragment)
            myProfileFragPayment.setDeliveryDetailsViewListener(this@MyProfileFragment)
            myProfileFragAddress.setChangeable(true)
            myProfileFragPayment.setChangeable(true)
        }
    }

    override fun onDietaryClick(dietary: SelectableIcon) {
        viewModel.updateClientAccount(dietaryIcons = listOf(dietary))
    }

    override fun onCuisineChoose(selectedCuisines: List<SelectableIcon>) {
        viewModel.updateClientAccount(cuisineIcons = selectedCuisines, forceUpdate = true)
    }

    override fun logout() {
        mainViewModel.logout()
    }

    override fun onCustomDetailsClick(type: Int) {
        when (type) {
            Constants.DELIVERY_DETAILS_LOCATION -> {
                mainViewModel.handleMainNavigation(
                    MainNavigationEvent.START_LOCATION_AND_ADDRESS_ACTIVITY
                )
            }
            Constants.DELIVERY_DETAILS_PAYMENT -> {
                mainViewModel.logEvent(Constants.EVENT_CLICK_PAYMENT)
                mainViewModel.startStripeOrReInit()
            }
        }
    }

    private fun handleCustomerCards(paymentMethods: PaymentMethod?) {
        if (paymentMethods != null) {
            updateCustomerPaymentMethod(paymentMethods)
        } else {
            setEmptyPaymentMethod()
        }
    }

    private fun updateCustomerPaymentMethod(paymentMethod: PaymentMethod) {
        val card = paymentMethod.card
        if (card != null) {
            with(binding!!){
                myProfileFragPayment.updateSubTitle("Selected Card: (${card.brand} ${card.last4})")
                myProfileFragPb.hide()
            }
        }
    }

    private fun setEmptyPaymentMethod() {
        binding!!.myProfileFragPayment.updateSubTitle("Insert payment method")
    }

    override fun onShareBannerClick(campaign: Campaign?) {
        mainViewModel.onShareCampaignClick(campaign?.shareUrl, campaign?.shareText)
    }

    override fun onUserImageClick(cook: Cook?) {
        editProfileBS = EditProfileBottomSheet()
        editProfileBS.show(childFragmentManager, Constants.EDIT_PROFILE_BOTTOM_SHEET)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = MyProfileFragment()
        const val TAG = "wowMyProfileFrag"
    }
}
