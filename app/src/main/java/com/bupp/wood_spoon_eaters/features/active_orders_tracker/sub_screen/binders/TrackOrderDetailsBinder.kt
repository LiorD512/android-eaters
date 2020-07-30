package com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.binders

import android.view.View
import mva2.adapter.ItemViewHolder
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.OrderTrackDetails
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.track_order_details_section.view.*
import mva2.adapter.ItemBinder


class TrackOrderDetailsBinder() : ItemBinder<OrderTrackDetails, TrackOrderDetailsBinder.TrackOrderDetailsViewHolder>() {

    override fun createViewHolder(parent: ViewGroup): TrackOrderDetailsViewHolder {
        return TrackOrderDetailsViewHolder(inflate(parent, R.layout.track_order_details_section))
    }

    override fun canBindData(item: Any): Boolean {
        return item is OrderTrackDetails
    }

    override fun bindViewHolder(holder: TrackOrderDetailsViewHolder, items: OrderTrackDetails) {
        holder.bindItems(items)
    }

    inner class TrackOrderDetailsViewHolder(itemView: View) : ItemViewHolder<OrderTrackDetails>(itemView) {
        fun bindItems(item: OrderTrackDetails) {

            val order = item.order
            val userInfo = item.orderUserInfo

            val adapter = TrackOrderItemDetailsAdapter(itemView.context)
            itemView.trackOrderDetailsSectionOrderItemList.layoutManager = LinearLayoutManager(itemView.context)
            itemView.trackOrderDetailsSectionOrderItemList.adapter = adapter
            val divider = DividerItemDecorator(ContextCompat.getDrawable(itemView.context, R.drawable.divider))
            itemView.trackOrderDetailsSectionOrderItemList.addItemDecoration(divider)
            adapter.submitList(order.orderItems)

            itemView.trackOrderDetailsSectionSubTotal.text = order.subtotal.formatedValue
            itemView.trackOrderDetailsSectionServiceFee.text = order.serviceFee.formatedValue
            itemView.trackOrderDetailsSectionDeliveryFee.text = order.deliveryFee.formatedValue
            itemView.trackOrderDetailsSectionTax.text = order.tax.formatedValue
            itemView.trackOrderDetailsSectionTotal.text = order.total.formatedValue
//
            itemView.trackOrderDetailsSectionDate.text = Utils.parseDateToFullDate(order.created_at)
            itemView.trackOrderDetailsSectionPayment.text = userInfo?.paymentMethod
            itemView.trackOrderDetailsSectionUserInfo.text = userInfo?.userInfo
            itemView.trackOrderDetailsSectionLocation.text = userInfo?.userLocation?.getUserLocationStr()
            itemView.trackOrderDetailsSectionOrderNumber.text = order.orderNumber
        }
    }

}