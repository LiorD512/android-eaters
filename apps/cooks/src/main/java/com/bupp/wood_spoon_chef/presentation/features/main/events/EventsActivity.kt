//package com.bupp.wood_spoon_chef.presentation.features.main.events
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import com.bupp.wood_spoon_chef.R
//import com.bupp.wood_spoon_chef.presentation.custom_views.HeaderView
//import com.bupp.wood_spoon_chef.presentation.features.main.events.sub_screen.event_validation.EventValidationFragment
//import com.bupp.wood_spoon_chef.presentation.features.main.events.sub_screen.join_event.MainEventsFragment
//import com.bupp.wood_spoon_chef.model.Event
//import com.bupp.wood_spoon_chef.common.Constants
//
//class EventsActivity : AppCompatActivity(), HeaderView.HeaderViewListener {
//
////    val viewModel by viewModel<EventsViewModel>()
//    private var lastFragmentTag: String = ""
//    private var currentFragmentTag: String = ""
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_events)
//
//        eventsActHeaderView.setHeaderViewListener(this)
//        loadMainEventsFragment()
//
//    }
//
//
//    private fun loadFragment(fragment: Fragment, tag: String) {
//        lastFragmentTag = currentFragmentTag
//        currentFragmentTag = tag
//        supportFragmentManager.beginTransaction()
//                .replace(R.id.eventsActContainer, fragment, tag)
//                .commit()
//    }
//
//    fun getFragmentByTag(tag: String): Fragment? {
//        val fragmentManager = this@EventsActivity.getSupportFragmentManager()
//        val fragments = fragmentManager.getFragments()
//        if (fragments != null) {
//            for (fragment in fragments) {
//                if (fragment.getTag() == tag)
//                    return fragment
//            }
//        }
//        return null
//    }
//
//    fun loadMainEventsFragment() {
//        loadFragment(MainEventsFragment.newInstance(), Constants.EVENTS_JOIN_TAG)
//        eventsActHeaderView.setType(Constants.HEADER_VIEW_TYPE_TITLE_CLOSE, "Join Event")
//    }
//
//    fun loadEventCodeValidation(event: Event) {
//        loadFragment(EventValidationFragment.newInstance(event.id), Constants.EVENTS_VALIDATION_TAG)
//        eventsActHeaderView.setType(Constants.HEADER_VIEW_TYPE_TITLE_BACK_NEXT, event.title)
//    }
//
//    override fun onBackPressed() {
//        when (currentFragmentTag) {
//            Constants.EVENTS_VALIDATION_TAG -> {
//                loadMainEventsFragment()
//            }
//            else -> {
//                super.onBackPressed()
//            }
//        }
//    }
//
//    override fun onHeaderCloseClick() {
//        finish()
//    }
//
//    override fun onHeaderBackClick() {
//        super.onHeaderBackClick()
//        onBackPressed()
//    }
//
//    override fun onHeaderNextClick() {
//        if(currentFragmentTag.equals(Constants.EVENTS_VALIDATION_TAG)){
//            (getFragmentByTag(Constants.EVENTS_VALIDATION_TAG) as EventValidationFragment).sendEventCode()
//        }
//    }
//
//    fun handleNextBtnState(isEnabled: Boolean){
//        eventsActHeaderView.enableNextBtn(isEnabled)
//    }
//
//    fun handlePb(shouldShow: Boolean?) {
//        shouldShow?.let{
//            if(it){
//                eventsActPb.show()
//            }else{
//                eventsActPb.hide()
//            }
//        }
//    }
//
//    fun onJoinEventDone(event: Event) {
//        setResult(RESULT_OK, getIntent().putExtra("event",event));
//        finish()
//    }
//
//
//}
