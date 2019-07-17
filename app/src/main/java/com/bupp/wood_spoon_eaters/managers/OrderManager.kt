package com.bupp.wood_spoon_eaters.managers

import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.OrderItemRequest
import com.bupp.wood_spoon_eaters.model.OrderRequest
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings
import com.bupp.wood_spoon_eaters.utils.Utils
import java.util.*

class OrderManager(val api: ApiService, val appSettings: AppSettings) {

    var curOrderResponse: Order? = null
    var currentOrderRequest: OrderRequest = OrderRequest()

    fun getOrderRequest():OrderRequest?{
        return currentOrderRequest
    }

    fun updateOrderRequest(cookingSlotId: Long? = null,
                           deliveryAt: String? = getLastOrderTimeParam(),
                           deliveryAddressId: Long? = null,
                           orderItemRequests: ArrayList<OrderItemRequest>? = null,
                           tipPercentage: Float? = null,
                           tip: Int? = null,
                           tipAmount: String? = null,
                           promoCodeId: Long? = null){
        if(cookingSlotId != null) currentOrderRequest.cookingSlotId = cookingSlotId
        if(deliveryAt != null) currentOrderRequest.deliveryAt = deliveryAt
        if(deliveryAddressId != null) currentOrderRequest.deliveryAddressId = deliveryAddressId
        if(orderItemRequests != null) currentOrderRequest.orderItemRequests = orderItemRequests
        if(tipPercentage != null) currentOrderRequest.tipPercentage = tipPercentage
        if(tip != null) currentOrderRequest.tip = tip
        if(tipAmount != null) currentOrderRequest.tipAmount = tipAmount
        if(promoCodeId != null) currentOrderRequest.promoCodeId = promoCodeId
    }


    fun addOrderItem(orderItemRequest: OrderItemRequest){
        if(currentOrderRequest.orderItemRequests == null){
            currentOrderRequest.orderItemRequests = arrayListOf()
        }
        currentOrderRequest.orderItemRequests?.add(orderItemRequest)
    }






















    var isDelivery: Boolean? = null

    var orderTime: Date? = null

    fun getLastOrderTime(): Date? {
        return if (orderTime != null) {
            orderTime
        } else {
            null
        }
    }

    fun getLastOrderTimeParam(): String? {
        //returns unix timestamp
        return if (getLastOrderTime() != null) {
            Utils.parseUnixTimestamp(getLastOrderTime()!!)
        } else {
            Utils.parseUnixTimestamp(Date())
        }
    }

    fun getLastOrderTimeString(): String {
        return if (getLastOrderTime() != null) {
            Utils.parseTime(getLastOrderTime())
        } else {
            "ASAP"
        }
    }

    fun setOrderResponse(order: Order?) {
        this.curOrderResponse = order
    }


}