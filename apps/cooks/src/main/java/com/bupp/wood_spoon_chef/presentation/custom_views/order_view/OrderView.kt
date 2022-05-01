package com.bupp.wood_spoon_chef.presentation.custom_views.order_view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.databinding.OrderViewBinding
import com.bupp.wood_spoon_chef.data.remote.model.Order
import com.bupp.wood_spoon_chef.utils.DateUtils
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class OrderView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        LinearLayout(context, attrs, defStyleAttr) {

    private var binding: OrderViewBinding = OrderViewBinding.inflate(LayoutInflater.from(context), this, true)

    private var curPreparationStatus: Int = Constants.PREPARATION_STATUS_NULL
    private lateinit var order: Order
    private lateinit var listener: OrderViewListener
    private var isNationwide: Boolean = false
    private var itemPosition: Int = -1

    interface OrderViewListener {
        fun onChatClick(curOrder: Order)
        fun onCancelClick(curOrder: Order)
        fun onWorldwideInfoClick()
        fun updateStatus(status: Int, orderId: Long, itemPosition: Int)
    }

    fun init(order: Order, listener: OrderViewListener, curPreparationStatus: Int = -1, isNationwide: Boolean = false, position: Int) {
        this.order = order
        this.listener = listener
        this.curPreparationStatus = curPreparationStatus
        this.isNationwide = isNationwide
        this.itemPosition = position
        Log.d("wowOrderView", "init orderView: $curPreparationStatus")
        initUi()
    }


    private fun initUi() {
        with(binding) {
            orderViewChatBtn.setOnClickListener { listener.onChatClick(order) }
            orderviewCancel.setOnClickListener { listener.onCancelClick(order) }
            orderViewBtn.setOnClickListener { nextStatus() }

            Glide.with(context).load(order.eater.thumbnail).apply(RequestOptions.circleCropTransform().placeholder(R.drawable.profile_pic_placeholder)).into(orderViewUserImg)
            orderViewNameAndPrice.text = "${order.eater.getFullName()}, ${order.cookEarnings?.formattedValue}"
            orderViewTime.text = "Pickup Time - ${DateUtils.parseDateToTime(order.pickupAt)}"
            orderViewSlotsView.init(order.orderItems)

            if (order.freeDelivery) {
                orderViewFreeDelivery.visibility = View.VISIBLE
            } else {
                orderViewFreeDelivery.visibility = View.GONE
            }
            if (!order.disposableTableware) {
                orderViewUtensils.visibility = View.VISIBLE
            } else {
                orderViewUtensils.visibility = View.GONE
            }

            updateStatusUi(curPreparationStatus)

            if (this@OrderView.isNationwide) {
                orderViewNationwideLayout.visibility = View.VISIBLE
                orderViewNationwideLayoutRect.visibility = View.VISIBLE

                orderViewNationwideLayout.setOnClickListener {
                    listener.onWorldwideInfoClick()
                }
            } else {
                orderViewNationwideLayout.visibility = View.GONE
                orderViewNationwideLayoutRect.visibility = View.GONE
            }
        }

    }

    private fun updateStatusUi(status: Int) {
        with(binding) {
            orderViewMainLayout.alpha = 1f
            orderViewCancelled.visibility = View.INVISIBLE
            orderViewCounter.visibility = View.GONE
            orderViewMainLayout.isEnabled = true
            orderviewCancel.isEnabled = true
            orderViewStatusImg.visibility = View.INVISIBLE
            orderViewBtn.visibility = VISIBLE
            orderViewBtn.setBtnEnabled(true)

            when (status) {
                Constants.PREPARATION_STATUS_IDLE -> {
                    orderViewBtn.setTitle("Got It")
                    orderViewBtn.setBtnEnabled(true)
                }
                Constants.PREPARATION_STATUS_RECEIVED -> {
                    showTimer()
                    orderViewBtn.setTitle("Start Preparing")
                    orderViewBtn.setBtnEnabled(true)
                }
                Constants.PREPARATION_STATUS_IN_PROGRESS -> {
                    showTimer()
                    orderViewBtn.setTitle("Ready For Delivery")
                    orderViewBtn.setBtnEnabled(true)
                }
                Constants.PREPARATION_STATUS_COMPLETED -> {
                    cancelTimer()
                    orderViewBtn.setTitle("Ready For Delivery")
                    orderViewBtn.setBtnEnabled(false)
                }
                Constants.PREPARATION_STATUS_DELIVERED -> {
                    orderViewBtn.visibility = View.GONE
                    orderViewStatusImg.visibility = View.VISIBLE
                    orderViewStatusImg.setImageResource(R.drawable.icons_on_its_way)
                }
                Constants.PREPARATION_STATUS_SHIPPED -> {
                    orderViewBtn.visibility = View.GONE
                    orderViewStatusImg.visibility = View.VISIBLE
                    orderViewStatusImg.setImageResource(R.drawable.ilus_delivered)
                }
                Constants.PREPARATION_STATUS_CANCELLED -> {
                    orderViewCancelled.visibility = View.VISIBLE
                    orderViewMainLayout.alpha = 0.3f
                    orderViewMainLayout.isEnabled = false
                    orderViewBtn.setBtnEnabled(false)
                    orderviewCancel.isEnabled = false
                }
            }
        }
    }

    private fun nextStatus() {
        listener.updateStatus(curPreparationStatus, order.id, itemPosition)
    }

    private fun showTimer() {
        with(binding) {
            order.pickupAt?.time?.let{ time->
                orderViewCounter.visibility = View.VISIBLE
                val diffInMillies = abs(time - Date().time)
                val diff = TimeUnit.MILLISECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS)
                Log.d("wowOrderView", "diff time from now to deliver time: $diff milliseconds")
                orderViewCounter.init(diff)
            }
        }
    }

    private fun cancelTimer() {
        binding.orderViewCounter.cancelTimer()
    }


}