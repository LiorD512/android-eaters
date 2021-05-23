package com.bupp.wood_spoon_eaters.custom_views.metrics_view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.databinding.MetricsViewItemBinding
import com.bupp.wood_spoon_eaters.model.DishMetricsRequest
import com.bupp.wood_spoon_eaters.model.Metrics

class MetricsViewAdapter(val context: Context, val metrics: ArrayList<Metrics>, val listener: MetricsViewAdapterListener) : RecyclerView.Adapter<MetricsViewAdapter.ViewHolder>() {

    val NEGATIVE = 0
    val POSITIVE = 1

    interface MetricsViewAdapterListener{
        fun onRate()
    }

//    val hashSet = hashSetOf<DishMetricsRequest>()
    val hashSet = hashMapOf<Long, DishMetricsRequest>()

    class ViewHolder(view: MetricsViewItemBinding) : RecyclerView.ViewHolder(view.root) {
        val title = view.metricsViewTitle
        val description = view.metricsViewDescription
        val positiveBtn = view.metricsViewPositive
        val negativeBtn = view.metricsViewNegative
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MetricsViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return metrics.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curMetrics = metrics.get(position)

        holder.title.text = curMetrics.name
        holder.description.text = curMetrics.description

        holder.positiveBtn.setOnClickListener { setClicked(curMetrics.id, POSITIVE) }
        holder.negativeBtn.setOnClickListener { setClicked(curMetrics.id, NEGATIVE) }

    }

    fun getMetricResult(): List<DishMetricsRequest>? {
        if (hashSet.size > 0) {
            return ArrayList(hashSet.values)
        }
        return null
    }


    private fun setClicked(id: Long, selectedValue: Int) {
        val metricsRequest =  DishMetricsRequest(id, selectedValue)
        Log.d("wowMetricsView","added metrics: $metricsRequest")
        hashSet[id] = metricsRequest
        listener.onRate()
    }



}
