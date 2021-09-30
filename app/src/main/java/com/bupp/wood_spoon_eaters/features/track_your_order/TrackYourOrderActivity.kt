package com.bupp.wood_spoon_eaters.features.track_your_order

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.track_order.TrackOrderData
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.databinding.ActivityTrackYourOrderBinding
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.ActiveOrderTrackerViewModel
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.OrderTrackDetails
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.OrderTrackProgress
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.OrderUserInfo
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.TrackOrderNewAdapter
import com.bupp.wood_spoon_eaters.features.base.BaseActivity
import com.bupp.wood_spoon_eaters.model.Order
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrackYourOrderActivity : BaseActivity(), TrackOrderNewAdapter.TrackOrderNewAdapterListener, OnMapReadyCallback {

    lateinit var binding: ActivityTrackYourOrderBinding

    private var adapter: TrackOrderNewAdapter? = null
    val viewModel by viewModel<ActiveOrderTrackerViewModel>()

    private var curOrderId: Long? = null
    private var mMap: GoogleMap? = null
    var currentBoundSize = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        postponeEnterTransition()

        binding = ActivityTrackYourOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.sharedElementEnterTransition = TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transition)
        binding.trackOrderMap.transitionName = "mapTransition"

        curOrderId = intent.getLongExtra("order_id", -1)
        Log.d("wowTrackOrderFragment", "newInstance ARGS: $curOrderId")

        viewModel.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_TRACK_ORDER)
        viewModel.logEvent(Constants.EVENT_CLICK_TRACK_YOUR_ORDER)

        initUi()
        initObservers()
        initMap()
//        viewModel.getCurrentOrder(curOrderId)
    }


    private fun initUi() {
        adapter = TrackOrderNewAdapter(this, this)
        binding.trackOrderActList.layoutManager = LinearLayoutManager(this)
        binding.trackOrderActList.adapter = adapter
    }

    private fun initObservers() {
        viewModel.getCurrentOrderDetails.observe(this, { result ->
            result.order.let{
                Log.d("wowTrackOrderFragment","updating orders")
                updateOrderUi(it, result.userInfo)

//                (view.parent as? ViewGroup)?.doOnPreDraw {
                    startPostponedEnterTransition()
//                }
            }
        })
    }

    private fun updateOrderUi(
        order: Order,
        userInfo: OrderUserInfo?){
        val adapterDetails = OrderTrackDetails(order, userInfo)
        val adapterProgress = OrderTrackProgress(order)

        val data = mutableListOf<TrackOrderData<Any>>(
            TrackOrderData(TrackOrderNewAdapter.VIEW_TYPE_DETAILS, adapterDetails),
            TrackOrderData(TrackOrderNewAdapter.VIEW_TYPE_PROGRESS, adapterProgress, false)
        )
        adapter?.submitList(data)
        updateMap(order)
//        val url = MapSyncUtil().getMapImage(order, 800, 1200)
//
//        Log.d("wowSTtaicMap","url $url")
//        Glide.with(this).load(url).into(binding.trackOrderActMap)
    }

    private fun initMap() {
        val mapFragment: SupportMapFragment = SupportMapFragment.newInstance()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.trackOrderMap, mapFragment)
        fragmentTransaction.commit()

        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        if(mMap == null){
            mMap = googleMap
            mMap?.uiSettings?.setAllGesturesEnabled(false)
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
//                val success: Boolean = mMap!!.setMapStyle(
//                    MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style
//                    )
//                )
//                if (!success) {
//                    Log.e("MapsActivityRaw", "Style parsing failed.")
//                }
            } catch (e: Resources.NotFoundException) {
                Log.e("MapsActivityRaw", "Can't find style.", e)
            }

            viewModel.getCurrentOrder(curOrderId)
//            startPostponedEnterTransition()
        }
    }

    private fun updateMap(
        curOrderData: Order
    ) {
        mMap?.setOnMapLoadedCallback {
            val curCourierData  = curOrderData.courier
            val builder = LatLngBounds.Builder()

            mMap?.clear()

            val chefLat = curOrderData.restaurant?.pickupAddress?.lat
            val chefLng = curOrderData.restaurant?.pickupAddress?.lng
            chefLat?.let{
                chefLng?.let{
                    val chefLocation = LatLng(chefLat, chefLng)
                    mMap?.addMarker(MarkerOptions().position(chefLocation).icon(bitmapDescriptorFromVector(this, R.drawable.ic_cook_marker)))
                    builder.include(chefLocation)
                    Log.d("wowMapBinder","chefLocation $chefLocation")
                }
            }
            val myLat = curOrderData.deliveryAddress?.lat
            val myLng = curOrderData.deliveryAddress?.lng
            myLat?.let{
                myLng?.let{
                    val myLocation = LatLng(myLat, myLng)
                    mMap?.addMarker(MarkerOptions().position(myLocation).icon(bitmapDescriptorFromVector(this, R.drawable.ic_my_marker)))
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
                        mMap?.addMarker(MarkerOptions().position(courierLocation).icon(bitmapDescriptorFromVector(this, R.drawable.ic_courier_marker)))
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
                mMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, currentBoundSize))
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


}