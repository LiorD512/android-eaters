package com.bupp.wood_spoon_eaters.managers

import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.google.models.GoogleAddressResponse
import com.bupp.wood_spoon_eaters.utils.AppSettings
import com.bupp.wood_spoon_eaters.utils.Utils
import java.util.*

class OrderManager(val api: ApiService, val appSettings: AppSettings) {


    var isDelivery: Boolean? = null

    var googleAddressResponse: GoogleAddressResponse? = null

    var orderAddress: Address? = null
        get() = getLastOrderAddress()
//        set(address) = setLastOrderAddress(address!!)

    var orderTime: Date? = null


    fun hasAddress(): Boolean {
        return appSettings.currentEater?.addresses?.size!! > 0
    }

    fun getLastOrderAddress(): Address? {
        return if (hasAddress()) {
            appSettings.currentEater?.addresses!!.last()
        } else {
            //return location manager
            null
        }
    }

//    private fun setLastOrderAddress(address: Address) {
//        var arraySize = appSettings.currentEater?.addresses!!.size
//        appSettings.currentEater?.addresses!!.add(arraySize, address)
//    }

    fun getLastOrderTime(): Date? {
        return if (orderTime != null) {
            orderTime
        } else {
            null
        }
    }

    fun getLastOrderTimeString(): String {
        return if (getLastOrderTime() != null) {
            Utils.parseTime(getLastOrderTime())
        } else {
            "ASAP"
        }
    }

    fun updateOrder(
        googleAddressResponse: GoogleAddressResponse? = null,
        orderAddress: Address? = null,
        isDelivery: Boolean? = null,
        orderTime: Date? = null
    ) {
        if (googleAddressResponse != null) {
            this.googleAddressResponse = googleAddressResponse
        }
        if (orderAddress != null) {
            this.orderAddress = orderAddress
        }
        if (isDelivery != null) {
            this.isDelivery = isDelivery
        }
        if (orderTime != null) {
            this.orderTime = orderTime
        }
    }

    fun getLastAddressResponse(): GoogleAddressResponse? {
        return if (googleAddressResponse != null) {
            googleAddressResponse
        } else {
            null
        }
    }


}