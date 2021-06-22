package com.bupp.wood_spoon_eaters.features.main.report_issue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import com.bupp.wood_spoon_eaters.databinding.ReportIssueFragmentBinding
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.model.ReportRequest
import com.bupp.wood_spoon_eaters.model.Reports
import org.koin.androidx.viewmodel.ext.android.viewModel


class ReportIssueFragment(val orderId: Long) : Fragment(R.layout.report_issue_fragment), InputTitleView.InputTitleViewListener,
    ReportIssueAdapter.ReportIssueAdapterListener {

    lateinit var binding: ReportIssueFragmentBinding
    private lateinit var adapter: ReportIssueAdapter
    private var reportRequests: ArrayList<ReportRequest> = arrayListOf()

    companion object {
        fun newInstance(orderId: Long) = ReportIssueFragment(orderId)
    }

    val viewModel by viewModel<ReportIssueViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = ReportIssueFragmentBinding.bind(view)
        initUi()
    }

    private fun initUi() {
        with(binding) {
            val reportTopics = viewModel.getReportTopics()
            reportIssueList.layoutManager = LinearLayoutManager(context)
            adapter = ReportIssueAdapter(requireContext(), reportTopics, this@ReportIssueFragment)
            reportIssueList.adapter = adapter

            reportFragReportBtn.setBtnEnabled(false)
            reportFragReportBtn.setOnClickListener { sendReport() }

            viewModel.postReport.observe(this@ReportIssueFragment, Observer { event ->
                reportIssuePb.hide()
                if (event.isSuccess) {
                    (activity as MainActivity).onReportIssueDone()
                }
            })
        }
    }

    override fun onReportChange(reportRequest: ReportRequest) {
        with(binding) {
            reportFragReportBtn.setBtnEnabled(true)
            reportFragReportBtn.alpha = 1f
        }
    }

    private fun sendReport() {
        binding.reportIssuePb.show()
        val reports = Reports(adapter.getReportsRequestArray())
        viewModel.postReport(orderId, reports)
    }


}
