package com.bupp.wood_spoon_eaters.features.main.profile.my_profile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.CustomDetailsView
import com.bupp.wood_spoon_eaters.views.favorites_view.FavoritesView
import com.bupp.wood_spoon_eaters.custom_views.feed_view.SingleFeedListView
import com.bupp.wood_spoon_eaters.dialogs.LogoutDialog
import com.bupp.wood_spoon_eaters.dialogs.web_docs.WebDocsDialog
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.utils.Utils
import com.stripe.android.model.PaymentMethod
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.content.Intent
import android.net.Uri
import com.bupp.wood_spoon_eaters.custom_views.empty_icons_grid_view.CuisinesChooserDialog
import com.bupp.wood_spoon_eaters.BuildConfig
import com.bupp.wood_spoon_eaters.custom_views.IconsGridView
import com.bupp.wood_spoon_eaters.custom_views.empty_icons_grid_view.EmptyIconsGridView
import com.bupp.wood_spoon_eaters.databinding.MyProfileFragmentBinding
import com.bupp.wood_spoon_eaters.dialogs.NationwideShippmentInfoDialog
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderActivity
import com.bupp.wood_spoon_eaters.features.splash.SplashActivity
import com.bupp.wood_spoon_eaters.managers.PaymentManager
import com.bupp.wood_spoon_eaters.model.SelectableIcon
import com.bupp.wood_spoon_eaters.views.horizontal_dietary_view.HorizontalDietaryView
import com.segment.analytics.Analytics
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class MyProfileFragment : Fragment(R.layout.my_profile_fragment), CustomDetailsView.CustomDetailsViewListener,
    SingleFeedListView.SingleFeedListViewListener, LogoutDialog.LogoutDialogListener,
    FavoritesView.FavoritesViewListener, EmptyIconsGridView.OnItemSelectedListener, CuisinesChooserDialog.CuisinesChooserListener,
    IconsGridView.IconsGridViewListener, HorizontalDietaryView.HorizontalDietaryViewListener {

    lateinit var binding: MyProfileFragmentBinding
    private val viewModel by viewModel<MyProfileViewModel>()
    private val mainViewModel by sharedViewModel<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Analytics.with(requireContext()).screen("Profile page")

        binding = MyProfileFragmentBinding.bind(view)
//        binding.myProfileFragEditPayment.setDeliveryDetailsViewListener(this)

        initClicks()
        initObservers()

    }

    private fun initProfileData(profileData: MyProfileViewModel.ProfileData) {
        profileData.eater?.let{
            handleUserDetails(it)
        }

        with(binding){
//            myProfileFragCuisineIcons.setListener(this@MyProfileFragment)
            profileData.dietary.let{
                myProfileFragDietary.initHorizontalDietaryView(it, this@MyProfileFragment)
            }

            profileData.eater?.let{ eater ->
    //            myProfileFragDietaryIcons.setIconsGridViewListener(this@MyProfileFragment)

                myProfileFragUserHey.text = "Hey, ${eater.firstName}"
                myProfileFragUserName.text = eater.getFullName()

                if(eater.ordersCount > 0){
                    myProfileFragOrderCount.text = "${eater.ordersCount} Orders"
                }

                myProfileFragUserImageView.setImage(eater.thumbnail)

                //load selected cuisines and dietary
                eater.cuisines?.let{
                    myProfileFragCuisine.setTextFromList(it as List<SelectableIcon>)
                }
                eater.diets?.let{
                    myProfileFragDietary.setSelectedDietary(it as List<SelectableIcon>)
                }
            }

            myProfileFragVersion.text = "Version: ${BuildConfig.VERSION_NAME}"
        }
    }

    override fun OnEmptyItemSelected() {
        var cuisineFragment = CuisinesChooserDialog(this, viewModel.getCuisineList(), Constants.MULTI_SELECTION)
//        cuisineFragment.setSelectedCuisine(binding.myProfileFragCuisineIcons.getSelectedCuisines())
        cuisineFragment.show(childFragmentManager, "CookingCuisine")
    }

    private fun initObservers() {
        viewModel.profileData.observe(viewLifecycleOwner, { profileData ->
            initProfileData(profileData)
        })
//        viewModel.getUserDetails.observe(viewLifecycleOwner, { getUserEvent ->
//        })

        viewModel.paymentLiveData.observe(viewLifecycleOwner, { cardsEvent ->
            handleCustomerCards(cardsEvent)
        })

        viewModel.myProfileActionEvent.observe(viewLifecycleOwner, {
            when(it.type){
                MyProfileViewModel.MyProfileActionType.LOGOUT -> {
                    val intent = Intent(context, SplashActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
            }
        })
        viewModel.favoritesLiveData.observe(viewLifecycleOwner, {
            binding.myProfileFragFavorites.setFavoritesViewData(it, this)
        })
        viewModel.progressData.observe(viewLifecycleOwner, {
            if(it){
                binding.myProfileFragPb.show()
            }else{
                binding.myProfileFragPb.hide()
            }
        })
        mainViewModel.stripeInitializationEvent.observe(viewLifecycleOwner, {
            Log.d(NewOrderActivity.TAG, "stripeInitializationEvent status: $it")
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

    }


    private fun handleUserDetails(eater: Eater) {
        with(binding){

        }
    }

    private fun initClicks() {
        with(binding) {
            myProfileFragEditAccount.setOnClickListener { (activity as MainActivity).loadEditMyProfile() }


            myProfileFragAddress.setOnClickListener {
                mainViewModel.handleMainNavigation(MainViewModel.MainNavigationEvent.START_LOCATION_AND_ADDRESS_ACTIVITY)
            }

            myProfileFragSettings.setOnClickListener { (activity as MainActivity).loadSettingsFragment() }
            myProfileFragPrivacy.setOnClickListener { WebDocsDialog(Constants.WEB_DOCS_PRIVACY).show(childFragmentManager, Constants.WEB_DOCS_DIALOG_TAG) }
            myProfileFragSupport.setOnClickListener { (activity as MainActivity).loadSupport() }

            myProfileFragJoinAsChef.setOnClickListener { openWoodSpoonGooglePlay() }
            myProfileFragBanner.setOnClickListener { share() }

            myProfileFragOrderHistory.setOnClickListener { openOrderHistoryDialog() }

            myProfileFragLogout.setOnClickListener {
                LogoutDialog(this@MyProfileFragment).show(childFragmentManager, Constants.LOGOUT_DIALOG_TAG)
            }


        }
    }

    override fun onDietaryClick(dietary: SelectableIcon) {

    }

    override fun onDishClick(dish: Dish) {
        dish.menuItem?.let{
            mainViewModel.onDishClick(it.id)
        }
    }

    override fun onIconClick(selectedDiets: List<SelectableIcon>) {
        viewModel.updateClientAccount(dietaryIcons = selectedDiets)
    }

    override fun onCuisineChoose(selectedCuisines: List<SelectableIcon>) {
        viewModel.updateClientAccount(cuisineIcons = selectedCuisines, forceUpdate = true)
    }

    private fun openWoodSpoonGooglePlay() {
        val url = getString(R.string.wood_spoon_chefs_play_store_url)
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    private fun share() {
        val text = viewModel.getShareText()
        Utils.shareText(requireActivity(), text)
    }

    override fun logout() {
        viewModel.logout()
    }

    private fun openOrderHistoryDialog() {
        (activity as MainActivity).loadOrderHistoryFragment()
    }

    override fun onCustomDetailsClick(type: Int) {
        when (type) {
            Constants.DELIVERY_DETAILS_LOCATION_PROFILE -> {
                //todo: check this
            }

            Constants.DELIVERY_DETAILS_PAYMENT -> {
                mainViewModel.startStripeOrReInit()
            }
        }
    }

    private fun handleCustomerCards(paymentMethods: List<PaymentMethod>?) {
        if(!paymentMethods.isNullOrEmpty()){
            val defaultPayment = paymentMethods[0]
            updateCustomerPaymentMethod(defaultPayment)
//            viewModel.attachCardToCustomer(defaultPayment.id!!)
        }else{
            setEmptyPaymentMethod()
        }
    }

    private fun updateCustomerPaymentMethod(paymentMethod: PaymentMethod) {
        val card = paymentMethod.card
        if(card != null){
            Log.d("wowMyProfile","updateCustomerPaymentMethod: ${paymentMethod.id}")
            binding.myProfileFragPayment.updateDeliveryDetails("Selected Card: (${card.brand} ${card.last4})")
            binding.myProfileFragPayment.setChangeable(true)
            viewModel.updateUserCustomerCard(paymentMethod)
        }
    }

    private fun setEmptyPaymentMethod() {
        binding.myProfileFragPayment.updateDeliveryDetails("Insert payment method")
        binding.myProfileFragPayment.setChangeable(true)
    }

    fun onAddressChooserSelected() {
        viewModel.getUserDetails()
    }

    override fun onWorldwideInfoClick() {
        NationwideShippmentInfoDialog().show(childFragmentManager, Constants.NATIONWIDE_SHIPPING_INFO_DIALOG)
    }


    companion object {
        fun newInstance() = MyProfileFragment()
        const val TAG = "wowMyProfileFrag"
    }


}
