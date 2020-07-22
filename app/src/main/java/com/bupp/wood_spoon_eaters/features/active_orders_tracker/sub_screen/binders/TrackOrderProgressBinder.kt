package com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.binders

import android.view.View
import android.view.ViewGroup
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.OrderTrackProgress
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.track_order_progress_section.view.*
import mva2.adapter.ItemBinder
import mva2.adapter.ItemViewHolder
import java.util.*


class TrackOrderProgressBinder() : ItemBinder<OrderTrackProgress, TrackOrderProgressBinder.TrackOrderProgressViewHolder>() {

    private var curOrderStage: Int = 0

    override fun createViewHolder(parent: ViewGroup): TrackOrderProgressViewHolder {
        return TrackOrderProgressViewHolder(inflate(parent, R.layout.track_order_progress_section))
    }

    override fun canBindData(item: Any): Boolean {
        return item is OrderTrackProgress
    }

    override fun bindViewHolder(holder: TrackOrderProgressViewHolder, items: OrderTrackProgress) {
        holder.bindItems(items)
    }

    inner class TrackOrderProgressViewHolder(itemView: View) : ItemViewHolder<OrderTrackProgress>(itemView) {
        fun bindItems(orderItem: OrderTrackProgress) {
            val order = orderItem.order
            order?.let {
                when (order.preparationStatus) {
                    "in_progress" -> {
                        itemView.trackOrderProgressCb2.isChecked = true
                        curOrderStage = 2
                    }
                    "completed" -> {
                        itemView.trackOrderProgressCb2.isChecked = true
                        curOrderStage = 2
                    }
                }

                when (order.deliveryStatus) {
                    "on_the_way" -> {
                        itemView.trackOrderProgressCb3.isChecked = true
                        curOrderStage = 3
                    }
                    "shipped" -> {
                        itemView.trackOrderProgressCb4.isChecked = true
                        curOrderStage = 4
                    }
                }

                val today = Calendar.getInstance()
                today.time = Date()

                val deliveryTime = Calendar.getInstance()
                deliveryTime.time = order.estDeliveryTime

                if (Utils.isSameDay(today, deliveryTime)) {
                    itemView.trackOrderProgressArrivalTime.text = "Estimated arrival - ${Utils.parseDateToTime(order.estDeliveryTime)}"
                } else {
                    itemView.trackOrderProgressArrivalTime.text = "Estimated arrival - ${Utils.parseDateToFullDate(order.estDeliveryTime)}"
                }
            }
        }
    }

}