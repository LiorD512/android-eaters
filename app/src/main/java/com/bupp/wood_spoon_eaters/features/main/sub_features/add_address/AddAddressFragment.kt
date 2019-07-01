package com.bupp.wood_spoon_eaters.features.main.sub_features.add_address

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import kotlinx.android.synthetic.main.fragment_add_address.*
import org.koin.android.viewmodel.ext.android.viewModel


class AddAddressFragment : Fragment(), CompoundButton.OnCheckedChangeListener {

    private val viewModel: AddAddressViewModel by viewModel<AddAddressViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        addAddressFragDeliveryCb.setOnCheckedChangeListener(this)
        addAddressFragPickUpCb.setOnCheckedChangeListener(this)

        viewModel.deliveryDetails.observe(this, Observer { details -> setDeliveryDetails(details) })
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (buttonView!!.id == addAddressFragDeliveryCb.id && addAddressFragDeliveryCb.isChecked) {
            //change font
            addAddressFragDeliveryCb.typeface = Typeface.DEFAULT_BOLD
            addAddressFragPickUpCb.typeface = Typeface.DEFAULT

            //uncheck the other button
            addAddressFragPickUpCb.isChecked = false

            //disable the other button
            addAddressFragDeliveryCb.isEnabled = false
            addAddressFragPickUpCb.isEnabled = true

        } else if (buttonView.id == addAddressFragPickUpCb.id && addAddressFragPickUpCb.isChecked) {
            //change font
            addAddressFragDeliveryCb.typeface = Typeface.DEFAULT
            addAddressFragPickUpCb.typeface = Typeface.DEFAULT_BOLD

            //uncheck the other button
            addAddressFragDeliveryCb.isChecked = false

            //disable the other button
            addAddressFragPickUpCb.isEnabled = false
            addAddressFragDeliveryCb.isEnabled = true
        }
    }


    private fun setDeliveryDetails(details: AddAddressViewModel.DeliveryDetails?) {

        if (details != null) {
            if (details.isDelivery) {
                addAddressFragDeliveryCb.isChecked = true
                addAddressFragPickUpCb.isChecked = false
            } else {
                addAddressFragDeliveryCb.isChecked = false
                addAddressFragPickUpCb.isChecked = true
            }
        }
    }
}