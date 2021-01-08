package com.bupp.wood_spoon_eaters.features.main.report_issue

import android.util.Log
import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.model.ReportTopic
import com.bupp.wood_spoon_eaters.model.Reports
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportIssueViewModel(val api: ApiService, val metaDataRepository: MetaDataRepository) : ViewModel() {

    fun getReportTopics(): ArrayList<ReportTopic> {
        return metaDataRepository.getReportTopics()
    }

    val postReport: SingleLiveEvent<PostReport> = SingleLiveEvent()
    data class PostReport(val isSuccess: Boolean = false)
    fun postReport(orderId: Long, reports: Reports){
        api.postReport(orderId, reports).enqueue(object : Callback<ServerResponse<Void>>{

            override fun onResponse(call: Call<ServerResponse<Void>>, response: Response<ServerResponse<Void>>) {
                if(response.isSuccessful){
                    Log.d("wowReportsVM","postReport success")
                    postReport.postValue(PostReport(true))
                }else{
                    Log.d("wowReportsVM","postReport fail")
                    postReport.postValue(PostReport(false))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Void>>, t: Throwable) {
                Log.d("wowReportsVM","postReport big fail")
                postReport.postValue(PostReport(false))
            }

        })
    }

}
