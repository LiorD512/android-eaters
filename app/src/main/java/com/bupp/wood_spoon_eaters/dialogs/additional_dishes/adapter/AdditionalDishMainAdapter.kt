package com.bupp.wood_spoon_eaters.dialogs.additional_dishes.adapter

import android.content.Context
import android.util.Log
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.OrderItem
import mva2.adapter.HeaderSection
import mva2.adapter.ItemSection
import mva2.adapter.MultiViewAdapter

class AdditionalDishMainAdapter(
    val context: Context,
    orderItemsListener: OrderItemsAdapter.OrderItemsListener,
    additionalDishesListener: AdditionalDishesAdapter.AdditionalDishesListener
) : MultiViewAdapter() {


    private val TAG: String = "wowAdditionalMainAdptr"

    private val orderItemsSection = ItemSection<OrderItems>()
    private val additionalDishesHeaderAndSection = HeaderSection<AdditionalDishHeader>()
    private val additionalDishesSection = ItemSection<AdditionalDishes>()

    init {
        this.registerItemBinders(OrderItemsBinder(orderItemsListener), AdditionalDishesHeaderBinder(), AdditionalDishesBinder(additionalDishesListener))

        this.addSection(orderItemsSection)
        additionalDishesHeaderAndSection.header = AdditionalDishHeader("")
        additionalDishesHeaderAndSection.addSection(additionalDishesSection)
        this.addSection(additionalDishesHeaderAndSection)
        hideAllSections()
    }

    private fun hideAllSections() {
        Log.d(TAG, "hideAllSections")
        orderItemsSection.hideSection()
        additionalDishesHeaderAndSection.hideSection()
    }

    fun refreshOrderItems(orderItemList: List<OrderItem>) {
        Log.d(TAG, "refreshOrderItems $orderItemList")
        orderItemsSection.removeItem()
        orderItemsSection.setItem(OrderItems(orderItemList))
        orderItemsSection.showSection()
    }

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
        val additionalDishes = dishes.mapNotNull { dish ->
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

        if (additionalDishes.isNotEmpty()) {
            additionalDishesSection.removeItem()
            additionalDishesSection.setItem(AdditionalDishes(ArrayList(additionalDishes)))
            additionalDishesHeaderAndSection.header = AdditionalDishHeader(ArrayList(additionalDishes)[0]?.cook?.firstName)
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