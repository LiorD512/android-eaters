package com.bupp.wood_spoon_eaters.managers

import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings
import java.util.*

class OrderManager(val api: ApiService, val eaterDataManager: EaterDataManager) {

    var curOrderResponse: Order? = null
    var currentOrderRequest: OrderRequest? = null

    fun getOrderRequest(): OrderRequest {
        return currentOrderRequest!!
    }

    fun getPromoCodeOrderRequest(): OrderRequest {
        val promoCodeOrderRequest = OrderRequest()
        promoCodeOrderRequest.promoCode = getOrderRequest().promoCode
        return promoCodeOrderRequest
    }

    fun initNewOrder(): OrderRequest {
        currentOrderRequest = OrderRequest()
        return currentOrderRequest!!
    }

    fun updateOrderRequest(
        cookId: Long? = null,
        cookingSlotId: Long? = null,
        deliveryAt: String? = eaterDataManager.getLastOrderTimeParam(),
        deliveryAddress: Address? = getLastOrderAddressParam(),
        orderItemRequests: ArrayList<OrderItemRequest>? = null,
        tipPercentage: Float? = null,
        tip: Int? = null,
        tipAmount: String? = null,
        promoCode: String? = null,
        addUtensils: Boolean? = null
    ) {
        if (currentOrderRequest == null) {
            initNewOrder()
        }
        currentOrderRequest!!.deliveryAt = deliveryAt
        if (cookId != null) currentOrderRequest!!.cookId = cookId
        if (cookingSlotId != null) currentOrderRequest!!.cookingSlotId = cookingSlotId
        if (deliveryAddress != null) currentOrderRequest!!.deliveryAddressId = deliveryAddress.id
        if (orderItemRequests != null) currentOrderRequest!!.orderItemRequests = orderItemRequests
        if (tipPercentage != null) currentOrderRequest!!.tipPercentage = tipPercentage
        if (tip != null) currentOrderRequest!!.tip = tip
        if (tipAmount != null) currentOrderRequest!!.tipAmount = tipAmount
        if (promoCode != null) currentOrderRequest!!.promoCode = promoCode
        if (addUtensils != null) currentOrderRequest!!.addUtensils = addUtensils
    }

    private fun getLastOrderAddressParam(): Address? {
        return eaterDataManager.getLastChosenAddress()
    }

    fun addOrderItem(orderItemRequest: OrderItemRequest) {
        currentOrderRequest?.let {
            if (it.orderItemRequests == null) {
                it.orderItemRequests = arrayListOf()
            }
            it.orderItemRequests?.add(orderItemRequest)
        }
    }


    fun setOrderResponse(order: Order?) {
        this.curOrderResponse = order
    }

    fun haveCurrentActiveOrder(): Boolean {
        return curOrderResponse != null
    }

    fun clearCurrentOrder() {
        currentOrderRequest = null
        curOrderResponse = null
    }

    fun getTotalCost(): String {
        val total = curOrderResponse?.total?.cents ?: 0
        val tip = curOrderResponse?.tip?.cents ?: 0
        val sum = total + tip
        return "$sum"
    }


}