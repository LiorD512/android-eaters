package com.bupp.wood_spoon_eaters.features.track_your_order

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestListener
import com.bupp.wood_spoon_eaters.bottom_sheets.track_order.TrackOrderData
import com.bupp.wood_spoon_eaters.databinding.*
import com.bupp.wood_spoon_eaters.di.GlideApp
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.OrderTrackDetails
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.OrderTrackProgress
import com.bupp.wood_spoon_eaters.features.order_checkout.checkout.models.CheckoutAdapterItem
import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.CustomOrderItem
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.bupp.wood_spoon_eaters.views.WSTitleValueView
import java.text.DecimalFormat
import android.R
import android.graphics.drawable.Drawable
import androidx.annotation.Nullable

import com.bumptech.glide.load.engine.GlideException

import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource


class TrackOrderNewAdapter(val context: Context, val listener: TrackOrderNewAdapterListener) :
    ListAdapter<TrackOrderData<Any>, RecyclerView.ViewHolder>(AdditionalDishesDiffCallback()), WSTitleValueView.WSTitleValueListener {


    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DETAILS -> {
                val binding = TrackOrderDetailsSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TrackOrderDetailsViewHolder(binding)
            }
            else -> { // VIEW_TYPE_PROGRESS
                val binding = TrackOrderProgressSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TrackOrderProgressViewHolder(binding)
            }
        }
    }

    interface TrackOrderNewAdapterListener {
        fun onContactUsClick(order: Order) {} //PROGRESS SECTION
        fun onShareImageClick(order: Order) {} //PROGRESS SECTION
        fun onOrderCanceled(orderState: Int, orderId: Long) {} //PROGRESS SECTION
        fun onToolTipClick(type: Int)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (item.viewType) {
            VIEW_TYPE_DETAILS -> {
                val orderItem = item.data as OrderTrackDetails
                (holder as TrackOrderDetailsViewHolder).bindItems(orderItem)
            }
            VIEW_TYPE_PROGRESS -> {
                val orderItem = item.data as OrderTrackProgress
                (holder as TrackOrderProgressViewHolder).bindItems(orderItem)
            }
        }

    }


    inner class TrackOrderDetailsViewHolder(val binding: TrackOrderDetailsSectionBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindItems(item: OrderTrackDetails) {

//            binding.trackOrderDetailsHeaderTitle.text = "Order #${item.order.orderNumber}"

            val order = item.order
            val userInfo = item.orderUserInfo

//            val adapter = TrackOrderItemDetailsAdapter(itemView.context)
//            binding.trackOrderDetailsSectionOrderItemList.layoutManager = LinearLayoutManager(itemView.context)
//            binding.trackOrderDetailsSectionOrderItemList.adapter = adapter
//            val divider = DividerItemDecorator(ContextCompat.getDrawable(itemView.context, R.drawable.divider))
//            binding.trackOrderDetailsSectionOrderItemList.addItemDecoration(divider)
//            adapter.submitList(order.orderItems)
            binding.trackOrderDetailsDeliveryFee.setWSTitleValueListener(this@TrackOrderNewAdapter)
            binding.trackOrderDetailsFees.setWSTitleValueListener(this@TrackOrderNewAdapter)
//
            binding.trackOrderDetailsSubtotal.setValue(order.subtotal?.formatedValue ?: "")
            binding.trackOrderDetailsDeliveryFee.setValue(order.deliveryFee?.formatedValue ?: "")

            var feeAndTax = 0.0
            order.serviceFee?.value?.let {
                feeAndTax += it
            }
            order.tax?.value?.let {
                feeAndTax += it
            }
            order.minOrderFee?.value?.let {
                feeAndTax += it
            }

            val feeAndTaxStr = DecimalFormat("##.##").format(feeAndTax)
            binding.trackOrderDetailsFees.setValue("$$feeAndTaxStr")

            val deliveryFee = order.deliveryFee?.value

            val promo = order.promoCode

            if (!promo.isNullOrEmpty()) {
                binding.trackOrderDetailsPromoCode.visibility = View.VISIBLE
                binding.trackOrderDetailsPromoCode.setTitle("Promo code $promo")
                binding.trackOrderDetailsPromoCode.setValue("${order.discount?.formatedValue}")
            }

            binding.trackOrderDetailsCourier.setValue(order.tip?.formatedValue ?: "")

            binding.trackOrderDetailsTotalBeforeTip.setValue(order.totalBeforeTip?.formatedValue ?: "")
//
//            order.minOrderFee?.value?.let {
//                if (it > 0) {
//                    binding.trackOrderDetailsSectionMinOrderFeeTitle.visibility = View.VISIBLE
//                    binding.trackOrderDetailsSectionMinOrderFee.visibility = View.VISIBLE
//                    binding.trackOrderDetailsSectionMinOrderFee.text = order.minOrderFee.formatedValue
//                }
//            }
//
//            order.promoCode?.let {
//                binding.trackOrderDetailsSectionPromoCodeLayout.visibility = View.VISIBLE
//                binding.trackOrderDetailsSectionPromoCodeName.text = "Promo code $it"
//                binding.trackOrderDetailsSectionPromoCode.text = "${order.discount?.formatedValue}"
//            }
//
//            order.tip?.let {
//                binding.trackOrderDetailsSectionTipLayout.visibility = View.VISIBLE
//                binding.trackOrderDetailsSectionTip.text = it.formatedValue
//
//            }

            binding.trackOrderDetailsItems.initView(null, null)
            val orderItems = order.orderItems
            val list = mutableListOf<CheckoutAdapterItem>()
            orderItems?.forEach {
                val customCartItem = CustomOrderItem(
                    orderItem = it,
                    cookingSlot = order.cookingSlot
                )
                list.add(CheckoutAdapterItem(customOrderItem = customCartItem))
            }
            binding.trackOrderDetailsItems.submitList(list)
//
            binding.trackOrderDetailsSectionDate.text = DateUtils.parseDateToFullDate(order.created_at)
            binding.trackOrderDetailsSectionPayment.text = userInfo?.paymentMethod
            binding.trackOrderDetailsSectionUserInfo.text = userInfo?.userInfo
            binding.trackOrderDetailsSectionOrderNumber.text = order.orderNumber
            val address = userInfo?.userLocation
            address?.let {
                binding.trackOrderDetailsSectionLocation1.text = "${it.streetLine1}, #${it.streetLine2}"
                binding.trackOrderDetailsSectionLocation2.text = "${it.city?.name ?: ""}, ${it.state?.name ?: ""} ${it.zipCode}"
            }

            userInfo?.note?.let {
                binding.trackOrderDetailsSectionNote.text = it
            }
        }
    }

    private var curOrderStage: Int = 1

    inner class TrackOrderProgressViewHolder(val binding: TrackOrderProgressSectionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItems(orderItem: OrderTrackProgress) {
            val order = orderItem.order
            order?.let {

                val orderState = order.getOrderState()
                binding.trackOrderProgressPb.setState(orderState)

                binding.trackOrderProgressName.text = order.restaurant?.restaurantName

            }
        }
    }

//    private fun setFontTypeDefault(binding: TrackOrderProgressSectionBinding, context: Context) {
//        val regTypeface = Typeface.createFromAsset(context.assets, "font/lato_reg.ttf")
//        binding.trackOrderProgressCb1.typeface = regTypeface
//        binding.trackOrderProgressCb2.typeface = regTypeface
//        binding.trackOrderProgressCb3.typeface = regTypeface
//        binding.trackOrderProgressCb4.typeface = regTypeface
//    }

    class AdditionalDishesDiffCallback : DiffUtil.ItemCallback<TrackOrderData<Any>>() {
        override fun areItemsTheSame(oldItem: TrackOrderData<Any>, newItem: TrackOrderData<Any>): Boolean {
            return oldItem.areItemsTheSame
        }

        override fun areContentsTheSame(oldItem: TrackOrderData<Any>, newItem: TrackOrderData<Any>): Boolean {
            return oldItem == newItem
        }
    }


    companion object {
        //        const val VIEW_TYPE_HEADER = 1
        const val VIEW_TYPE_DETAILS = 2
        const val VIEW_TYPE_PROGRESS = 3
    }

    override fun onToolTipClick(type: Int) {
        listener.onToolTipClick(type)
    }


}


