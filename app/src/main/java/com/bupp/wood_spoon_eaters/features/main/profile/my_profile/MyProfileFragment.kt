package com.bupp.wood_spoon_eaters.features.main.profile.my_profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.DeliveryDetailsView
import com.bupp.wood_spoon_eaters.custom_views.favorites_view.FavoritesView
import com.bupp.wood_spoon_eaters.custom_views.feed_view.SingleFeedListView
import com.bupp.wood_spoon_eaters.dialogs.LogoutDialog
import com.bupp.wood_spoon_eaters.dialogs.web_docs.WebDocsDialog
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.utils.Utils
import com.stripe.android.model.PaymentMethod
import kotlinx.android.synthetic.main.my_profile_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.content.Intent
import android.net.Uri
import com.bupp.wood_spoon.dialogs.CuisinesChooserDialog
import com.bupp.wood_spoon_eaters.BuildConfig
import com.bupp.wood_spoon_eaters.custom_views.IconsGridView
import com.bupp.wood_spoon_eaters.custom_views.empty_icons_grid_view.EmptyIconsGridView
import com.bupp.wood_spoon_eaters.dialogs.NationwideShippmentInfoDialog
import com.bupp.wood_spoon_eaters.features.splash.SplashActivity
import com.bupp.wood_spoon_eaters.model.SelectableIcon


class MyProfileFragment : Fragment(), DeliveryDetailsView.DeliveryDetailsViewListener,
    SingleFeedListView.SingleFeedListViewListener, LogoutDialog.LogoutDialogListener,
    FavoritesView.FavoritesViewListener, EmptyIconsGridView.OnItemSelectedListener, CuisinesChooserDialog.CuisinesChooserListener,
    IconsGridView.IconsGridViewListener {

    companion object {
        fun newInstance() = MyProfileFragment()
    }

    val myProfileViewModel by viewModel<MyProfileViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.my_profile_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        myProfileFragEditLocation.setDeliveryDetailsViewListener(this)
        myProfileFragEditPayment.setDeliveryDetailsViewListener(this)

        initClicks()
        initProfileData()
        initObservers()

    }

    private fun initProfileData() {
        myProfileFragPb.show()
        myProfileViewModel.initStripe((activity as MainActivity))
        myProfileViewModel.getUserDetails()
        myProfileViewModel.getStripeCustomerCards(requireContext())
        myProfileFragFavorites.initFavorites()

        myProfileFragCuisineIcons.setListener(this)
        myProfileFragDietaryIcons.initIconsGrid(myProfileViewModel.getDietaryList(), Constants.MULTI_SELECTION)
        myProfileFragDietaryIcons.setIconsGridViewListener(this)

        myProfileFragVersion.text = "Version: ${BuildConfig.VERSION_NAME}"
    }

    override fun OnEmptyItemSelected() {
        var cuisineFragment = CuisinesChooserDialog(this, myProfileViewModel.getCuisineList(), Constants.MULTI_SELECTION)
        cuisineFragment.setSelectedCuisine(myProfileFragCuisineIcons.getSelectedCuisines())
        cuisineFragment.show(childFragmentManager, "CookingCuisine")
    }

    override fun onCuisineChoose(selectedCuisines: ArrayList<SelectableIcon>) {
        myProfileFragCuisineIcons.updateItems(selectedCuisines)
        myProfileViewModel.updateClientAccount(cuisineIcons = selectedCuisines)
    }

    override fun onIconClick(selectedDiets: ArrayList<SelectableIcon>) {
        myProfileViewModel.updateClientAccount(dietaryIcons = selectedDiets)
    }

    private fun initObservers() {
        myProfileViewModel.getUserDetails.observe(this, Observer { getUserEvent ->
            myProfileFragPb.hide()
            if (getUserEvent.isSuccess) {
                handleUserDetails(getUserEvent.eater!!)
            }
        })

        myProfileViewModel.getStripeCustomerCards.observe(this, Observer { cardsEvent ->
            if(cardsEvent.isSuccess){
                handleCustomerCards(cardsEvent.paymentMethods)
            }else {
                setEmptyPaymentMethod()
            }
        })

        myProfileViewModel.myProfileActionEvent.observe(viewLifecycleOwner, Observer{
            when(it.type){
                MyProfileViewModel.MyProfileActionType.LOGOUT -> {
                    val intent = Intent(context, SplashActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
            }
        })

    }


    private fun handleUserDetails(eater: Eater) {
        myProfileFragUserName.text = eater.getFullName()
//        myProfileFragEditLocation.setOnClickListener { (activity as MainActivity).openAddressChooser() }
        myProfileFragUserPhoto.setImage(eater.thumbnail)

        //load selected cuisines and dietary
        eater.cuisines?.let{
            myProfileFragCuisineIcons.initIconsGrid(eater.cuisines as ArrayList<SelectableIcon>)
        }
        eater.diets?.let{
            myProfileFragDietaryIcons.setSelectedItems(eater.diets as ArrayList<SelectableIcon>)
        }
    }



    override fun onDishClick(dish: Dish) {
        dish.menuItem?.let{
            (activity as MainActivity).loadNewOrderActivity(it.id)
        }
    }

    private fun initClicks() {
        myProfileFragUserPhoto.setOnClickListener { }
        myProfileFragEditProfileBtn.setOnClickListener { (activity as MainActivity).loadEditMyProfile() }

        myProfileFragFavorites.setFavoritesViewListener(this)
//        myProfileFragPromoCodesBtn.setOnClickListener { }

        myProfileFragLocationSettingsBtn.setOnClickListener { (activity as MainActivity).loadSettingsFragment() }
        myProfileFragPrivacyBtn.setOnClickListener { WebDocsDialog(Constants.WEB_DOCS_PRIVACY).show(childFragmentManager, Constants.WEB_DOCS_DIALOG_TAG) }
        myProfileFragSupportBtn.setOnClickListener { (activity as MainActivity).loadSupport() }

        myProfileFragJoinBtn.setOnClickListener { OpenWoodSpoonGooglePlay() }
        myProfileFragEventsBtn.setOnClickListener { joinEvent() }
        myProfileFragShareBtnLayout.setOnClickListener { share() }

        myProfileFragOrderHistoryBtn.setOnClickListener { openOrderHistoryDialog() }

        profileFragLogout.setOnClickListener {
            LogoutDialog(this).show(childFragmentManager, Constants.LOGOUT_DIALOG_TAG)
        }
    }

    private fun joinEvent() {
        (activity as MainActivity).startEventActivity()
    }

    private fun OpenWoodSpoonGooglePlay() {
        val url = "https://play.google.com/store/apps/details?id=com.bupp.wood_spoon_chef"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    private fun share() {
        val text = myProfileViewModel.getShareText()
        Utils.shareText(requireActivity(), text)
    }

//    fun loadPromoCodeDialog() {
//        PromoCodeFragment()
//    }

    override fun logout() {
        myProfileViewModel.logout()
    }

    private fun openOrderHistoryDialog() {
        (activity as MainActivity).loadOrderHistoryFragment()
    }

    override fun onChangeLocationClick() {
//        (activity as MainActivity).openAddressChooser()
    }

    override fun onChangePaymentClick() {
        (activity as MainActivity).startPaymentMethodActivity()
    }

    private fun handleCustomerCards(paymentMethods: List<PaymentMethod>?) {
        if(paymentMethods != null && paymentMethods.size!! > 0){
            val defaultPayment = paymentMethods[0]
            updateCustomerPaymentMethod(defaultPayment)
//            viewModel.attachCardToCustomer(defaultPayment.id!!)
        }else{
            setEmptyPaymentMethod()
        }
    }

    fun updateCustomerPaymentMethod(paymentMethod: PaymentMethod) {
        val card = paymentMethod.card
        if(card != null){
            Log.d("wowMyProfile","updateCustomerPaymentMethod: ${paymentMethod.id}")
            myProfileFragEditPayment.updateDeliveryDetails("Selected Card: (${card.brand} ${card.last4})")
            myProfileFragEditPayment.setChangeable(true)
            myProfileViewModel.updateUserCustomerCard(paymentMethod)
        }
    }

    private fun setEmptyPaymentMethod() {
        myProfileFragEditPayment.updateDeliveryDetails("Insert payment method")
        myProfileFragEditPayment.setChangeable(true)
    }

    fun onAddressChooserSelected() {
        myProfileViewModel.getUserDetails()
    }

    override fun onWorldwideInfoClick() {
        NationwideShippmentInfoDialog().show(childFragmentManager, Constants.NATIONWIDE_SHIPPING_INFO_DIALOG)
    }
}
