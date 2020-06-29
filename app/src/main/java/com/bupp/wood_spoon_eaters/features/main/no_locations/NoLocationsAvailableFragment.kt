package com.bupp.wood_spoon_eaters.features.main.no_locations


import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.provider.SyncStateContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.fragment_no_locations_available.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class NoLocationsAvailableFragment : Fragment() {

    val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_no_locations_available, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noLocationFragSettings.setOnClickListener { viewModel.startAndroidLocationSettings() }
    }



}
