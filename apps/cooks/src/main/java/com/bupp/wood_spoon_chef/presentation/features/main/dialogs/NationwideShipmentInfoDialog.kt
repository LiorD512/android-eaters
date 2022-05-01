package com.bupp.wood_spoon_chef.presentation.features.main.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.WorldwideShippmentDialogBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseDialogFragment

class NationwideShipmentInfoDialog: BaseDialogFragment(R.layout.worldwide_shippment_dialog){

    var binding : WorldwideShippmentDialogBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = WorldwideShippmentDialogBinding.bind(view)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))

        super.onViewCreated(view, savedInstanceState)
        initUi()
}

    private fun initUi() {
        with(binding!!) {
            worldwideDialogCloseBtn.setOnClickListener {
                dismiss()
            }
            worldwideDialogBkg.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun clearClassVariables() {
        binding = null
    }
}