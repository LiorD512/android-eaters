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


class MapSyncUtil()  {
//
//    lateinit var listener: MapSyncListener
////    lateinit var order: Order
//
//    private var currentBoundSize: Int = 100
//    private var googleMap: GoogleMap? = null
//
//    private var mMapView: MapView? = null
//    private val mMapWidth = Utils.toPx(314)
//    private val mMapHeight = 800
//
////    private val cookMarker = ContextCompat.getDrawable(context, R.drawable.ic_cook_marker)
//
//    val KYIV = LatLng(50.450311, 30.523730)
//
//
//    interface MapSyncListener {
//        fun onImageReady(url: String?)
//    }
//
//    fun getMapBounds(order: Order): LatLngBounds {
//        val curCourierData = order.courier
//        val builder = LatLngBounds.Builder()
//
//        googleMap?.clear()
//
//        val chefLat = order.restaurant?.pickupAddress?.lat
//        val chefLng = order.restaurant?.pickupAddress?.lng
//        chefLat?.let {
//            chefLng?.let {
//                val chefLocation = LatLng(chefLat, chefLng)
//                googleMap?.addMarker(MarkerOptions().position(chefLocation).icon(bitmapDescriptorFromVector(context, R.drawable.ic_cook_marker)))
//                builder.include(chefLocation)
//                Log.d("wowMapBinder", "chefLocation $chefLocation")
//            }
//        }
//        val myLat = order.deliveryAddress?.lat
//        val myLng = order.deliveryAddress?.lng
//        myLat?.let {
//            myLng?.let {
//                val myLocation = LatLng(myLat, myLng)
//                googleMap?.addMarker(MarkerOptions().position(myLocation).icon(bitmapDescriptorFromVector(context, R.drawable.ic_my_marker)))
//                builder.include(myLocation)
//                Log.d("wowMapBinder", "myLocation $myLocation")
//            }
//        }
//        curCourierData?.let {
//            val courierLat = curCourierData.lat
//            val courierLng = curCourierData.lng
//            courierLat?.let {
//                courierLng?.let {
//                    val courierLocation = LatLng(courierLat, courierLng)
//                    googleMap?.addMarker(MarkerOptions().position(courierLocation).icon(bitmapDescriptorFromVector(context, R.drawable.ic_courier_marker)))
//                    builder.include(courierLocation)
//                    Log.d("wowMapBinder", "courierLocation $courierLocation")
//                }
//            }
//        }
//        val bounds = builder.build()
//        return bounds
//    }
//
//    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
//        return ContextCompat.getDrawable(context, vectorResId)?.run {
//            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
//            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
//            draw(Canvas(bitmap))
//            BitmapDescriptorFactory.fromBitmap(bitmap)
//        }
//    }


    fun getMapImage(order: Order): String {
        val lat = order.deliveryAddress?.lat
        val lng = order.deliveryAddress?.lng
        val url = "http://maps.google.com/maps/api/staticmap?center=$lat,$lng&zoom=17&" +
                "size=600x400&" +
                "sensor=false&" +
                "markers=icon:https://res.cloudinary.com/woodspoon/image/upload/c_scale,h_64/uploads/app/assets/map_pin.png|$lat,$lng&" +
                "key=AIzaSyCowuTI2_0q8rpGYlqueBX6nbk2kSjjitU"
        return url
//                    "markers=color:blue|37.446754,-77.572746"
//                    "markers=icon:http://foursquare.com/img/categories_v2/shops/financial_bg_64.png|37.446754,-77.572746"

//        https://maps.googleapis.com/maps/api/staticmap?size=512x512&maptype=roadmap\
//        &markers=size:mid%7Ccolor:red%7CSan+Francisco,CA%7COakland,CA%7CSan+Jose,CA&key=YOUR_API_KEY

    }


}