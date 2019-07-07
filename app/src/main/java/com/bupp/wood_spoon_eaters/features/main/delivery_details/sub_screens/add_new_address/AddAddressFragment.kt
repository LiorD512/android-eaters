package com.bupp.wood_spoon_eaters.features.main.delivery_details.sub_screens.add_new_address

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.ActionTitleView
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.network.google.models.AddressResponse
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.fragment_add_address.*
import org.koin.android.viewmodel.ext.android.viewModel


class AddAddressFragment : Fragment(), ActionTitleView.ActionTitleViewListener,
    InputTitleView.InputTitleViewListener, View.OnClickListener {


    private var hasUpdated: Boolean = false
    private var isDelivery: Boolean = true
    private val viewModel: AddAddressViewModel by viewModel<AddAddressViewModel>()
    private var selectedAddress: AddressResponse? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        addAddressFragDeliveryCb.setOnClickListener(this)
        addAddressFragPickUpCb.setOnClickListener(this)

        addAddressFragAddress.setActionTitleViewListener(this)
        addAddressFragAddress2ndLine.setInputTitleViewListener(this)
        addAddressFragDeliveryNote.setInputTitleViewListener(this)

        viewModel.navigationEvent.observe(this, Observer { event -> handleSaveResponse(event) })
        setCurrentDeliveryDetails()
    }

    private fun handleSaveResponse(event: AddAddressViewModel.NavigationEvent?) {
        if (event != null) {
            (activity as MainActivity).handlePb(false)
            if (event!!.isSuccessful) {
                (activity as MainActivity).onNewAddressDone(location = event.address?.streetLine1)
            } else {
                Toast.makeText(context, "There was a problem", Toast.LENGTH_SHORT).show()
            }
        }
    }

//        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
//        hasUpdated = true
//        this.isDelivery = buttonView!!.id == addAddressFragDeliveryCb.id
//        addAddressFragDeliveryCb.typeface = Typeface.DEFAULT
//        addAddressFragPickUpCb.typeface = Typeface.DEFAULT
//
//        if (isChecked) {
//            buttonView.typeface = Typeface.DEFAULT_BOLD
//        }
//    }

    override fun onClick(view: View?) {
        if (view is RadioButton) {

            hasUpdated = true
            this.isDelivery = view!!.id == addAddressFragDeliveryCb.id

            addAddressFragDeliveryCb.typeface = Typeface.DEFAULT
            addAddressFragPickUpCb.typeface = Typeface.DEFAULT

            addAddressFragDeliveryCb.isSelected = false
            addAddressFragPickUpCb.isSelected = false

            if (view.isChecked) {
                view.typeface = Typeface.DEFAULT_BOLD
                view.isSelected = true
            }
        }
    }


    private fun setCurrentDeliveryDetails() {
        val currentDeliveryDetails = viewModel.getCurrentDeliveryDetails().currentNewAddress
        selectedAddress = currentDeliveryDetails.addressResponse
        addAddressFragAddress.setText(currentDeliveryDetails.address!!.streetLine1)
        addAddressFragAddress2ndLine.setText(currentDeliveryDetails.address!!.streetLine2)

        if (currentDeliveryDetails.isDelivery != null && currentDeliveryDetails.isDelivery) {
            addAddressFragDeliveryNote.setText(viewModel.prepareNotes(currentDeliveryDetails.address!!.notes,currentDeliveryDetails.isDelivery))
            addAddressFragRadioGroup.check(R.id.addAddressFragDeliveryCb)

            addAddressFragDeliveryCb.typeface = Typeface.DEFAULT_BOLD
            addAddressFragPickUpCb.typeface = Typeface.DEFAULT

            addAddressFragDeliveryCb.isSelected = true

        } else {
            addAddressFragDeliveryNote.setText(currentDeliveryDetails.address!!.notes)
            addAddressFragRadioGroup.check(R.id.addAddressFragPickUpCb)

            addAddressFragDeliveryCb.typeface = Typeface.DEFAULT
            addAddressFragPickUpCb.typeface = Typeface.DEFAULT_BOLD

            addAddressFragPickUpCb.isSelected = true
        }
    }

    override fun onActionViewClick(type: Int) {
        when (type) {
            Constants.LOCATION_CHOOSER_ACTION -> {
                (activity as MainActivity).loadLocationChooser()
            }
            else -> {
                Toast.makeText(context, "Not implemented onActionViewClick with Type: $type", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun onLocationSelected(selected: AddressResponse?) {
        hasUpdated = true
        this.selectedAddress = selected!!
        if (selected != null) {
            addAddressFragAddress.setText(selected.results!!.formattedAddress!!)
        } else {
            addAddressFragAddress.setText("")
        }
    }

    fun saveAddressDetails() {
        if (hasUpdated) {
            if (selectedAddress != null) {
                viewModel.postNewAddress(
                    selectedAddress!!,
                    addAddressFragAddress.getText(),
                    addAddressFragAddress2ndLine.getText(),
                    addAddressFragDeliveryNote.getText(),
                    isDelivery
                )
            } else {
                Toast.makeText(context, "address did not selected", Toast.LENGTH_SHORT).show()
            }
        } else {
            (activity as MainActivity).onNewAddressDone()
        }
    }

}