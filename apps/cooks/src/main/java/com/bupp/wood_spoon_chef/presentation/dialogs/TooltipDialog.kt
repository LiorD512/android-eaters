package com.bupp.wood_spoon_chef.presentation.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.TooltipDialogBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseDialogFragment


class TooltipDialog: BaseDialogFragment(R.layout.tooltip_dialog){

    var binding : TooltipDialogBinding?= null

    private var msg: String = ""

    companion object {
        const val MSG_PARAM = "msg"

        fun newInstance(msg: String): TooltipDialog {
            val fragment = TooltipDialog()
            val args = Bundle()
            args.putString(MSG_PARAM, msg)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = TooltipDialogBinding.bind(view)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        arguments?.let{
            msg = requireArguments().getString(MSG_PARAM) ?: ""
        }
        super.onViewCreated(view, savedInstanceState)
        initUi()
}

    private fun initUi() {
        with(binding!!) {
            tooltipDialogMsg.text = msg
            tooltipDialogClose.setOnClickListener {
                dismiss()
            }
            tooltipDialogBkg.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun clearClassVariables() {
        binding = null
    }
}