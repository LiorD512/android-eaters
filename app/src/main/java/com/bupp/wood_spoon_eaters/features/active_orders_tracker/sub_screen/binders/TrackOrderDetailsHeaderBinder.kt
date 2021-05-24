//package com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.binders
//
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.view.animation.Animation
//import android.view.animation.RotateAnimation
//import com.bupp.wood_spoon_eaters.R
//import com.bupp.wood_spoon_eaters.databinding.TrackOrderDetailsHeaderSectionBinding
//import com.bupp.wood_spoon_eaters.databinding.TrackOrderDetailsSectionBinding
//import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.OrderTrackHeader
//import mva2.adapter.ItemBinder
//import mva2.adapter.ItemViewHolder
//
//
//class TrackOrderDetailsHeaderBinder(val listener: TrackOrderHeaderListener) : ItemBinder<OrderTrackHeader, TrackOrderDetailsHeaderBinder.TrackOrderDetailsViewHolder>() {
//
//    private var isExpended = false
//    interface TrackOrderHeaderListener{
//        fun onHeaderClick(isExpanded: Boolean)
//    }
//
//    override fun createViewHolder(parent: ViewGroup): TrackOrderDetailsViewHolder {
//        val binding = TrackOrderDetailsHeaderSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return TrackOrderDetailsViewHolder(binding)
//    }
//
//    override fun canBindData(item: Any): Boolean {
//        return item is OrderTrackHeader
//    }
//
//    override fun bindViewHolder(holder: TrackOrderDetailsViewHolder, items: OrderTrackHeader) {
//        holder.bindItems(items)
//    }
//
//    inner class TrackOrderDetailsViewHolder(val binding: TrackOrderDetailsHeaderSectionBinding) : ItemViewHolder<OrderTrackHeader>(binding.root) {
//        fun bindItems(item: OrderTrackHeader) {
//            binding.trackOrderDetailsHeaderTitle.text = "Order #${item.orderNumber}"
//            binding.trackOrderDetailsHeaderLayout.setOnClickListener{
////                toggleItemExpansion()
//                isExpended = !isExpended
//                Log.d("wowTrackOrderHeader","on Title Click: $isExpended")
//                listener.onHeaderClick(isExpended)
//                if(isExpended){
//                    val rotateClock = RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
////                    val aniRotate: Animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise)
//                    rotateClock.fillAfter = true
//                    rotateClock.repeatCount = 0
//                    rotateClock.duration = 500
//                    binding.trackOrderDetailsHeaderArrow.startAnimation(rotateClock)
//                }else{
//                    val rotateAntiClock = RotateAnimation(180f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
////                    val aniRotate: Animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anti_clockwise)
//                    rotateAntiClock.fillAfter = true
//                    rotateAntiClock.repeatCount = 0
//                    rotateAntiClock.duration = 500
//                    binding.trackOrderDetailsHeaderArrow.startAnimation(rotateAntiClock)
//                }
//            }
//        }
//    }
//
//}