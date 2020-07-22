package com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.binders

import android.util.Log
import android.view.View
import mva2.adapter.ItemViewHolder
import android.view.ViewGroup
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.OrderTrackDetailsHeader
import kotlinx.android.synthetic.main.track_order_details_header_section.view.*
import mva2.adapter.ItemBinder


class TrackOrderDetailsHeaderBinder(val listener: TrackOrderHeaderListener) : ItemBinder<OrderTrackDetailsHeader, TrackOrderDetailsHeaderBinder.TrackOrderDetailsViewHolder>() {

    private var isExpended = true
    interface TrackOrderHeaderListener{
        fun onHeaderClick(isExpanded: Boolean)
    }

    override fun createViewHolder(parent: ViewGroup): TrackOrderDetailsViewHolder {
        return TrackOrderDetailsViewHolder(inflate(parent, R.layout.track_order_details_header_section))
    }

    override fun canBindData(item: Any): Boolean {
        return item is OrderTrackDetailsHeader
    }

    override fun bindViewHolder(holder: TrackOrderDetailsViewHolder, items: OrderTrackDetailsHeader) {
        holder.bindItems(items)
    }

    inner class TrackOrderDetailsViewHolder(itemView: View) : ItemViewHolder<OrderTrackDetailsHeader>(itemView) {
        fun bindItems(item: OrderTrackDetailsHeader) {
            itemView.trackOrderDetailsHeaderTitle.text = "Order #${item.orderNumber}"
            itemView.trackOrderDetailsHeaderLayout.setOnClickListener{
//                toggleItemExpansion()
                isExpended = !isExpended
                Log.d("wowTrackOrderHeader","on Title Click: $isExpended")
                listener.onHeaderClick(isExpended)
            }

        }
    }

}