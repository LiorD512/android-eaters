package com.bupp.wood_spoon_eaters.features.main.delivery_details.sub_screens.delivery_details_setup

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bupp.wood_spoon_eaters.R

class DeliveryDetailsSetup : Fragment() {

    companion object {
        fun newInstance() = DeliveryDetailsSetup()
    }

    private lateinit var viewModel: DeliveryDetailsSetupViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.delivery_details_setup_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DeliveryDetailsSetupViewModel::class.java)

    }

}
