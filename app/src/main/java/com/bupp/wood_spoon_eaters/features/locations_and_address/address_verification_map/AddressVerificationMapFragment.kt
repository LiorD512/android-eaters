package com.bupp.wood_spoon_eaters.features.locations_and_address.address_verification_map

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FragmentAddressVerificationMapBinding
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressViewModel
import com.bupp.wood_spoon_eaters.model.AddressRequest
import com.bupp.wood_spoon_eaters.utils.Utils
import com.bupp.wood_spoon_eaters.views.MapHeaderView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class AddressVerificationMapFragment : Fragment(R.layout.fragment_address_verification_map), OnMapReadyCallback {

    private var zoomLevel: Float = 18f
    private var binding: FragmentAddressVerificationMapBinding? = null
    val viewModel by viewModel<AddressMapVerificationViewModel>()
    val mainViewModel by sharedViewModel<LocationAndAddressViewModel>()
    private var googleMap: GoogleMap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddressVerificationMapBinding.bind(view)

        zoomLevel = requireArguments().getString("zoomLevel", "18").toFloat()
        val shouldShowBtn = requireArguments().getBoolean("showBtns")

        Log.d(TAG, "zoomLevel: $zoomLevel")
        initUi(shouldShowBtn)
        initObservers()

        mainViewModel.initMapLocation()
        binding!!.addressMapFragPb.show()

    }

    private fun initUi(shouldShowDefaultUi: Boolean) {
        if(shouldShowDefaultUi){
            with(binding!!){
                addressMapDoneBtn.visibility = View.VISIBLE
                addressMapFragHeader.visibility = View.VISIBLE
                addressMapMyLocationBtn.visibility = View.VISIBLE
                addressMapFragPin.enableAnimation()

                addressMapDoneBtn.setOnClickListener {
                    viewModel.onMapVerificationDoneClick()
                }

                addressMapMyLocationBtn.setOnClickListener {
                    viewModel.redirectToMyLocation()
                }

                googleMap?.isBuildingsEnabled = true
            }
        }else{
            googleMap?.isBuildingsEnabled = false
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.addressMapFragMap) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.let {
            this.googleMap = it

            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                val success: Boolean = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(), R.raw.map_style
                    )
                )
                if (!success) {
                    Log.e("MapsActivityRaw", "Style parsing failed.")
                }
            } catch (e: Resources.NotFoundException) {
                Log.e("MapsActivityRaw", "Can't find style.", e)
            }

            initObservers()
        }
    }

    private fun initObservers() {
        mainViewModel.newAddressLiveData.observe(viewLifecycleOwner, {
            it?.let { address ->
                viewModel.setAnchorLocation(address)
                updateMap(address)
            }
        })
        viewModel.addressMapVerificationStatus.observe(viewLifecycleOwner, {
            with(binding!!) {
                when (it) {
                    AddressMapVerificationViewModel.AddressMapVerificationStatus.CORRECT -> {
                        addressMapFragHeader.updateMapHeaderView(MapHeaderView.MapHeaderViewType.CORRECT)
                    }
                    AddressMapVerificationViewModel.AddressMapVerificationStatus.WRONG -> {
                        addressMapFragHeader.updateMapHeaderView(MapHeaderView.MapHeaderViewType.WRONG)
                    }
                    AddressMapVerificationViewModel.AddressMapVerificationStatus.SHAKE -> {
                        addressMapFragHeader.updateMapHeaderView(MapHeaderView.MapHeaderViewType.SHAKE)
                    }
                    else -> {
                    }
                }
            }
        })
        viewModel.addressMapVerificationDoneEvent.observe(viewLifecycleOwner, {
            val event = it.getContentIfNotHandled()
            event?.let {
                if (it) {
                    mainViewModel.onAddressMapVerificationDone()
                }
            }
        })
        viewModel.vibrateEvent.observe(viewLifecycleOwner, {
            val event = it.getContentIfNotHandled()
            event?.let {
                Utils.vibrate(requireContext())
            }
        })
        viewModel.redirectToMyLocation.observe(viewLifecycleOwner, {
            it?.let {
                val myLocation = LatLng(it.latitude, it.longitude)
                val location = CameraUpdateFactory.newLatLngZoom(myLocation, zoomLevel)
                googleMap?.animateCamera(location)
            }
        })
    }

    fun updateMap(addressRequest: AddressRequest) {

        googleMap?.setOnMapLoadedCallback {
            Log.d(TAG, "map loaded")
            googleMap?.clear()
            val locationLat = addressRequest.lat
            val locationLng = addressRequest.lng
            locationLat?.let{
                locationLng?.let{
                    val myLocation = LatLng(locationLat, locationLng)
                    val location = CameraUpdateFactory.newLatLngZoom(myLocation, zoomLevel)
                    googleMap?.moveCamera(location)

                    binding!!.addressMapFragPin.visibility = View.VISIBLE
                    viewModel.checkCenterLatLngPosition(myLocation)

                    binding!!.addressMapFragPb.hide()
                }
            }
        }
        googleMap?.setOnCameraMoveListener(OnCameraMoveListener {
            val centerLatLng: LatLng? = googleMap?.cameraPosition?.target
            Log.d(TAG, "onMove: $centerLatLng")
            centerLatLng?.let {
                viewModel.checkCenterLatLngPosition(it)
                mainViewModel.updateUnsavedAddressLatLng(it)
            }
        })
        googleMap?.setOnCameraIdleListener {
            Log.d(TAG, "camera idle")
        }

    }

    override fun onDestroy() {
        googleMap?.clear()
        googleMap = null
        binding = null
        super.onDestroy()
    }

    override fun onPause() {
        binding!!.addressMapFragPin.stopAllAnimations()
        super.onPause()
    }

    companion object {
        const val TAG = "wowAddressMapVerification"
    }
}