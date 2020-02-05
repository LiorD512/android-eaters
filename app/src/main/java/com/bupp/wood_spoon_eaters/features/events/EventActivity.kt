package com.bupp.wood_spoon_eaters.features.events

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.features.events.event_feed.EventFeedFragment
import com.bupp.wood_spoon_eaters.features.events.event_validation.GetEventByIdFragment
import com.bupp.wood_spoon_eaters.features.login.LoginActivity
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.Event
import com.bupp.wood_spoon_eaters.utils.Constants
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderActivity
import kotlinx.android.synthetic.main.activity_event.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class EventActivity : AppCompatActivity(), HeaderView.HeaderViewListener {

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
    }

    private fun initUi() {
        eventActHeaderView.setHeaderViewListener(this)
        loadGetEventFragment()
        setHeaderViewLocationDetails(viewModel.getLastOrderTime(), viewModel.getCurrentAddress())
    }

    fun setHeaderViewLocationDetails(time: String? = null, location: Address? = null) {
        eventActHeaderView.setLocationTitle(time, location?.streetLine1)
    }

    private fun loadGetEventFragment() {
        loadFragment(GetEventByIdFragment.newInstance(), Constants.GET_EVENT_TAG)
        eventActHeaderView.setType(Constants.HEADER_VIEW_TYPE_CLOSE_TITLE_DONE, "Join Event")
    }

    private fun loadEventFeed() {
        loadFragment(EventFeedFragment.newInstance(), Constants.EVENT_FEED_TAG)
        eventActHeaderView.setType(Constants.HEADER_VIEW_TYPE_EVENT)
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

    override fun onHeaderDoneClick() {
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
            Intent(this, NewOrderActivity::class.java).putExtra("menuItemId", id),
            Constants.NEW_ORDER_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.NEW_ORDER_REQUEST_CODE) {
            finish()
//            loadFeed()
//            checkForActiveOrder()
//            checkCartStatus()
        }
    }

}
