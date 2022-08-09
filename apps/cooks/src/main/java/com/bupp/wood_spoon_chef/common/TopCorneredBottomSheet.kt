package com.bupp.wood_spoon_chef.common

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.presentation.features.base.BaseBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

abstract class TopCorneredBottomSheet : BaseBottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                val layoutParams = it.layoutParams as CoordinatorLayout.LayoutParams
                it.layoutParams = layoutParams
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                behaviour.skipCollapsed = true
                it.setBackgroundColor(Color.TRANSPARENT)
            }
        }
        return dialog
    }

    fun setFullScreenDialog() {
        val parent = view?.parent as View
        val layoutParams = parent.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
    }

    fun setDialogAdjustPan(){
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }
}