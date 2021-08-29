package com.bupp.wood_spoon_eaters.bottom_sheets.report_issue

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.custom_views.report_issues.ReportIssueView
import com.bupp.wood_spoon_eaters.databinding.ReportIssueItemBinding
import com.bupp.wood_spoon_eaters.model.ReportRequest
import com.bupp.wood_spoon_eaters.model.ReportTopic

class ReportIssueAdapter constructor(val listener: ReportIssueAdapterListener) :
    ListAdapter<ReportTopic, RecyclerView.ViewHolder>(DiffCallback()), ReportIssueView.ReportIssueViewListener {

    var reportsHash: LinkedHashMap<Long, ReportRequest> = linkedMapOf()

    interface ReportIssueAdapterListener {
        fun onReportChange(reportRequest: ReportRequest)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ReportIssueItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val reportTopic = getItem(position)
        val itemViewHolder = holder as ViewHolder
        itemViewHolder.reportIssueView.initReportIssue(reportTopic, this)
    }

    class ViewHolder(binding: ReportIssueItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val reportIssueView: ReportIssueView = binding.reportIssueView
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

    class DiffCallback : DiffUtil.ItemCallback<ReportTopic>() {

        override fun areItemsTheSame(oldItem: ReportTopic, newItem: ReportTopic): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ReportTopic, newItem: ReportTopic): Boolean {
            return oldItem == newItem
        }
    }

    fun getReportsRequestArray(): List<ReportRequest> {
        return ArrayList(reportsHash.values)
    }
}