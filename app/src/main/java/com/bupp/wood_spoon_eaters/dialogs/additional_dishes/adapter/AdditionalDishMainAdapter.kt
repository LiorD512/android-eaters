package com.bupp.wood_spoon_eaters.dialogs.additional_dishes.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.OrderItem
import mva2.adapter.HeaderSection
import mva2.adapter.ItemSection
import mva2.adapter.MultiViewAdapter

class AdditionalDishMainAdapter(
    val context: Context,
    orderItemsListener: OrderItemsAdapter.OrderItemsListener,
    additionalDishesListener: AdditionalDishesAdapter.AdditionalDishesListener
) : MultiViewAdapter() {


    private val TAG: String = "wowAdditionalMainAdApter"

    val orderItemsSection = ItemSection<OrderItems>()
    val additionalDishesHeaderAndSection = HeaderSection<AdditionalDishHeader>()
    val additionalDishesSection = ItemSection<AdditionalDishes>()

    init {
        this.registerItemBinders(OrderItemsBinder(orderItemsListener), AdditionalDishesHeaderBinder(), AdditionalDishesBinder(additionalDishesListener))

        this.addSection(orderItemsSection)
        additionalDishesHeaderAndSection.header = AdditionalDishHeader("")
        additionalDishesHeaderAndSection.addSection(additionalDishesSection)
        this.addSection(additionalDishesHeaderAndSection)
        hideAllSections()
    }

    private fun hideAllSections() {
        orderItemsSection.hideSection()
        additionalDishesHeaderAndSection.hideSection()
    }

    @SuppressLint("LongLogTag")
    fun refreshOrderItems(orderItemList: List<OrderItem>) {
        Log.d(TAG, "refreshOrderItems $orderItemList")
        orderItemsSection.removeItem()
        orderItemsSection.setItem(OrderItems(orderItemList))
        orderItemsSection.showSection()
    }

//    fun List<Dish>.f(orderItems: List<Dish>) = filter { dish ->
//        orderItems.any { orderItem ->
//            orderItem.id != dish.id
//        }
//    }

    @SuppressLint("LongLogTag")
    fun refreshAdditionalDishes(dishes: List<Dish>) {
        Log.d(TAG, "refreshAdditionalDishes $dishes")
        val orderItems = orderItemsSection.item?.orderItems
        //remove order items from additional dish list
        val sum = mutableSetOf<Dish>()
        val orderItemDishes = mutableListOf<Dish>()
        orderItems?.forEach {
            if(it.quantity > 0){
                orderItemDishes.add(it.dish)
            }
        }
        val additioanlDishes = dishes.mapNotNull { dish ->
            if (orderItemDishes.find { it.id == dish.id} != null){
                null
            }else{
                if(dish.isSoldOut()) {
                    null
                }else{
                    dish
                }
            }
        }

        if (additioanlDishes.isNotEmpty()) {
            additionalDishesSection.removeItem()
            additionalDishesSection.setItem(AdditionalDishes(ArrayList(additioanlDishes)))
            additionalDishesHeaderAndSection.header = AdditionalDishHeader(ArrayList(additioanlDishes)[0]?.cook?.firstName)
            additionalDishesSection.showSection()
            additionalDishesHeaderAndSection.showSection()
        }
    }

    fun removeDish(currentAddedDish: Dish) {
        val currentAdditional = additionalDishesSection.item?.dishes
        currentAdditional?.remove(currentAddedDish)
        additionalDishesSection.removeItem()
        additionalDishesSection.setItem(AdditionalDishes(currentAdditional!!))

        if(currentAdditional.size == 0){
            additionalDishesHeaderAndSection.hideSection()
        }
    }


}