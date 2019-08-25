package com.bupp.wood_spoon_eaters.managers

import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.OrderItemRequest
import com.bupp.wood_spoon_eaters.model.OrderRequest
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings
import java.util.*

class OrderManager(val api: ApiService, val appSettings: AppSettings, val eaterDataManager: EaterDataManager) {

    var curOrderResponse: Order? = null
    var currentOrderRequest: OrderRequest? = null

    fun getOrderRequest():OrderRequest{
        return currentOrderRequest!!
    }

    fun initNewOrder() {
        currentOrderRequest = OrderRequest()
    }


    fun updateOrderRequest(cookingSlotId: Long? = null,
                           deliveryAt: String? = eaterDataManager.getLastOrderTimeParam(),
                           deliveryAddress: Address? = getLastOrderAddressParam(),
                           orderItemRequests: ArrayList<OrderItemRequest>? = null,
                           tipPercentage: Float? = null,
                           tip: Int? = null,
                           tipAmount: String? = null,
                           promoCodeId: Long? = null){
        if(currentOrderRequest != null){
            if(cookingSlotId != null) currentOrderRequest!!.cookingSlotId = cookingSlotId
            if(deliveryAt != null) currentOrderRequest!!.deliveryAt = deliveryAt
            if(deliveryAddress != null) currentOrderRequest!!.deliveryAddressId = deliveryAddress.id
            if(orderItemRequests != null) currentOrderRequest!!.orderItemRequests = orderItemRequests
            if(tipPercentage != null) currentOrderRequest!!.tipPercentage = tipPercentage
            if(tip != null) currentOrderRequest!!.tip = tip
            if(tipAmount != null) currentOrderRequest!!.tipAmount = tipAmount
            if(promoCodeId != null) currentOrderRequest!!.promoCodeId = promoCodeId
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

    fun setOrderResponse(order: Order?) {
        this.curOrderResponse = order
    }

    fun haveCurrentActiveOrder(): Boolean{
        return curOrderResponse != null
    }

    fun finalizeOrder(){
        currentOrderRequest = null
    }

    fun clearCurrentOrder() {
        currentOrderRequest = null
        curOrderResponse = null
    }


}