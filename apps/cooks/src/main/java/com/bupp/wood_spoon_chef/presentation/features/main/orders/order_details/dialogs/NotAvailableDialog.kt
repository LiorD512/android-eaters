package com.bupp.wood_spoon_chef.presentation.features.main.orders.order_details.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.NotAvailableDialogBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseDialogFragment

class NotAvailableDialog(val listener: NotAvailableDialogListener) :
    BaseDialogFragment(R.layout.not_available_dialog) {

    var binding: NotAvailableDialogBinding? = null

    interface NotAvailableDialogListener {
        fun onDoNotShowNotAvailable(shouldShow: Boolean)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.dark_43
                )
            )
        )
        binding = NotAvailableDialogBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {

        with(binding!!) {
            notAvailableDialogBkg.setOnClickListener {
                dismiss()
            }
            notAvailableDialogClose.setOnClickListener {
                dismiss()
            }
            notAvailableDialogCloseCb.setOnCheckedChangeListener { buttonView, isChecked ->
                listener.onDoNotShowNotAvailable(
                    !isChecked
                )
            }
        }
    }


    override fun clearClassVariables() {
        binding = null
    }

}