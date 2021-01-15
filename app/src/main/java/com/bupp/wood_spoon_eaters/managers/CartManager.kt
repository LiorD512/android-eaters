package com.bupp.wood_spoon_eaters.managers

import com.bupp.wood_spoon_eaters.model.FullDish
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.repositories.NewOrderRepository
import java.util.*

class CartManager(val apiService: ApiService, val eaterDataManager: EaterDataManager, val newOrderRepository: NewOrderRepository) {

    var currentShowingDish: FullDish? = null

    data class GetFullDishResult(
        val fullDish: FullDish,
        val isAvailable: Boolean,
        val startingTime: Date?,
        val isSoldOut: Boolean
    )

    suspend fun getFullDish(menuItemId: Long): GetFullDishResult? {
        val feedRequest = eaterDataManager.getFinalTimeAndLocationParam()
        val result = newOrderRepository.getFullDish(menuItemId, feedRequest)
        result?.let {
            this.currentShowingDish = it
            return GetFullDishResult(
                it,
                isAvailable = checkCookingSlotAvailability(),
                startingTime = getStartingDate(),
                isSoldOut = checkDishSellout()
            )
        }
        return null
    }

    private fun checkCookingSlotAvailability(): Boolean {
        currentShowingDish?.let {
            val orderFrom: Date? = it.menuItem?.cookingSlot?.orderFrom
            val start: Date? = it.menuItem?.cookingSlot?.startsAt
            val end: Date? = it.menuItem?.cookingSlot?.endsAt
            var userSelection: Date? = eaterDataManager.getLastOrderTime()

            if (start == null || end == null) {
                return false
            }
            if (userSelection == null) {
                //in this case order is ASAP - then check from starting time and not orderingFrom time
                userSelection = Date()
                return (userSelection.equals(start) || userSelection.equals(end)) || (userSelection.after(start) && userSelection.before(end))
            }
            return (userSelection.equals(orderFrom) || userSelection.equals(end)) || (userSelection.after(orderFrom) && userSelection.before(end))
        }
        return true
    }

    private fun getStartingDate(): Date? {
        var newDate = Date()
        currentShowingDish?.menuItem?.cookingSlot?.orderFrom?.let {
            if (it.after(newDate)) {
                newDate = it
            }
        }
        return newDate
    }


    private fun checkDishSellout(): Boolean {
        currentShowingDish?.let {
            val quantity = it.menuItem?.quantity
            val unitsSold = it.menuItem?.unitsSold
            quantity?.let {
                unitsSold?.let {
                    return ((quantity - unitsSold <= 0))
                }
            }
        }
        return false
    }
}