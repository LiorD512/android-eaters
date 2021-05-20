package com.bupp.wood_spoon_eaters.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.TipCourierDialogLayoutBinding

class TipCourierDialog(val listener: TipCourierDialogListener) : DialogFragment() {

    lateinit var binding: TipCourierDialogLayoutBinding
    interface TipCourierDialogListener {
        fun onTipDone(tipAmount: Int)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.tip_courier_dialog_layout, null)
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = TipCourierDialogLayoutBinding.bind(view)
        initUi()
    }

    private fun initUi() {
        with(binding){
            tipCourierDialogCloseBtn.setOnClickListener { dismiss() }
            tipCourierDialogDoneBtn.setOnClickListener {
                val enteredAmountStr = tipCourierDialogTipAmount.text.toString()
                var enteredAmount = 0

                if (enteredAmountStr.isNotEmpty()) {
                    enteredAmount = Integer.parseInt(enteredAmountStr)
                }
                listener.onTipDone(enteredAmount)
                dismiss()
            }
        }
    }
}