package com.bupp.wood_spoon_eaters.features.main.feed

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.TimePickerBottomSheet
import com.bupp.wood_spoon_eaters.custom_views.feed_view.MultiSectionFeedView
import com.bupp.wood_spoon_eaters.dialogs.NationwideShippmentInfoDialog
import com.bupp.wood_spoon_eaters.features.main.cook_profile.CookProfileDialog
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.FragmentFeedBinding
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.utils.Utils
import com.bupp.wood_spoon_eaters.views.feed_header.FeedHeaderView
import com.segment.analytics.Analytics
import it.sephiroth.android.library.xtooltip.ClosePolicy
import it.sephiroth.android.library.xtooltip.Tooltip
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class FeedFragment : Fragment(R.layout.fragment_feed), MultiSectionFeedView.MultiSectionFeedViewListener,
    CookProfileDialog.CookProfileDialogListener, FeedHeaderView.FeedHeaderViewListener {


    override fun onEmptyhDishList() {
        handleBannerEvent(Constants.BANNER_NO_AVAILABLE_DISHES)
//        NoDishesAvailableDialog().show(childFragmentManager, Constants.NO_DISHES_AVAILABLE_DIALOG)
    }


    companion object {
        fun newInstance() = FeedFragment()
        const val TAG = "wowFeedFragment"
    }

    private var tooltip: Tooltip? = null
    val binding: FragmentFeedBinding by viewBinding()
    private val viewModel: FeedViewModel by viewModel<FeedViewModel>()
    private val mainViewModel by sharedViewModel<MainViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Analytics.with(requireContext()).screen("Feed")
        initUi()

        initObservers()

        viewModel.initFeed()
        binding.feedFragPb.show()

    }


    private fun initUi() {
        binding.feedFragHeader.setFeedHeaderViewListener(this)
        binding.feedFragSectionsView.setMultiSectionFeedViewListener(this)
    }

    private fun initObservers() {
        viewModel.getFinalAddressParams().observe(viewLifecycleOwner, {
            binding.feedFragHeader.setAddress(it?.shortTitle)
        })
        viewModel.getDeliveryTimeLiveData().observe(viewLifecycleOwner, {
            binding.feedFragHeader.setDate(it?.deliveryDateUi)
        })










        viewModel.feedUiStatusLiveData.observe(viewLifecycleOwner, {
            handleFeedBannerUi(it)
        })
        viewModel.getLocationLiveData().observe(viewLifecycleOwner, {
            Log.d(TAG, "getLocationLiveData observer called ")
            viewModel.refreshFeedByLocationIfNeeded()
        })
        viewModel.getDeliveryTimeLiveData().observe(viewLifecycleOwner, {
            Log.d(TAG, "getLocationLiveData observer called ")
            viewModel.onPullToRefresh()
        })
        viewModel.getFinalAddressParams().observe(viewLifecycleOwner, {
            viewModel.refreshFeedForNewAddress(Address(id = it.id, lat = it.lat, lng = it.lng))
            viewModel.refreshFavorites()
        })
        viewModel.feedResultData.observe(viewLifecycleOwner, { event ->
            if (event.isSuccess) {
                event.feedArr?.let { initFeed(it) }
            }
        })
        viewModel.campaignLiveData.observe(viewLifecycleOwner, { campaigns ->
            handleShareCampaign(campaigns)
        })
        viewModel.favoritesLiveData.observe(viewLifecycleOwner, {
            binding.feedFragSectionsView.initFavorites(it)
        })
        viewModel.progressData.observe(viewLifecycleOwner, {
            if (it) {
                binding.feedFragPb.show()
            } else {
                binding.feedFragPb.hide()
            }
        })
        mainViewModel.onFloatingBtnHeightChange.observe(viewLifecycleOwner, {
            binding.feedFragHeightCorrection.isVisible = isVisible
        })
//        mainViewModel.campaignUpdateEvent.observe(viewLifecycleOwner, {
//            Log.d(TAG, "campaign: $it")
//            mainViewModel.checkCampaignForFeed()
//        })
    }

    override fun onHeaderAddressClick() {
        mainViewModel.handleMainNavigation(MainViewModel.MainNavigationEvent.START_LOCATION_AND_ADDRESS_ACTIVITY)
    }

    override fun onHeaderDateClick() {
        val timePickerBottomSheet = TimePickerBottomSheet()
        timePickerBottomSheet.show(childFragmentManager, Constants.TIME_PICKER_BOTTOM_SHEET)
    }

    private fun handleShareCampaign(campaigns: List<Campaign>?) {
        campaigns?.let {
            campaigns.forEach { campaign ->
                campaign.viewTypes?.forEach { viewType ->
                    when (viewType) {
                        CampaignViewType.FEED -> {
                            binding.feedFragSectionsView.initShareCampaign(campaign)
                        }
                    }
                }
            }
        }
    }

    private fun handleFeedBannerUi(feedUiStatus: FeedUiStatus?) {
        feedUiStatus?.let {
            Log.d(TAG, "handleFeedUi: ${it.type}")
            when (it.type) {
                FeedUiStatusType.CURRENT_LOCATION, FeedUiStatusType.KNOWN_ADDRESS, FeedUiStatusType.HAS_LOCATION -> {
                    handleBannerEvent(Constants.NO_BANNER)
                }
                FeedUiStatusType.KNOWN_ADDRESS_WITH_BANNER -> {
                    handleBannerEvent(Constants.BANNER_KNOWN_ADDRESS)
                }
                FeedUiStatusType.NO_GPS_ENABLED_AND_NO_LOCATION -> {
                    handleBannerEvent(Constants.BANNER_MY_LOCATION)
                }
                FeedUiStatusType.HAS_GPS_ENABLED_BUT_NO_LOCATION -> {
                    handleBannerEvent(Constants.BANNER_NO_GPS)
                }
                else -> {
                }
            }
        }
    }

    override fun refreshList() {
        viewModel.onPullToRefresh()
    }


    override fun onDishClick(menuItemId: Long) {
        mainViewModel.onDishClick(menuItemId)
    }

    private fun initFeed(feedArr: List<Feed>) {
        if (feedArr.isEmpty()) {
//            showEmptyLayout()
            handleBannerEvent(Constants.BANNER_NO_AVAILABLE_DISHES)
        } else {
            binding.feedFragEmptyLayout.visibility = View.GONE
            binding.feedFragListLayout.visibility = View.VISIBLE
            binding.feedFragSectionsView.initFeed(feedArr, stubView = Constants.FEED_VIEW_STUB_SHARE)
        }
        binding.feedFragPb.hide()
    }


    @SuppressLint("SetTextI18n")
    private fun showEmptyLayout() {
        with(binding) {
            feedFragListLayout.visibility = View.GONE
            feedFragEmptyLayout.visibility = View.VISIBLE
//            feedFragEmptyFeedTitle.text = "Hey ${viewModel.getEaterFirstName() ?: "Guest"}"
            feedFragEmptyLayout.setOnClickListener {
                mainViewModel.startLocationAndAddressAct()
            }
        }
    }

    private fun handleBannerEvent(bannerType: Int) {
        bannerType.let {
            Log.d(TAG, "handleBannerEvent: $bannerType")
            when (bannerType) {
                Constants.NO_BANNER -> {
                    tooltip?.dismiss()
                }
                Constants.BANNER_KNOWN_ADDRESS -> {
                    showBanner(getString(R.string.banner_known_address))
                }
                Constants.BANNER_MY_LOCATION -> {
                    showBanner(getString(R.string.banner_my_location))
                }
                Constants.BANNER_NO_GPS -> {
                    showBanner(getString(R.string.banner_no_gps))
                }
                Constants.BANNER_NO_AVAILABLE_DISHES -> {
                    showBanner(getString(R.string.banner_no_available_dishes))
                }
                else -> {
                }
            }
        }
    }

    private fun showBanner(text: String) {
        with(binding) {
            tooltip = Tooltip.Builder(requireContext())
                .anchor(feedFragHeader, 0, -30, true)
                .text(text)
                .arrow(true)
                .floatingAnimation(Tooltip.Animation.SLOW)
                .closePolicy(ClosePolicy.TOUCH_ANYWHERE_CONSUME)
                .overlay(false)
                .maxWidth(feedFragHeader.measuredWidth - 50)
                .create()

            tooltip!!
                .doOnHidden { }
                .doOnFailure { }
                .doOnShown { }
                .show(feedFragHeader, Tooltip.Gravity.BOTTOM, false)
        }

    }


    override fun onDishClick(dish: Dish) {
        dish.menuItem?.let {
            mainViewModel.onDishClick(it.id)
        }
    }

    override fun onCookClick(cook: Cook) {
        Analytics.with(requireContext()).screen("Home chef page (from feed)")
        val args = Bundle()
        args.putLong(Constants.ARG_COOK_ID, cook.id)
        val cookDialog = CookProfileDialog(this)
        cookDialog.arguments = args
        cookDialog.show(childFragmentManager, Constants.COOK_PROFILE_DIALOG_TAG)
    }

    override fun onShareClick(campaign: Campaign) {
        mainViewModel.onShareCampaignClick(campaign)
    }

    override fun onWorldwideInfoClick() {
        NationwideShippmentInfoDialog().show(childFragmentManager, Constants.NATIONWIDE_SHIPPING_INFO_DIALOG)
    }

    fun silentRefresh() {
        Log.d("wowFeedFrag", "silentRefresh")
//        viewModel.getFeed()
    }




}