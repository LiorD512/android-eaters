package com.bupp.wood_spoon_eaters.features.main.report_issue

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.custom_views.report_issues.ReportIssueView
import com.bupp.wood_spoon_eaters.databinding.ReportIssueItemBinding
import com.bupp.wood_spoon_eaters.model.ReportRequest
import com.bupp.wood_spoon_eaters.model.ReportTopic

class ReportIssueAdapter constructor(val context: Context, private var reportTopics: List<ReportTopic>, val listener: ReportIssueAdapterListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), ReportIssueView.ReportIssueViewListener {

    var reportsHash: LinkedHashMap<Long, ReportRequest> = linkedMapOf()
    interface ReportIssueAdapterListener{
        fun onReportChange(reportRequest: ReportRequest)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val reportTopic = reportTopics[position]
        (holder as ItemViewHolder).reportIssueView.initReportIssue(reportTopic, this)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ReportIssueItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return reportTopics.size
    }

    override fun onReportIssueChange(reportRequest: ReportRequest) {
        updateReportRequestArr(reportRequest)
        listener.onReportChange(reportRequest)
    }

    override fun onReportIssueTextChange(reportRequest: ReportRequest) {
        updateReportRequestArr(reportRequest)
        listener.onReportChange(reportRequest)
    }

    private fun updateReportRequestArr(reportRequest: ReportRequest) {
        reportsHash[reportRequest.topicId!!] = reportRequest
    }

    fun getReportsRequestArray(): ArrayList<ReportRequest>? {
        return ArrayList(reportsHash.values)
    }

}

class ItemViewHolder(view: ReportIssueItemBinding) : RecyclerView.ViewHolder(view.root) {
    val reportIssueView: ReportIssueView = view.reportIssueView

}