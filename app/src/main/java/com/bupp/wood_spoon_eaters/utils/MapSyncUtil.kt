package com.bupp.wood_spoon_eaters.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.Order
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*


class MapSyncUtil(val context: Context) : OnMapReadyCallback {

    lateinit var listener: MapSyncListener
    lateinit var curOrder: Order

    private var currentBoundSize: Int = 100
    private var googleMap: GoogleMap? = null

    private var mMapView: MapView? = null
    private val mMapWidth = Utils.toPx(314)
    private val mMapHeight = 800

//    private val cookMarker = ContextCompat.getDrawable(context, R.drawable.ic_cook_marker)

    val KYIV = LatLng(50.450311, 30.523730)


    interface MapSyncListener {
        fun onBitmapReady(bitmap: Bitmap?)
    }

    fun getMapBounds(): LatLngBounds {
        val curCourierData = curOrder.courier
        val builder = LatLngBounds.Builder()

        googleMap?.clear()

        val chefLat = curOrder.restaurant?.pickupAddress?.lat
        val chefLng = curOrder.restaurant?.pickupAddress?.lng
        chefLat?.let {
            chefLng?.let {
                val chefLocation = LatLng(chefLat, chefLng)
                googleMap?.addMarker(MarkerOptions().position(chefLocation).icon(bitmapDescriptorFromVector(context, R.drawable.ic_cook_marker)))
                builder.include(chefLocation)
                Log.d("wowMapBinder", "chefLocation $chefLocation")
            }
        }
        val myLat = curOrder.deliveryAddress?.lat
        val myLng = curOrder.deliveryAddress?.lng
        myLat?.let {
            myLng?.let {
                val myLocation = LatLng(myLat, myLng)
                googleMap?.addMarker(MarkerOptions().position(myLocation).icon(bitmapDescriptorFromVector(context, R.drawable.ic_my_marker)))
                builder.include(myLocation)
                Log.d("wowMapBinder", "myLocation $myLocation")
            }
        }
        curCourierData?.let {
            val courierLat = curCourierData.lat
            val courierLng = curCourierData.lng
            courierLat?.let {
                courierLng?.let {
                    val courierLocation = LatLng(courierLat, courierLng)
                    googleMap?.addMarker(MarkerOptions().position(courierLocation).icon(bitmapDescriptorFromVector(context, R.drawable.ic_courier_marker)))
                    builder.include(courierLocation)
                    Log.d("wowMapBinder", "courierLocation $courierLocation")
                }
            }
        }
        val bounds = builder.build()
        return bounds
    }

    //
//    private fun animateCamera(bounds: LatLngBounds?) {
//        bounds?.let{
//            try{
//                googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, currentBoundSize), 150, null)
//                Log.d("wowTrackOrder","bound size: $currentBoundSize")
//            }catch (ex: Exception){
//                if(currentBoundSize > 100){
//                    currentBoundSize -= 50
//                    Log.d("wowTrackOrder","changing bound size: $currentBoundSize")
//                    animateCamera(bounds)
//                }else{
//                    Log.d("wowTrackOrder","map ex: $ex")
//
//                }
//            }
//        }
//    }
//
    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }


    fun sync(listener: MapSyncListener, curOrder: Order) {
        this.listener = listener
        this.curOrder = curOrder

        var mapViewBundle: Bundle? = null

        val options = GoogleMapOptions()
            .compassEnabled(false)
            .mapToolbarEnabled(false)
            .latLngBoundsForCameraTarget(getMapBounds())
//            .camera(CameraPosition.fromLatLngZoom(KYIV, 15f))
//            .camera(CameraUpdateFactory.newLatLngBounds(getMapBounds(), currentBoundSize), 150, null)
            .liteMode(true)
        mMapView = MapView(context, options)
        mMapView!!.onCreate(mapViewBundle)

        var b: Bitmap? = null

        mMapView!!.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        // set map size in pixels and initiate image loading
        mMapView?.measure(
            View.MeasureSpec.makeMeasureSpec(mMapWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(mMapHeight, View.MeasureSpec.EXACTLY)
        )
        mMapView?.layout(0, 0, mMapWidth, mMapHeight)
        googleMap.setOnMapLoadedCallback {
            Log.d("wowOrderMap", "setOnMapLoadedCallback : ${getMapBounds()}")

            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(getMapBounds(), 10))

            //todo - try to avoid using Handler
            Handler().postDelayed({
                googleMap.snapshot {
                    listener.onBitmapReady(it)
                    mMapView?.onDestroy()
                }
            }, 100)

        }
    }


}