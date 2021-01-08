package com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.dialogs.cancel_order.CancelOrderDialog
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.ActiveOrderTrackerViewModel
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.binders.TrackOrderProgressBinder
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.common.Constants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.track_order_dialog.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class TrackOrderFragment() : Fragment(),
    CancelOrderDialog.CancelOrderDialogListener, TrackOrderProgressBinder.TrackOrderProgressListener, OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    var curOrderId: Long? = null
    var listener: TrackOrderDialogListener? = null

    var currentBoundSize = 150


    private lateinit var mainAdapter: TrackOrderMainAdapter
    val viewModel by sharedViewModel<ActiveOrderTrackerViewModel>()

    private lateinit var progressList: ArrayList<CheckBox>

    interface TrackOrderDialogListener {
        fun onContactUsClick(order: Order)
        fun onMessageClick(order: Order)
        fun onShareImageClick(order: Order)
        fun onOrderCanceled()
        fun onCloseClick()
    }

    companion object {
        private val CUR_ORDER_ID_PARAM = "curOrderId"

        fun newInstance(curOrderId: Long): TrackOrderFragment {
            val fragment = TrackOrderFragment()
            try {
                val args = Bundle()
                args.putLong(CUR_ORDER_ID_PARAM, curOrderId)
                fragment.arguments = args
            } catch (e: Exception) {
                Log.d("wowTrackOrderFragment", "newInstance exception")
            }
            Log.d("wowTrackOrderFragment", "newInstance withId: $curOrderId")
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        curOrderId = requireArguments().getLong(CUR_ORDER_ID_PARAM)
        Log.d("wowTrackOrderFragment", "newInstance ARGS: $curOrderId")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.track_order_dialog, null)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        initBtns()
        initUi()
        initMap()
        initUpdateObserver()
    }

    private fun initMap() {
        val mapFragment: SupportMapFragment = SupportMapFragment.newInstance()
        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.trackOrderMap, mapFragment)
        fragmentTransaction.commit()

        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap?.uiSettings?.isScrollGesturesEnabled = false
        mMap?.uiSettings?.isZoomControlsEnabled = false
    }

    fun updateMap(
        curOrderData: Order
    ) {
        mMap?.setOnMapLoadedCallback {
            val curCourierData  = curOrderData.courier
            val builder = LatLngBounds.Builder()

            val chefLat = curOrderData.cook.pickupAddress?.lat
            val chefLng = curOrderData.cook.pickupAddress?.lng
            chefLat?.let{
                chefLng?.let{
                    val chefLocation = LatLng(chefLat, chefLng)
                    mMap?.addMarker(MarkerOptions().position(chefLocation).icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_cook_marker)))
                    builder.include(chefLocation)
                    Log.d("wowMapBinder","chefLocation $chefLocation")
                }
            }
            val myLat = curOrderData.deliveryAddress.lat
            val myLng = curOrderData.deliveryAddress.lng
            myLat?.let{
                myLng?.let{
                    val myLocation = LatLng(myLat, myLng)
                    mMap?.addMarker(MarkerOptions().position(myLocation).icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_my_marker)))
                    builder.include(myLocation)
                    Log.d("wowMapBinder","myLocation $myLocation")
                }
            }
            curCourierData?.let{
                val courierLat = curCourierData.lat
                val courierLng = curCourierData.lng
                courierLat?.let{
                    courierLng?.let{
                        val courierLocation = LatLng(courierLat, courierLng)
                        mMap?.addMarker(MarkerOptions().position(courierLocation).icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_courier_marker)))
                        builder.include(courierLocation)
                        Log.d("wowMapBinder","courierLocation $courierLocation")
                    }
                }
            }
            val bounds = builder.build()
            //change mechnic to monig map by scroll and target bound on the courer or chef location
            animateCamera(bounds)

        }
    }

    private fun animateCamera(bounds: LatLngBounds?) {
        bounds?.let{
            try{
                mMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, currentBoundSize),1000, null)
                Log.d("wowTrackOrder","bound size: $currentBoundSize")
            }catch (ex: Exception){
                if(currentBoundSize > 100){
                    currentBoundSize -= 50
                    Log.d("wowTrackOrder","changing bound size: $currentBoundSize")
                    animateCamera(bounds)
                }else{
                    Log.d("wowTrackOrder","map ex: $ex")

                }
            }
        }
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    private fun initUpdateObserver() {
        viewModel.getActiveOrders.observe(this, Observer { result ->
            result.orders?.size?.let{
                if(result.isSuccess && result.orders?.size > 0){
                    Log.d("wowTrackOrderFragment","updating orders")
                    for(order in result.orders){
                        Log.d("wowTrackOrderFragment","curOrderId: $curOrderId, order: ${order.id}")
                        if(order.id == curOrderId){
                            Log.d("wowTrackOrderFragment","found Id: ${order.id}")
                            updateOrderUi(order, result.userInfo)
                            updateMap(order)
                        }
                    }
                }
            }
        })
    }

    override fun onContactUsClick(order: Order) {
        listener?.onContactUsClick(order)
    }

    override fun onShareImageClick(order: Order) {
        listener?.onShareImageClick(order)
    }

    override fun onOrderCanceled(orderState: Int, orderId: Long) {
        if(orderState <= 1){
            CancelOrderDialog(Constants.CANCEL_ORDER_STAGE_1, curOrderId, this).show(childFragmentManager, Constants.CANCEL_ORDER_DIALOG_TAG)
        }
        if(orderState == 2){
            CancelOrderDialog(Constants.CANCEL_ORDER_STAGE_2, curOrderId, this).show(childFragmentManager, Constants.CANCEL_ORDER_DIALOG_TAG)
        }
        if(orderState == 3){
            CancelOrderDialog(Constants.CANCEL_ORDER_STAGE_3, curOrderId, this).show(childFragmentManager, Constants.CANCEL_ORDER_DIALOG_TAG)
        }
    }

    private fun updateOrderUi(
        order: Order,
        userInfo: OrderUserInfo?
    ){
        mainAdapter.updateUi(order, userInfo)
        trackOrderDialogList.scrollToPosition(0)
    }

    override fun onOrderCanceled() {
        listener?.onOrderCanceled()
    }

    private fun initUi() {
        Log.d("wowTrackOrderFragment","initUing now")
        mainAdapter = TrackOrderMainAdapter(requireContext(), childFragmentManager, this)
        trackOrderDialogList.layoutManager = LinearLayoutManager(requireContext())
        trackOrderDialogList.adapter = mainAdapter

        trackOrderDialogCloseBtn.setOnClickListener { listener?.onCloseClick() }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TrackOrderDialogListener) {
            listener = context
        }
        else if (parentFragment is TrackOrderDialogListener){
            this.listener = parentFragment as TrackOrderDialogListener
        }
        else {
            throw RuntimeException(context.toString() + " must implement TrackOrderDialogListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onDestroy() {
        viewModel.endUpdates()
        super.onDestroy()
    }



//
////    private fun handleOrderDetails(details: ActiveOrderTrackerViewModel.OrderDetailsEvent) {
////        setProgress(details.orderProgress)
////        setMessagesIcon(details.isNewMsgs)
////        setArrivalTime(details.arrivalTime)
////    }
//
//    private fun setArrivalTime(arrivalTime: String){
////        trackOrderDialogArrivalTime.text = arrivalTime
//    }
//
//    private fun setMessagesIcon(isNewMessages: Boolean) {
//        trackOrderDialogMessageBtn.isSelected = isNewMessages
//    }
//
////    private fun setProgress(stepNum: Int) {
////        clearProgress()
////        setOrderProgress(stepNum)
////    }
////
////    private fun clearProgress() {
////        for (cb in progressList){
////            cb.isSelected = false
////            cb.text = Utils.setCustomFontTypeSpan(context!!,cb.text.toString(),0,cb.text.toString().length,R.font.open_sans_reg)
////            cb.setTextColor(ContextCompat.getColor(context!!,R.color.dark_50))
////        }
////    }
////
////    private fun setOrderProgress(stepNum: Int) {
////        if (stepNum == Constants.ORDER_PROGRESS_NO_PROGRESS) {
////            return
////        }
////
////        var cbItem = progressList[stepNum]
////
////        cbItem.isSelected = true
////        cbItem.text = Utils.setCustomFontTypeSpan(context!!,cbItem.text.toString(),0,cbItem.text.toString().length,R.font.open_sans_semi_bold)
////        cbItem.setTextColor(ContextCompat.getColor(context!!,R.color.dark))
////
////        setOrderProgress(stepNum - 1)
////    }




}