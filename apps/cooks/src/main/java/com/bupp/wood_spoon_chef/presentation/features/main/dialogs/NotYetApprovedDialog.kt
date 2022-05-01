package com.bupp.wood_spoon_chef.presentation.features.main.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.NotYetApprovedDialogBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseDialogFragment

class NotYetApprovedDialog : BaseDialogFragment(R.layout.not_yet_approved_dialog){

    var binding : NotYetApprovedDialogBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = NotYetApprovedDialogBinding.bind(view)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        super.onViewCreated(view, savedInstanceState)
        initUi()
}

    private fun initUi() {
        with(binding!!) {
            notApprovedDialogBkg.setOnClickListener {
                dismiss()
            }
            notApprovedDialogOk.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun clearClassVariables() {
        binding = null
    }

}