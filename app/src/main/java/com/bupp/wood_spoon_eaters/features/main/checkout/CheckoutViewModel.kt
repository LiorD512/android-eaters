package com.bupp.wood_spoon_eaters.features.main.checkout

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService

class CheckoutViewModel(val api: ApiService,val orderManager:OrderManager) : ViewModel() {

    val checkOutDetails: SingleLiveEvent<CheckoutDetails> = SingleLiveEvent()

    data class CheckoutDetails(val order: Order2, val cook:Cook2)

    fun getDumbCheckoutDetails(){
        var ingr = Ingredient(69,"Ingreideint missing", arrayListOf())
        var dishIng = DishIngredient2(ingredient = ingr)
        var dish: Dish2 = Dish2(name="dish Name",price=Price(4201,420.0,"420$$"),removedIngredients= arrayListOf(dishIng))
        var dish2: Dish2 = Dish2(name="dish Name",price=Price(4201,420.0,"420$$"),removedIngredients= arrayListOf(dishIng,dishIng,dishIng))

        var orderItems = OrderItem2(id=1,price= Price(3,3.0,"3.0$$"),notes="WTFTFTFTFFFFF",dish=dish,quantity = 1)
        var orderItems2 = OrderItem2(id=1,price= Price(3,3.0,"3.0$$"),notes="WTFTFTFTFFFFF",dish=dish2,quantity = 1)

        var order: Order2= Order2(orderItems = arrayListOf(orderItems,orderItems2))

        //TODO:: remove cook2 Model

        //TODO:: remove Order2,Dish2 Model
        val cook: Cook2= Cook2(firstName = "Eyal",lastName = "TheCook")
        checkOutDetails.postValue(CheckoutDetails(order,cook))
    }

    fun getLastOrderAddress(): String? {
        return orderManager.getLastOrderAddress()?.streetLine1
    }}
