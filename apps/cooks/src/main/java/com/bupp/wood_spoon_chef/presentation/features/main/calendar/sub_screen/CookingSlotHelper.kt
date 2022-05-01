package com.bupp.wood_spoon_chef.presentation.features.main.calendar.sub_screen

import android.util.Log
import com.bupp.wood_spoon_chef.presentation.features.main.calendar.sub_screen.calendar_menu_adapter.CookingSlotDetailsAdapter
import com.bupp.wood_spoon_chef.data.remote.model.MenuItem
import com.bupp.wood_spoon_chef.data.remote.model.MenuItemRequest
import java.util.*

class CookingSlotHelper {

    //represents open items, but not yes ready (without quantity)
    private var selectedMenuItems: HashMap<Long, MenuItemRequest> = hashMapOf()
    //represents ready items - valid for cooking slot
    private var validMenuItems: HashMap<Long, MenuItemRequest> = hashMapOf()

    private fun isAllItemsValid(): Boolean {
        return validMenuItems.isNotEmpty()
    }

    fun getAllValidItems(): List<MenuItemRequest> {
        return validMenuItems.values.toList()
    }

    fun updateExpandedViews(dishId: Long, isExpended: Boolean) {
        if (isExpended) {
            if (!selectedMenuItems.containsKey(dishId)) {
                selectedMenuItems[dishId] = MenuItemRequest(dishId = dishId)
            }
        } else {
            if (selectedMenuItems.containsKey(dishId) && !isExpended) {
                selectedMenuItems.remove(dishId)
            }
            validMenuItems.remove(dishId)
        }
    }

    fun isExpanded(dishId: Long?): Boolean {
        dishId?.let {
            return selectedMenuItems.containsKey(dishId)
        }
        return false
    }

    fun updateViewQuantity(id: Long, quantity: Int) {
        val menuItemRequest = selectedMenuItems[id]
        menuItemRequest?.let {
            it.quantity = quantity
            if (quantity > 0) {
                validMenuItems.put(id, it)
            } else {
                validMenuItems.remove(id)
            }
        }
    }

    fun getCachedItemQuantity(dishId: Long?): Int? {
        dishId?.let {
            val cachedItem = validMenuItems[dishId]
            cachedItem?.let {
                return it.quantity
            }
        }
        return null
    }

    fun isAllDataValid(): Boolean {
        return isAllItemsValid() && isDatesValid()
    }

    //Time And Dates -
    var isFreeDelivery: Boolean = false
    var isWorldWide: Boolean = false
    var lastClickedView: Int = 0
    var startTime: Date? = null
    var finishTime: Date? = null
    var lastCallTime: Date? = null


    fun updateTime(date: Date) {
        when (lastClickedView) {
            CookingSlotDetailsAdapter.START_TIME -> startTime = date
            CookingSlotDetailsAdapter.FINISH_TIME -> finishTime = date
            CookingSlotDetailsAdapter.LAST_CALL -> lastCallTime = date
        }
    }

    private fun isDatesValid(): Boolean {
        return startTime != null && finishTime != null
    }

    //EditMode
    fun parseMenuItemToRequestItems(menuItems: List<MenuItem>) {
        menuItems.forEach { menuItem ->
            selectedMenuItems[menuItem.dish.id!!] =
                MenuItemRequest(id = menuItem.id, dishId = menuItem.dish.id)
            validMenuItems[menuItem.dish.id!!] = menuItem.parseToMenuItemRequest()
        }
    }

    fun destroyItem(dishId: Long) {
        val menuItemRequest = validMenuItems[dishId]
        menuItemRequest?.let {
            it._destroy = true
            selectedMenuItems.remove(dishId)
        }
    }

    fun unDestroyItem(dishId: Long) {
        val menuItemRequest = validMenuItems[dishId]
        menuItemRequest?.let {
            it._destroy = false
        }
        updateExpandedViews(dishId, true)
    }

    fun clearData() {
        selectedMenuItems.clear()
        validMenuItems.clear()
        isWorldWide = false
        isFreeDelivery = false
        startTime = null
        finishTime = null
        lastCallTime = null
    }

    companion object {
        const val TAG = "wowCookingSlotHelper"
    }
}
