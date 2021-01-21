package com.bupp.wood_spoon_eaters.features.main.feed

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
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.features.main.cook_profile.CookProfileDialog
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.utils.Utils
import com.segment.analytics.Analytics
import kotlinx.android.synthetic.main.fragment_feed.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


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


//        viewModel.updateLocationStatus()

        showEmptyLayout()
        initObservers()
    }

    private fun initUi() {
        feedFragSectionsView.setMultiSectionFeedViewListener(this)
    }

    private fun initObservers() {
        viewModel.getFinalAddressLiveData.observe(viewLifecycleOwner, Observer{
            handleAddress(it)
        })

        viewModel.updateMainHeaderUiEvent.observe(viewLifecycleOwner, Observer{
            mainViewModel.refreshMainHeaderUi()
        })
        viewModel.oldfeedEventOld.observe(this, Observer { event ->
//            (activity as MainActivity).isFeedReady = true
            if(event.isSuccess){
                initFeed(event.feedArr!!)
            }
        })
        viewModel.getCookEvent.observe(this, Observer { event ->
            if(event.isSuccess){
                Analytics.with(requireContext()).screen("Home chef page (from feed)")
                CookProfileDialog(this, event.cook!!).show(childFragmentManager, Constants.COOK_PROFILE_DIALOG_TAG)
            }
        })
        viewModel.locationStatusEvent.observe(viewLifecycleOwner, Observer{
            Log.d("wowFeedFrag","feed location status: ${it.type}")
            handleLocationStatus(it)
        })
//        mainViewModel.getLocationLiveData().observe(viewLifecycleOwner, Observer {
//            Log.d("wowFeedFrag","getLocationLiveData observer called ")
//            doSomething(0)
////            viewModel.initAddressBasedUi(it)
////            viewModel.updateLocationStatus()
//        })
    }

    private fun doSomething(i: Int) {

    }

    private fun handleLocationStatus(status: LocationStatus){
        when(status.type){
            LocationStatusType.KNOWN_LOCATION_WITH_BANNER -> {
                handleAddress(status.address)
            }
            LocationStatusType.NO_GPS_ENABLED_AND_NO_LOCATION -> {}
            LocationStatusType.HAS_GPS_ENABLED_BUT_NO_LOCATION -> {}
        }
    }

    private fun handleAddress(address: Address?) {
        Log.d(TAG, "handleAddress $address")
        address?.let{
            viewModel.initAddressBasedUi(it)
        }
    }

    //CookProfileDialog interface
    override fun onDishClick(menuItemId: Long) {
        mainViewModel.onDishClick(menuItemId)
//        (activity as MainActivity).loadNewOrderActivity(menuItemId)
    }

    private fun initFeed(feedArr: ArrayList<Feed>) {
        feedFragEmptyLayout.visibility = View.GONE
        feedFragListLayout.visibility = View.VISIBLE
        feedFragSectionsView.initFeed(feedArr, stubView = Constants.FEED_VIEW_STUB_SHARE)
    }

    private fun FetchLocationAndFeed() {
//        Log.d(TAG, "feed fragment started")
//        feedFragPb.show()
//        viewModel.getFeed()
    }

    override fun refreshList() {
        FetchLocationAndFeed()
    }

    private fun showEmptyLayout() {
        feedFragEmptyLayout.visibility = View.VISIBLE
        feedFragEmptyFeedTitle.text =  String.format("Hey %s", viewModel.getEaterFirstName() ?: "Guest")
        feedFragEmptyLayout.setOnClickListener { startDeliveryDetails() }
    }

    private fun startDeliveryDetails() {
        (activity as MainActivity).loadDeliveryDetails()

    }

    override fun onDishClick(dish: Dish) {
        dish.menuItem?.let{
            (activity as MainActivity).loadNewOrderActivity(it.id)
        }
    }

    override fun onCookClick(cook: Cook) {
        viewModel.getCurrentCook(cook.id)
    }

    override fun onShareClick() {
        val text = viewModel.getShareText()
        activity?.let { Utils.shareText(it, text) }
    }

    override fun onWorldwideInfoClick() {
        NationwideShippmentInfoDialog().show(childFragmentManager, Constants.NATIONWIDE_SHIPPING_INFO_DIALOG)
    }

    fun silentRefresh() {
        Log.d("wowFeedFrag","silentRefresh")
        viewModel.getFeed()
    }

}