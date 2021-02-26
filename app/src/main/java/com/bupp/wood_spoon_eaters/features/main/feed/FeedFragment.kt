package com.bupp.wood_spoon_eaters.features.main.feed

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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
    }



    private val TAG = "wowFeedFragment"
    private val viewModel: FeedViewModel by viewModel<FeedViewModel>()
    private val mainViewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Analytics.with(requireContext()).screen("Feed")
        initUi()

        showEmptyLayout()
        initObservers()

        viewModel.initFeed()
    }

    private fun initUi() {
        feedFragSectionsView.setMultiSectionFeedViewListener(this)
    }

    private fun initObservers() {
        viewModel.feedUiStatusLiveData.observe(viewLifecycleOwner, {
            handleFeedUi(it)
        })
        viewModel.getLocationLiveData().observe(viewLifecycleOwner, {
            Log.d(TAG, "getLocationLiveData observer called ")
            viewModel.refreshFeedByLocationIfNeeded()
        })
        viewModel.getFinalAddressParams().observe(viewLifecycleOwner, {
            viewModel.refreshFeedForNewAddress(Address(id = it.id, lat = it.lat, lng = it.lng))
        })
        viewModel.feedResultData.observe(viewLifecycleOwner, { event ->
            if(event.isSuccess){
                initFeed(event.feedArr!!)
            }
        })
        viewModel.getCookEvent.observe(viewLifecycleOwner, { cook ->
            cook?.let{
                    Analytics.with(requireContext()).screen("Home chef page (from feed)")
                CookProfileDialog(this, it).show(childFragmentManager, Constants.COOK_PROFILE_DIALOG_TAG)
            }
        })
        viewModel.favoritesLiveData.observe(viewLifecycleOwner, {
            feedFragSectionsView.initFavorites(it)
        })
    }

    private fun handleFeedUi(feedUiStatus: FeedUiStatus?) {
        feedUiStatus?.let{
            Log.d(TAG, "handleFeedUi: ${it.type}")
            when(it.type){
                FeedUiStatusType.CURRENT_LOCATION, FeedUiStatusType.KNOWN_ADDRESS, FeedUiStatusType.HAS_LOCATION -> {
                    mainViewModel.showBanner(Constants.NO_BANNER)
                }
                FeedUiStatusType.KNOWN_ADDRESS_WITH_BANNER -> {
                    mainViewModel.showBanner(Constants.BANNER_KNOWN_ADDRESS)
                }
                FeedUiStatusType.NO_GPS_ENABLED_AND_NO_LOCATION -> {
                    mainViewModel.showBanner(Constants.BANNER_MY_LOCATION)
                }
                FeedUiStatusType.HAS_GPS_ENABLED_BUT_NO_LOCATION -> {
                    mainViewModel.showBanner(Constants.BANNER_NO_GPS)
                }
                else -> {}
            }
        }
    }

    override fun refreshList() {
        viewModel.initFeed()
    }


    override fun onDishClick(menuItemId: Long) {
        mainViewModel.onDishClick(menuItemId)
    }

    private fun initFeed(feedArr: List<Feed>) {
        feedFragEmptyLayout.visibility = View.GONE
        feedFragListLayout.visibility = View.VISIBLE
        feedFragSectionsView.initFeed(feedArr, stubView = Constants.FEED_VIEW_STUB_SHARE)
    }


    @SuppressLint("SetTextI18n")
    private fun showEmptyLayout() {
        feedFragEmptyLayout.visibility = View.VISIBLE
        feedFragEmptyFeedTitle.text = "Hey ${viewModel.getEaterFirstName() ?: "Guest"}"
        feedFragEmptyLayout.setOnClickListener {
            mainViewModel.startLocationAndAddressAct()
        }
    }


    override fun onDishClick(dish: Dish) {
        dish.menuItem?.let{
            mainViewModel.onDishClick(it.id)
        }
    }

    override fun onCookClick(cook: Cook) {
        viewModel.getCurrentCook(cook.id)
    }

    override fun onShareClick() {
//        val text = viewModel.getShareText()
//        activity?.let { Utils.shareText(it, text) }
    }

    override fun onWorldwideInfoClick() {
        NationwideShippmentInfoDialog().show(childFragmentManager, Constants.NATIONWIDE_SHIPPING_INFO_DIALOG)
    }

    fun silentRefresh() {
        Log.d("wowFeedFrag","silentRefresh")
//        viewModel.getFeed()
    }

}