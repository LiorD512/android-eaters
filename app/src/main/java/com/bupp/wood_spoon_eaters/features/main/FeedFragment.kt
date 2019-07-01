//package com.bupp.wood_spoon_eaters.features.main
//
//import android.content.Context
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import com.bupp.wood_spoon_eaters.R
//import com.bupp.wood_spoon_eaters.custom_views.LocationDetailsView
//import com.bupp.wood_spoon_eaters.utils.Constants
//import kotlinx.android.synthetic.main.fragment_feed.*
//import org.koin.android.viewmodel.ext.android.viewModel
//
//
//class FeedFragment : Fragment(), LocationDetailsView.LocationDetailsViewListener {
//
//    private val viewModel: MainViewModel by viewModel<MainViewModel>()
//    private val LOCATION_PERMISSION: Array<String> = arrayOf(Constants.LOCATION_PERMISSION)
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_feed, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        initUi()
//        initEmptyView()
//    }
//
//    private fun initUi() {
//        feedFragEmptyFeedLayout.setOnClickListener{
//            onLocationClick()
//        }
//    }
//
//    private fun initEmptyView() {
//        feedFragEmptyFeedLayout.visibility = View.VISIBLE
//        feedFragEmptyFeedTitle.text = String.format("Hey %s", "Neta")
//        (activity as MainActivity).setHeaderViewLocationDetails("ASAP", "Address")
//        (activity as MainActivity).setHeaderViewLocationListener(this)
//    }
//
//    override fun onTimeChange(str: String?) {
//
//    }
//
//    override fun onLocationChange(str: String?) {
//
//    }
//
//    override fun onLocationClick() {
//        if (viewModel.checkPermission(activity as Context, LOCATION_PERMISSION)) {
//            (activity as MainActivity).showSupportDialog()
//        } else {
//            viewModel.requestPermission(activity!!, LOCATION_PERMISSION, Constants.LOCATION_PERMISSION_REQUEST_CODE)
//        }
//    }
//
//    override fun onTimeClick() {
//        Toast.makeText(context, "onTimeClick", Toast.LENGTH_SHORT).show()
//    }
//}