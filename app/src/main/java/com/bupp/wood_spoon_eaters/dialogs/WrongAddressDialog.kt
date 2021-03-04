package com.bupp.wood_spoon_eaters.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import kotlinx.android.synthetic.main.error_dialog.*
import kotlinx.android.synthetic.main.error_dialog.errorDialogBkg
import kotlinx.android.synthetic.main.wrong_address_dialog.*

class WrongAddressDialog: DialogFragment(){

    var listener: WrongAddressDialogListener? = null
    interface WrongAddressDialogListener{
        fun onReEnterAddressClick()
    }
    fun setWrongAddressDialogListener(listener: WrongAddressDialogListener): WrongAddressDialog{
        this.listener = listener
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.wrong_address_dialog, null)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
}

    private fun initUi() {
        wrongAddressDialogBtn.setOnClickListener{
            listener?.onReEnterAddressClick()
            dismiss()}
        wrongAddressDialogCloseBtn.setOnClickListener{
            dismiss()}
    }

}