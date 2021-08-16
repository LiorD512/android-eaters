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

    private val orderListData: MutableMap<Int, MutableList<OrderHistoryBaseItem>> = mutableMapOf()//add skeleton ads default here

    val orderLiveData = MutableLiveData<List<OrderHistoryBaseItem>>()


    fun initList(){
        orderListData.put(SECTION_ACTIVE, mutableListOf<OrderHistoryBaseItem>())
        orderListData.put(SECTION_ARCHIVE, mutableListOf<OrderHistoryBaseItem>())
    }

    fun fetchData() {
        initList()
        getArchivedOrders()
        getActiveOrders()
    }

    fun getArchivedOrders() {
        viewModelScope.launch {
            val result = orderRepository.getAllOrders()
            when (result.type) {
                OrderRepository.OrderRepoStatus.GET_All_ORDERS_SUCCESS -> {
                    if (result.data != null && result.data.isNotEmpty()) {
                        val tempList = mutableListOf<OrderHistoryBaseItem>()
                        tempList.add(OrderAdapterItemTitle("Past orders"))
                        result.data.forEach {
                            tempList.add(OrderAdapterItemOrder(it))
                        }
                        orderListData[SECTION_ARCHIVE]?.clear()
                        orderListData[SECTION_ARCHIVE]?.addAll(tempList)
                        arrangeData()
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun arrangeData() {
        val finalList = mutableListOf<OrderHistoryBaseItem>()
        orderListData[SECTION_ACTIVE]?.let{
            finalList.addAll(it)
        }
        orderListData[SECTION_ARCHIVE]?.let{
            finalList.addAll(it)
        }
        orderLiveData.postValue(finalList)
    }

    fun getActiveOrders() {
        viewModelScope.launch {
            val data = eaterDataManager.checkForTraceableOrders()
            data?.let { it ->
                val tempList = mutableListOf<OrderHistoryBaseItem>()
                it.forEach {
                    tempList.add(OrderAdapterItemActiveOrder(it))
                }
                orderListData[SECTION_ACTIVE]?.clear()
                orderListData[SECTION_ACTIVE]?.addAll(tempList)
                arrangeData()
            }
        }
    }


    companion object {
        const val TAG = "wowOrderHistoryVM"
        const val SECTION_ACTIVE = 0
        const val SECTION_ARCHIVE = 1
        const val TYPE_ACTIVE_ORDER = "active"
        const val TYPE_ARCHIVE_ORDER = "archive"
    }

}
