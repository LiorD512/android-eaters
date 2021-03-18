package com.bupp.wood_spoon_eaters.features.main.feed

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.feed_view.MultiSectionFeedView
import com.bupp.wood_spoon_eaters.dialogs.NoDishesAvailableDialog
import com.bupp.wood_spoon_eaters.dialogs.NationwideShippmentInfoDialog
import com.bupp.wood_spoon_eaters.features.main.cook_profile.CookProfileDialog
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.utils.Utils
import com.segment.analytics.Analytics
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_feed.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class FeedFragment : Fragment(), MultiSectionFeedView.MultiSectionFeedViewListener,
    CookProfileDialog.CookProfileDialogListener {


    override fun onEmptyhDishList() {
        NoDishesAvailableDialog().show(childFragmentManager, Constants.NO_DISHES_AVAILABLE_DIALOG)
    }


    companion object {
        fun newInstance() = FeedFragment()
        const val TAG = "wowFeedFragment"
    }


    private val viewModel: FeedViewModel by viewModel<FeedViewModel>()
    private val mainViewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Analytics.with(requireContext()).screen("Feed")
        initUi()

        initObservers()

        viewModel.initFeed()
        feedFragPb.show()
    }


    private fun initUi() {
        feedFragSectionsView.setMultiSectionFeedViewListener(this)
    }

    private fun initObservers() {
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
            if(event.isSuccess){
                initFeed(event.feedArr!!)
            }
        })
        viewModel.favoritesLiveData.observe(viewLifecycleOwner, {
            feedFragSectionsView.initFavorites(it)
        })
        viewModel.progressData.observe(viewLifecycleOwner, {
            if(it){
                feedFragPb.show()
            }else{
                feedFragPb.hide()
            }
        })
    }

    private fun handleFeedBannerUi(feedUiStatus: FeedUiStatus?) {
        feedUiStatus?.let{
            Log.d(TAG, "handleFeedUi: ${it.type}")
            when(it.type){
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
                else -> {}
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
        if(feedArr.isEmpty()){
            showEmptyLayout()
        }else{
            feedFragEmptyLayout.visibility = View.GONE
            feedFragListLayout.visibility = View.VISIBLE
            feedFragSectionsView.initFeed(feedArr, stubView = Constants.FEED_VIEW_STUB_SHARE)
        }
        feedFragPb.hide()
    }


    @SuppressLint("SetTextI18n")
    private fun showEmptyLayout() {
        feedFragListLayout.visibility = View.GONE
        feedFragEmptyLayout.visibility = View.VISIBLE
        feedFragEmptyFeedTitle.text = "Hey ${viewModel.getEaterFirstName() ?: "Guest"}"
        feedFragEmptyLayout.setOnClickListener {
            mainViewModel.startLocationAndAddressAct()
        }
    }

    private fun handleBannerEvent(bannerType: Int) {
        bannerType.let{
            Log.d(TAG, "handleBannerEvent: $bannerType")
            when(bannerType){
                Constants.NO_BANNER -> {
                    feedFragHeaderError.visibility = View.GONE
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
                else -> {}
            }
        }
    }

    private fun showBanner(text: String) {
        feedFragHeaderError.text = text
        feedFragHeaderError.visibility = View.VISIBLE
        feedFragHeaderError.setOnClickListener {
            feedFragHeaderError.visibility = View.GONE
        }
//        tooltip = Tooltip.Builder(this)
//            .anchor(headerCard, 0, -30, true)
//            .text(text)
//            .arrow(true)
//            .floatingAnimation(Tooltip.Animation.SLOW)
//            .closePolicy(ClosePolicy.TOUCH_ANYWHERE_CONSUME)
//            .overlay(false)
//            .maxWidth(mainActHeaderView.measuredWidth-50)
//            .create()
//
//        tooltip!!
//            .doOnHidden { }
//            .doOnFailure { }
//            .doOnShown { }
//            .show(headerCard, Tooltip.Gravity.BOTTOM, false)
    }


    override fun onDishClick(dish: Dish) {
        dish.menuItem?.let{
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

    override fun onShareClick() {
        val text = mainViewModel.getShareText()
        activity?.let { Utils.shareText(it, text) }
    }

    override fun onWorldwideInfoClick() {
        NationwideShippmentInfoDialog().show(childFragmentManager, Constants.NATIONWIDE_SHIPPING_INFO_DIALOG)
    }

    fun silentRefresh() {
        Log.d("wowFeedFrag","silentRefresh")
//        viewModel.getFeed()
    }


}