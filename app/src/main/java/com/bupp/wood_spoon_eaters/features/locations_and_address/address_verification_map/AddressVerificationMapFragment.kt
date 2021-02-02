package com.bupp.wood_spoon_eaters.features.locations_and_address.address_verification_map

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FragmentAddressVerificationMapBinding
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressViewModel
import com.bupp.wood_spoon_eaters.model.AddressRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_address_verification_map.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class AddressVerificationMapFragment : Fragment(R.layout.fragment_address_verification_map), OnMapReadyCallback {

    private var binding: FragmentAddressVerificationMapBinding? = null
    val viewModel by sharedViewModel<LocationAndAddressViewModel>()
    private var googleMap: GoogleMap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddressVerificationMapBinding.bind(view)
        initMap()
        initObservers()

    }

    private fun initMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.addressMapFragMap) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.let {
            this.googleMap = it
            initObservers()
        }
    }

    private fun initObservers() {
        viewModel.addressFoundEvent.observe(viewLifecycleOwner, {
            it?.let { address ->
                updateMap(address)
            }
        })
        viewModel.markerDistanceEvent.observe(viewLifecycleOwner, {
            binding!!.addressMapFragHeader.updateMapHeaderView(it)
        })
    }

    fun updateMap(addressRequest: AddressRequest) {
        googleMap?.setOnMapLoadedCallback {
            val builder = LatLngBounds.Builder()

            googleMap?.clear()



            val locationLat = addressRequest.lat
            val locationLng = addressRequest.lng
            locationLat?.let{
                locationLng?.let{
                    val myLocation = LatLng(locationLat, locationLng)
//                    myLocationMarker = MarkerOptions().position(myLocation).draggable(true).icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_pin))
//                    googleMap?.addMarker(myLocationMarker)
//                    builder.include(myLocation)
//                    Log.d("wowMapBinder", "locationLng $locationLng")

                    val location = CameraUpdateFactory.newLatLngZoom(myLocation, 18f)
                    googleMap?.moveCamera(location)

//                    binding!!.addressMapFragPin.visibility = View.VISIBLE

                }
            }
        }
        googleMap?.setOnCameraMoveListener(OnCameraMoveListener {
            val centerLatLng: LatLng? = googleMap?.cameraPosition?.target
            Log.d(TAG, "onMove: $centerLatLng")
            centerLatLng?.let {
                viewModel.checkCenterLatLngPosition(it)
//                addPulsatingEffect(centerLatLng, 10f)

            }
//            myLocationMarker = MarkerOptions().position(midLatLng!!).icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_cook_marker))
        })

    }


//    private var lastUserCircle: Circle? = null
//    private val pulseDuration: Long = 1000
//    private var lastPulseAnimator: ValueAnimator? = null
//
//    private fun addPulsatingEffect(myLocation: LatLng, accuracy: Float) {
//        lastPulseAnimator?.let {
//            it.cancel()
//            Log.d("onLocationUpdated: ", "cancelled")
//        }
//        lastUserCircle?.let{
//            it.setCenter(myLocation)
//        }
//        lastPulseAnimator = valueAnimate(accuracy, pulseDuration) { animation ->
//            if (lastUserCircle != null){
//                lastUserCircle!!.setRadius(10.0)
//            } else {
//                lastUserCircle = googleMap?.addCircle(
//                    CircleOptions()
//                        .center(myLocation)
//                        .radius(20.0)
//                        .strokeColor(Color.RED)
//                        .fillColor(Color.BLUE)
//                )
//            }
//        }
//    }
//
//    protected fun valueAnimate(accuracy: Float, duration: Long, updateListener: AnimatorUpdateListener?): ValueAnimator {
//        Log.d("valueAnimate: ", "called")
//        val va = ValueAnimator.ofFloat(0f, accuracy)
//        va.duration = duration
//        va.addUpdateListener(updateListener)
//        va.repeatCount = ValueAnimator.INFINITE
//        va.repeatMode = ValueAnimator.RESTART
//        va.start()
//        return va
//    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

//    private fun updateMap(address: AddressRequest) {
//        googleMap?.let {
//            val location = LatLng(address.lat ?: 40.7128, address.lng ?: 74.0060)
//            Log.d(TAG, "latLng: ${address.lat} ${address.lng}")
//            it.addMarker(
//                MarkerOptions()
//                    .position(location)
//                    .title(it.getUserLocationStr())
//            )
//        }
//    }

    companion object {
        const val TAG = "wowAddressVerification"
    }
}