package com.bupp.wood_spoon_eaters.features.locations_and_address.address_list_chooser

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.common.Constants
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import kotlinx.android.synthetic.main.fragment_address_list_chooser.*
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.dialogs.address_chooser.sub_screen.AddressMenuDialog
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressViewModel

class AddressListChooserFragment : Fragment(R.layout.fragment_address_list_chooser), AddressChooserAdapter.AddressChooserAdapterListener,
    AddressMenuDialog.EditAddressDialogListener {

    private lateinit var addressAdapter: AddressChooserAdapter
    private val mainViewModel by sharedViewModel<LocationAndAddressViewModel>()
    val viewModel by viewModel<AddressChooserViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initObservers()
    }

    private fun initUi() {
        addressChooserAddBtn.setOnClickListener {
            onAddAddressClick()
        }

        addressChooserList.layoutManager = LinearLayoutManager(requireContext())
        addressAdapter = AddressChooserAdapter(this)
        val dividerItemDecoration = DividerItemDecorator(ContextCompat.getDrawable(requireContext(), R.drawable.divider))
        addressChooserList.addItemDecoration(dividerItemDecoration)
        addressChooserList.adapter = addressAdapter

        viewModel.initAddressChooser()
    }

    private fun initObservers() {
        viewModel.addressChooserData.observe(viewLifecycleOwner, Observer{
            addressAdapter.submitList(it)
        })
    }

    private fun onAddAddressClick() {
        mainViewModel.onAddNewAddressClick()
    }

    override fun onAddressClick(selected: Address) {
        viewModel.setChosenAddress(selected)
        mainViewModel.onAddressChooserDone()
    }

    override fun onMenuClick(selected: Address) {
        AddressMenuDialog(selected, this).show(childFragmentManager, Constants.EDIT_ADDRESS_DIALOG)
    }

    override fun onEditAddress(address: Address) {
        mainViewModel.onEditAddressClick(address)
//        loadFragment(AddAddressFragment.newInstance(address), Constants.ADD_NEW_ADDRESS_TAG)
//        mainAddressChooserActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE_SAVE, "Select Your Delivery Address")
//        setHeaderViewSaveBtnClickable(false)
    }

    override fun onAddressDeleted() {
        viewModel.setNoAddress()
        initUi()
    }


}