package com.bupp.wood_spoon_eaters.features.main.profile.my_profile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.CustomDetailsView
import com.bupp.wood_spoon_eaters.views.favorites_view.FavoritesView
import com.bupp.wood_spoon_eaters.custom_views.feed_view.SingleFeedListView
import com.bupp.wood_spoon_eaters.dialogs.LogoutDialog
import com.bupp.wood_spoon_eaters.dialogs.web_docs.WebDocsDialog
import com.bupp.wood_spoon_eaters.common.Constants
import com.stripe.android.model.PaymentMethod
import org.koin.androidx.viewmodel.ext.android.viewModel
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.bottom_sheets.delete_account.DeleteAccountBottomSheet
import com.bupp.wood_spoon_eaters.custom_views.empty_icons_grid_view.CuisinesChooserDialog
import com.bupp.wood_spoon_eaters.bottom_sheets.edit_profile.EditProfileBottomSheet
import com.bupp.wood_spoon_eaters.bottom_sheets.join_as_chef.JoinAsChefBottomSheet
import com.bupp.wood_spoon_eaters.bottom_sheets.settings.SettingsBottomSheet
import com.bupp.wood_spoon_eaters.bottom_sheets.support_center.SupportCenterBottomSheet
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.databinding.MyProfileFragmentBinding
import com.bupp.wood_spoon_eaters.dialogs.NationwideShippmentInfoDialog
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.views.ShareBanner
import com.bupp.wood_spoon_eaters.views.UserImageVideoView
import com.bupp.wood_spoon_eaters.views.WSEditText
import com.bupp.wood_spoon_eaters.views.horizontal_dietary_view.HorizontalDietaryView
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class MyProfileFragment : Fragment(R.layout.my_profile_fragment), CustomDetailsView.CustomDetailsViewListener,
    SingleFeedListView.SingleFeedListViewListener, LogoutDialog.LogoutDialogListener,
    FavoritesView.FavoritesViewListener, CuisinesChooserDialog.CuisinesChooserListener,
    HorizontalDietaryView.HorizontalDietaryViewListener, ShareBanner.WSCustomBannerListener, UserImageVideoView.UserImageViewListener {

    val binding: MyProfileFragmentBinding by viewBinding()
    private val viewModel by viewModel<MyProfileViewModel>()
    private val mainViewModel by sharedViewModel<MainViewModel>()

    private lateinit var editProfileBS: EditProfileBottomSheet

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initClicks()
        initObservers()

    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchProfileData()
        mainViewModel.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_ACCOUNT)
    }

    private fun initProfileData(profileData: MyProfileViewModel.ProfileData) {
        with(binding) {
            profileData.dietary.let {
                myProfileFragDietary.initHorizontalDietaryView(it, this@MyProfileFragment)
            }

//            profileData.bannerUrl?.let{
//                Glide.with(requireContext()).load(it).transform(CenterCrop(), RoundedCorners(15)).into(myProfileFragBanner)
//                myProfileFragBannerLayout.visibility = View.VISIBLE
//            }

            profileData.eater?.let { eater ->

                myProfileFragUserHey.text = "Hey, ${eater.firstName}"
                myProfileFragUserName.text = eater.getFullName()

                if (eater.ordersCount > 0) {
                    myProfileFragOrderCount.text = "${eater.ordersCount} Orders"
                }

                myProfileFragUserImageView.setImage(eater.thumbnail)
                myProfileFragUserImageView.setUserImageViewListener(this@MyProfileFragment)


                //load selected cuisines and dietary
                eater.cuisines?.let {
                    myProfileFragCuisine.setTextFromList(it as List<SelectableIcon>)
                }
                eater.diets?.let {
                    myProfileFragDietary.setSelectedDietary(it as List<SelectableIcon>)
                }

                myProfileFragCuisine.setIsEditable(true, object : WSEditText.WSEditTextListener {
                    override fun onWSEditUnEditableClick() {
                        val cuisineFragment = CuisinesChooserDialog(this@MyProfileFragment, viewModel.getCuisineList(), Constants.ENDLESS_SELECTION)
                        cuisineFragment.setSelectedCuisine(eater.getSelectedCuisines())
                        cuisineFragment.show(childFragmentManager, "CookingCuisine")
                    }
                })
            }


        }
    }

    private fun initObservers() {
        viewModel.profileData.observe(viewLifecycleOwner, { profileData ->
            initProfileData(profileData)
        })

        viewModel.paymentLiveData.observe(viewLifecycleOwner, { cardsEvent ->
            handleCustomerCards(cardsEvent)
        })
//        viewModel.favoritesLiveData.observe(viewLifecycleOwner, {
//            binding.myProfileFragFavorites.setFavoritesViewData(it, this)
//        })
        viewModel.progressData.observe(viewLifecycleOwner, {
            if (it) {
                binding.myProfileFragPb.show()
            } else {
                binding.myProfileFragPb.hide()
            }
        })
        viewModel.campaignLiveData.observe(viewLifecycleOwner, {
            handleCampaign(it)
        })
        viewModel.versionLiveData.observe(viewLifecycleOwner, {
            binding.myProfileFragVersion.text = "$it"
        })
        mainViewModel.stripeInitializationEvent.observe(viewLifecycleOwner, {
            Log.d(TAG, "stripeInitializationEvent status: $it")
            when (it) {
                PaymentManager.StripeInitializationStatus.START -> {
                    binding.myProfileFragPb.show()
                }
                PaymentManager.StripeInitializationStatus.SUCCESS -> {
                    binding.myProfileFragPb.hide()
                }
                PaymentManager.StripeInitializationStatus.FAIL -> {
                    binding.myProfileFragPb.hide()
                }
            }
        })
        mainViewModel.getFinalAddressParams().observe(viewLifecycleOwner, {
            binding.myProfileFragAddress.updateDeliveryAddressFullDetails(it.address)
        })
        mainViewModel.onFloatingBtnHeightChange.observe(viewLifecycleOwner, {
            binding.myProfileFragHeightCorrection.isVisible = it
        })
    }

    private fun handleCampaign(campaigns: List<Campaign>?) {
        campaigns?.forEach { campaign ->
            campaign.viewTypes?.forEach { viewType ->
                when (viewType) {
                    CampaignViewType.PROFILE -> {
                        binding.myProfileFragShareBanner.initCustomBannerByCampaign(campaign, this)
                    }
                }
            }
        }
    }


    private fun initClicks() {
        with(binding) {
//            myProfileFragEditAccount.setOnClickListener { (activity as MainActivity).loadEditMyProfile() }//todo - fix this feed 2.0

            myProfileFragAddress.setOnClickListener {
                mainViewModel.handleMainNavigation(MainViewModel.MainNavigationEvent.START_LOCATION_AND_ADDRESS_ACTIVITY)
            }

            myProfileFragSettings.setOnClickListener {
                SettingsBottomSheet().show(childFragmentManager, Constants.SETTINGS_BOTTOM_SHEET)
            }
            myProfileFragPrivacy.setOnClickListener { WebDocsDialog(Constants.WEB_DOCS_PRIVACY).show(childFragmentManager, Constants.WEB_DOCS_DIALOG_TAG) }
            myProfileFragSupport.setOnClickListener {
                SupportCenterBottomSheet().show(childFragmentManager, Constants.SUPPORT_CENTER_BOTTOM_SHEET)
            }

            myProfileFragJoinAsChef.setOnClickListener {
                JoinAsChefBottomSheet().show(childFragmentManager, Constants.JOIN_AS_CHEF_BOTTOM_SHEET)
            }

            myProfileFragLogout.setOnClickListener {
                LogoutDialog(this@MyProfileFragment).show(childFragmentManager, Constants.LOGOUT_DIALOG_TAG)
                mainViewModel.logEvent(Constants.EVENT_CLICK_SIGN_OUT)

            }
            myProfileFragDeleteAccount.setOnClickListener {
                DeleteAccountBottomSheet().show(childFragmentManager, Constants.DELETE_ACCOUNT_BOTTOM_SHEET)
            }
            myProfileFragEditAccount.setOnClickListener { onUserImageClick(null) }
            myProfileFragAddress.setDeliveryDetailsViewListener(this@MyProfileFragment)
            myProfileFragPayment.setDeliveryDetailsViewListener(this@MyProfileFragment)
            myProfileFragAddress.setChangeable(true)
            myProfileFragPayment.setChangeable(true)
        }
    }

    override fun onDietaryClick(dietary: SelectableIcon) {
        viewModel.updateClientAccount(dietaryIcons = listOf(dietary))

    }

    override fun onDishClick(dish: Dish) {
        dish.menuItem?.let {
            mainViewModel.onDishClick(it.id)
        }
    }

    override fun onCuisineChoose(selectedCuisines: List<SelectableIcon>) {
        viewModel.updateClientAccount(cuisineIcons = selectedCuisines, forceUpdate = true)
    }


    override fun logout() {
        mainViewModel.logout()
    }

    private fun openOrderHistoryDialog() {
        //remove this
    }

    override fun onCustomDetailsClick(type: Int) {
        when (type) {
            Constants.DELIVERY_DETAILS_LOCATION -> {
                mainViewModel.handleMainNavigation(MainViewModel.MainNavigationEvent.START_LOCATION_AND_ADDRESS_ACTIVITY)
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
            Log.d("wowMyProfile", "updateCustomerPaymentMethod: ${paymentMethod.id}")
            binding.myProfileFragPayment.updateSubTitle("Selected Card: (${card.brand} ${card.last4})")
            viewModel.updateUserCustomerCard(paymentMethod)
            binding.myProfileFragPb.hide()
        }
    }

    private fun setEmptyPaymentMethod() {
        binding.myProfileFragPayment.updateSubTitle("Insert payment method")
    }

    fun onAddressChooserSelected() {
        viewModel.getUserDetails()
    }

    override fun onWorldwideInfoClick() {
        NationwideShippmentInfoDialog().show(childFragmentManager, Constants.NATIONWIDE_SHIPPING_INFO_DIALOG)
    }

    override fun onShareBannerClick(campaign: Campaign?) {
        mainViewModel.onShareCampaignClick(campaign)
    }

    override fun onUserImageClick(cook: Cook?) {
        editProfileBS = EditProfileBottomSheet()
        editProfileBS.show(childFragmentManager, Constants.EDIT_PROFILE_BOTTOM_SHEET)
    }

    companion object {
        fun newInstance() = MyProfileFragment()
        const val TAG = "wowMyProfileFrag"
    }


}
