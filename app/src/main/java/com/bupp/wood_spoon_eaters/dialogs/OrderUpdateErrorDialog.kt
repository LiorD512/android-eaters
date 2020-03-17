package com.bupp.wood_spoon_eaters.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import kotlinx.android.synthetic.main.order_update_error_dialog.*

class OrderUpdateErrorDialog(val listener: updateErrorDialogListener) : DialogFragment() {


    interface updateErrorDialogListener {
        fun onClearCart()
        fun onCancelUpdateOrderError(){}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.order_update_error_dialog, null)
        dialog!!.window.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        updateErrorDialogCancelBtn.setOnClickListener {
            listener.onCancelUpdateOrderError()
            dismiss()
        }
        updateErrorDialogCloseBtn.setOnClickListener {
            listener.onCancelUpdateOrderError()
            dismiss()}
        updateErrorDialogClearBtn.setOnClickListener {
            listener.onClearCart()
            dismiss()
        }

    }

}