package com.bupp.wood_spoon_eaters.features.main.order_history

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen.TrackOrderFragment
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.model.Order
//import com.bupp.wood_spoon_eaters.model.Report
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import kotlinx.coroutines.launch

class OrdersHistoryViewModel(val orderRepository: OrderRepository, val eaterDataManager: EaterDataManager) : ViewModel() {

    val TAG = "wowOrderHistoryVM"

//    var archiveOrderData: MutableList<OrderAdapterItemOrder> = mutableListOf()
//    var activeOrderData: MutableList<OrderAdapterItemActiveOrder> = mutableListOf()
    private val orderListData: MutableList<OrderHistoryBaseItem> = mutableListOf()

    val orderLiveData = MutableLiveData<List<OrderHistoryBaseItem>>()

    fun fetchData() {
        orderListData.clear()
        getArchivedOrders()
        getActiveOrders()
    }

    fun getArchivedOrders() {
        viewModelScope.launch {
            val result = orderRepository.getAllOrders()
            when (result.type) {
                OrderRepository.OrderRepoStatus.GET_All_ORDERS_SUCCESS -> {
                    handleData(result.data, TYPE_ARCHIVE_ORDER)
                }
                else -> {}
            }
        }
    }

    fun getActiveOrders() {
        viewModelScope.launch {
            val data = eaterDataManager.checkForTraceableOrders()
            handleData(data, TYPE_ACTIVE_ORDER)
        }
    }

    private fun handleData(data: List<Order>?, type: String) {
        Log.d(TAG, "handleData: $type")
        when (type) {
            TYPE_ACTIVE_ORDER -> {
                data?.let{ it ->
                    it.forEach {
                        orderListData.add(OrderAdapterItemActiveOrder(it))
                    }
                }
            }
            TYPE_ARCHIVE_ORDER -> {
                data?.let{ it ->
                    orderListData.add(OrderAdapterItemTitle("Past orders"))
                    it.forEach {
                        orderListData.add(OrderAdapterItemOrder(it))
                    }
                }
            }
        }
        orderLiveData.postValue(orderListData)
    }


    companion object {
        const val TAG = "wowOrderHistoryVM"
        const val TYPE_ACTIVE_ORDER = "active"
        const val TYPE_ARCHIVE_ORDER = "archive"
    }

}
