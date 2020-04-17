package com.bupp.wood_spoon_eaters.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import kotlinx.android.synthetic.main.payment_method_accepted_dialog.*

class PaymentMethodAcceptedDialog() : DialogFragment(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.payment_method_accepted_dialog, null)
        getDialog()!!.getWindow()?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)));
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
}

    private fun initUi() {
        paymentSuccessDialogBkg.setOnClickListener{
            dismiss()}
        paymentSuccessDialogCloseBtn.setOnClickListener{
            dismiss()}
    }

}