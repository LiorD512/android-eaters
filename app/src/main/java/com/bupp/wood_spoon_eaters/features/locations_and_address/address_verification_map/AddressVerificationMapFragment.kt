package com.bupp.wood_spoon_eaters.features.locations_and_address.address_verification_map

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FragmentAddressVerificationMapBinding
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressViewModel
import com.bupp.wood_spoon_eaters.model.AddressRequest
import com.bupp.wood_spoon_eaters.views.MapHeaderView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class AddressVerificationMapFragment : Fragment(R.layout.fragment_address_verification_map), OnMapReadyCallback {

    private var binding: FragmentAddressVerificationMapBinding? = null
    val viewModel by viewModel<AddressMapVerificationViewModel>()
    val mainViewModel by sharedViewModel<LocationAndAddressViewModel>()
    private var googleMap: GoogleMap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddressVerificationMapBinding.bind(view)

        initUi(requireArguments().getBoolean("showBtns"))
        initObservers()

        mainViewModel.initMapLocation()

    }

    private fun initUi(shouldShowDefaultUi: Boolean) {
        if(shouldShowDefaultUi){
            with(binding!!){
                addressMapDoneBtn.visibility = View.VISIBLE
                addressMapFragHeader.visibility = View.VISIBLE
                addressMapFragPin.enableAnimation()

                addressMapDoneBtn.setOnClickListener {
                    viewModel.onMapVerificationDoneClick()
                }
            }

        }

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
        mainViewModel.newAddressLiveData.observe(viewLifecycleOwner, {
            it?.let { address ->
                viewModel.setAnchorLocation(address)
                updateMap(address)
            }
        })
        viewModel.addressMapVerificationStatus.observe(viewLifecycleOwner, {
            with(binding!!){
                when(it){
                    AddressMapVerificationViewModel.AddressMapVerificationStatus.CORRECT -> {
                        addressMapFragHeader.updateMapHeaderView(MapHeaderView.MapHeaderViewType.CORRECT)
                    }
                    AddressMapVerificationViewModel.AddressMapVerificationStatus.WRONG -> {
                        addressMapFragHeader.updateMapHeaderView(MapHeaderView.MapHeaderViewType.WRONG)
                    }
                    AddressMapVerificationViewModel.AddressMapVerificationStatus.SHAKE -> {
                        addressMapFragHeader.updateMapHeaderView(MapHeaderView.MapHeaderViewType.SHAKE)
                    }
                    else -> {}
                }
            }
        })
        viewModel.addressMapVerificationDoneEvent.observe(viewLifecycleOwner, {
            val event = it.getContentIfNotHandled()
            event?.let{
                if(it){
                    mainViewModel.onAddressMapVerificationDone()
                }
            }
        })
    }

    fun updateMap(addressRequest: AddressRequest) {
        googleMap?.setOnMapLoadedCallback {
            googleMap?.clear()

            val locationLat = addressRequest.lat
            val locationLng = addressRequest.lng
            locationLat?.let{
                locationLng?.let{
                    val myLocation = LatLng(locationLat, locationLng)
                    val location = CameraUpdateFactory.newLatLngZoom(myLocation, 18f)
                    googleMap?.moveCamera(location)

                    binding!!.addressMapFragPin.visibility = View.VISIBLE
                    viewModel.checkCenterLatLngPosition(myLocation)
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
        const val TAG = "wowAddressVerification"
    }
}