package com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.binders

import android.view.View
import mva2.adapter.ItemViewHolder
import android.view.ViewGroup
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.OrderTrackBottom
import com.bupp.wood_spoon_eaters.model.Order
import kotlinx.android.synthetic.main.track_order_bottom_section.view.*
import mva2.adapter.ItemBinder


class TrackOrderBottomBinder(val listener: TrackOrderBottomListener) : ItemBinder<OrderTrackBottom, TrackOrderBottomBinder.TrackOrderBottomViewHolder>() {

    interface TrackOrderBottomListener {
        fun onContactUsClick(order: Order)
        fun onShareImageClick(order: Order)
        fun onOrderCanceled()
    }

    override fun createViewHolder(parent: ViewGroup): TrackOrderBottomViewHolder {
        return TrackOrderBottomViewHolder(inflate(parent, R.layout.track_order_bottom_section))
    }

    override fun canBindData(item: Any): Boolean {
        return item is OrderTrackBottom
    }

    override fun bindViewHolder(holder: TrackOrderBottomViewHolder, items: OrderTrackBottom) {
        holder.bindItems(items)
    }

    inner class TrackOrderBottomViewHolder(itemView: View) : ItemViewHolder<OrderTrackBottom>(itemView) {
        fun bindItems(item: OrderTrackBottom) {

            itemView.trackOrderBottomContactUsBtn.setOnClickListener {
//                listener.onContactUsClick(curOrder)
            }


//            trackOrderBottomCancelBtn.setOnClickListener {
//                if(curOrderStage <= 1){
//                    CancelOrderDialog(Constants.CANCEL_ORDER_STAGE_1, curOrder.id, this).show(childFragmentManager, Constants.CANCEL_ORDER_DIALOG_TAG)
//                }
//                if(curOrderStage == 2){
//                    CancelOrderDialog(Constants.CANCEL_ORDER_STAGE_2, curOrder.id, this).show(childFragmentManager, Constants.CANCEL_ORDER_DIALOG_TAG)
//                }
//                if(curOrderStage == 3){
//                    CancelOrderDialog(Constants.CANCEL_ORDER_STAGE_3, curOrder.id, this).show(childFragmentManager, Constants.CANCEL_ORDER_DIALOG_TAG)
//                }
//            }

//            itemView.trackOrderBottomShareImageBtn.setOnClickListener {
//                listener.onShareImageClick(curOrder)
//            }


        }
    }

}