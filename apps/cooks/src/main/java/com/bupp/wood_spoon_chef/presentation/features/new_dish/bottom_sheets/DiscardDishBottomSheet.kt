package com.bupp.wood_spoon_chef.presentation.features.new_dish.bottom_sheets

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.DiscardDishBottomSheetBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


class DiscardDishBottomSheet(val listener: DiscardDishListener) : BaseBottomSheetDialogFragment(){

    interface DiscardDishListener{
        fun onDiscardDishClicked()
    }

    private var binding: DiscardDishBottomSheetBinding ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.discard_dish_bottom_sheet, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }

    private lateinit var behavior: BottomSheetBehavior<View>
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(sheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DiscardDishBottomSheetBinding.bind(view)

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.popup_window_transparent)

        initUi()
    }


    private fun initUi() {
        binding?.apply{
            discardDish.setOnClickListener {
                listener.onDiscardDishClicked()
                dismiss()
            }
            discardDishCancel.setOnClickListener {
                dismiss()
            }
        }

    }

    override fun clearClassVariables() {
        binding = null
    }

}