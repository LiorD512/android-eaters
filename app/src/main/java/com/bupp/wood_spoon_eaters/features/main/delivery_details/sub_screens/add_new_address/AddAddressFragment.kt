package com.bupp.wood_spoon_eaters.features.main.delivery_details.sub_screens.add_new_address

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.ActionTitleView
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.utils.Constants
import com.bupp.wood_spoon_eaters.network.google.models.AddressResponse
import kotlinx.android.synthetic.main.fragment_add_address.*
import org.koin.android.viewmodel.ext.android.viewModel


class AddAddressFragment() : Fragment(), CompoundButton.OnCheckedChangeListener, ActionTitleView.ActionTitleViewListener,
    InputTitleView.InputTitleViewListener {

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
        addAddressFragDeliveryCb.setOnCheckedChangeListener(this)
        addAddressFragPickUpCb.setOnCheckedChangeListener(this)

        addAddressFragAddress.setActionTitleViewListener(this)
        addAddressFragAddress2ndLine.setInputTitleViewListener(this)
        addAddressFragDeliveryNote.setInputTitleViewListener(this)

        viewModel.navigationEvent.observe(this, Observer { event -> handleSaveResponse(event) })
        setCurrentDeliveryDetails()
    }

    private fun handleSaveResponse(event: AddAddressViewModel.NavigationEvent?) {
        if (event!!.isSuccessful) {
            (activity as MainActivity).onNewAddressDone(location = event.address?.streetLine1)
        }else{
            (activity as MainActivity).handlePb(false)
            Toast.makeText(context,"There was a problem",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        hasUpdated = true
        this.isDelivery = buttonView!!.id == addAddressFragDeliveryCb.id
        addAddressFragPickUpCb.typeface = Typeface.DEFAULT
        addAddressFragDeliveryCb.typeface = Typeface.DEFAULT

        if(isChecked){
            buttonView!!.typeface = Typeface.DEFAULT_BOLD
        }
    }


    private fun setCurrentDeliveryDetails() {
        val currentDeliveryDetails = viewModel.getCurrentDeliveryDetails()
        if (currentDeliveryDetails != null) {
            selectedAddress = currentDeliveryDetails.currentNewAddress.addressResponse
            addAddressFragAddress.setText(currentDeliveryDetails.currentNewAddress.address!!.streetLine1)
            addAddressFragAddress2ndLine.setText(currentDeliveryDetails.currentNewAddress.address!!.streetLine2)
            addAddressFragDeliveryNote.setText(currentDeliveryDetails.currentNewAddress.address!!.notes)
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
        if(hasUpdated){
            viewModel.postNewAddress(
                selectedAddress!!,
                addAddressFragAddress.getText(),
                addAddressFragAddress2ndLine.getText(),
                addAddressFragDeliveryNote.getText(),
                isDelivery
            )
        }else{
            (activity as MainActivity).onNewAddressDone()
        }
    }

}