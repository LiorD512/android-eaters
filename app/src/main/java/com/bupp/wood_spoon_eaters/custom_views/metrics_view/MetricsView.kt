package com.bupp.wood_spoon_eaters.custom_views.metrics_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.databinding.MetricsViewBinding
import com.bupp.wood_spoon_eaters.model.Metrics

class MetricsView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr){

    private var binding: MetricsViewBinding = MetricsViewBinding.inflate(LayoutInflater.from(context), this, true)

    interface MetricsViewListener{
        fun onRate()
    }

    fun initMetricsView(metrics: ArrayList<Metrics>, listener: MetricsViewAdapter.MetricsViewAdapterListener) {
        with(binding){
            metricsViewList.layoutManager = LinearLayoutManager(context)
            metricsViewList.isNestedScrollingEnabled = false
            val adapter = MetricsViewAdapter(context, metrics, listener)
            metricsViewList.adapter = adapter
        }
    }





}