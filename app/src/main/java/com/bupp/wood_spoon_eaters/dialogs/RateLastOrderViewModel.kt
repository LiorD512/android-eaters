package com.bupp.wood_spoon_eaters.dialogs

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.google.interfaces.GoogleApi
import com.taliazhealth.predictix.network_google.models.google_api.AddressIdResponse
import com.bupp.wood_spoon_eaters.network.google.models.GoogleAddressResponse
import com.bupp.wood_spoon_eaters.utils.Constants
import retrofit2.Call
import retrofit2.Response

class RateLastOrderViewModel(private val api: ApiService,private val orderManager: OrderManager) : ViewModel() {



    val orderDetails: SingleLiveEvent<OrderDetails> = SingleLiveEvent()

    data class OrderDetails(val orders: ArrayList<OrderItem2>)

    fun getDumbOrderDetails() {
        var cook: Cook2 = Cook2(firstName = "Eyal",lastName = "Yaakobi",thumbnail ="https://image.tmdb.org/t/p/w500_and_h282_face/87mvGYiKkV4PDNu9m0NIr0OPBrY.jpg")
        var dish: Dish2 = Dish2(name = " Tomato Soup",thumbnail = "https://www.cleaneatingkitchen.com/wp-content/uploads/2015/11/Healthy-Cream-of-Tomato-Soup.jpg",cook = cook)
        var dish2: Dish2 = Dish2(name = " Tomato Soup",thumbnail = "https://www.cleaneatingkitchen.com/wp-content/uploads/2015/11/Healthy-Cream-of-Tomato-Soup.jpg",cook = cook)
        var dish3: Dish2 = Dish2(name = " Tomato Soup",thumbnail = "https://www.cleaneatingkitchen.com/wp-content/uploads/2015/11/Healthy-Cream-of-Tomato-Soup.jpg",cook = cook)

        var order: OrderItem2 = OrderItem2(id=1,dish = dish,quantity = 5)
        var order2: OrderItem2 = OrderItem2(id=2,dish = dish2,quantity = 2)
        var order3: OrderItem2 = OrderItem2(id=3,dish = dish3,quantity = 1)

        orderDetails.postValue(OrderDetails(arrayListOf(order,order2,order3)))
    }
}