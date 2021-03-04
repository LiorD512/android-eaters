package com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.binders

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.OrderTrackDetailsHeader
import com.facebook.FacebookSdk.getApplicationContext
import kotlinx.android.synthetic.main.track_order_details_header_section.view.*
import mva2.adapter.ItemBinder
import mva2.adapter.ItemViewHolder


class TrackOrderDetailsHeaderBinder(val listener: TrackOrderHeaderListener) : ItemBinder<OrderTrackDetailsHeader, TrackOrderDetailsHeaderBinder.TrackOrderDetailsViewHolder>() {

    private var isExpended = false
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
                if(isExpended){
                    val rotateClock = RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
//                    val aniRotate: Animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise)
                    rotateClock.fillAfter = true
                    rotateClock.repeatCount = 0
                    rotateClock.duration = 500
                    itemView.trackOrderDetailsHeaderArrow.startAnimation(rotateClock)
                }else{
                    val rotateAntiClock = RotateAnimation(180f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
//                    val aniRotate: Animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anti_clockwise)
                    rotateAntiClock.fillAfter = true
                    rotateAntiClock.repeatCount = 0
                    rotateAntiClock.duration = 500
                    itemView.trackOrderDetailsHeaderArrow.startAnimation(rotateAntiClock)
                }
            }
        }
    }

}