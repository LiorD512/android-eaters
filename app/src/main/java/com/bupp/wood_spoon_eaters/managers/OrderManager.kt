package com.bupp.wood_spoon_eaters.managers

import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.AddressRequest
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings
import com.bupp.wood_spoon_eaters.utils.Utils
import com.bupp.wood_spoon_eaters.network.google.models.AddressResponse
import java.util.*

class OrderManager(val api: ApiService, val appSettings: AppSettings) {



    var isDelivery: Boolean? = null

    var addressResponse: AddressResponse? = null

    var orderAddress: Address? = null
        get() = getLastOrderAddress()

    var orderTime: Date? = null




    fun hasAddress(): Boolean{
        return appSettings.currentEater?.addresses?.size!! > 0
    }

    fun getLastOrderAddress(): Address?{
        return if(hasAddress()){
            appSettings.currentEater?.addresses!!.last()
        }else{
            //return location manager
            null
        }
    }

    fun getLastOrderTime(): Date?{
        return if(orderTime != null){
            orderTime
        }else{
            null
        }
    }

    fun getLastOrderTimeString(): String{
        return if(getLastOrderTime() != null){
            Utils.parseTime(getLastOrderTime())
        }else{
            "ASAP"
        }
    }

    fun updateOrder(addressResponse: AddressResponse? = null, orderAddress: Address? = null, isDelivery: Boolean? = null, orderTime: Date? = null) {
        if(addressResponse != null){
            this.addressResponse = addressResponse
        }
        if(orderAddress != null){
            this.orderAddress = orderAddress
        }
        if(isDelivery != null){
          this.isDelivery = isDelivery
        }
        if(orderTime != null){
            this.orderTime = orderTime
        }
    }

    fun getLastAddressResponse(): AddressResponse? {
        return if(addressResponse != null){
            addressResponse
        }else{
            null
        }
    }


}