package com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.binders

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.OrderTrackProgress
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.track_order_progress_section.view.*
import kotlinx.android.synthetic.main.track_order_progress_section.view.trackOrderBottomCancelBtn
import kotlinx.android.synthetic.main.track_order_progress_section.view.trackOrderBottomContactUsBtn
import kotlinx.android.synthetic.main.track_order_progress_section.view.trackOrderBottomShareImageBtn
import mva2.adapter.ItemBinder
import mva2.adapter.ItemViewHolder
import java.util.*


class TrackOrderProgressBinder(val listener: TrackOrderProgressBinder.TrackOrderProgressListener) : ItemBinder<OrderTrackProgress, TrackOrderProgressBinder.TrackOrderProgressViewHolder>() {

    private var curOrderStage: Int = 1

    interface TrackOrderProgressListener {
        fun onContactUsClick(order: Order)
        fun onShareImageClick(order: Order)
        fun onOrderCanceled(orderState: Int, orderId: Long)
    }

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


                val typeface = Typeface.createFromAsset(itemView.context.getAssets(),"font/open_sans_bold.ttf")
                setFontTypeDefault(itemView)
                when(curOrderStage){
                    1 -> {itemView.trackOrderProgressCb1.setTypeface(typeface)}
                    2 -> {itemView.trackOrderProgressCb2.setTypeface(typeface)}
                    3 -> {itemView.trackOrderProgressCb3.setTypeface(typeface)}
                    4 -> {itemView.trackOrderProgressCb4.setTypeface(typeface)}
                }

                val today = Calendar.getInstance()
                today.time = Date()

                if(order.estDeliveryTime != null){
                    val deliveryTime = Calendar.getInstance()
                    deliveryTime.time = order.estDeliveryTime

                    if (DateUtils.isSameDay(today, deliveryTime)) {
                        itemView.trackOrderProgressArrivalTime.text = "Estimated arrival - ${DateUtils.parseDateToTime(order.estDeliveryTime)}"
                    } else {
                        itemView.trackOrderProgressArrivalTime.text = "Estimated arrival - ${DateUtils.parseDateToFullDate(order.estDeliveryTime)}"
                    }
                }else{
                    itemView.trackOrderProgressArrivalTime.text = "${order.estDeliveryTimeText}"
                }

                itemView.trackOrderBottomContactUsBtn.setOnClickListener {
                    listener.onContactUsClick(order)
                }

                itemView.trackOrderBottomCancelBtn.setOnClickListener {
                    listener.onOrderCanceled(curOrderStage, order.id)

                }

                itemView.trackOrderBottomShareImageBtn.setOnClickListener {
                    listener.onShareImageClick(order)
                }
            }
        }
    }

    private fun setFontTypeDefault(itemView: View) {
        val regTypeface = Typeface.createFromAsset(itemView.context.getAssets(),"font/open_sans_reg.ttf")
        itemView.trackOrderProgressCb1.setTypeface(regTypeface)
        itemView.trackOrderProgressCb2.setTypeface(regTypeface)
        itemView.trackOrderProgressCb3.setTypeface(regTypeface)
        itemView.trackOrderProgressCb4.setTypeface(regTypeface)
    }


}