package com.bupp.wood_spoon_eaters.bottom_sheets.delete_account

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.abs.FullScreenBottomSheetBase
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.databinding.DeleteAccountBottomSheetBinding
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.views.WSCounterEditText
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DeleteAccountBottomSheet: FullScreenBottomSheetBase(), WSCounterEditText.WSCounterListener, HeaderView.HeaderViewListener {

    private var binding: DeleteAccountBottomSheetBinding? = null
    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.delete_account_bottom_sheet, container, false)
        binding = DeleteAccountBottomSheetBinding.bind(view)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }

//    private lateinit var behavior: BottomSheetBehavior<View>
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
//        dialog.setOnShowListener {
//            val d = it as BottomSheetDialog
//            val sheet = d.findViewById<View>(R.id.design_bottom_sheet)
//            behavior = BottomSheetBehavior.from(sheet!!)
//            behavior.isFitToContents = true
//            behavior.isDraggable = true
//            behavior.state = BottomSheetBehavior.STATE_EXPANDED
////            behavior.expandedOffset = Utils.toPx(230)
//        }
//
//        return dialog
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.top_cornered_bkg)

        viewModel.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_DELETE_ACCOUNT)

        initUI()
    }

    private fun initUI() {
        with(binding!!){
            deleteAccountBtn.setOnClickListener {
                viewModel.deleteAccount()
            }

            deleteAccountHeader.setHeaderViewListener(this@DeleteAccountBottomSheet)
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