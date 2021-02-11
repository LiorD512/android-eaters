package com.bupp.wood_spoon_eaters.features.locations_and_address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FragmentLocationPermissionBinding
import org.koin.android.ext.android.bind
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LocationPermissionFragment : Fragment() {

    private var binding: FragmentLocationPermissionBinding? = null
    private val mainViewModel by sharedViewModel<LocationAndAddressViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLocationPermissionBinding.bind(view)
        initUi()
    }

    private fun initUi() {
        binding!!.locationPermissionFragAllow.setOnClickListener {
            mainViewModel.askLocationPermission()
        }

        binding!!.locationPermissionFragReject.setOnClickListener {
            mainViewModel.onLocationPermissionDone()
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }


}