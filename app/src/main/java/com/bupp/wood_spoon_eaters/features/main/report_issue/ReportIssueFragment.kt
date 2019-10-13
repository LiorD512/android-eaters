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
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.model.ReportRequest
import com.bupp.wood_spoon_eaters.model.Reports
import kotlinx.android.synthetic.main.report_issue_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel


class ReportIssueFragment(val orderId: Long) : Fragment(), InputTitleView.InputTitleViewListener,
    ReportIssueAdapter.ReportIssueAdapterListener {

    private lateinit var adapter: ReportIssueAdapter
    private var reportRequests: ArrayList<ReportRequest> = arrayListOf()

    companion object {
        fun newInstance(orderId: Long) = ReportIssueFragment(orderId)
    }

    val viewModel by viewModel<ReportIssueViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.report_issue_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        val reportTopics = viewModel.getReportTopics()
        reportIssueList.layoutManager = LinearLayoutManager(context)
        adapter = ReportIssueAdapter(context!!, reportTopics, this)
        reportIssueList.adapter = adapter

        reportFragReportBtn.setBtnEnabled(false)
        reportFragReportBtn.setOnClickListener { sendReport() }

        viewModel.postReport.observe(this, Observer{ event ->
            reportIssuePb.hide()
            if(event.isSuccess){
                (activity as MainActivity).onReportIssueDone()
            }
        })
    }

    override fun onReportChange(reportRequest: ReportRequest) {
//        reportRequests.add(reportRequest)
        reportFragReportBtn.setBtnEnabled(true)
        reportFragReportBtn.alpha = 1f
    }

    private fun sendReport() {
        reportIssuePb.show()
        val reports = Reports(adapter.getReportsRequestArray()!!)
        viewModel.postReport(orderId, reports)
    }




}
