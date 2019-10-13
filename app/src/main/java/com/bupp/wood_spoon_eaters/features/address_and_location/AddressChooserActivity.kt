package com.bupp.wood_spoon_eaters.features.address_and_location

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.dialogs.address_chooser.sub_screen.AddressMenuDialog
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.dialogs.address_chooser.AddressChooserAdapter
import com.bupp.wood_spoon_eaters.dialogs.locationAutoComplete.LocationChooserFragment
import com.bupp.wood_spoon_eaters.features.main.delivery_details.sub_screens.add_new_address.AddAddressFragment
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.network.google.models.GoogleAddressResponse
import com.bupp.wood_spoon_eaters.utils.Constants

import kotlinx.android.synthetic.main.activity_address_chooser.*
import org.koin.android.viewmodel.ext.android.viewModel


class AddressChooserActivity : AppCompatActivity(), AddressChooserAdapter.AddressChooserAdapterListener,
    AddressMenuDialog.EditAddressDialogListener, HeaderView.HeaderViewListener,
    LocationChooserFragment.LocationChooserFragmentListener {


    val viewModel by viewModel<AddressChooserViewModel>()

    private var currentFragmentTag: String = ""
    private var addresses: ArrayList<Address>? = null
    var lastOrderAddress: Address? = null
    private lateinit var addressAdapter: AddressChooserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_chooser)

        initUi()
    }

    private fun initUi() {
        addresses = viewModel.getListOfAddresses()
        lastOrderAddress = viewModel.getChosenAddress()

        mainAddressChooserActHeaderView.setHeaderViewListener(this)
        addressChooserDialogAddBtn.setOnClickListener {
            onAddAddressClick()
        }

        addressChooserDialogRecycler.layoutManager = LinearLayoutManager(this)
        addressAdapter = AddressChooserAdapter(this, addresses, this)
        val dividerItemDecoration = DividerItemDecorator(ContextCompat.getDrawable(this, R.drawable.divider))
        addressChooserDialogRecycler.addItemDecoration(dividerItemDecoration)
        addressChooserDialogRecycler.adapter = addressAdapter
        if(lastOrderAddress != null){
            addressAdapter.setSelected(lastOrderAddress!!)
        }
    }

    override fun onAddressClick(address: Address) {
        viewModel.setChosenAddress(address)
        addressAdapter.setSelected(address)
        setResult(Activity.RESULT_OK)
        finish()
    }

    fun loadLocationChooser(input: String?) {
        LocationChooserFragment(this, input)
            .show(supportFragmentManager, Constants.LOCATION_CHOOSER_TAG)
    }

    override fun onLocationSelected(selected: GoogleAddressResponse?) {
        if (getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) != null && selected != null) {
            (getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) as AddAddressFragment).onLocationSelected(selected)
        }
    }

    override fun onAddAddressClick() {
        loadFragment(AddAddressFragment(null), Constants.ADD_NEW_ADDRESS_TAG)
        mainAddressChooserActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE_SAVE, "Select Your Delivery Address")
        setHeaderViewSaveBtnClickable(false)
    }

    override fun onMenuClick(address: Address) {
        AddressMenuDialog(address, this).show(supportFragmentManager, Constants.EDIT_ADDRESS_DIALOG)
    }

    override fun onEditAddress(address: Address) {
        loadFragment(AddAddressFragment(address), Constants.ADD_NEW_ADDRESS_TAG)
        mainAddressChooserActHeaderView.setType(Constants.HEADER_VIEW_TYPE_BACK_TITLE_SAVE, "Select Your Delivery Address")
        setHeaderViewSaveBtnClickable(false)
    }

    override fun onAddressDeleted() {
        viewModel.setNoAddress()
        initUi()
    }

    fun setHeaderViewSaveBtnClickable(isClickable: Boolean) {
        mainAddressChooserActHeaderView.setSaveButtonClickable(isClickable)
    }

    fun onNewAddressDone(location: String? = null) {
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun loadFragment(fragment: Fragment, tag: String) {
        mainAddressChooserLayout.visibility = View.GONE
        mainAddressChooserActContainer.visibility = View.VISIBLE
        currentFragmentTag = tag
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainAddressChooserActContainer, fragment, tag)
            .commit()
    }

    private fun getFragmentByTag(tag: String): Fragment? {
        val fragmentManager = this@AddressChooserActivity.supportFragmentManager
        val fragments = fragmentManager.fragments
        for (fragment in fragments) {
            if (fragment.tag == tag)
                return fragment
        }
        return null
    }

    override fun onHeaderSaveClick() {
        if (getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) != null) {
            (getFragmentByTag(Constants.ADD_NEW_ADDRESS_TAG) as AddAddressFragment).saveAddressDetails()
        }
    }

    override fun onHeaderBackClick() {
        setResult(Activity.RESULT_OK)
        finish()
    }

}
