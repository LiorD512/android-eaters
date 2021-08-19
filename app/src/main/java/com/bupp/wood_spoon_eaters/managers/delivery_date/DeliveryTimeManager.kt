git package com.bupp.wood_spoon_eaters.managers.delivery_date

import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.utils.DateUtils
import java.util.*

class DeliveryTimeManager {

    data class DeliveryTimeLiveData(val deliveryDate: Date?, val deliveryTimestamp: String?, val deliveryDateUi: String)

    private var hasChangedTime: Boolean = false
    private var previousDeliveryTime: Date? = null
    private var deliveryTime: Date? = null
    private val deliveryTimeDateLiveData = MutableLiveData<DeliveryTimeLiveData?>()
    fun getDeliveryTimeLiveData() = deliveryTimeDateLiveData

    private var tempDeliveryTime: Date? = null
    private var tempDeliveryTimeStamp: String? = null
    fun getTempDeliveryTimeStamp() = tempDeliveryTimeStamp

    fun setNewDeliveryTime(newDeliveryTime: Date?){
        this.deliveryTime = newDeliveryTime
        tempDeliveryTime = newDeliveryTime
        tempDeliveryTimeStamp = getDeliveryTimestamp()
        deliveryTimeDateLiveData.postValue(DeliveryTimeLiveData(getDeliveryTimeDate(), getDeliveryTimestamp(), getDeliveryDateUiString()))
    }

    fun setTemporaryDeliveryTimeDate(tempDate: Date?){
        tempDeliveryTime = tempDate
        tempDeliveryTimeStamp = DateUtils.parseUnixTimestamp(tempDate)
    }

    fun clearDeliveryTime(){
        tempDeliveryTime = null
        tempDeliveryTimeStamp = null
//        setNewDeliveryTime(null)
    }

    fun getTempDeliveryTimeDate(): Date? {
        return tempDeliveryTime
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

    private fun getDeliveryDateUiString(): String {
        getDeliveryTimeDate()?.let{
            return DateUtils.parseDateToDayDateNumberOrToday(it)
        }
        return "Today"
    }
//
//    fun rollBackToPreviousDeliveryTime(){
////        Log.d(TAG, "rollBackToPreviousDeliveryTime")
////        previousDeliveryTime.let{
////            Log.d(TAG, "rollBackToPreviousDeliveryTime - rolling back")
////            setNewDeliveryTime(it)
////            this.previousDeliveryTime = null
////        }
//    }

    companion object{
        const val TAG = "DeliveryTimeManager"
    }
}