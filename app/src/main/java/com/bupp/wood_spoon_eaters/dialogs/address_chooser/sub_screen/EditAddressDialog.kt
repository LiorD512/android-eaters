package com.bupp.wood_spoon.dialogs

import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.dialogs.address_chooser.AddressChooserAdapter
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.model.Address
import com.example.matthias.mvvmcustomviewexample.custom.EditAddressViewModel
import kotlinx.android.synthetic.main.address_chooser_dialog.*
import kotlinx.android.synthetic.main.edit_address_dialog.*

class EditAddressDialog(val curAddress: Address, val listener: EditAddressDialogListener) : DialogFragment(),
    EditAddressViewModel.EditAddressVMListener {


    val viewModel = EditAddressViewModel()
    private var addressAdapter: AddressChooserAdapter? = null

    interface EditAddressDialogListener {
        fun onEditAddress(address: Address)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.edit_address_dialog, null)
        dialog.window.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        editAddressTitle.text = curAddress?.streetLine1
        editAddressClose.setOnClickListener { dismiss() }
        editAddressEditBtn.setOnClickListener { editAddress() }
        editAddressRemoveBtn.setOnClickListener { removeAddress() }
    }

    private fun editAddress() {
        dismiss()
        listener?.onEditAddress(curAddress)
    }

    private fun removeAddress() {
        editAddressPb.show()
        viewModel.removeAddress(curAddress?.id!!, this)
    }

    override fun onDone(isSuccess: Boolean) {
        editAddressPb.hide()
        if(isSuccess){
            Toast.makeText(context, "Address Removed!", Toast.LENGTH_SHORT).show()
            dismiss()
        }else{
            Toast.makeText(context, "Address Remove Failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        Log.d("wow", "WelcomeDialog dismiss")
    }


}