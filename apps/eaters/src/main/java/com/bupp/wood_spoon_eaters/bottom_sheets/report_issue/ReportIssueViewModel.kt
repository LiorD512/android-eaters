package com.bupp.wood_spoon_eaters.bottom_sheets.report_issue

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.di.abs.ProgressData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EventsManager
import com.bupp.wood_spoon_eaters.model.ReportTopic
import com.bupp.wood_spoon_eaters.model.Reports
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import kotlinx.coroutines.launch

class ReportIssueViewModel(private val orderRepository: OrderRepository, val metaDataRepository: MetaDataRepository,
    private val eventsManager: EventsManager) : ViewModel() {

    val progressData = ProgressData()
    private var currentOrderId: Long = -1

    val reportIssueData = MutableLiveData<List<ReportTopic>>()
    init{
        reportIssueData.postValue(metaDataRepository.getReportTopics())
    }

    fun setOrderId(orderId: Long) {
        this.currentOrderId = orderId
    }

    val postReport: SingleLiveEvent<PostReport> = SingleLiveEvent()
    data class PostReport(val isSuccess: Boolean = false)
    fun postReport(reports: Reports){
        viewModelScope.launch {
            progressData.startProgress()
            val result = orderRepository.postReportIssue(currentOrderId, reports)
            when(result.type){
                OrderRepository.OrderRepoStatus.REPORT_ISSUE_SUCCESS -> {
                    postReport.postValue(PostReport(true))
                }
                else -> {
                    postReport.postValue(PostReport(false))
                }
            }
            progressData.endProgress()
        }
    }

    fun logOpenScreenEvent() {
        eventsManager.logEvent(Constants.EVENT_ORDER_REPORT_ISSUE_CLICK)
    }

}
