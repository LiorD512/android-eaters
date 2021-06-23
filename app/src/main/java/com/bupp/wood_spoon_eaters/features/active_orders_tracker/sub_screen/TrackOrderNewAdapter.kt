package com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.databinding.*
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.binders.TrackOrderItemDetailsAdapter
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.utils.DateUtils
import mva2.adapter.ItemViewHolder
import java.util.*


class TrackOrderNewAdapter(val context: Context, val listener: TrackOrderNewAdapterListener):
    ListAdapter<TrackOrderData<Any>, RecyclerView.ViewHolder>(AdditionalDishesDiffCallback()) {


    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DETAILS -> {
                val binding = TrackOrderDetailsSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return TrackOrderDetailsViewHolder(binding)
            }
            else -> { // VIEW_TYPE_PROGRESS
                val binding = TrackOrderProgressSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return TrackOrderProgressViewHolder(binding)
            }
        }
    }

    interface TrackOrderNewAdapterListener {
        fun onHeaderClick(isExpanded: Boolean){} //HEADER SECTION

        fun onContactUsClick(order: Order){} //PROGRESS SECTION
        fun onShareImageClick(order: Order){} //PROGRESS SECTION
        fun onOrderCanceled(orderState: Int, orderId: Long){} //PROGRESS SECTION
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


    private var isExpended = true
//    inner class TrackOrderHeaderViewHolder(val binding: TrackOrderDetailsHeaderSectionBinding) : ItemViewHolder<OrderTrackHeader>(binding.root) {
//        fun bindItems(item: OrderTrackHeader) {
//
//        }
//    }

    inner class TrackOrderDetailsViewHolder(val binding: TrackOrderDetailsSectionBinding) : ItemViewHolder<OrderTrackDetails>(binding.root) {
        fun bindItems(item: OrderTrackDetails) {

            binding.trackOrderDetailsHeaderTitle.text = "Order #${item.order.orderNumber}"
            binding.trackOrderDetailsHeaderLayout.setOnClickListener {
//                toggleItemExpansion()
                isExpended = !isExpended
                Log.d("wowTrackOrderHeader", "on Title Click: $isExpended")
                if (isExpended) {
                    binding.trackOrderDetailsSectionMainLayout.visibility = View.VISIBLE
                    val rotateClock = RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                    rotateClock.fillAfter = true
                    rotateClock.repeatCount = 0
                    rotateClock.duration = 500
                    binding.trackOrderDetailsHeaderArrow.startAnimation(rotateClock)
                } else {
                    binding.trackOrderDetailsSectionMainLayout.visibility = View.GONE
                    val rotateAntiClock = RotateAnimation(180f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                    rotateAntiClock.fillAfter = true
                    rotateAntiClock.repeatCount = 0
                    rotateAntiClock.duration = 500
                    binding.trackOrderDetailsHeaderArrow.startAnimation(rotateAntiClock)
                }
            }

            val order = item.order
            val userInfo = item.orderUserInfo

            val adapter = TrackOrderItemDetailsAdapter(itemView.context)
            binding.trackOrderDetailsSectionOrderItemList.layoutManager = LinearLayoutManager(itemView.context)
            binding.trackOrderDetailsSectionOrderItemList.adapter = adapter
            val divider = DividerItemDecorator(ContextCompat.getDrawable(itemView.context, R.drawable.divider))
            binding.trackOrderDetailsSectionOrderItemList.addItemDecoration(divider)
            adapter.submitList(order.orderItems)

            binding.trackOrderDetailsSectionSubTotal.text = order.subtotal?.formatedValue ?: ""
            binding.trackOrderDetailsSectionServiceFee.text = order.serviceFee?.formatedValue ?: ""
            binding.trackOrderDetailsSectionDeliveryFee.text = order.deliveryFee?.formatedValue ?: ""
            binding.trackOrderDetailsSectionTax.text = order.tax?.formatedValue ?: ""
            binding.trackOrderDetailsSectionTotal.text = order.total?.formatedValue ?: ""

            order.tip?.let{
                binding.trackOrderDetailsSectionTipLayout.visibility = View.VISIBLE
                binding.trackOrderDetailsSectionTip.text = it.formatedValue

            }
//
            binding.trackOrderDetailsSectionDate.text = DateUtils.parseDateToFullDate(order.created_at)
            binding.trackOrderDetailsSectionPayment.text = userInfo?.paymentMethod
            binding.trackOrderDetailsSectionUserInfo.text = userInfo?.userInfo
            binding.trackOrderDetailsSectionOrderNumber.text = order.orderNumber
            val address = userInfo?.userLocation
            address?.let{
                binding.trackOrderDetailsSectionLocation1.text = "${it.streetLine1}, #${it.streetLine2}"
                binding.trackOrderDetailsSectionLocation2.text = "${it.city?.name ?: ""}, ${it.state?.name ?: ""} ${it.zipCode}"
            }

            userInfo?.note?.let{
                binding.trackOrderDetailsSectionNote.text = it
            }
        }
    }

    private var curOrderStage: Int = 1
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

                binding.trackOrderProgressArrivalTime.text = "Estimated arrival - ${order.etaToDisplay}"

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




}


