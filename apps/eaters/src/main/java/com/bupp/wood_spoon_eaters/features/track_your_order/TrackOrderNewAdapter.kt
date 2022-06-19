package com.bupp.wood_spoon_eaters.features.track_your_order

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.bottom_sheets.track_order.TrackOrderData
import com.bupp.wood_spoon_eaters.databinding.*
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.OrderTrackDetails
import com.bupp.wood_spoon_eaters.features.order_checkout.checkout.models.CheckoutAdapterItem
import com.bupp.wood_spoon_eaters.features.order_checkout.checkout.order_items_view.CheckoutOrderItemsView
import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.CustomOrderItem
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.bupp.wood_spoon_eaters.views.WSTitleValueView
import java.text.DecimalFormat


class TrackOrderNewAdapter(val context: Context, val listener: TrackOrderNewAdapterListener) :
    ListAdapter<TrackOrderData<Any>, RecyclerView.ViewHolder>(AdditionalDishesDiffCallback()), WSTitleValueView.WSTitleValueListener,
    CheckoutOrderItemsView.CheckoutOrderItemsViewListener {


    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = TrackOrderDetailsSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackOrderDetailsViewHolder(binding)
    }

    interface TrackOrderNewAdapterListener {
        fun onToolTipClick(type: Int)
        fun onChefPageClick()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (item.viewType) {
            VIEW_TYPE_DETAILS -> {
                val orderItem = item.data as OrderTrackDetails
                (holder as TrackOrderDetailsViewHolder).bindItems(orderItem)
            }
//            VIEW_TYPE_PROGRESS -> {
//                val orderItem = item.data as OrderTrackProgress
//                (holder as TrackOrderProgressViewHolder).bindItems(orderItem)
//            }
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
            binding.trackOrderDetailsFees.setTitle(item.pricingExperimentParams.feeAndTaxTitle)
//
            binding.trackOrderDetailsSubtotal.setValue(order.subtotal?.formatedValue ?: "")
            binding.trackOrderDetailsDeliveryFee.setValue(order.deliveryFee?.formatedValue ?: "")
            binding.trackOrderDetailsCourier.setValue(order.tip?.formatedValue ?: "")
            binding.trackOrderDetailsTotal.setValue(order.total?.formatedValue ?: "")

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

            val promo = order.promoCode

            if (!promo.isNullOrEmpty()) {
                binding.trackOrderDetailsPromoCode.visibility = View.VISIBLE
                binding.trackOrderDetailsPromoCode.setTitle("Promo code $promo")
                binding.trackOrderDetailsPromoCode.setValue("${order.discount?.formatedValue}")
            }

            binding.trackOrderDetailsItems.initView(this@TrackOrderNewAdapter, null)
            binding.trackOrderDetailsItems.setBtnText("Home chef's page")
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
            binding.trackOrderDetailsSectionDate.text = DateUtils.parseDateToOrderAtStr(order.created_at)
            binding.trackOrderDetailsSectionPayment.text = order.paymentMethodStr
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
            binding.trackOrderDetailsSectionGiftRecipient.isVisible = order.isGift == true
            binding.trackOrderDetailsSectionGiftRecipientInfo.text = with(order) {
                listOfNotNull(
                    "${recipientFirstName ?: ""} ${recipientLastName ?: ""}",
                    order.recipientPhoneNumber,
                    order.recipientEmail
                ).joinToString(separator = "\n")
            }
        }
    }

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

    override fun onEditOrderBtnClicked() {
        listener.onChefPageClick()
    }


}


