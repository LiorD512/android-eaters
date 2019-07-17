package com.bupp.wood_spoon_eaters.features.main.order_details

import androidx.lifecycle.ViewModel;
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.model.OrderItem
import com.bupp.wood_spoon_eaters.model.Price
import com.bupp.wood_spoon_eaters.network.ApiService

class OrderDetailsViewModel(val apiService: ApiService, val orderManager: OrderManager) : ViewModel() {



    val orderItems: SingleLiveEvent<OrderDetails> = SingleLiveEvent()

    data class OrderDetails(val orders: ArrayList<OrderItem>)

    fun getDumbOrderItems() {

//        var price: Price = Price(1,1.0,"$1")
//
//        var cook: Cook2 = Cook2(firstName = "Eyal",lastName = "Yaakobi",thumbnail ="https://image.tmdb.org/t/p/w500_and_h282_face/87mvGYiKkV4PDNu9m0NIr0OPBrY.jpg")
//        var dish: Dish2 = Dish2(name = " Tomato Soup",thumbnail = "https://www.cleaneatingkitchen.com/wp-content/uploads/2015/11/Healthy-Cream-of-Tomato-Soup.jpg",cook = cook)
//        var dish2: Dish2 = Dish2(name = " Tomato Soup",thumbnail = "https://www.cleaneatingkitchen.com/wp-content/uploads/2015/11/Healthy-Cream-of-Tomato-Soup.jpg",cook = cook)
//        var dish3: Dish2 = Dish2(name = " Tomato Soup",thumbnail = "https://www.cleaneatingkitchen.com/wp-content/uploads/2015/11/Healthy-Cream-of-Tomato-Soup.jpg",cook = cook)
//
//        var order: OrderItem2 = OrderItem2(id=1,dish = dish,quantity = 5,price= price)
//        var order2: OrderItem2 = OrderItem2(id=2,dish = dish2,quantity = 2,price= price)
//        var order3: OrderItem2 = OrderItem2(id=3,dish = dish3,quantity = 1,price= price)
//
//        orderItems.postValue(OrderDetails(arrayListOf(order,order2,order3)))
    }

}
