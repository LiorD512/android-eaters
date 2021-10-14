package com.bupp.wood_spoon_eaters.bottom_sheets.custom_tip

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.databinding.CustomTipBottomSheetBinding
import com.bupp.wood_spoon_eaters.databinding.JoinAsChefBottomSheetBinding
import com.bupp.wood_spoon_eaters.dialogs.title_body_dialog.TitleBodyDialog
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.utils.AnimationUtil
import com.bupp.wood_spoon_eaters.views.WSCounterEditText
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CustomTipBottomSheet: BottomSheetDialogFragment(), HeaderView.HeaderViewListener {

    private val binding: CustomTipBottomSheetBinding by viewBinding()
    val mainViewModel by sharedViewModel<MainViewModel>()
    var listener: CustomTipListener? = null

    interface CustomTipListener{
        fun onCustomTipDone(tip: Double)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.custom_tip_bottom_sheet, container, false)
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

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.top_cornered_bkg)

        mainViewModel.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_JOIN_HOME_CHEF)

        initUI()
    }

    private fun initUI() {
        with(binding){
            customTipDoneBtn.setOnClickListener {
                onTipDone()
            }

            customTipHeader.setHeaderViewListener(this@CustomTipBottomSheet)
        }
    }

    private fun onTipDone() {
        val input = binding.customTipTipAmount.text.toString()
        if(input.isEmpty()){
            AnimationUtil().shakeView(binding.customTipTipAmount)
        }else{
            listener?.onCustomTipDone(input.toDouble()) //convert cents to dollars
            dismiss()
        }
    }


    override fun onHeaderCloseClick() {
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener = null
        super.onDismiss(dialog)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CustomTipListener) {
            listener = context
        }
        else if (parentFragment is CustomTipListener){
            this.listener = parentFragment as CustomTipListener
        }
        else {
            throw RuntimeException("$context must implement CustomTipListener")
        }
    }


}