package com.bupp.wood_spoon_eaters.repositories

import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.model.FeedRequest
import com.bupp.wood_spoon_eaters.model.FullDish
import com.bupp.wood_spoon_eaters.model.Review
import com.bupp.wood_spoon_eaters.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewOrderRepository(val apiService: ApiService, val eaterDataManager: EaterDataManager) {

    suspend fun getFullDish(menuItemId: Long, feedRequest: FeedRequest): FullDish? {
        return withContext(Dispatchers.IO) {
            apiService.getSingleDish(
                menuItemId = menuItemId,
                lat = feedRequest.lat,
                lng = feedRequest.lng,
                addressId = feedRequest.addressId,
                timestamp = feedRequest.timestamp
            ).data
        }
    }

    suspend fun getDishReview(dishId: Long): Review? {
        return withContext(Dispatchers.IO) {
            apiService.getDishReview(dishId).data
        }
    }

}