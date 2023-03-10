package com.bupp.wood_spoon_eaters.features.locations_and_address.address_verification_map

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FragmentAddressVerificationMapBinding
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressViewModel
import com.bupp.wood_spoon_eaters.features.order_checkout.checkout.CheckoutViewModel
import com.bupp.wood_spoon_eaters.model.AddressRequest
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.utils.Utils
import com.bupp.wood_spoon_eaters.views.MapHeaderView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class AddressVerificationMapFragment : Fragment(R.layout.fragment_address_verification_map),
    OnMapReadyCallback {

    private var binding: FragmentAddressVerificationMapBinding? = null
    private val viewModel by viewModel<AddressMapVerificationViewModel>()
    private val mainViewModel by sharedViewModel<LocationAndAddressViewModel>()
    private val checkoutViewModel by sharedViewModel<CheckoutViewModel>()

    private var googleMap: GoogleMap? = null
    private var currentBoundSize = 100
    private var zoomLevel: Float = 18f
    private var mapFragment: SupportMapFragment? = null
    private var curOrder: Order? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddressVerificationMapBinding.bind(view)

        zoomLevel = requireArguments().getFloat("zoomLevel", 18f)
        val shouldShowBtn = requireArguments().getBoolean("showBtns")
        val isCheckout = requireArguments().getBoolean("isCheckout", false)
        curOrder = requireArguments().getParcelable<Order>("order")

        initUi(shouldShowBtn)
        initObservers()

        if (!isCheckout) {
            mainViewModel.initMapLocation()
        }

        binding!!.addressMapFragPb.show()

//        postponeEnterTransition()


    }


    private fun initUi(shouldShowDefaultUi: Boolean) {
        if (shouldShowDefaultUi) {
            with(binding!!) {
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
        } else {
            googleMap?.isBuildingsEnabled = false
        }

        mapFragment =
            childFragmentManager.findFragmentById(R.id.addressMapFragMap) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.let {
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
                    Timber.e("Style parsing failed.")
                }
            } catch (e: Resources.NotFoundException) {
                Timber.e(e, "Can't find style.")
            }

            initObservers()
            curOrder?.let { currentOrder ->
                updateCheckoutMap(currentOrder)
            }
        }
    }

    private fun initObservers() {
        checkoutViewModel.orderLiveData.observe(viewLifecycleOwner) { orderData ->
            if (orderData != null) {
                updateCheckoutMap(orderData)
            }
        }
        mainViewModel.newAddressLiveData.observe(viewLifecycleOwner) {
            it?.let { address ->
                viewModel.setAnchorLocation(address)
                updateMap(address)
            }
        }
        viewModel.addressMapVerificationStatus.observe(viewLifecycleOwner) {
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
        }
        viewModel.addressMapVerificationDoneEvent.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { event ->
                if (event) {
                    mainViewModel.onAddressMapVerificationDone()
                }
            }
        }

        viewModel.vibrateEvent.observe(viewLifecycleOwner) {
            val event = it.getContentIfNotHandled()
            event?.let {
                Utils.vibrate(requireContext())
            }
        }
        viewModel.redirectToMyLocation.observe(viewLifecycleOwner) {
            it?.let {
                val myLocation = LatLng(it.latitude, it.longitude)
                val location = CameraUpdateFactory.newLatLngZoom(myLocation, zoomLevel)
                googleMap?.animateCamera(location)
            }
        }
    }

    fun updateMap(addressRequest: AddressRequest) {

        googleMap?.setOnMapLoadedCallback {
            googleMap?.clear()
            val locationLat = addressRequest.lat
            val locationLng = addressRequest.lng
            locationLat?.let {
                locationLng?.let {
                    val myLocation = LatLng(locationLat, locationLng)
                    val location = CameraUpdateFactory.newLatLngZoom(myLocation, zoomLevel)
                    googleMap?.moveCamera(location)

                    binding!!.addressMapFragPin.visibility = View.VISIBLE
                    viewModel.checkCenterLatLngPosition(myLocation)

                    binding!!.addressMapFragPb.hide()
                }
            }
        }
        googleMap?.setOnCameraMoveListener {
            val centerLatLng: LatLng? = googleMap?.cameraPosition?.target
            centerLatLng?.let {
                viewModel.checkCenterLatLngPosition(it)
                mainViewModel.updateUnsavedAddressLatLng(it)
            }
        }
        googleMap?.setOnCameraIdleListener {}

    }

    private fun updateCheckoutMap(curOrderData: Order) {
        googleMap?.setOnMapLoadedCallback {
            val curCourierData = curOrderData.courier
            val builder = LatLngBounds.Builder()

            googleMap?.clear()
            googleMap?.uiSettings?.setAllGesturesEnabled(false)
            googleMap?.uiSettings?.isMapToolbarEnabled = false

            curOrderData.deliveryAddress?.lat?.let {
                curOrderData.deliveryAddress.lng?.let {

                    val chefLat = curOrderData.restaurant?.pickupAddress?.lat
                    val chefLng = curOrderData.restaurant?.pickupAddress?.lng
                    chefLat?.let {
                        chefLng?.let {
                            val chefLocation = LatLng(chefLat, chefLng)
                            googleMap?.addMarker(
                                MarkerOptions().position(chefLocation).icon(
                                    bitmapDescriptorFromVector(
                                        requireContext(),
                                        R.drawable.ic_chef_marker
                                    )
                                )
                            )

                            builder.include(chefLocation)
                        }
                    }
                    val myLat = curOrderData.deliveryAddress.lat ?: 0.0
                    val myLng = curOrderData.deliveryAddress.lng ?: 0.0
                    val myLocation = LatLng(myLat, myLng)
                    googleMap?.addMarker(
                        MarkerOptions().position(myLocation)
                            .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_pin))
                    )

                    builder.include(myLocation)

                    val bounds = builder.build()

                    //change mechnic to monig map by scroll and target bound on the courer or chef location
                    animateCamera(bounds)
                }
            }
            binding!!.addressMapFragPb.hide()
        }
    }

    private fun animateCamera(bounds: LatLngBounds?) {
        bounds?.let {
            try {
                googleMap?.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        bounds,
                        currentBoundSize
                    ), 150, null
                )
            } catch (ex: Exception) {
                if (currentBoundSize > 100) {
                    currentBoundSize -= 50
                    animateCamera(bounds)
                } else {
                    Timber.e( ex)

                }
            }
        }
//        startPostponedEnterTransition()

    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }


    override fun onDestroy() {
        googleMap?.clear()
        googleMap = null
        mapFragment = null
        binding = null
        super.onDestroy()
    }

    override fun onPause() {
        binding!!.addressMapFragPin.stopAllAnimations()
        super.onPause()
    }

    companion object {
        const val TAG = "AddressVerificationMapFragment"

        fun newInstance(bundle: Bundle): AddressVerificationMapFragment {
            val fragment = AddressVerificationMapFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

}