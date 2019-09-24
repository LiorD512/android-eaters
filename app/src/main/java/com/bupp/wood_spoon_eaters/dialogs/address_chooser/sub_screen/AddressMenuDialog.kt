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
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.dialogs.address_chooser.AddressChooserAdapter
import com.bupp.wood_spoon_eaters.model.Address
import com.example.matthias.mvvmcustomviewexample.custom.AddressMenuViewModel
import kotlinx.android.synthetic.main.address_menu_dialog.*

class AddressMenuDialog(val curAddress: Address, val listener: EditAddressDialogListener) : DialogFragment(),
    AddressMenuViewModel.EditAddressVMListener {


    val viewModel = AddressMenuViewModel()
    private var addressAdapter: AddressChooserAdapter? = null

    interface EditAddressDialogListener {
        fun onEditAddress(address: Address)
        fun onAddressDeleted()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.address_menu_dialog, null)
        dialog.window.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        editAddressTitle.text = curAddress.streetLine1
        editAddressClose.setOnClickListener { dismiss() }
        editAddressEditBtn.setOnClickListener { editAddress() }
        editAddressRemoveBtn.setOnClickListener { removeAddress() }
    }

    private fun editAddress() {
        listener.onEditAddress(curAddress)
        dismiss()
    }

    private fun removeAddress() {
        editAddressPb.show()
        viewModel.removeAddress(curAddress.id!!, this)
    }

    override fun onRemoveAddressDone(isSuccess: Boolean) {
        editAddressPb.hide()
        if(isSuccess){
            Toast.makeText(context, "Address Removed!", Toast.LENGTH_SHORT).show()
            listener.onAddressDeleted()
            dismiss()
        }else{
            Toast.makeText(context, "Address Remove Failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        Log.d("wow", "AddressMenuDialog dismiss")
    }


}