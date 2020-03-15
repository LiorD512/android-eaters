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
import kotlinx.android.synthetic.main.clear_cart_dialog.*

class ClearCartDialog(val listener: ClearCartDialogListener) : DialogFragment() {

    interface ClearCartDialogListener {
        fun onClearCart()
        fun onCancelClearCart(){}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.clear_cart_dialog, null)
        dialog!!.window.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        clearCartDialogYesBtn.setOnClickListener {
            listener.onClearCart()
            dismiss()
        }
        clearCartDialogCloseBtn.setOnClickListener {
            listener.onCancelClearCart()
            dismiss()}
        clearCartDialogNoBtn.setOnClickListener {
            listener.onCancelClearCart()
            dismiss()
        }

    }

}