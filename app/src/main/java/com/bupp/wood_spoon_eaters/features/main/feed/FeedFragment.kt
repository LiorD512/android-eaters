package com.bupp.wood_spoon_eaters.features.main.feed

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.SingleColumnTimePickerBottomSheet
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.databinding.FragmentFeedBinding
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.features.main.feed.adapters.FeedMainAdapter
import com.bupp.wood_spoon_eaters.features.reviews.review_activity.ReviewActivity
import com.bupp.wood_spoon_eaters.model.RestaurantInitParams
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.utils.Utils
import com.bupp.wood_spoon_eaters.views.feed_header.FeedHeaderView
import it.sephiroth.android.library.xtooltip.ClosePolicy
import it.sephiroth.android.library.xtooltip.Tooltip
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class FeedFragment : Fragment(R.layout.fragment_feed),
     FeedHeaderView.FeedHeaderViewListener, FeedMainAdapter.FeedMainAdapterListener, SingleColumnTimePickerBottomSheet.TimePickerListener {


    companion object {
        fun newInstance() = FeedFragment()
        const val TAG = "wowFeedFragment"
    }

    private var tooltip: Tooltip? = null
    var binding: FragmentFeedBinding? = null
    private val viewModel: FeedViewModel by viewModel()
    private val mainViewModel by sharedViewModel<MainViewModel>()

    private lateinit var feedAdapter: FeedMainAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFeedBinding.bind(view)

        initUi()
        initObservers()

        viewModel.initFeed()
    }


    private fun initUi() {
        with(binding!!) {
            feedFragHeader.setFeedHeaderViewListener(this@FeedFragment)
            feedAdapter = FeedMainAdapter(this@FeedFragment)
            feedFragList.layoutManager = LinearLayoutManager(requireContext())
            feedFragList.adapter = feedAdapter

            feedFragRefreshLayout.setOnRefreshListener { refreshList() }
        }
    }


    private fun initObservers() {
        viewModel.getFinalAddressParams().observe(viewLifecycleOwner, {
            binding!!.feedFragHeader.setAddress(it?.shortTitle)
        })
        viewModel.feedSkeletonEvent.observe(viewLifecycleOwner, {
            it.feedData?.let { skeletons ->
                handleFeedResult(skeletons)
            }
        })
        viewModel.feedResultData.observe(viewLifecycleOwner, { event ->
            handleFeedUi(event.isLargeItems)
            event.feedData?.let { handleFeedResult(it) }
        })
        viewModel.feedUiStatusLiveData.observe(viewLifecycleOwner, {
            handleFeedBannerUi(it)
        })
        viewModel.getLocationLiveData().observe(viewLifecycleOwner, {
            Log.d(TAG, "getLocationLiveData observer called")
            viewModel.refreshFeedByLocationIfNeeded()
        })
        viewModel.getFinalAddressParams().observe(viewLifecycleOwner, {
            viewModel.refreshFeedForNewAddress(Address(id = it.id, lat = it.lat, lng = it.lng))
        })

        viewModel.campaignLiveData.observe(viewLifecycleOwner, { campaigns ->
            handleShareCampaign(campaigns)
        })
        mainViewModel.onFloatingBtnHeightChange.observe(viewLifecycleOwner, {
            binding!!.feedFragList.setPadding(0, Utils.toPx(16), 0, Utils.toPx(80))
        })
        mainViewModel.forceFeedRefresh.observe(viewLifecycleOwner, {
            viewModel.onPullToRefresh()
        })
        mainViewModel.scrollFeedToTop.observe(viewLifecycleOwner, {
            binding!!.feedFragList.smoothScrollToPosition(0)
        })
    }

    private fun handleFeedUi(isLargeItems: Boolean) {
        Log.d(TAG, "handleFeedUi: $isLargeItems")
        if(!isLargeItems){
            binding!!.feedFragList.enableSnapping(isLargeItems)
        }
    }

    override fun onHeaderAddressClick() {
        mainViewModel.handleMainNavigation(MainViewModel.MainNavigationEvent.START_LOCATION_AND_ADDRESS_ACTIVITY)
    }

    override fun onHeaderDateClick() {
        val timePickerBottomSheet = SingleColumnTimePickerBottomSheet(this)
        timePickerBottomSheet.setDatesFromNow(7)
        timePickerBottomSheet.show(childFragmentManager, Constants.TIME_PICKER_BOTTOM_SHEET)
    }

    override fun onTimerPickerChange(deliveryTimeParam: SingleColumnTimePickerBottomSheet.DeliveryTimeParam?) {
        viewModel.onTimePickerChanged(deliveryTimeParam)
        binding!!.feedFragHeader.setDate(deliveryTimeParam)
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
            }
        }
    }

    private fun refreshList() {
        viewModel.onPullToRefresh()
    }

    private fun handleFeedResult(feedArr: List<FeedAdapterItem>) {
        binding!!.feedFragRefreshLayout.isRefreshing = false
        feedAdapter.setDataList(feedArr)
//        binding.feedFragEmptyLayout.visibility = View.GONE
    }

    override fun onChangeAddressClick() {
        mainViewModel.startLocationAndAddressAct()
    }


//    @SuppressLint("SetTextI18n")
//    private fun showEmptyLayout() {
//        with(binding) {
//            feedFragList.visibility = View.GONE
//            feedFragEmptyLayout.visibility = View.VISIBLE
//            feedFragEmptyLayout.setOnClickListener {
//                mainViewModel.startLocationAndAddressAct()
//            }
//        }
//    }

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
        with(binding!!) {
            //.post call make sure the tool bar doesn't get called before the UI is ready to prevent crash
            feedFragHeader.post{
                tooltip = Tooltip.Builder(requireContext())
                    .anchor(feedFragHeader, 0, -30, true)
                    .text(text)
                    .arrow(true)
                    .closePolicy(ClosePolicy.TOUCH_INSIDE_NO_CONSUME)
                    .fadeDuration(250)
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
    }


    //Feed main adapter interface
    override fun onShareBannerClick(campaign: Campaign) {
        mainViewModel.onShareCampaignClick(campaign)
    }

    override fun onRestaurantClick(restaurantInitParams: RestaurantInitParams) {
        mainViewModel.startRestaurantActivity(restaurantInitParams)
    }

    override fun onDishSwiped() {
        viewModel.logEvent(Constants.EVENT_SWIPE_BETWEEN_DISHES)
    }

    override fun onRefreshFeedClick() {
        viewModel.onPullToRefresh()
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_FEED)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}