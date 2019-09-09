package com.bupp.wood_spoon.dialogs

import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.dialogs.address_chooser.AddressChooserAdapter
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.model.Address
import kotlinx.android.synthetic.main.address_chooser_dialog.*

class AddressChooserDialog(val listener: AddressChooserDialogListener, private val addresses: ArrayList<Address>?,var lastOrderAddress: Address?) : DialogFragment(),
    AddressChooserAdapter.AddressChooserAdapterListener {

    private var addressAdapter: AddressChooserAdapter? = null

    interface AddressChooserDialogListener {
        fun onAddressChoose(address: Address)
        fun onAddressMenuClick(address: Address)
        fun onAddAddress()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.address_chooser_dialog, null)
        dialog.window.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        addressChooserDialogBkg.setOnClickListener {
            dismiss()
        }

        addressChooserDialogAddBtn.setOnClickListener {
            onAddAddressClick()
        }

        addressChooserDialogRecycler.layoutManager = LinearLayoutManager(context)

        addressAdapter = AddressChooserAdapter(context!!, addresses, this)

        val dividerItemDecoration = DividerItemDecorator(ContextCompat.getDrawable(context!!, R.drawable.divider))

        addressChooserDialogRecycler.addItemDecoration(dividerItemDecoration)

        addressChooserDialogRecycler.adapter = addressAdapter

        if(lastOrderAddress != null){
            addressAdapter!!.setSelected(lastOrderAddress!!)
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        Log.d("wow", "WelcomeDialog dismiss")
    }

    override fun onAddressClick(selected: Address) {
        listener.onAddressChoose(selected)
        addressAdapter!!.setSelected(selected)
        dismiss()
    }

    override fun onAddAddressClick() {
        listener.onAddAddress()
        dismiss()
    }

    override fun onMenuClick(selected: Address) {
        listener.onAddressMenuClick(selected)
        dismiss()
    }

}