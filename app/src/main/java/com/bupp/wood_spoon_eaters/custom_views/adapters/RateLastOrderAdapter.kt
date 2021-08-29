package com.bupp.wood_spoon_eaters.custom_views.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_eaters.databinding.RateDishItemViewBinding
import com.bupp.wood_spoon_eaters.model.DishMetricsRequest
import com.bupp.wood_spoon_eaters.model.OrderItem

class RateLastOrderAdapter(val context: Context, private var orderItems: List<OrderItem>?, val listener: RateOrderAdapterListener) : RecyclerView.Adapter<RateLastOrderAdapter.DishViewHolder>() {

    val NEGATIVE = 0
    val POSITIVE = 1
    var metricsRequestsMap: LinkedHashMap<Int, DishMetricsRequest> = linkedMapOf()

    interface RateOrderAdapterListener{
        fun onRate()
    }

    class DishViewHolder(view: RateDishItemViewBinding) : RecyclerView.ViewHolder(view.root) {
        val image: ImageView = view.orderItemImage
        val name: TextView = view.orderItemName
        val positiveBtn: RadioButton = view.rateDishItemPositive
        val negativeBtn: RadioButton = view.rateDishItemNegative
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val binding = RateDishItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DishViewHolder(binding)
    }

    override fun getItemCount(): Int {
        if(orderItems == null)
            return 0
        return orderItems!!.size
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        val orderItem: OrderItem = orderItems!![position]

        Glide.with(context).load(orderItem.dish.thumbnail).apply(RequestOptions.circleCropTransform())
            .into(holder.image)

        holder.name.text = "${orderItem.dish.name} x${orderItem.quantity}"

        holder.positiveBtn.setOnClickListener { setClicked(position, orderItem.dish.id, POSITIVE) }
        holder.negativeBtn.setOnClickListener { setClicked(position, orderItem.dish.id, NEGATIVE) }

    }

    private fun setClicked(position: Int, id: Long, selectedValue: Int) {
        val metricsRequest =  DishMetricsRequest(id, selectedValue)
        Log.d("wowMetricsView","added metrics: $metricsRequest")
        metricsRequestsMap[position] = metricsRequest
        listener.onRate()
    }

    fun getRatedDishes(): ArrayList<DishMetricsRequest> {
        return ArrayList(metricsRequestsMap.values)
    }

    fun isAllRated(): Boolean{
        return metricsRequestsMap.values.size == itemCount
    }
}