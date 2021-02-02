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
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.databinding.FragmentSelectAddressBinding
import com.bupp.wood_spoon_eaters.features.bottom_sheets.address_menu.AddressMenuBottomSheet
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressViewModel
import com.bupp.wood_spoon_eaters.features.locations_and_address.address_list_chooser.AddressChooserAdapter
import com.bupp.wood_spoon_eaters.managers.location.GPSBroadcastReceiver
import com.bupp.wood_spoon_eaters.managers.location.GpsUtils
import com.bupp.wood_spoon_eaters.model.Address
import kotlinx.android.synthetic.main.fragment_address_list_chooser.*
import kotlinx.android.synthetic.main.fragment_select_address.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectAddressFragment : Fragment(R.layout.fragment_select_address), GPSBroadcastReceiver.GPSBroadcastListener,
    SelectAddressAdapter.SelectAddressAdapterListener {

    private lateinit var addressAdapter: SelectAddressAdapter
    private lateinit var gpsBroadcastReceiver: GPSBroadcastReceiver
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
        viewModel.updateMyLocationUiState(isLocationEnabled(requireContext()))
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

    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    private fun initUi() {
        binding!!.selectAddressFragMyLocationLayout.setOnClickListener {
            viewModel.onMyLocationClick()
        }
        binding!!.selectAddressFragDone.setOnClickListener {
            mainViewModel.onDoneClick()
        }

        binding!!.selectAddressFragAutoComplete.setOnClickListener {
            mainViewModel.onSearchAddressAutoCompleteClick()
        }

        binding!!.selectAddressFraList.layoutManager = LinearLayoutManager(requireContext())
        addressAdapter = SelectAddressAdapter(this)
        val dividerItemDecoration = DividerItemDecorator(ContextCompat.getDrawable(requireContext(), R.drawable.divider))
        binding!!.selectAddressFraList.addItemDecoration(dividerItemDecoration)
        binding!!.selectAddressFraList.adapter = addressAdapter

    }

    private fun initObservers() {
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
                }
            }
        })

        mainViewModel.getLocationLiveData().observe(viewLifecycleOwner, {
            Log.d(TAG, "getLocationLiveData observer called ")
            binding!!.selectAddressFragMyLocationAddress.text = it.getUserLocationStr()
            binding!!.selectAddressFragMyLocationPickup.text = it.getDropoffLocationStr()
            binding!!.selectAddressFragMyLocationPickup.visibility = View.VISIBLE
        })

        viewModel.myLocationEvent.observe(viewLifecycleOwner, { myLocation -> handleMyLocationEvent(myLocation) })

        viewModel.myAddressEvent.observe(viewLifecycleOwner, {
            addressAdapter.submitList(it)
        })
        mainViewModel.addressFoundEvent.observe(viewLifecycleOwner, {
            selectAddressFragAutoComplete.text = it.getUserLocationStr()
        })
    }

    private fun handleMyLocationEvent(myLocation: SelectAddressViewModel.MyLocationEvent) {
        when (myLocation.status) {
            SelectAddressViewModel.MyLocationStatus.FETCHING -> {
                binding!!.selectAddressFragMyLocationAddress.text = "Fetching your location"
                binding!!.selectAddressFragMyLocationPickup.visibility = View.GONE
            }
            SelectAddressViewModel.MyLocationStatus.NO_PERMISSION -> {
                binding!!.selectAddressFragMyLocationAddress.text = "Need to turn on location permission."
                binding!!.selectAddressFragMyLocationPickup.text = "Tap here to enable it."
            }
            SelectAddressViewModel.MyLocationStatus.NO_GPS_ENABLED -> {
                binding!!.selectAddressFragMyLocationAddress.text = "Please enable GPS on device"
                binding!!.selectAddressFragMyLocationPickup.text = "Tap here to enable it."
            }
        }

    }

    override fun onAddressClick(selected: Address) {
        Log.d(TAG, "onAddressClick")
    }

    override fun onMenuClick(selected: Address) {
        Log.d(TAG, "onMenuClick")
        val bundle = Bundle()
        bundle.putParcelable("address", selected)
        val addressMenu = AddressMenuBottomSheet()
        addressMenu.arguments = bundle
        addressMenu.show(childFragmentManager, Constants.ADDRESS_MENU_BOTTOM_SHEET)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        registerGpsBroadcastReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().unregisterReceiver(gpsBroadcastReceiver)
    }

    companion object {
        const val TAG = "wowSelectAddressFrag"
    }


}