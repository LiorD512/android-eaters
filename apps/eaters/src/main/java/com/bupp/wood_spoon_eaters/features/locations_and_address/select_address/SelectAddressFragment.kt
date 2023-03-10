package com.bupp.wood_spoon_eaters.features.locations_and_address.select_address

import android.content.Context
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.databinding.FragmentSelectAddressBinding
import com.bupp.wood_spoon_eaters.bottom_sheets.address_menu.AddressMenuBottomSheet
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressViewModel
import com.bupp.wood_spoon_eaters.managers.location.GPSBroadcastReceiver
import com.bupp.wood_spoon_eaters.managers.location.GpsUtils
import com.bupp.wood_spoon_eaters.model.Address
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectAddressFragment : Fragment(R.layout.fragment_select_address), GPSBroadcastReceiver.GPSBroadcastListener,
    SelectAddressAdapter.SelectAddressAdapterListener {

    private var addressAdapter: SelectAddressAdapter? = null
    private var gpsBroadcastReceiver: GPSBroadcastReceiver? = null
    private val viewModel by viewModel<SelectAddressViewModel>()
    private val mainViewModel by sharedViewModel<LocationAndAddressViewModel>()
    private var binding: FragmentSelectAddressBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSelectAddressBinding.bind(view)

        initUi()
        initObservers()
        initData()
    }

    private fun initData() {
        viewModel.updateMyLocationUiState(isGpsEnabled(requireContext()))
        viewModel.fetchAddress()
    }

    private fun registerGpsBroadcastReceiver() {
        gpsBroadcastReceiver = GPSBroadcastReceiver(this)
        requireContext().registerReceiver(gpsBroadcastReceiver, IntentFilter("android.location.PROVIDERS_CHANGED"))
    }

    override fun onGPSChanged(isEnabled: Boolean) {
        Log.d("wowLocationManager", "onGPSChanged: $isEnabled")
        viewModel.updateMyLocationUiState(isEnabled)
    }

    private fun isGpsEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    private fun initUi() {
        with(binding!!) {
            selectAddressFragMyLocationLayout.setOnClickListener {
                viewModel.onMyLocationClick()
            }
            selectAddressFragDone.setOnClickListener {
                mainViewModel.onDoneClick(addressAdapter?.selectedAddress)
            }

            selectAddressFragAutoComplete.setOnClickListener {
                mainViewModel.onSearchAddressAutoCompleteClick()
            }

            selectAddressFragList.layoutManager = LinearLayoutManager(requireContext())
            initAdapterIfNeeded()
//            addressAdapter = SelectAddressAdapter(this@SelectAddressFragment)
            val dividerItemDecoration = DividerItemDecorator(ContextCompat.getDrawable(requireContext(), R.drawable.divider))
            selectAddressFragList.addItemDecoration(dividerItemDecoration)
            selectAddressFragList.adapter = addressAdapter
        }
    }

    private fun initLocationObserver() {
        mainViewModel.getLocationLiveData().observe(viewLifecycleOwner, { result ->
            result?.let {
                Log.d(TAG, "getLocationLiveData observer called")
                with(binding!!) {
                    viewModel.onMyLocationReceived(it)
                    selectAddressFragMyLocationAddress.text = it.getUserLocationStr()
                    hideMyLocationPb()
                }
            }
        })
    }

    private fun initObservers() {


//        mainViewModel.addressFoundUiEvent.observe(viewLifecycleOwner, {
////            selectAddressFragAutoComplete.text = it.getUserLocationStr()
//        })
        mainViewModel.actionEvent.observe(viewLifecycleOwner, {
            when (it) {
                LocationAndAddressViewModel.ActionEvent.REFRESH_MY_LOCATION_STATE -> {
                    viewModel.updateMyLocationUiState(isGpsEnabled(requireContext()))
                    initLocationObserver()
                }
                LocationAndAddressViewModel.ActionEvent.FETCH_MY_ADDRESS -> {
                    viewModel.fetchAddress()
                }
                else -> {}
            }
        })


        viewModel.navigationEvent.observe(viewLifecycleOwner, {
            val result = it.getContentIfNotHandled()
            result?.let {
                when (it) {
                    SelectAddressViewModel.NavigationEventType.OPEN_LOCATION_PERMISSION_SCREEN -> {
                        mainViewModel.showLocationPermissionScreen()
                    }
                    SelectAddressViewModel.NavigationEventType.OPEN_GPS_SETTINGS -> {
                        GpsUtils(requireContext()).turnGPSOn(object : GpsUtils.OnGpsListener {
                            override fun gpsStatus(isGPSEnable: Boolean) {
                                Log.d(TAG, "gpsStatus: $isGPSEnable")
                                viewModel.updateMyLocationUiState(isGPSEnable)
                            }
                        })
                    }
                    SelectAddressViewModel.NavigationEventType.OPEN_MAP_VERIFICATION_WITH_MY_LOCATION -> {
                        mainViewModel.openAddressMapVerificationWithMyLocation()
                    }
                }
            }
        })
        viewModel.myLocationEvent.observe(viewLifecycleOwner, { myLocation -> handleMyLocationUiEvent(myLocation) })

        viewModel.myAddressesEvent.observe(viewLifecycleOwner, {
            with(binding!!){
                if (it.address.isNullOrEmpty()) {
                    selectAddressFragEmptyList.visibility = View.VISIBLE
                    selectAddressFragList.visibility = View.GONE
                    addressAdapter?.selectedAddress = null
                } else {
                    selectAddressFragList.visibility = View.VISIBLE
                    selectAddressFragEmptyList.visibility = View.GONE
                    addressAdapter?.submitList(it.address)
                    addressAdapter?.selectedAddress = it?.currentAddress
                }
            }
        })
        viewModel.getFinalAddressParams().observe(viewLifecycleOwner, {
            viewModel.updateSelectedAddressUi(it.id)
        })
    }

    private fun handleMyLocationUiEvent(myLocation: SelectAddressViewModel.MyLocationStatus) {
        with(binding!!) {
            when (myLocation) {
                SelectAddressViewModel.MyLocationStatus.FETCHING -> {
                    showMyLocationPb()
                    selectAddressFragMyLocationAddress.text = "Fetching your location"
                    selectAddressFragMyLocationPickup.visibility = View.GONE
                    initLocationObserver()
                }
                SelectAddressViewModel.MyLocationStatus.NO_PERMISSION -> {
                    selectAddressFragMyLocationAddress.text = "Need to turn on location permission."
                    selectAddressFragMyLocationPickup.text = "Tap here to enable it."
                    selectAddressFragMyLocationPickup.visibility = View.VISIBLE
                }
                SelectAddressViewModel.MyLocationStatus.NO_GPS_ENABLED -> {
                    selectAddressFragMyLocationAddress.text = "Please enable GPS on device"
                    selectAddressFragMyLocationPickup.text = "Tap here to enable it."
                    selectAddressFragMyLocationPickup.visibility = View.VISIBLE
                }
                else -> {}
            }
        }
    }

    private fun showMyLocationPb() {
        with(binding!!){
            selectAddressFragMyLocationPb.setAnimation("loader.json")
            selectAddressFragMyLocationPb.repeatCount = LottieDrawable.INFINITE
            selectAddressFragMyLocationPb.playAnimation()
            selectAddressFragMyLocationPb.visibility = View.VISIBLE
        }
    }

    private fun hideMyLocationPb() {
        with(binding!!) {
            selectAddressFragMyLocationPb.cancelAnimation()
            selectAddressFragMyLocationPb.visibility = View.GONE
        }
    }

    override fun onAddressClick(selected: Address) {
        Log.d(TAG, "onAddressClick")
//        mainViewModel.setTempSelectedAddress(selected)
//        addressAdapter?.notifyDataSetChanged()
//        viewModel.onAddressSelected(selected)
    }

    override fun onMenuClick(selected: Address) {
        Log.d(TAG, "onMenuClick")
        val bundle = Bundle()
        bundle.putParcelable("address", selected)
        val addressMenu = AddressMenuBottomSheet()
        addressMenu.arguments = bundle
        addressMenu.show(childFragmentManager, Constants.ADDRESS_MENU_BOTTOM_SHEET)
//        addressMenu.dialog?.setOnDismissListener { viewModel.fetchAddress() }
    }

    override fun onResume() {
        super.onResume()
        initAdapterIfNeeded()
        registerGpsBroadcastReceiver()
    }

    private fun initAdapterIfNeeded() {
        if(addressAdapter == null){
            addressAdapter = SelectAddressAdapter(this@SelectAddressFragment)
        }
    }

    override fun onPause() {
        requireContext().unregisterReceiver(gpsBroadcastReceiver)
        addressAdapter = null
        super.onPause()
    }

    override fun onDestroy() {
        binding = null
        gpsBroadcastReceiver = null
        super.onDestroy()
    }

    companion object {
        const val TAG = "wowSelectAddressFrag"
    }


}