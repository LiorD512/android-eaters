package com.bupp.wood_spoon_eaters.features.events

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.features.events.event_feed.EventFeedFragment
import com.bupp.wood_spoon_eaters.features.events.event_validation.GetEventByIdFragment
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.utils.Constants
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.custom_views.orders_bottom_bar.OrdersBottomBar
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.ActiveOrderTrackerDialog
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderActivity
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.activity_event.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class EventActivity : AppCompatActivity(), HeaderView.HeaderViewListener,
    ActiveOrderTrackerDialog.ActiveOrderTrackerDialogListener, OrdersBottomBar.OrderBottomBatListener {


    private var lastFragmentTag: String = ""
    private var currentFragmentTag: String = ""
    val viewModel by viewModel<EventActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        initUi()
        initObservers()
    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(this, Observer{ navEvent->
            when(navEvent){
                EventActivityViewModel.NavigationEvent.GET_EVENT_BY_ID_SUCCESS ->{
                    loadEventFeed()
                }
                else -> {}
            }
        })

        viewModel.getActiveOrders.observe(this, Observer { ordersEvent ->
            eventActPb.hide()
            if (ordersEvent.isSuccess) {
                eventActOrdersBB.handleBottomBar(showActiveOrders = true)
                if(ordersEvent.showDialog){
                    openActiveOrdersDialog(ordersEvent.orders!!)
                }
//                handleActiveOrderBottomBar(true)
            } else {
                eventActOrdersBB.handleBottomBar(showActiveOrders = false)
//                handleActiveOrderBottomBar(false)
            }
        })

        viewModel.checkCartStatus.observe(this, Observer { pendingOrderEvent ->
            if (pendingOrderEvent.hasPendingOrder) {
                eventActOrdersBB.handleBottomBar(showCheckout = true)
//                showCheckOutBottomBar(true)
            }else{
                eventActOrdersBB.handleBottomBar(showCheckout = false)
//                showCheckOutBottomBar(false)
            }
        })
    }

    private fun initUi() {
        eventActHeaderView.setHeaderViewListener(this)
        eventActOrdersBB.setOrdersBottomBarListener(this)
        loadGetEventFragment()
    }

    fun setHeaderViewLocationDetails(time: String? = null, location: Address? = null) {
        eventActHeaderView.setLocationTitle(time, location?.streetLine1)
    }

    private fun loadGetEventFragment() {
        loadFragment(GetEventByIdFragment.newInstance(), Constants.GET_EVENT_TAG)
        eventActHeaderView.setType(Constants.HEADER_VIEW_TYPE_CLOSE_TITLE_NEXT, "Join Event")
    }

    private fun loadEventFeed() {
        loadFragment(EventFeedFragment.newInstance(), Constants.EVENT_FEED_TAG)
        eventActHeaderView.setType(Constants.HEADER_VIEW_TYPE_EVENT)
        setHeaderViewLocationDetails(viewModel.getEventOrderTime(), viewModel.getEventCurrentAddress())
    }

    private fun loadFragment(fragment: Fragment, tag: String) {
        // todo - check status bar status??
        lastFragmentTag = currentFragmentTag
        currentFragmentTag = tag
        supportFragmentManager.beginTransaction()
            .replace(R.id.eventActContainer, fragment, tag)
            .commit()
    }

    private fun getFragmentByTag(tag: String): Fragment? {
        val fragmentManager = this@EventActivity.supportFragmentManager
        val fragments = fragmentManager.fragments
        for (fragment in fragments) {
            if (fragment.tag == tag)
                return fragment
        }
        return null
    }

    fun handlePb(shouldShow: Boolean?) {
        shouldShow?.let{
            if(it){
                eventActPb.show()
            }else{
                eventActPb.hide()
            }
        }
    }

    override fun onHeaderNextClick() {
        getFragmentByTag(Constants.GET_EVENT_TAG)?.let{
            (it as GetEventByIdFragment).sendEventCode()
        }
    }

    override fun onHeaderCloseClick() {
        finish()
    }

    fun handleDoneBtnState(isEnabled: Boolean) {
        eventActHeaderView.setDoneButtonClickable(isEnabled)
    }

    fun loadNewOrderActivity(id: Long) {
        Log.d("wowEventAct","loadNewOrderActivity $id")
        startActivityForResult(
            Intent(this, NewOrderActivity::class.java).putExtra("menuItemId", id).putExtra("isEvent", true),
            Constants.NEW_ORDER_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.NEW_ORDER_REQUEST_CODE) {
            checkForActiveOrder()
            checkCartStatus()
        }
    }

    fun checkCartStatus(){
        viewModel.checkCartStatus()
    }

    override fun onBottomBarOrdersClick(type: Int) {
        viewModel.checkForActiveOrder(true)
    }

    override fun onBottomBarCheckoutClick() {
        startActivityForResult(
            Intent(this, NewOrderActivity::class.java).putExtra("isCheckout", true).putExtra("isEvent", true),
            Constants.NEW_ORDER_REQUEST_CODE
        )
    }

//    private fun showCheckOutBottomBar(shouldShow: Boolean) {
//        if(shouldShow){
//            eventActCheckoutBB.visibility = View.VISIBLE
//            eventActCheckoutBB.setOnClickListener { loadNewOrderActivityCheckOut() }
//        }else{
//            eventActCheckoutBB.visibility = View.GONE
//        }
////        handleContainerPadding()
//    }

//    fun loadNewOrderActivityCheckOut() {
//
//    }

//    private fun handleActiveOrderBottomBar(shouldShow: Boolean) {
//        if (shouldShow) {
//            Log.d("wowMain", "show bottom bar")
//            handleContainerPadding()
//            eventActActiveOrderBB.visibility = View.VISIBLE
//            eventActActiveOrderBB.setOnClickListener {
//                eventActPb.show()
//                viewModel.checkForActiveOrder()
//            }
//        } else {
//            Log.d("wowMain", "hide bottom bar")
//            handleContainerPadding()
//            eventActActiveOrderBB.visibility = View.GONE
//        }
//    }

//    private fun handleContainerPadding() {
//        val padding = viewModel.getContainerPaddingBottom()
//        eventActContainer.setPadding(0,0,0, padding)
//    }

    fun checkForActiveOrder() {
        viewModel.checkForActiveOrder()
    }

    private fun openActiveOrdersDialog(orders: ArrayList<Order>) {
        ActiveOrderTrackerDialog.newInstance(orders).show(supportFragmentManager, Constants.TRACK_ORDER_DIALOG_TAG)
    }

    override fun onContactUsClick() {
        Utils.callPhone(this)
    }

    override fun onDestroy() {
        viewModel.removeEventData()
        super.onDestroy()
    }

}
