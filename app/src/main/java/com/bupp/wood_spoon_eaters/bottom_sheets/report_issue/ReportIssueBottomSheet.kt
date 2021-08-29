package com.bupp.wood_spoon_eaters.bottom_sheets.report_issue

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import com.bupp.wood_spoon_eaters.databinding.ReportIssueFragmentBinding
import com.bupp.wood_spoon_eaters.dialogs.title_body_dialog.TitleBodyDialog
import com.bupp.wood_spoon_eaters.model.ReportRequest
import com.bupp.wood_spoon_eaters.model.Reports
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class ReportIssueBottomSheet() : BottomSheetDialogFragment(), InputTitleView.InputTitleViewListener,
    ReportIssueAdapter.ReportIssueAdapterListener, TitleBodyDialog.TitleBodyDialogListener {

    val binding: ReportIssueFragmentBinding by viewBinding()
    private var adapter: ReportIssueAdapter ? = null
    val viewModel by viewModel<ReportIssueViewModel>()

    companion object {
        private const val SINGLE_ORDER_ARGS = "single_order_args"
        fun newInstance(orderId: Long): ReportIssueBottomSheet {
            return ReportIssueBottomSheet().apply {
                arguments = Bundle().apply {
                    putLong(SINGLE_ORDER_ARGS, orderId)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.report_issue_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
        arguments?.let {
            viewModel.setOrderId(it.getLong(SINGLE_ORDER_ARGS))
        }
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
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.top_cornered_bkg)

        initUi()
        initObservers()
    }

    private fun initUi() {
        with(binding) {
            reportIssueList.layoutManager = LinearLayoutManager(context)
            adapter = ReportIssueAdapter(this@ReportIssueBottomSheet)
            reportIssueList.adapter = adapter

            reportFragReportBtn.setBtnEnabled(false)
            reportFragReportBtn.setOnClickListener { sendReport() }
        }
    }

    private fun initObservers() {
        viewModel.postReport.observe(viewLifecycleOwner,  { event ->
            if (event.isSuccess) {
                TitleBodyDialog.newInstance(getString(R.string.thank_you), getString(R.string.report_issue_done_body)).show(childFragmentManager, Constants.TITLE_BODY_DIALOG)
            }
        })

        viewModel.reportIssueData.observe(viewLifecycleOwner, {
            adapter?.submitList(it)
        })

        viewModel.progressData.observe(viewLifecycleOwner, {
            if(it){
                binding.reportIssuePb.show()
            }else{
                binding.reportIssuePb.hide()
            }
        })
    }


    override fun onReportChange(reportRequest: ReportRequest) {
        with(binding) {
            reportFragReportBtn.setBtnEnabled(true)
            reportFragReportBtn.alpha = 1f
        }
    }

    private fun sendReport() {
        adapter?.let{ adapter->
            val reports = Reports(adapter.getReportsRequestArray())
            viewModel.postReport(reports)
        }
    }

    override fun onTitleBodyDialogDismiss() {
        dismiss()
    }
}
