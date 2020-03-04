package com.bupp.wood_spoon_eaters.managers

import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings
import java.util.*

class OrderManager(val api: ApiService, val appSettings: AppSettings, val eaterDataManager: EaterDataManager) {

    var curOrderResponse: Order? = null
    var currentOrderRequest: OrderRequest? = null

    fun getOrderRequest():OrderRequest{
        return currentOrderRequest!!
    }

    fun getPromoCodeOrderRequest(): OrderRequest {
        val promoCodeOrderRequest = OrderRequest()
        promoCodeOrderRequest.promoCode = getOrderRequest().promoCode
//        promoCodeOrderRequest.promoCode = getOrderRequest().id
        return promoCodeOrderRequest
    }

    fun initNewOrder(): OrderRequest {
        currentOrderRequest = OrderRequest()
        return currentOrderRequest!!
    }

    fun updateOrderRequest(cookId: Long? = null,
                           cookingSlotId: Long? = null,
                           deliveryAt: String? = eaterDataManager.getLastOrderTimeParam(),
                           deliveryAddress: Address? = getLastOrderAddressParam(),
                           orderItemRequests: ArrayList<OrderItemRequest>? = null,
                           tipPercentage: Float? = null,
                           tip: Int? = null,
                           tipAmount: String? = null,
                           promoCode: String? = null,
                           addUtensils: Boolean? = null,
                           recurringOrder: Boolean? = null){
        if(currentOrderRequest != null){
            if(cookId != null) currentOrderRequest!!.cookId = cookId
            if(cookingSlotId != null) currentOrderRequest!!.cookingSlotId = cookingSlotId
            if(deliveryAt != null) currentOrderRequest!!.deliveryAt = deliveryAt
            if(deliveryAddress != null) currentOrderRequest!!.deliveryAddressId = deliveryAddress.id
            if(orderItemRequests != null) currentOrderRequest!!.orderItemRequests = orderItemRequests
            if(tipPercentage != null) currentOrderRequest!!.tipPercentage = tipPercentage
            if(tip != null) currentOrderRequest!!.tip = tip
            if(tipAmount != null) currentOrderRequest!!.tipAmount = tipAmount
            if(promoCode != null) currentOrderRequest!!.promoCode = promoCode
            if(addUtensils != null) currentOrderRequest!!.addUtensils = addUtensils
//            if(recurringOrder != null) currentOrderRequest!!.recurringOrder = recurringOrder
        }
    }

    private fun getLastOrderAddressParam(): Address? {
        return eaterDataManager.getLastChosenAddress()
    }

    fun addOrderItem(orderItemRequest: OrderItemRequest){
        if(currentOrderRequest != null){
            if(currentOrderRequest!!.orderItemRequests == null){
                currentOrderRequest!!.orderItemRequests = arrayListOf()
            }
            currentOrderRequest!!.orderItemRequests?.add(orderItemRequest)
        }
    }

    fun updateOrderItems(orderItemRequests: ArrayList<OrderItemRequest>){
        currentOrderRequest!!.orderItemRequests?.clear()
        currentOrderRequest!!.orderItemRequests?.addAll(orderItemRequests)
    }

    fun setOrderResponse(order: Order?) {
        this.curOrderResponse = order
    }

    fun haveCurrentActiveOrder(): Boolean{
        return curOrderResponse != null
    }

    fun clearCurrentOrder() {
        currentOrderRequest = null
        curOrderResponse = null
    }


    var tempTipPercentage: Int = 0
    var tempTipInDollars: Int = 0




}