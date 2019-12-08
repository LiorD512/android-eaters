package com.bupp.wood_spoon_eaters.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderActivity
import kotlinx.android.synthetic.main.address_missing_dialog.*
import kotlinx.android.synthetic.main.clear_cart_dialog.*
import kotlinx.android.synthetic.main.clear_cart_dialog.clearCartDialogCloseBtn

class AddressMissingDialog(val listener: AddressMissingDialogListener) : DialogFragment() {

    interface AddressMissingDialogListener {
        fun pnUpdateAddress()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.address_missing_dialog, null)
        dialog!!.window.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        addressmissingUpdateBtn.setOnClickListener {
            listener.pnUpdateAddress()
            dismiss()
        }
        caddressmissingCloseBtn.setOnClickListener { dismiss()}
    }

}