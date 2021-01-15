package com.bupp.wood_spoon_eaters.features.login.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.login.LoginViewModel
import kotlinx.android.synthetic.main.fragment_location_permission.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LocationPermissionFragment : Fragment() {

    val viewModel by sharedViewModel<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        locationPermissionFragAllow.setOnClickListener {
            viewModel.askLocationPermission()
        }

        locationPermissionFragReject.setOnClickListener {
            viewModel.onLocationPermissionDone()
        }
    }




}