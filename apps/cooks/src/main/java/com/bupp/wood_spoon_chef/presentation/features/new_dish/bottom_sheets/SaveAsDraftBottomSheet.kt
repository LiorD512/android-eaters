package com.bupp.wood_spoon_chef.presentation.features.new_dish.bottom_sheets

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.SaveAsDraftBottomSheetBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


class SaveAsDraftBottomSheet(val listener: SaveAsDraftListener) : BaseBottomSheetDialogFragment() {

    interface SaveAsDraftListener {
        fun onSaveAsDraftClicked()
    }

    private var binding: SaveAsDraftBottomSheetBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.save_as_draft_bottom_sheet, container, false)
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
//            behavior.isFitToContents = true
//            behavior.expandedOffset = Utils.toPx(230)
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SaveAsDraftBottomSheetBinding.bind(view)

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.popup_window_transparent)

        initUi()
    }


    private fun initUi() {
        binding?.apply {
            saveAsDraftSave.setOnClickListener {
                listener.onSaveAsDraftClicked()
                dismiss()
            }
            saveAsDraftCancel.setOnClickListener {
                dismiss()
            }
        }

    }

    override fun clearClassVariables() {
        binding = null
    }

}