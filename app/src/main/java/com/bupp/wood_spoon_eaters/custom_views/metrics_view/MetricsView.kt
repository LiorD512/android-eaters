package com.bupp.wood_spoon_eaters.custom_views.metrics_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.Metrics
import kotlinx.android.synthetic.main.metrics_view.view.*

class MetricsView: LinearLayout{


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.metrics_view, this, true)
    }

    interface MetricsViewListener{
        fun onRate()
    }

    fun initMetricsView(metrics: ArrayList<Metrics>, listener: MetricsViewAdapter.MetricsViewAdapterListener) {
        metricsViewList.layoutManager = LinearLayoutManager(context)
        metricsViewList.isNestedScrollingEnabled = false
        val adapter = MetricsViewAdapter(context, metrics, listener)
        metricsViewList.adapter = adapter
    }





}