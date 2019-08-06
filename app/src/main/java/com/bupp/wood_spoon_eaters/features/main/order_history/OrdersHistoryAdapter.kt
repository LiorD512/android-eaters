package com.bupp.wood_spoon_eaters.features.main.order_history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.orders_history_item.view.*

class OrdersHistoryAdapter(val context: Context, private var orders: ArrayList<Order>, val listener: OrdersHistoryAdapterListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OrdersHistoryAdapterListener{
        fun onRateClick(reportId: Long)
        fun onReportClick(reportId: Long)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).initItem(context, orders[position])

        holder.reOrder.setOnClickListener {
            listener?.onRateClick(orders[position].id)
        }
        holder.reportIssue.setOnClickListener {
            listener?.onReportClick(orders[position].id)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.orders_history_item, parent, false))
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    fun setOrders(orders: ArrayList<Order>) {
        this.orders = orders
        notifyDataSetChanged()
    }
}

class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun initItem(context: Context, order: Order) {
        Glide.with(context).load(order.cook.thumbnail).apply(RequestOptions.circleCropTransform()).into(img)
        title.text = context.getString(R.string.order_history_item_by_cook) + " ${order.cook.firstName}, "
        price.text = order.total.formatedValue

        date.text = Utils.parseDateToDateAndTime(order.deliverAt)

    }

    private val img: ImageView = view.orderHistoryItemImg
    private val title: TextView = view.orderHistoryItemCookBy
    private val price: TextView = view.orderHistoryItemPrice
    private val date: TextView = view.orderHistoryItemDate
    var reOrder: TextView = view.orderHistoryItemReorder
    var reportIssue: TextView = view.orderHistoryItemReport

}