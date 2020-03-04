package com.bupp.wood_spoon_eaters.dialogs.additional_dishes.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.bupp.wood_spoon_eaters.model.Dish
import com.bupp.wood_spoon_eaters.model.OrderItem
import mva2.adapter.HeaderSection
import mva2.adapter.ItemSection
import mva2.adapter.MultiViewAdapter

class AdditionalDishMainAdapter(val context: Context, orderItemsListener: OrderItemsAdapter.OrderItemsListener, additionalDishesListener: AdditionalDishesAdapter.AdditionalDishesListener)
    : MultiViewAdapter() {


    private val TAG: String = "wowAdditionalMainAdApter"

    val orderItemsSection = ItemSection<OrderItems>()
    val additionalDishesHeaderAndSection = HeaderSection<AdditionalDishHeader>()
    val additionalDishesSection = ItemSection<AdditionalDishes>()

    init{
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
        Log.d(TAG,"refreshOrderItems")
        orderItemsSection.setItem(OrderItems(orderItemList))
        orderItemsSection.showSection()
    }

    @SuppressLint("LongLogTag")
    fun refreshAdditionalDishes(dishes: ArrayList<Dish>) {
        Log.d(TAG,"refreshAdditionalDishes")
        if(dishes.isNotEmpty()){
            additionalDishesSection.setItem(AdditionalDishes(dishes))
            additionalDishesHeaderAndSection.header =  AdditionalDishHeader(dishes[0].cook.firstName)
            additionalDishesSection.showSection()
            additionalDishesHeaderAndSection.showSection()
        }
    }

    fun updateHeaderText(firstName: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}