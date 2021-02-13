package com.bupp.wood_spoon_eaters.features.main.report_issue

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.report_issues.ReportIssueView
import com.bupp.wood_spoon_eaters.model.ReportIssue
import com.bupp.wood_spoon_eaters.model.ReportRequest
import com.bupp.wood_spoon_eaters.model.ReportTopic
import kotlinx.android.synthetic.main.report_issue_item.view.*

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
        return ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.report_issue_item, parent, false))
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

class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val reportIssueView: ReportIssueView = view.reportIssueView

}