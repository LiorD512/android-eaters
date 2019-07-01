package com.bupp.wood_spoon_eaters.features.main.delivery_details

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.DeliveryDetailsView
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import kotlinx.android.synthetic.main.delivery_details_fragment.*

class DeliveryDetailsFragment : Fragment(), DeliveryDetailsView.DeliveryDetailsViewListener {


    companion object {
        fun newInstance() = DeliveryDetailsFragment()
    }

    private lateinit var viewModel: DeliveryDetailsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.delivery_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DeliveryDetailsViewModel::class.java)

        deliveryDetailsFragLocation.setDeliveryDetailsViewListener(this)
        deliveryDetailsFragTime.setDeliveryDetailsViewListener(this)

    }

    override fun onChangeLocationClick() {
        (activity as MainActivity).loadDeliveryDetailsSetup()
    }

    override fun onChangeTimeClick() {
        //open date picker
    }

}
