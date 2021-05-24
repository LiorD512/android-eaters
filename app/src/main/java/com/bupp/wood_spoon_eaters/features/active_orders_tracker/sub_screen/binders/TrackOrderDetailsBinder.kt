//package com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.binders
//
//import android.view.LayoutInflater
//import android.view.View
//import mva2.adapter.ItemViewHolder
//import android.view.ViewGroup
//import androidx.core.content.ContextCompat
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.bupp.wood_spoon_eaters.R
//import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
//import com.bupp.wood_spoon_eaters.databinding.CooksProfileDishItemBinding
//import com.bupp.wood_spoon_eaters.databinding.TrackOrderDetailsSectionBinding
//import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.OrderTrackDetails
//import com.bupp.wood_spoon_eaters.features.main.cook_profile.CooksDishesAdapter
//import com.bupp.wood_spoon_eaters.utils.DateUtils
//import com.bupp.wood_spoon_eaters.utils.Utils
//import mva2.adapter.ItemBinder
//
//
//class TrackOrderDetailsBinder() : ItemBinder<OrderTrackDetails, TrackOrderDetailsBinder.TrackOrderDetailsViewHolder>() {
//
//    override fun createViewHolder(parent: ViewGroup): TrackOrderDetailsViewHolder {
//        val binding = TrackOrderDetailsSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return TrackOrderDetailsViewHolder(binding)
//    }
//
//    override fun canBindData(item: Any): Boolean {
//        return item is OrderTrackDetails
//    }
//
//    override fun bindViewHolder(holder: TrackOrderDetailsViewHolder, items: OrderTrackDetails) {
//        holder.bindItems(items)
//    }
//
//    inner class TrackOrderDetailsViewHolder(val binding: TrackOrderDetailsSectionBinding) : ItemViewHolder<OrderTrackDetails>(binding.root) {
//        fun bindItems(item: OrderTrackDetails) {
//
//            val order = item.order
//            val userInfo = item.orderUserInfo
//
//            val adapter = TrackOrderItemDetailsAdapter(itemView.context)
//            binding.trackOrderDetailsSectionOrderItemList.layoutManager = LinearLayoutManager(itemView.context)
//            binding.trackOrderDetailsSectionOrderItemList.adapter = adapter
//            val divider = DividerItemDecorator(ContextCompat.getDrawable(itemView.context, R.drawable.divider))
//            binding.trackOrderDetailsSectionOrderItemList.addItemDecoration(divider)
//            adapter.submitList(order.orderItems)
//
//            binding.trackOrderDetailsSectionSubTotal.text = order.subtotal?.formatedValue ?: ""
//            binding.trackOrderDetailsSectionServiceFee.text = order.serviceFee?.formatedValue ?: ""
//            binding.trackOrderDetailsSectionDeliveryFee.text = order.deliveryFee?.formatedValue ?: ""
//            binding.trackOrderDetailsSectionTax.text = order.tax?.formatedValue ?: ""
//            binding.trackOrderDetailsSectionTotal.text = order.total?.formatedValue ?: ""
//
//            order.tip?.let{
//                binding.trackOrderDetailsSectionTipLayout.visibility = View.VISIBLE
//                binding.trackOrderDetailsSectionTip.text = it.formatedValue
//
//            }
////
//            binding.trackOrderDetailsSectionDate.text = DateUtils.parseDateToFullDate(order.created_at)
//            binding.trackOrderDetailsSectionPayment.text = userInfo?.paymentMethod
//            binding.trackOrderDetailsSectionUserInfo.text = userInfo?.userInfo
//            binding.trackOrderDetailsSectionOrderNumber.text = order.orderNumber
//            val address = userInfo?.userLocation
//            address?.let{
//                binding.trackOrderDetailsSectionLocation1.text = "${it.streetLine1}, #${it.streetLine2}"
//                binding.trackOrderDetailsSectionLocation2.text = "${it.city?.name ?: ""}, ${it.state?.name ?: ""} ${it.zipCode}"
//            }
//
//            userInfo?.note?.let{
//                binding.trackOrderDetailsSectionNote.text = it
//            }
//        }
//    }
//
//}