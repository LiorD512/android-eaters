//package com.bupp.wood_spoon_chef.presentation.features.main.events.sub_screen.event_validation
//
//
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.util.Log
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.lifecycle.Observer
//import com.bupp.wood_spoon.dialogs.JoinEventSuccessDialog
//import com.bupp.wood_spoon_chef.R
//import com.bupp.wood_spoon_chef.presentation.features.main.events.EventsActivity
//import com.bupp.wood_spoon_chef.common.Constants
//import com.bupp.wood_spoon_chef.databinding.FragmentEventValidationBinding
//import org.koin.androidx.viewmodel.ext.android.viewModel
//
//
//class EventValidationFragment : Fragment(), JoinEventSuccessDialog.JoinEventSuccessListener {
//
//    var binding: FragmentEventValidationBinding? = null
//    val viewModel by viewModel<EventsValidationViewModel>()
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_event_validation, container, false)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        if (arguments != null) {
//            viewModel.curEventId = requireArguments().getLong(ARG_PARAM)
//        }
//    }
//
//    companion object {
//        private const val ARG_PARAM = "eventId"
//
//        fun newInstance(eventId: Long): EventValidationFragment {
//            val fragment = EventValidationFragment()
//            val args = Bundle()
//            args.putLong(ARG_PARAM, eventId)
//            fragment.arguments = args
//            return fragment
//        }
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding = FragmentEventValidationBinding.bind(view)
//
//
//        initUi()
//        initObservers()
//    }
//
//    private fun initObservers() {
//        viewModel.validateCode.observe(this, Observer { event ->
//            if(event.isSuccess){
//                event.event?.let{
//                    (activity as EventsActivity).onJoinEventDone(it)
//                }
//            }else{
//                binding!!.eventValidationFragError.visibility = View.VISIBLE
//                Log.d("wowEventValidation","request to join event failed")
//            }
//        })
//
//        viewModel.joinEvent.observe(this, Observer { event ->
//            if(event.isSuccess){
//                JoinEventSuccessDialog(this).show(childFragmentManager, Constants.JOIN_EVENT_SUCCESS_DIALOG)
//            }else{
//                Log.d("wowEventValidation","request to join event failed")
//            }
//        })
//
//        viewModel.progressData.observe(viewLifecycleOwner, Observer {
//            (activity as EventsActivity).handlePb(it)
//        })
//    }
//
//    override fun onJoinEventDismiss() {
//        (activity as EventsActivity).loadMainEventsFragment()
//    }
//
//    private fun initUi() {
//        binding!!.eventValidationFragRequest.setOnClickListener {
//            viewModel.requestToJoinEvent()
//        }
//
//        binding!!.eventValidationFragInput.addTextChangedListener(object: TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                s?.let {
//                    if(it.toString().length > 0){
//                        (activity as EventsActivity).handleNextBtnState(true)
//                    }else{
//                        (activity as EventsActivity).handleNextBtnState(false)
//                    }
//                }
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//            }
//
//        })
//
//    }
//
//    fun sendEventCode() {
//        val code = binding!!.eventValidationFragInput.text.toString()
//        viewModel.validateEventCode(code)
//    }
//
//
//}
