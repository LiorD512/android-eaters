package com.bupp.wood_spoon_eaters.dialogs.additional_dishes.adapter

import android.annotation.SuppressLint
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
    fun refreshOrderItems(orderItemList: ArrayList<OrderItem>) {
        Log.d(TAG, "refreshOrderItems")
        orderItemsSection.removeItem()
        orderItemsSection.setItem(OrderItems(orderItemList))
        orderItemsSection.showSection()
    }

    @SuppressLint("LongLogTag")
    fun refreshAdditionalDishes(dishes: ArrayList<Dish>) {
        Log.d(TAG, "refreshAdditionalDishes")
        val orderItems = orderItemsSection.item?.orderItems
        val additionalArr = arrayListOf<Dish>()
        //remove order items from additional dish list
        dishes.forEach { dish ->
            orderItems?.forEach { orderItem ->
                if (orderItem.dish.id == dish.id) {
                    return@forEach
                }
                additionalArr.add(dish)
            }
        }
        if (additionalArr.isNotEmpty()) {
            additionalDishesSection.removeItem()
            additionalDishesSection.setItem(AdditionalDishes(additionalArr))
            additionalDishesHeaderAndSection.header = AdditionalDishHeader(additionalArr[0].cook.firstName)
            additionalDishesSection.showSection()
            additionalDishesHeaderAndSection.showSection()
        }
    }

    fun updateHeaderText(firstName: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun removeDish(currentAddedDish: Dish) {
        val currentAdditional = additionalDishesSection.item?.dishes
        currentAdditional?.remove(currentAddedDish)
        additionalDishesSection.removeItem()
        additionalDishesSection.setItem(AdditionalDishes(currentAdditional!!))
    }


}