package com.bupp.wood_spoon_eaters.features.main.order_history

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.OrdersHistoryItemBinding
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.utils.DateUtils

class OrdersHistoryAdapter(val context: Context, private var orders: ArrayList<Order>, val listener: OrdersHistoryAdapterListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OrdersHistoryAdapterListener{
        fun onOrderClick(orderId: Long)
        fun onRateClick(reportId: Long)
        fun onReportClick(reportId: Long)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val order = orders[position]
        (holder as ItemViewHolder).initItem(context,order )

        if(order?.wasRated == true || order.status.equals("cancelled")){
            holder.rateBtn.isEnabled = false
            holder.rateBtn.setOnClickListener(null)
            holder.rateBtn.setTextColor(ContextCompat.getColor(context, R.color.teal_blue_50))
        }else{
            holder.rateBtn.isEnabled = true
            holder.rateBtn.isClickable = true
            holder.rateBtn.setTextColor(ContextCompat.getColor(context, R.color.teal_blue))
            holder.rateBtn.setOnClickListener {
                listener.onRateClick(order.id!!)
            }
        }

        holder.reportIssue.setOnClickListener {
            listener.onReportClick(order.id!!)
        }

        holder.mainLayout.setOnClickListener {
            listener.onOrderClick(order.id!!)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = OrdersHistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    fun setOrders(orders: ArrayList<Order>) {
        this.orders = orders
        notifyDataSetChanged()
    }
}

class ItemViewHolder(view: OrdersHistoryItemBinding) : RecyclerView.ViewHolder(view.root) {
    fun initItem(context: Context, order: Order) {
        Glide.with(context).load(order.cook?.thumbnail).apply(RequestOptions.circleCropTransform()).into(img)
        title.text = context.getString(R.string.order_history_item_by_cook) + " ${order.cook?.firstName}"
        price.text = "Total: ${order.total?.formatedValue}"
        if(order.estDeliveryTime != null){
            date.text = DateUtils.parseDateToDateAndTime(order.estDeliveryTime)
        }else{
            date.text = "${order.estDeliveryTimeText}"
        }

    }

    private val img: ImageView = view.orderHistoryItemImg
    private val title: TextView = view.orderHistoryItemCookBy
    private val price: TextView = view.orderHistoryItemPrice
    private val date: TextView = view.orderHistoryItemDate
    var rateBtn: TextView = view.orderHistoryItemReorder
    var reportIssue: TextView = view.orderHistoryItemReport
    val mainLayout = view.orderHistoryMainLayout

}