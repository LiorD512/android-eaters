package com.bupp.wood_spoon_eaters.features.main.order_history

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class OrdersHistoryViewModel(val orderRepository: OrderRepository, val eaterDataManager: EaterDataManager) : ViewModel() {

    private var refreshRepeatedJob: Job? = null
    val TAG = "wowOrderHistoryVM"

    private val orderListData: MutableMap<Int, MutableList<OrderHistoryBaseItem>> = mutableMapOf()//add skeleton ads default here

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
        startSilentUpdate()
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


    fun getActiveOrders() {
        viewModelScope.launch {
            val data = eaterDataManager.checkForTraceableOrders()
            data?.let { it ->
                updateActiveOrders(data)
                updateListData()
            }
        }
    }

    private fun updateActiveOrders(newData: List<Order>) {
        val currentList = orderListData[SECTION_ACTIVE]!!
        newData.forEachIndexed { index, order ->
            val itemInList = currentList.find { order.id == (it as OrderAdapterItemActiveOrder).order.id }
            val isLast = (index == newData.size-1)
                Log.d("wowStatus", "isLast $isLast")
            if (itemInList == null) {
                currentList.add(OrderAdapterItemActiveOrder(order, isLast))
                Log.d("wowStatus", "add new to list ${order.id}")
            } else {
                var isSame = false
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

    private fun startSilentUpdate() {
        if (refreshRepeatedJob == null) {
            refreshRepeatedJob = repeatRequest()
        }
    }

    fun endUpdates() {
        refreshRepeatedJob?.cancel()
        refreshRepeatedJob = null
    }



    companion object {
        const val TAG = "wowOrderHistoryVM"
        const val SECTION_ACTIVE = 0
        const val SECTION_ARCHIVE = 1
    }

}
