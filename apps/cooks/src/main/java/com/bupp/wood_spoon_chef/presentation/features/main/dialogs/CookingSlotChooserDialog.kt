package com.bupp.wood_spoon_chef.presentation.features.main.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.CookingSlotChooserDialogBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseDialogFragment
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot

class CookingSlotChooserDialog(
    val cookingSlot: CookingSlot,
    val listener: CookingSlotChooserListener
) : BaseDialogFragment(R.layout.cooking_slot_chooser_dialog) {

    var binding : CookingSlotChooserDialogBinding? =null

    interface CookingSlotChooserListener{
        fun onCancelCookingSlot(cookingSlot: CookingSlot)
        fun onEditCookingSlot(cookingSlot: CookingSlot)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        binding = CookingSlotChooserDialogBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        initUi()
}

    private fun initUi() {
        with(binding!!) {
            cookingSlotChooserDialogClose.setOnClickListener {
                dismiss()
            }
            cookingSlotChooserDialogCancel.setOnClickListener {
                listener.onCancelCookingSlot(cookingSlot)
                dismiss()
            }
            cookingSlotChooserDialogEdit.setOnClickListener {
                listener.onEditCookingSlot(cookingSlot)
                dismiss()
            }
        }
    }

    override fun clearClassVariables() {
        binding = null
    }

}