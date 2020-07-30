package com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.binders

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.OrderTrackMapData
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.OrderUserInfo
import com.bupp.wood_spoon_eaters.model.Courier
import com.bupp.wood_spoon_eaters.model.Order
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import mva2.adapter.ItemBinder
import mva2.adapter.ItemViewHolder


class TrackOrderMapBinder(val fragManager: FragmentManager): ItemBinder<OrderTrackMapData, TrackOrderMapBinder.TrackOrderMapViewHolder>() {

    private lateinit var curOrderData: Order
    private var curCourierData: Courier? = null
//    private var curUserInfoData: OrderUserInfo? = null
    private lateinit var mMap: GoogleMap

    override fun createViewHolder(parent: ViewGroup): TrackOrderMapViewHolder {
        return TrackOrderMapViewHolder(inflate(parent, R.layout.track_order_map_section))
    }

    override fun canBindData(item: Any): Boolean {
        return item is OrderTrackMapData
    }

    override fun bindViewHolder(holder: TrackOrderMapViewHolder, items: OrderTrackMapData) {
        holder.bindItems(items)
    }

    inner class TrackOrderMapViewHolder(itemView: View) : ItemViewHolder<OrderTrackMapData>(itemView), OnMapReadyCallback {
        fun bindItems(item: OrderTrackMapData) {
            curOrderData = item.order
            curCourierData  = item.order.courier
//            curUserInfoData = item.orderUserInfo
            val mapFragment: SupportMapFragment = SupportMapFragment.newInstance()
            val fragmentTransaction = fragManager.beginTransaction()
            fragmentTransaction.add(R.id.trackOrderMap, mapFragment)
            fragmentTransaction.commit()

            mapFragment.getMapAsync(this)
        }

        override fun onMapReady(googleMap: GoogleMap?) {
            googleMap?.let{
                mMap = googleMap

                mMap.uiSettings.isScrollGesturesEnabled = false
                mMap.uiSettings.isZoomControlsEnabled = false

                mMap.setOnMapLoadedCallback {

                    val builder = LatLngBounds.Builder()

                    val chefLat = curOrderData.cook.pickupAddress?.lat
                    val chefLng = curOrderData.cook.pickupAddress?.lng
                    chefLat?.let{
                        chefLng?.let{
                            val chefLocation = LatLng(chefLat, chefLng)
                            mMap.addMarker(MarkerOptions().position(chefLocation).icon(bitmapDescriptorFromVector(itemView.context, R.drawable.ic_cook_marker)))
                            builder.include(chefLocation)
                            Log.d("wowMapBinder","chefLocation $chefLocation")
                        }
                    }
                    val myLat = curOrderData.deliveryAddress.lat
                    val myLng = curOrderData.deliveryAddress.lng
                    myLat?.let{
                        myLng?.let{
                            val myLocation = LatLng(myLat, myLng)
                            mMap.addMarker(MarkerOptions().position(myLocation).icon(bitmapDescriptorFromVector(itemView.context, R.drawable.ic_my_marker)))
                            builder.include(myLocation)
                            Log.d("wowMapBinder","myLocation $myLocation")
                        }
                    }
                    curCourierData?.let{
                        val courierLat = curCourierData?.lat
                        val courierLng = curCourierData?.lng
                        courierLat?.let{
                            courierLng?.let{
                                val courierLocation = LatLng(courierLat, courierLng)
                                mMap.addMarker(MarkerOptions().position(courierLocation).icon(bitmapDescriptorFromVector(itemView.context, R.drawable.ic_courier_marker)))
                                builder.include(courierLocation)
                                Log.d("wowMapBinder","courierLocation $courierLocation")
                            }
                        }
                    }
                    val bounds = builder.build()
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150))
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

}