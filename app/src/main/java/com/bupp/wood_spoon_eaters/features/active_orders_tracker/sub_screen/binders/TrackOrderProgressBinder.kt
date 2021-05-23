package com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.binders

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bupp.wood_spoon_eaters.databinding.TrackOrderProgressSectionBinding
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.OrderTrackProgress
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.utils.DateUtils
import mva2.adapter.ItemBinder
import mva2.adapter.ItemViewHolder
import java.util.*


class TrackOrderProgressBinder(val listener: TrackOrderProgressListener) : ItemBinder<OrderTrackProgress, TrackOrderProgressBinder.TrackOrderProgressViewHolder>() {

    private var curOrderStage: Int = 1

    interface TrackOrderProgressListener {
        fun onContactUsClick(order: Order)
        fun onShareImageClick(order: Order)
        fun onOrderCanceled(orderState: Int, orderId: Long)
    }

    override fun createViewHolder(parent: ViewGroup): TrackOrderProgressViewHolder {
        val binding = TrackOrderProgressSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackOrderProgressViewHolder(binding)
    }

    override fun canBindData(item: Any): Boolean {
        return item is OrderTrackProgress
    }

    override fun bindViewHolder(holder: TrackOrderProgressViewHolder, items: OrderTrackProgress) {
        holder.bindItems(items)
    }

    inner class TrackOrderProgressViewHolder(val binding: TrackOrderProgressSectionBinding) : ItemViewHolder<OrderTrackProgress>(binding.root) {
        fun bindItems(orderItem: OrderTrackProgress) {
            val order = orderItem.order
            order?.let {
                when (order.preparationStatus) {
                    "in_progress" -> {
                        binding.trackOrderProgressCb2.isChecked = true
                        curOrderStage = 2
                    }
                    "completed" -> {
                        binding.trackOrderProgressCb2.isChecked = true
                        curOrderStage = 2
                    }
                }

                when (order.deliveryStatus) {
                    "on_the_way" -> {
                        binding.trackOrderProgressCb3.isChecked = true
                        curOrderStage = 3
                    }
                    "shipped" -> {
                        binding.trackOrderProgressCb4.isChecked = true
                        curOrderStage = 4
                    }
                }


                val typeface = Typeface.createFromAsset(itemView.context.getAssets(),"font/open_sans_bold.ttf")
                setFontTypeDefault(binding, itemView.context)
                when(curOrderStage){
                    1 -> {binding.trackOrderProgressCb1.setTypeface(typeface)}
                    2 -> {binding.trackOrderProgressCb2.setTypeface(typeface)}
                    3 -> {binding.trackOrderProgressCb3.setTypeface(typeface)}
                    4 -> {binding.trackOrderProgressCb4.setTypeface(typeface)}
                }

                val today = Calendar.getInstance()
                today.time = Date()

                if(order.estDeliveryTime != null){
                    val deliveryTime = Calendar.getInstance()
                    deliveryTime.time = order.estDeliveryTime

                    if (DateUtils.isSameDay(today, deliveryTime)) {
                        binding.trackOrderProgressArrivalTime.text = "Estimated arrival - ${DateUtils.parseDateHalfHourInterval(order.estDeliveryTime)}"
                    } else {
                        binding.trackOrderProgressArrivalTime.text = "Estimated arrival - ${DateUtils.parseDateToDayDateAndTime(order.estDeliveryTime)}"
                    }
                }else{
                    binding.trackOrderProgressArrivalTime.text = "${order.estDeliveryTimeText}"
                }

                binding.trackOrderBottomContactUsBtn.setOnClickListener {
                    listener.onContactUsClick(order)
                }

                binding.trackOrderBottomCancelBtn.setOnClickListener {
                    listener.onOrderCanceled(curOrderStage, order.id!!)

                }

                binding.trackOrderBottomShareImageBtn.setOnClickListener {
                    listener.onShareImageClick(order)
                }
            }
        }
    }

    private fun setFontTypeDefault(binding: TrackOrderProgressSectionBinding, context: Context) {
        val regTypeface = Typeface.createFromAsset(context.getAssets(),"font/open_sans_reg.ttf")
        binding.trackOrderProgressCb1.setTypeface(regTypeface)
        binding.trackOrderProgressCb2.setTypeface(regTypeface)
        binding.trackOrderProgressCb3.setTypeface(regTypeface)
        binding.trackOrderProgressCb4.setTypeface(regTypeface)
    }


}