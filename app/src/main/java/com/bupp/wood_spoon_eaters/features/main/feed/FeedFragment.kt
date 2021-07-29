package com.bupp.wood_spoon_eaters.features.main.feed

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.TimePickerBottomSheet
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.feed_view.MultiSectionFeedView
import com.bupp.wood_spoon_eaters.databinding.FragmentFeedBinding
import com.bupp.wood_spoon_eaters.dialogs.NationwideShippmentInfoDialog
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.features.main.cook_profile.CookProfileDialog
import com.bupp.wood_spoon_eaters.features.main.feed.adapter.FeedMainAdapter
import com.bupp.wood_spoon_eaters.features.restaurant.RestaurantActivity
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

//    private lateinit var skeletonView: Skeleton
    private var tooltip: Tooltip? = null
    val binding: FragmentFeedBinding by viewBinding()
    private val viewModel: FeedViewModel by viewModel<FeedViewModel>()
    private val mainViewModel by sharedViewModel<MainViewModel>()

    private lateinit var feedAdapter: FeedMainAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Analytics.with(requireContext()).screen("Feed")
        initUi()

        initObservers()

        viewModel.initFeed()
    }


    private fun initUi() {
        with(binding){
            feedFragHeader.setFeedHeaderViewListener(this@FeedFragment)
            feedAdapter = FeedMainAdapter(getFeedMainAdapterListener())
            feedFragList.layoutManager = LinearLayoutManager(requireContext())
            feedFragList.adapter = feedAdapter

            feedFragRefreshLayout.setOnRefreshListener { refreshList() }



//            feedFragRefreshLayout.setOnRefreshListener { refreshlayout ->
//                refreshList()
//            }
//            feedFragRefreshLayout.setOnLoadMoreListener { refreshlayout ->
////                refreshlayout.finishLoadMore(2000 /*,false*/) //传入false表示加载失败
//            }
        }
    }


    private fun initObservers() {
        viewModel.getFinalAddressParams().observe(viewLifecycleOwner, {
            binding.feedFragHeader.setAddress(it?.shortTitle)
        })
        viewModel.getDeliveryTimeLiveData().observe(viewLifecycleOwner, {
            binding.feedFragHeader.setDate(it?.deliveryDateUi)
        })
        viewModel.feedSkeletonEvent.observe(viewLifecycleOwner, {
            it.feedData?.let { skeletons ->
//                feedAdapter.submitList(skeletons)
                handleFeedResult(skeletons)
            }
        })
        viewModel.feedResultData.observe(viewLifecycleOwner, { event ->
            event.feedData?.let { handleFeedResult(it) }
        })







        viewModel.feedUiStatusLiveData.observe(viewLifecycleOwner, {
            handleFeedBannerUi(it)
        })
        viewModel.getLocationLiveData().observe(viewLifecycleOwner, {
            Log.d(TAG, "getLocationLiveData observer called")
            viewModel.refreshFeedByLocationIfNeeded()
        })
        viewModel.getDeliveryTimeLiveData().observe(viewLifecycleOwner, {
            Log.d(TAG, "getLocationLiveData observer called")
            refreshList()
        })
        viewModel.getFinalAddressParams().observe(viewLifecycleOwner, {
            viewModel.refreshFeedForNewAddress(Address(id = it.id, lat = it.lat, lng = it.lng))
            viewModel.refreshFavorites()
        })

        viewModel.campaignLiveData.observe(viewLifecycleOwner, { campaigns ->
            handleShareCampaign(campaigns)
        })
//        viewModel.favoritesLiveData.observe(viewLifecycleOwner, {
//            binding.feedFragSectionsView.initFavorites(it)
//        })
        viewModel.progressData.observe(viewLifecycleOwner, { isLoading ->
            if (isLoading) {
//                skeletonView.showSkeleton()
            } else {

            }
        })
        mainViewModel.onFloatingBtnHeightChange.observe(viewLifecycleOwner, {
            binding.feedFragList.setPadding(0, Utils.toPx(16), 0, Utils.toPx(80))
        })
//        mainViewModel.campaignUpdateEvent.observe(viewLifecycleOwner, {
//            Log.d(TAG, "campaign: $it")
//            mainViewModel.checkCampaignForFeed()
//        })
    }
    private fun getFeedMainAdapterListener(): FeedMainAdapter.FeedMainAdapterListener =
        object: FeedMainAdapter.FeedMainAdapterListener {
            override fun onRestaurantClick(cook: Cook) {
                startActivity(Intent(requireContext(), RestaurantActivity::class.java)
                    .putExtra(Constants.ARG_RESTAURANT, cook)
                )
            }
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
//                            binding.feedFragSectionsView.initShareCampaign(campaign)
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

    private fun handleFeedResult(feedArr: List<FeedAdapterItem>) {
        if (feedArr.isEmpty()) {
            showEmptyLayout()
            handleBannerEvent(Constants.BANNER_NO_AVAILABLE_DISHES)
        } else {
            binding.feedFragRefreshLayout.isRefreshing = false
            binding.feedFragEmptyLayout.visibility = View.GONE
            feedAdapter.submitList(feedArr)
        }
    }

    private fun initSkeletons() {
//        skeletonView = binding.feedFragList.applySkeleton(
//            R.layout.feed_adapter_restaurant_item_skeleton,
//            15,
//            shimmerColor = ContextCompat.getColor(
//                requireContext(),
//                R.color.dark_40
//            ),
//            showShimmer = true,
//            shimmerDurationInMillis = 1000L
//        )
    }


    @SuppressLint("SetTextI18n")
    private fun showEmptyLayout() {
        with(binding) {
            feedFragList.visibility = View.GONE
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
//                    tooltip?.dismiss()
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
                .closePolicy(ClosePolicy.TOUCH_INSIDE_NO_CONSUME)
                .fadeDuration(500)
                .showDuration(10000)
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