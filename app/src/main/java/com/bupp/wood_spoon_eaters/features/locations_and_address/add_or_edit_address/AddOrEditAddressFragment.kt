package com.bupp.wood_spoon_eaters.features.locations_and_address.add_or_edit_address

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.ActionTitleView
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressViewModel
import com.bupp.wood_spoon_eaters.model.Address
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.fragment_add_or_edit_address.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class AddOrEditAddressFragment : Fragment(R.layout.fragment_add_or_edit_address), ActionTitleView.ActionTitleViewListener,
    InputTitleView.InputTitleViewListener, View.OnClickListener {

    private val viewModel: AddOrEditAddressViewModel by viewModel()
    private val mainViewModel: LocationAndAddressViewModel by sharedViewModel()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initAddOrEdit(arguments?.getParcelable<Address>("address"))

        initUi()
        initObservers()
    }

    private fun initUi() {
        addAddressFragDeliveryCb.setOnClickListener(this)
        addAddressFragPickUpCb.setOnClickListener(this)

        addAddressFragAddress.setActionTitleViewListener(this)
        addAddressFragAddress.updateLocationIcon(viewModel.isLocationEnabled())
        addAddressFragAddress2ndLine.setInputTitleViewListener(this)
        addAddressFragDeliveryNote.setInputTitleViewListener(this)

    }

    private fun initObservers() {
        viewModel.editAddressEvent.observe(viewLifecycleOwner, Observer{
//            (activity as AddressChooserActivity).loadLocationChooser(it.streetLine1)
            addAddressFragDeliveryNote.setText(it.notes)
            when (it.dropOfLocationStr) {
                "pickup_outside" -> addAddressFragPickUpCb.performClick()
                else -> addAddressFragDeliveryCb.performClick()
            }
        })

        viewModel.getLocationLiveData().observe(viewLifecycleOwner, Observer {
            Log.d("wowAddOrEditAddress'","getLocationLiveData observer called ")
        })

        viewModel.myLocationEvent.observe(viewLifecycleOwner, Observer { myLocation -> handleMyLocationEvent(myLocation.myLocation) })

        mainViewModel.actionEvent.observe(viewLifecycleOwner, Observer {
            if(validateAddressData()){
                viewModel.saveNewAddress()
            }
        })

        mainViewModel.addressFoundEvent.observe(viewLifecycleOwner, Observer{
            addAddressFragAddress.setText(it)
        })

    }

    private fun validateAddressData(): Boolean{
        return true
    }

    private fun handleMyLocationEvent(myLocation: Address) {
        addAddressFragAddress.setText(myLocation.streetLine1)
//        hasAddress = true
//        myLocationAddress = myLocation
    }

    override fun onMyLocationClick() {
        viewModel.fetchMyLocation()
    }

    override fun onClick(v: View?) {
    }

    override fun onActionViewClick(type: Int) {
        mainViewModel.onSearchAddressAutoCompleteClick()
    }


//    var curAddress: Address? = null
//
//    companion object {
//        const val VAR_ARGS = "addressObj"
//        fun newInstance(curAddress: Address? = null): AddOrEditAddressFragment {
//            val fragment = AddOrEditAddressFragment()
//            curAddress?.let {
//                val bundle = Bundle()
//                bundle.putParcelable(VAR_ARGS, curAddress)
//                fragment.arguments = bundle
//            }
//            return fragment
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        try {
//            curAddress = arguments?.getParcelable<Address?>(VAR_ARGS)
////            requireArguments()?.let {
////                curAddress = it.getParcelable<Address?>(VAR_ARGS)
////            }
//        } catch (ex: Exception) {
//            Log.d("wowAddAddresFrag", "exception: ${ex.message}")
//        }
//    }
//
//    private var hasApt: Boolean = false
//    private var hasAddress: Boolean = false
//    private var isDelivery: Boolean = true
//    private var myLocationAddress: Address? = null
//    private var selectedGoogleAddress: GoogleAddressResponse? = null
//
//
//
//
//
//
//    private fun initEditAddress() {
//        curAddress?.let {
//            (activity as AddressChooserActivity).loadLocationChooser(it.streetLine1)
//            addAddressFragDeliveryNote.setText(it.notes)
//        }
//
//        (activity as AddressChooserActivity).setHeaderViewSaveBtnClickable(true)
//
//    }
//
//    override fun onInputTitleChange(streetLine2: String?) {
//        hasApt = !streetLine2.isNullOrEmpty()
//        myLocationAddress?.streetLine2 = streetLine2!!
//        validateFields()
//    }
//
//    private fun handleMyLocationEvent(myLocation: Address) {
//        addAddressFragAddress.setText(myLocation.streetLine1)
//        hasAddress = true
//        myLocationAddress = myLocation
//        validateFields()
//    }
//
//    private fun handleSaveResponse(event: AddAddressViewModel.NavigationEvent?) {
//        if (event != null) {
//            addAddressFragPb.hide()
//            if (event.isSuccessful) {
//                (activity as AddressChooserActivity).onNewAddressDone(location = event.addressStreetStr)
//            } else {
//                Toast.makeText(context, "There was a problem", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//
//    override fun onClick(view: View?) {
//        if (view is RadioButton) {
//            this.isDelivery = view.id == addAddressFragDeliveryCb.id
//
//            addAddressFragDeliveryCb.typeface = Typeface.DEFAULT
//            addAddressFragPickUpCb.typeface = Typeface.DEFAULT
//
//            addAddressFragDeliveryCb.isSelected = false
//            addAddressFragPickUpCb.isSelected = false
//
//            if (view.isChecked) {
//                view.typeface = Typeface.DEFAULT_BOLD
//                view.isSelected = true
//            }
//        }
//    }
//
//

//
//    override fun onMyLocationClick() {
//        viewModel.fetchMyLocation()
//    }
//
//    fun onLocationSelected(selected: GoogleAddressResponse?) {
//        hasAddress = true
//        this.selectedGoogleAddress = selected!!
//        if (selected != null) {
//            addAddressFragAddress.setText(selected.results!!.formattedAddress!!)
//        } else {
//            addAddressFragAddress.setText("")
//        }
//    }
//
//    fun validateFields() {
//        if (hasAddress && hasApt) {
//            if (activity is AddressChooserActivity) {
//                (activity as AddressChooserActivity).setHeaderViewSaveBtnClickable(true)
//            } else if (activity is NewOrderActivity) {
////                (activity as NewOrderActivity).setHeaderViewSaveBtnClickable(true)
//            }
//        }
//    }
//
//    fun saveAddressDetails() {
//        addAddressFragPb.show()
//        Log.d("wowAddressBug","saveAddressDetails -> selectedGoogleAddress: $selectedGoogleAddress")
//        if (selectedGoogleAddress != null || myLocationAddress != null || curAddress != null) {
//            viewModel.postNewAddress(
//                selectedGoogleAddress,
//                myLocationAddress,
//                addAddressFragAddress.getText(),
//                addAddressFragAddress2ndLine.getText(),
//                addAddressFragDeliveryNote.getText(),
//                isDelivery,
//                curAddress?.id
//            )
//        } else {
//            addAddressFragPb.hide()
//            Toast.makeText(context, "error sending address", Toast.LENGTH_SHORT)
//        }
//    }
}


