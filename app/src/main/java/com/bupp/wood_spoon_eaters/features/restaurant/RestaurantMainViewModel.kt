package com.bupp.wood_spoon_eaters.features.restaurant

import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet.CustomCartItem
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.RestaurantPageFragmentDirections
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishInitParams
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishSectionSingleDish
import com.bupp.wood_spoon_eaters.managers.EventsManager
import com.bupp.wood_spoon_eaters.model.CookingSlot
import com.bupp.wood_spoon_eaters.model.MenuItem

class RestaurantMainViewModel(private val flowEventsManager: FlowEventsManager, private val eventsManager: EventsManager) : ViewModel() {

    enum class NavigationType {
        OPEN_DISH_PAGE,
        START_ORDER_CHECKOUT_ACTIVITY,
        FINISH_RESTAURANT_ACTIVITY
    }

    data class NavigationEvent(
        val navigationType: NavigationType?,
        val navDirections: NavDirections?
    )

    val navigationEvent = LiveEventData<NavigationEvent>()

    fun handleNavigation(navigationType: NavigationType?) {
        when (navigationType) {
            NavigationType.FINISH_RESTAURANT_ACTIVITY -> {
                navigationEvent.postRawValue(NavigationEvent(navigationType, null))
            }
            NavigationType.START_ORDER_CHECKOUT_ACTIVITY -> {
                navigationEvent.postRawValue(NavigationEvent(navigationType, null))
            }
        }
    }

    fun openDishPage(menuItem: MenuItem, curCookingSlot: CookingSlot?) {
        val dishSectionTitle = menuItem.sectionTitle.toString()
        val dishOrderInSection = menuItem.dishOrderInSection
        val extras = DishInitParams(
            menuItem = menuItem,
            cookingSlot = curCookingSlot,
            orderItem = null,
            dishSectionTitle = dishSectionTitle,
            dishOrderInSection = dishOrderInSection
        )
        val action = RestaurantPageFragmentDirections.actionRestaurantPageFragmentToDishPageFragment(extras)
        navigationEvent.postRawValue(NavigationEvent(NavigationType.OPEN_DISH_PAGE, action))
    }

    fun openDishPageWithOrderItem(customCartItem: CustomCartItem, finishToFeed: Boolean = false) {
        val extras =
            DishInitParams(orderItem = customCartItem.orderItem, cookingSlot = customCartItem.cookingSlot, menuItem = null, finishToFeed = finishToFeed)
        val action = RestaurantPageFragmentDirections.actionRestaurantPageFragmentToDishPageFragment(extras)
        navigationEvent.postRawValue(NavigationEvent(NavigationType.OPEN_DISH_PAGE, action))
    }

    fun logPageEvent(eventType: FlowEventsManager.FlowEvents) {
        flowEventsManager.logPageEvent(eventType)
    }

    fun logDishSwipeEvent(eventName: String, item: DishSectionSingleDish) {
        eventsManager.logEvent(eventName, getDishSwipedData(item))
    }

    fun logClickVideo(fullName: String, id: Long) {
        val data = mutableMapOf<String, String>()
        data["home_chef_name"] = fullName
        data["home_chef_id"] = id.toString()
        eventsManager.logEvent(Constants.EVENT_CLICK_VIDEO_IN_RESTAURANT, data)
    }

    private fun getDishSwipedData(item: DishSectionSingleDish): Map<String, String> {
        val data = mutableMapOf<String, String>()
        data["dish_name"] = item.menuItem.dish?.name.toString()
        data["dish_id"] = item.menuItem.dish?.id.toString()
        data["dish_price"] = item.menuItem.dish?.price?.formatedValue.toString()
        return data
    }


}
