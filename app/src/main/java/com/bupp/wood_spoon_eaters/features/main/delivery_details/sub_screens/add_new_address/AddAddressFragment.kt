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
import com.bupp.wood_spoon_eaters.features.address_and_location.AddressChooserActivity
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderActivity
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.network.google.models.GoogleAddressResponse
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.fragment_add_address.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class AddAddressFragment() : Fragment(), ActionTitleView.ActionTitleViewListener,
    InputTitleView.InputTitleViewListener, View.OnClickListener {

    var curAddress: Address? = null
    companion object{
        const val VAR_ARGS = "addressObj"
        fun newInstance(curAddress: Address? = null): AddAddressFragment{
            val fragment = AddAddressFragment()
            curAddress?.let{
                val bundle = Bundle()
                bundle.putParcelable(VAR_ARGS, curAddress)
                fragment.arguments = bundle
            }
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let{
            curAddress = it.getParcelable<Address?>(VAR_ARGS)
        }
    }

    private var hasApt: Boolean = false
    private var hasAddress: Boolean = false
    private var isDelivery: Boolean = true
    private var myLocationAddress: Address? = null
    private var selectedGoogleAddress: GoogleAddressResponse? = null
    private val viewModel: AddAddressViewModel by viewModel<AddAddressViewModel>()

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
        addAddressFragAddress.updateLocationIcon(viewModel.isLocationEnabled())
        addAddressFragAddress2ndLine.setInputTitleViewListener(this)
        addAddressFragDeliveryNote.setInputTitleViewListener(this)

        if(curAddress != null){
            initEditAddress()
        }

        when (curAddress?.dropOfLocationStr) {
            "pickup_outside" -> addAddressFragPickUpCb.performClick()
            else -> addAddressFragDeliveryCb.performClick()
        }

        viewModel.updateAddressEvent.observe(this, Observer { event -> handleSaveResponse(event) })
        viewModel.myLocationEvent.observe(this, Observer { myLocation -> handleMyLocationEvent(myLocation.myLocation) })
    }

    private fun initEditAddress() {
        curAddress?.let{
            (activity as AddressChooserActivity).loadLocationChooser(it.streetLine1)
            addAddressFragDeliveryNote.setText(it.notes)
        }

        (activity as AddressChooserActivity).setHeaderViewSaveBtnClickable(true)

    }

    override fun onInputTitleChange(streetLine2: String?) {
        hasApt = !streetLine2.isNullOrEmpty()
        myLocationAddress?.streetLine2 = streetLine2!!
        validateFields()
    }

    private fun handleMyLocationEvent(myLocation: Address) {
        addAddressFragAddress.setText(myLocation.streetLine1)
        hasAddress = true
        myLocationAddress = myLocation
        validateFields()
    }

    private fun handleSaveResponse(event: AddAddressViewModel.NavigationEvent?) {
        if (event != null) {
            addAddressFragPb.hide()
            if (event.isSuccessful) {
                (activity as AddressChooserActivity).onNewAddressDone(location = event.addressStreetStr)
            } else {
                Toast.makeText(context, "There was a problem", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onClick(view: View?) {
        if (view is RadioButton) {
            this.isDelivery = view.id == addAddressFragDeliveryCb.id

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


    override fun onActionViewClick(type: Int) {
        when (type) {
            Constants.LOCATION_CHOOSER_ACTION -> {
                if(activity is AddressChooserActivity){
                    (activity as AddressChooserActivity).loadLocationChooser(null)
                }else if(activity is NewOrderActivity){
                    (activity as NewOrderActivity).loadLocationChooser(null)
                }
            }
            else -> {
                Toast.makeText(context, "Not implemented onActionViewClick with Type: $type", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMyLocationClick() {
        viewModel.fetchMyLocation()
    }

    fun onLocationSelected(selected: GoogleAddressResponse?) {
        hasAddress = true
        this.selectedGoogleAddress = selected!!
        if (selected != null) {
            addAddressFragAddress.setText(selected.results!!.formattedAddress!!)
        } else {
            addAddressFragAddress.setText("")
        }
    }

    fun validateFields(){
        if(hasAddress && hasApt){
            if(activity is AddressChooserActivity) {
                (activity as AddressChooserActivity).setHeaderViewSaveBtnClickable(true)
            }else if(activity is NewOrderActivity){
//                (activity as NewOrderActivity).setHeaderViewSaveBtnClickable(true)
            }
        }
    }

    fun saveAddressDetails() {
        addAddressFragPb.show()

        if (selectedGoogleAddress != null || myLocationAddress != null || curAddress != null) {
            viewModel.postNewAddress(
                selectedGoogleAddress,
                myLocationAddress,
                addAddressFragAddress.getText(),
                addAddressFragAddress2ndLine.getText(),
                addAddressFragDeliveryNote.getText(),
                isDelivery,
                curAddress?.id
            )
        }else{
            addAddressFragPb.hide()
            Toast.makeText(context, "error sending address", Toast.LENGTH_SHORT)
        }
    }
}


