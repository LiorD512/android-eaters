package com.bupp.wood_spoon_eaters.features.events.event_feed


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.feed_view.MultiSectionFeedView
import com.bupp.wood_spoon_eaters.dialogs.NationwideShippmentInfoDialog
import com.bupp.wood_spoon_eaters.features.events.EventActivity
import com.bupp.wood_spoon_eaters.features.events.EventActivityViewModel
import com.bupp.wood_spoon_eaters.features.main.cook_profile.CookProfileDialog
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.utils.Constants
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.fragment_event_feed.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class EventFeedFragment : Fragment(), MultiSectionFeedView.MultiSectionFeedViewListener,
    CookProfileDialog.CookProfileDialogListener {

    val TAG = "EventFeedFragment"
    val viewModel by sharedViewModel<EventActivityViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_feed, container, false)
    }

    companion object {
        fun newInstance() = EventFeedFragment()

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObservers()

        checkForActiveOrder()

    }

    fun checkForActiveOrder() {
        viewModel.checkForActiveOrder()
    }

    private fun initUi() {
        eventFeedFeedView.setMultiSectionFeedViewListener(this)

        viewModel.initEvent()
    }


    private fun initObservers() {
        viewModel.initEvent.observe(this, Observer { event ->
            eventFeedPb.hide()
            event.event.let {
                initEventUi(event.event)
                initFeed(event.event.feed)
            }
        })
//
        viewModel.getCookEvent.observe(this, Observer { event ->
            eventFeedPb.hide()
            if(event.isSuccess){
                CookProfileDialog(this, event.cook!!).show(childFragmentManager, Constants.COOK_PROFILE_DIALOG_TAG)
            }
        })
    }

    //CookProfileDialog interface
    override fun onDishClick(menuItemId: Long) {
        (activity as EventActivity).loadNewOrderActivity(menuItemId)
    }

    private fun initEventUi(event: Event) {
        Glide.with(context!!).load(event.thumbnail).apply(RequestOptions.circleCropTransform()).into(eventFeedImage)
        eventFeedTitle.text = event.title
        eventFeedDescription.text = event.description
    }

    private fun initFeed(
        feedArr: ArrayList<Feed>?
    ) {
        feedArr?.let{
            eventFeedFeedView.initFeed(feedArr, false, Constants.FEED_VIEW_STUB_PROMO, false, true)
        }
    }

//    private fun FetchLocationAndFeed() {
//        Log.d(TAG, "feed fragment started")
//        eventFeedPb.show()
//        viewModel.getFeed()
//    }

    override fun onDishClick(dish: Dish) {
        dish.menuItem?.let{
            (activity as EventActivity).loadNewOrderActivity(it.id)
        }
    }

    override fun onCookClick(cook: Cook) {
        eventFeedPb.show()
        viewModel.getCurrentCook(cook.id)
    }

    override fun onShareClick() {
        val text = viewModel.getShareText()
        Utils.shareText(activity!!, text)
    }

    override fun refreshList(){

    }

    override fun onWorldwideInfoClick() {
        NationwideShippmentInfoDialog().show(childFragmentManager, Constants.NATIONWIDE_SHIPPING_INFO_DIALOG)
    }





}
