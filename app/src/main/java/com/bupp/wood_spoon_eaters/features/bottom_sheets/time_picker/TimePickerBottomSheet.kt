package com.bupp.wood_spoon_eaters.features.bottom_sheets.time_picker

import android.app.Dialog
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.TimePickerBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class TimePickerBottomSheet: BottomSheetDialogFragment() {

    private lateinit var binding: TimePickerBottomSheetBinding
    val viewModel by viewModel<TimePickerViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.time_picker_bottom_sheet, container, false)
    }

//    private lateinit var behavior: BottomSheetBehavior<View>
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
//        dialog.setOnShowListener {
//            val d = it as BottomSheetDialog
//            val sheet = d.findViewById<View>(R.id.design_bottom_sheet)
//            behavior = BottomSheetBehavior.from(sheet!!)
//            behavior.isHideable = false
//            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            behavior.isDraggable = false
//        }
//        return dialog
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resources = resources

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val parent = view.parent as View
            val layoutParams = parent.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.setMargins(
                resources.getDimensionPixelSize(R.dimen.bottom_sheet_horizontal_margin), // LEFT 16dp
                0,
                resources.getDimensionPixelSize(R.dimen.bottom_sheet_horizontal_margin), // RIGHT 16dp
                0
            )
            parent.layoutParams = layoutParams
            parent.setBackgroundResource(R.drawable.bottom_sheet_bkg)
        }


        binding = TimePickerBottomSheetBinding.bind(view)

        initUi()
    }

    private fun initUi() {
        binding.timePickerAsapBtn.setOnClickListener {
            viewModel.setDeliveryTime(null)
            dismiss()
        }
        binding.timePickerScheduleBtn.setOnClickListener {
            viewModel.setDeliveryTime(binding.singleDayPicker.date)
            dismiss()
        }
    }

}