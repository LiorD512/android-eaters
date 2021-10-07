package com.bupp.wood_spoon_eaters.bottom_sheets.free_text_bottom_sheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.databinding.FreeTextBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FreeTextBottomSheet : BottomSheetDialogFragment(), HeaderView.HeaderViewListener {

    private var binding: FreeTextBottomSheetBinding? = null

    companion object {
        private const val FREE_TEXT_TITLE = "free_text_title"
        private const val FREE_TEXT_BODY = "free_text_body"
        fun newInstance(title: String, body: String): FreeTextBottomSheet {
            return FreeTextBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(FREE_TEXT_TITLE, title)
                    putString(FREE_TEXT_BODY, body)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.free_text_bottom_sheet, container, false)
        binding = FreeTextBottomSheetBinding.bind(view)
        return view
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
            behavior.isFitToContents = true
            behavior.isDraggable = true
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            behavior.expandedOffset = Utils.toPx(230)
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FreeTextBottomSheetBinding.bind(view)

        arguments?.let {
            val title = it.getString(FREE_TEXT_TITLE) ?: "Terms of use"
            val body = it.getString(FREE_TEXT_BODY)
            binding!!.freeTextBottomSheetHeader.setTitle(title)
            binding!!.freeTextBottomSheet.text = body
        }

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.top_cornered_bkg)

        initUI()
    }

    private fun initUI() {
        with(binding!!) {
            freeTextBottomSheetHeader.setHeaderViewListener(this@FreeTextBottomSheet)
        }
    }

    override fun onHeaderCloseClick() {
        dismiss()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}

