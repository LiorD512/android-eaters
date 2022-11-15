package com.bupp.wood_spoon_eaters.features.main.order_history

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.EatersAnalyticsTracker
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.RestaurantInitParams
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class OrdersHistoryViewModel(
    val orderRepository: OrderRepository, val eaterDataManager: EaterDataManager,
    private val eatersAnalyticsTracker: EatersAnalyticsTracker
) : ViewModel() {

    private var refreshRepeatedJob: Job? = null
    val TAG = "wowOrderHistoryVM"

    private val orderListData: MutableMap<Int, MutableList<OrderHistoryBaseItem>> =
        mutableMapOf()//add skeleton ads default here

    val orderLiveData = MutableLiveData<List<OrderHistoryBaseItem>>()


    private fun initList() {
        orderListData[SECTION_ACTIVE] = mutableListOf()
        orderListData[SECTION_ARCHIVE] = mutableListOf()
        orderLiveData.postValue(getSkeletonList().toMutableList())
    }

    fun fetchData() {
        initList()
        getArchivedOrders()
        getActiveOrders()
    }

    private fun getSkeletonList(): MutableList<OrderAdapterItemSkeleton> {
        val skeletons = mutableListOf<OrderAdapterItemSkeleton>()
        skeletons.add(OrderAdapterItemSkeleton())
        return skeletons
    }

    private fun getArchivedOrders() {
        viewModelScope.launch {
            val result = orderRepository.getAllOrders()
            when (result.type) {
                OrderRepository.OrderRepoStatus.GET_All_ORDERS_SUCCESS -> {
                    if (result.data != null && result.data.isNotEmpty()) {
                        updateArchivedOrders(result.data)
                        updateListData()
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun updateArchivedOrders(newData: List<Order>) {
        val currentList = orderListData[SECTION_ARCHIVE]
        if (currentList?.isEmpty() == true && newData.isNotEmpty()) {
            //add title on first init
            currentList.add(OrderAdapterItemTitle("Past orders"))
        }
        newData.forEach { order ->
            val itemInList = currentList!!.find {
                if (it is OrderAdapterItemOrder) {
                    order.id == it.order.id
                } else {
                    false
                }
            }
            if (itemInList == null) {
                currentList.add(OrderAdapterItemOrder(order))
            } else {
                (itemInList as OrderAdapterItemOrder).order = order
            }
        }
    }


    private fun getActiveOrders() {
        viewModelScope.launch {
            val data = eaterDataManager.checkForTraceableOrders()
            data?.let {
                updateActiveOrders(data)
                updateListData()
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun updateActiveOrders(newData: List<Order>) {
        val currentList = orderListData[SECTION_ACTIVE]!!
        newData.forEachIndexed { index, order ->
            val itemInList =
                currentList.find { order.id == (it as OrderAdapterItemActiveOrder).order.id }
            val isLast = (index == newData.size - 1)
            Log.d("wowStatus", "isLast $isLast")
            if (itemInList == null) {
                currentList.add(OrderAdapterItemActiveOrder(order, isLast))
                Log.d("wowStatus", "add new to list ${order.id}")
            } else {
                val isSame: Boolean
                if (itemInList is OrderAdapterItemActiveOrder) {
                    isSame = order.deliveryStatus == itemInList.order.deliveryStatus &&
                            order.preparationStatus == itemInList.order.preparationStatus
                    Log.d("wowStatus", "isSame: $isSame ${order.id}")
                    if (!isSame) {
                        currentList.remove(itemInList)
                        currentList.add(OrderAdapterItemActiveOrder(order, isLast))
                    }
                }
            }
        }
    }

    private fun updateListData() {
        val finalList = mutableListOf<OrderHistoryBaseItem>()
        orderListData[SECTION_ACTIVE]?.let {
            finalList.addAll(it)
        }
        orderListData[SECTION_ARCHIVE]?.let {
            finalList.addAll(it)
        }
        orderLiveData.postValue(finalList)
    }

    private fun repeatRequest(): Job {
        return viewModelScope.launch {
            while (isActive) {
                //do your request
                Log.d(TAG, "fetching FromServer")
                getActiveOrders()
                delay(10000)
            }
        }
    }

    fun startSilentUpdate() {
        if (refreshRepeatedJob == null) {
            refreshRepeatedJob = repeatRequest()
        }
    }

    fun endUpdates() {
        refreshRepeatedJob?.cancel()
        refreshRepeatedJob = null
    }

    val restaurantInitParamsLiveData = MutableLiveData<RestaurantInitParams>()
    fun onOrderAgainClick(order: Order) {
        order.restaurant?.let {
            val restaurantParam = RestaurantInitParams(
                restaurantId = it.id,
                chefThumbnail = it.thumbnail,
                coverPhoto = it.cover,
                rating = it.getAvgRating(),
                restaurantName = it.restaurantName,
                chefName = it.firstName,
                isFavorite = it.isFavorite ?: false,
            )
            restaurantInitParamsLiveData.postValue(restaurantParam)
        }
        logEvent(Constants.EVENT_ORDER_AGAIN_CLICKED)
    }

    fun logTrackOrderClick(orderId: Long) {
        var orderItemPosition = 0

        orderListData[SECTION_ACTIVE]?.let {
            if (it.size == 1) {
                orderItemPosition = 0
            } else {
                it.forEachIndexed { index, orderHistoryBaseItem ->
                    if (orderHistoryBaseItem is OrderAdapterItemActiveOrder) {
                        if (orderHistoryBaseItem.order.id == orderId) {
                            orderItemPosition = index + 1
                        }
                    }
                }
            }
        }
        logEvent(
            Constants.EVENT_ORDERS_TRACK_ORDER_CLICK,
            mapOf(Pair("order_position", orderItemPosition.toString()))
        )
    }


    fun logEvent(eventName: String, params: Map<String, String>? = null) {
        eatersAnalyticsTracker.logEvent(eventName, params)
    }


    companion object {
        const val TAG = "wowOrderHistoryVM"
        const val SECTION_ACTIVE = 0
        const val SECTION_ARCHIVE = 1
    }

}
