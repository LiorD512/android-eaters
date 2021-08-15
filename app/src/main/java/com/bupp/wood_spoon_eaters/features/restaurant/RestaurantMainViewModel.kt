package com.bupp.wood_spoon_eaters.features.restaurant

import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet.CustomCartItem
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.RestaurantPageFragmentDirections
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.DishInitParams
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.models.RestaurantInitParams
import com.bupp.wood_spoon_eaters.model.CookingSlot
import com.bupp.wood_spoon_eaters.model.MenuItem

class RestaurantMainViewModel : ViewModel() {

//    var currentRestaurant: RestaurantInitParams? = null
    val restaurantInitParamsLiveData = LiveEventData<RestaurantInitParams>()
    fun initExtras(restaurantInitParams: RestaurantInitParams?) {
        restaurantInitParams?.let{
            restaurantInitParamsLiveData.postRawValue(it)
        }
    }

    enum class NavigationType{
        OPEN_DISH_PAGE,
        START_ORDER_CHECKOUT_ACTIVITY,
        FINISH_RESTAURANT_ACTIVITY
    }
    data class NavigationEvent(
        val navigationType: NavigationType?,
        val navDirections: NavDirections?
    )
    val navigationEvent = LiveEventData<NavigationEvent>()

    fun handleNavigation(navigationType: NavigationType?){
        when(navigationType){
            NavigationType.FINISH_RESTAURANT_ACTIVITY -> {
                navigationEvent.postRawValue(NavigationEvent(navigationType, null))
            }
            NavigationType.START_ORDER_CHECKOUT_ACTIVITY -> {
                navigationEvent.postRawValue(NavigationEvent(navigationType, null))
            }
        }
    }

    fun openDishPage(menuItem: MenuItem, curCookingSlot: CookingSlot?){
        val extras = DishInitParams(menuItem = menuItem, cookingSlot = curCookingSlot, orderItem = null)
        val action = RestaurantPageFragmentDirections.actionRestaurantPageFragmentToDishPageFragment(extras)
        navigationEvent.postRawValue(NavigationEvent(NavigationType.OPEN_DISH_PAGE, action))
    }

    fun openDishPageWithOrderItem(customCartItem: CustomCartItem) {
        val extras = DishInitParams(orderItem = customCartItem.orderItem, cookingSlot = customCartItem.cookingSlot, menuItem = null)
        val action = RestaurantPageFragmentDirections.actionRestaurantPageFragmentToDishPageFragment(extras)
        navigationEvent.postRawValue(NavigationEvent(NavigationType.OPEN_DISH_PAGE, action))
    }


}
