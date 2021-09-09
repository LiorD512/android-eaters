package com.bupp.wood_spoon_eaters.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.WrongAddressDialogBinding

class WrongAddressDialog: DialogFragment(){

    val binding: WrongAddressDialogBinding by viewBinding()

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
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.wrong_address_dialog, null)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
}

    private fun initUi() {
        with(binding){
            wrongAddressDialogBtn.setOnClickListener{
                listener?.onReEnterAddressClick()
                dismiss()}
            wrongAddressDialogCloseBtn.setOnClickListener{
                dismiss()}
        }
    }

}