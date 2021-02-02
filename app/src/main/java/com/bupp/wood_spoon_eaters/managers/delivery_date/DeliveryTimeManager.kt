package com.bupp.wood_spoon_eaters.managers.delivery_date

import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.utils.DateUtils
import java.util.*

class DeliveryTimeManager {

    data class DeliveryTimeLiveData(val deliveryDate: Date?, val deliveryTimestamp: String?, val deliveryDateUi: String)

    private var deliveryTime: Date? = null
    private val deliveryTimeDateLiveData = MutableLiveData<DeliveryTimeLiveData?>()
    fun getDeliveryTimeLiveData() = deliveryTimeDateLiveData

    fun setNewDeliveryTime(newDeliveryTime: Date?){
        this.deliveryTime = newDeliveryTime
        deliveryTimeDateLiveData.postValue(DeliveryTimeLiveData(getDeliveryTimeDate(), getDeliveryTimestamp(), getDeliveryTimeUiString()))
    }

    fun getDeliveryTimeDate(): Date? {
        deliveryTime?.let{
            return it
        }
        return null
    }

    fun getDeliveryTimestamp(): String? {
        //returns unix timestamp
        getDeliveryTimeDate()?.let{
            return DateUtils.parseUnixTimestamp(getDeliveryTimeDate()!!)
        }
        return null
    }

    private fun getDeliveryTimeUiString(): String {
        getDeliveryTimeDate()?.let{
            return DateUtils.parseDateToUsDayTime(it)
        }
        return "Now"
    }
}