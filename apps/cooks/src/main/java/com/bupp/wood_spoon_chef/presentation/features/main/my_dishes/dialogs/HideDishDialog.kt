package com.bupp.wood_spoon_chef.presentation.features.main.my_dishes.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.HideDishDialogBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseDialogFragment

class HideDishDialog(val listener: HideDishDialogListener) : BaseDialogFragment(R.layout.hide_dish_dialog){

    var binding : HideDishDialogBinding? =null

    interface HideDishDialogListener{
        fun onDishHide()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = HideDishDialogBinding.bind(view)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        super.onViewCreated(view, savedInstanceState)
        initUi()
}

    private fun initUi() {
        with(binding!!) {
            hideDishDialogBkg.setOnClickListener {
                dismiss()
            }
            hideDishDialogCancel.setOnClickListener {
                dismiss()
            }
            hideDishDialogHide.setOnClickListener {
                listener.onDishHide()
                dismiss()
            }
        }

    }

    override fun clearClassVariables() {
        binding = null
    }
}