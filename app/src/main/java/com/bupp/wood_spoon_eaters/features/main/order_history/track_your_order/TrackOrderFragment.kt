//package com.bupp.wood_spoon_eaters.features.main.order_history.track_your_order
//
//import android.app.Dialog
//import android.content.Context
//import android.content.res.Resources
//import android.graphics.Bitmap
//import android.graphics.Canvas
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.DialogFragment
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.bupp.wood_spoon_eaters.R
//import com.bupp.wood_spoon_eaters.bottom_sheets.track_order.TrackOrderData
//import com.bupp.wood_spoon_eaters.common.Constants
//import com.bupp.wood_spoon_eaters.common.FlowEventsManager
//import com.bupp.wood_spoon_eaters.custom_views.HeaderView
//import com.bupp.wood_spoon_eaters.databinding.TrackOrderFragmentBinding
//import com.bupp.wood_spoon_eaters.dialogs.cancel_order.CancelOrderDialog
//import com.bupp.wood_spoon_eaters.features.track_your_order.ActiveOrderTrackerViewModel
//import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.*
//import com.bupp.wood_spoon_eaters.model.Order
//import com.bupp.wood_spoon_eaters.utils.Utils
//import com.bupp.wood_spoon_eaters.views.WSCounterEditText
//import com.google.android.gms.maps.CameraUpdateFactory
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.OnMapReadyCallback
//import com.google.android.gms.maps.SupportMapFragment
//import com.google.android.gms.maps.model.*
//import com.google.android.material.bottomsheet.BottomSheetBehavior
//import com.google.android.material.bottomsheet.BottomSheetDialog
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment
//import org.koin.androidx.viewmodel.ext.android.viewModel
//
//class TrackOrderFragment: Fragment(R.layout.track_order_fragment), WSCounterEditText.WSCounterListener, HeaderView.HeaderViewListener,
//    TrackOrderNewAdapter.TrackOrderNewAdapterListener, CancelOrderDialog.CancelOrderDialogListener {
//
//    val binding: TrackOrderFragmentBinding by viewBinding()
//
////    private var mMap: GoogleMap? = null
//    var curOrderId: Long? = null
//
//    var currentBoundSize = 100
//
//    private var adapter: TrackOrderNewAdapter? = null
//    val viewModel by viewModel<ActiveOrderTrackerViewModel>()
//
//
//    companion object {
//        private const val CUR_ORDER_ID_PARAM = "curOrderId"
//    }
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
////        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
//
//        curOrderId = requireArguments().getLong(CUR_ORDER_ID_PARAM)
//        Log.d("wowTrackOrderFragment", "newInstance ARGS: $curOrderId")
//
//        viewModel.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_TRACK_ORDER)
//        viewModel.logEvent(Constants.EVENT_CLICK_TRACK_YOUR_ORDER)
//    }
//
////    private lateinit var behavior: BottomSheetBehavior<View>
////    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
////        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
////        dialog.setOnShowListener {
////            val d = it as BottomSheetDialog
////            val sheet = d.findViewById<View>(R.id.design_bottom_sheet)
////            behavior = BottomSheetBehavior.from(sheet!!)
////            behavior.isFitToContents = true
////            behavior.isDraggable = true
////            behavior.state = BottomSheetBehavior.STATE_EXPANDED
////        }
////        return dialog
////    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
////        val parent = view.parent as View
////        parent.setBackgroundResource(R.drawable.top_cornered_30_bkg)
//
//        initUi()
////        initMap()
//        initUpdateObserver()
//
//        viewModel.getCurrentOrder(curOrderId)
//
//    }
//
////    private fun initMap() {
////
////        val mapFragment: SupportMapFragment = SupportMapFragment.newInstance()
////        val fragmentTransaction = childFragmentManager.beginTransaction()
////        fragmentTransaction.add(R.id.trackOrderMap, mapFragment)
////        fragmentTransaction.commit()
////
////        mapFragment.getMapAsync(this)
////    }
////
////
//////    override fun onMapReady(googleMap: GoogleMap?) {
//////        if(mMap == null){
//////            mMap = googleMap
//////            mMap?.uiSettings?.setAllGesturesEnabled(false)
//////            try {
//////                // Customise the styling of the base map using a JSON object defined
//////                // in a raw resource file.
//////                val success: Boolean = mMap!!.setMapStyle(
//////                    MapStyleOptions.loadRawResourceStyle(
//////                        requireContext(), R.raw.map_style
//////                    )
//////                )
//////                if (!success) {
//////                    Log.e("MapsActivityRaw", "Style parsing failed.")
//////                }
//////            } catch (e: Resources.NotFoundException) {
//////                Log.e("MapsActivityRaw", "Can't find style.", e)
//////            }
//////
////            viewModel.getCurrentOrder(curOrderId)
//////        }
//////    }
//////
//////    private fun updateMap(
//////        curOrderData: Order
//////    ) {
//////        mMap?.setOnMapLoadedCallback {
//////            val curCourierData  = curOrderData.courier
//////            val builder = LatLngBounds.Builder()
//////
//////            mMap?.clear()
//////
//////            val chefLat = curOrderData.restaurant?.pickupAddress?.lat
//////            val chefLng = curOrderData.restaurant?.pickupAddress?.lng
//////            chefLat?.let{
//////                chefLng?.let{
//////                    val chefLocation = LatLng(chefLat, chefLng)
//////                    mMap?.addMarker(MarkerOptions().position(chefLocation).icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_cook_marker)))
//////                    builder.include(chefLocation)
//////                    Log.d("wowMapBinder","chefLocation $chefLocation")
//////                }
//////            }
//////            val myLat = curOrderData.deliveryAddress?.lat
//////            val myLng = curOrderData.deliveryAddress?.lng
//////            myLat?.let{
//////                myLng?.let{
//////                    val myLocation = LatLng(myLat, myLng)
//////                    mMap?.addMarker(MarkerOptions().position(myLocation).icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_my_marker)))
//////                    builder.include(myLocation)
//////                    Log.d("wowMapBinder","myLocation $myLocation")
//////                }
//////            }
//////            curCourierData?.let{
//////                val courierLat = curCourierData.lat
//////                val courierLng = curCourierData.lng
//////                courierLat?.let{
//////                    courierLng?.let{
//////                        val courierLocation = LatLng(courierLat, courierLng)
//////                        mMap?.addMarker(MarkerOptions().position(courierLocation).icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_courier_marker)))
//////                        builder.include(courierLocation)
//////                        Log.d("wowMapBinder","courierLocation $courierLocation")
//////                    }
//////                }
//////            }
//////            val bounds = builder.build()
//////            //change mechnic to monig map by scroll and target bound on the courer or chef location
//////            animateCamera(bounds)
//////
//////        }
//////    }
//////
//////    private fun animateCamera(bounds: LatLngBounds?) {
//////        bounds?.let{
//////            try{
//////                mMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, currentBoundSize), 150, null)
//////                Log.d("wowTrackOrder","bound size: $currentBoundSize")
//////            }catch (ex: Exception){
//////                if(currentBoundSize > 100){
//////                    currentBoundSize -= 50
//////                    Log.d("wowTrackOrder","changing bound size: $currentBoundSize")
//////                    animateCamera(bounds)
//////                }else{
//////                    Log.d("wowTrackOrder","map ex: $ex")
//////
//////                }
//////            }
//////        }
//////    }
////
////    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
////        return ContextCompat.getDrawable(context, vectorResId)?.run {
////            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
////            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
////            draw(Canvas(bitmap))
////            BitmapDescriptorFactory.fromBitmap(bitmap)
////        }
////    }
//
//    private fun initUpdateObserver() {
//        viewModel.getCurrentOrderDetails.observe(viewLifecycleOwner, { result ->
//            result.order.let{
//                Log.d("wowTrackOrderFragment","updating orders")
//                updateOrderUi(it, result.userInfo)
////                updateMap(it)
//            }
//        })
//    }
//
//    override fun onContactUsClick(order: Order) {
//        val phone = viewModel.getContactUsPhoneNumber()
//        Utils.callPhone(requireActivity(), phone)
//    }
//
//    override fun onShareImageClick(order: Order) {
//        val text = viewModel.getShareText()
//        Utils.shareText(requireActivity(), text)
//    }
//
//    override fun onOrderCanceled(orderState: Int, orderId: Long) {
//        if(orderState <= 1){
//            CancelOrderDialog(Constants.CANCEL_ORDER_STAGE_1, curOrderId).show(childFragmentManager, Constants.CANCEL_ORDER_DIALOG_TAG)
//        }
//        if(orderState == 2){
//            CancelOrderDialog(Constants.CANCEL_ORDER_STAGE_2, curOrderId).show(childFragmentManager, Constants.CANCEL_ORDER_DIALOG_TAG)
//        }
//        if(orderState == 3){
//            CancelOrderDialog(Constants.CANCEL_ORDER_STAGE_3, curOrderId).show(childFragmentManager, Constants.CANCEL_ORDER_DIALOG_TAG)
//        }
//    }
//
//    override fun onOrderCanceled() {
////        dismiss()
//    }
//
//    private fun updateOrderUi(
//        order: Order,
//        userInfo: OrderUserInfo?){
//        val adapterDetails = OrderTrackDetails(order, userInfo)
//        val adapterProgress = OrderTrackProgress(order)
//
//        val data = mutableListOf<TrackOrderData<Any>>(
//            TrackOrderData(TrackOrderNewAdapter.VIEW_TYPE_DETAILS, adapterDetails),
//            TrackOrderData(TrackOrderNewAdapter.VIEW_TYPE_PROGRESS, adapterProgress, false)
//        )
//        adapter?.submitList(data)
////        mainAdapter.updateUi(order, userInfo)
//        binding.trackOrderDialogList.scrollToPosition(0)
//    }
//
//
//    private fun initUi() {
//        with(binding){
//            Log.d("wowTrackOrderFragment","initUing now")
////            mainAdapter = TrackOrderMainAdapter(requireContext(), childFragmentManager, this@TrackOrderFragment)
//            adapter = TrackOrderNewAdapter(requireContext(), this@TrackOrderFragment)
//            trackOrderDialogList.layoutManager = LinearLayoutManager(requireContext())
//            trackOrderDialogList.adapter = adapter
//
////            trackOrderDialogCloseBtn.setOnClickListener { dismiss() }
//        }
//    }
//
//
//
//    override fun onResume() {
//        super.onResume()
//        viewModel.startSilentUpdate()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        viewModel.endUpdates()
//    }
//
//
//    override fun onDestroy() {
////        mMap?.clear()
////        mMap = null
//        adapter = null
////        binding = null
//        super.onDestroy()
//    }
//}