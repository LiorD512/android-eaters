//package com.bupp.wood_spoon_chef.presentation.features.main.events.sub_screen.join_event
//
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.Observer
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.bupp.wood_spoon_chef.R
//import com.bupp.wood_spoon_chef.presentation.custom_views.adapters.DividerItemDecorator
//import com.bupp.wood_spoon_chef.databinding.FragmentMainEventsBinding
//import com.bupp.wood_spoon_chef.presentation.features.main.events.EventsActivity
//import com.bupp.wood_spoon_chef.presentation.features.main.events.MainEventsViewModel
//import com.bupp.wood_spoon_chef.model.Event
//import org.koin.androidx.viewmodel.ext.android.viewModel
//import java.util.ArrayList
//
//
//class MainEventsFragment : Fragment(), EventsAdapter.EventsAdapterListener {
//
//    lateinit var binding: FragmentMainEventsBinding
//    private lateinit var adapter: EventsAdapter
//    val viewModel by viewModel<MainEventsViewModel>()
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_main_events, container, false)
//    }
//
//    companion object {
//        fun newInstance() = MainEventsFragment()
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding = FragmentMainEventsBinding.bind(view)
//        viewModel.getEvents()
//        initObservers()
//    }
//
//    private fun initObservers() {
//        viewModel.getEventsEvent.observe(this, Observer { events ->
//            events.curEvents?.let {
//                handleEvents(events.curEvents)
//            }
//        })
//    }
//
//    private fun handleEvents(curEvents: ArrayList<Event>) {
//        with(binding) {
//            joinEventFragList.layoutManager = LinearLayoutManager(context)
//            adapter = EventsAdapter(requireContext(), curEvents, this@MainEventsFragment)
//            joinEventFragList.adapter = adapter
//
//            joinEventFragList.addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(requireContext(), R.drawable.divider)))
//        }
//    }
//
//    override fun onEventClicked(event: Event) {
//        if(event.invitation != null){
//            when(event.invitation.status){
//                "active" -> {
//                    (activity as EventsActivity).onJoinEventDone(event)
//                }
//                "pending" -> {
//                    (activity as EventsActivity).loadEventCodeValidation(event)
//                }
//            }
//        }else{
//            (activity as EventsActivity).loadEventCodeValidation(event)
//        }
//    }
//
//}
