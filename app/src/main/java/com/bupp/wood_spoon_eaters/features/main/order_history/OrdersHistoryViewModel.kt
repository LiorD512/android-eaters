package com.bupp.wood_spoon_eaters.features.main.order_history

//import com.bupp.wood_spoon_eaters.model.Report
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import com.bupp.wood_spoon_eaters.utils.MapSyncUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class OrdersHistoryViewModel(val orderRepository: OrderRepository, val eaterDataManager: EaterDataManager) : ViewModel() {

    private var isUpdating: Boolean = false
    private var refreshRepeatedJob: Job? = null
    val TAG = "wowOrderHistoryVM"

    private val orderListData: MutableMap<Int, MutableList<OrderHistoryBaseItem>> = mutableMapOf()//add skeleton ads default here

    val orderLiveData = MutableLiveData<List<OrderHistoryBaseItem>>()


    fun initList() {
        orderListData[SECTION_ACTIVE] = mutableListOf()
        orderListData[SECTION_ARCHIVE] = mutableListOf()
        orderLiveData.postValue(getSkeletonList().toMutableList())
    }

    fun fetchData() {
        initList()
        getArchivedOrders()
        getActiveOrders()
    }

    fun getSkeletonList(): MutableList<OrderAdapterItemSkeleton>{
        val skeletons = mutableListOf<OrderAdapterItemSkeleton>()
        for (i in 0 until 10){
            skeletons.add(OrderAdapterItemSkeleton())
        }
        return skeletons
    }

    fun getArchivedOrders() {
        viewModelScope.launch {
            val result = orderRepository.getAllOrders()
            when (result.type) {
                OrderRepository.OrderRepoStatus.GET_All_ORDERS_SUCCESS -> {
                    if (result.data != null && result.data.isNotEmpty()) {
                        updateArchivedOrders(result.data)
                        updateListData()
//                        val tempList = mutableListOf<OrderHistoryBaseItem>()
//                        tempList.add(OrderAdapterItemTitle("Past orders"))
//                        result.data.forEach {
//                            tempList.add(OrderAdapterItemOrder(it))
//                        }
//                        orderListData[SECTION_ARCHIVE]?.clear()
//                        orderListData[SECTION_ARCHIVE]?.addAll(tempList)
//                        updateListData()
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun updateArchivedOrders(newData: List<Order>) {
        val currentList = orderListData[SECTION_ARCHIVE]
//        if(currentList!!.isEmpty()){
//            currentList.add(OrderAdapterItemTitle("Past orders"))
//        }
        newData.forEach { order ->
            val itemInList = currentList!!.find { order.id == (it as OrderAdapterItemOrder).order.id }
            if(itemInList == null){
                currentList.add(OrderAdapterItemOrder(order))
            }else{
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
//                val tempList = mutableListOf<OrderHistoryBaseItem>()
//                it.forEach {
//                    tempList.add(OrderAdapterItemActiveOrder(it))
//                }
//                orderListData[SECTION_ACTIVE]?.clear()
//                orderListData[SECTION_ACTIVE]?.addAll(tempList)
            }
        }
    }

    private fun updateActiveOrders(newData: List<Order>) {
        val currentList = orderListData[SECTION_ACTIVE]!!
        newData.forEach { order ->
            val itemInList = currentList.find { order.id == (it as OrderAdapterItemActiveOrder).order.id }
            if(itemInList == null){
                currentList.add(OrderAdapterItemActiveOrder(order))
            }else{
                (itemInList as OrderAdapterItemActiveOrder).order = order
            }
        }
    }


//    private fun updateActiveOrders(newData: List<Order>) {
//        val currentList = orderListData[SECTION_ACTIVE]
//        currentList?.forEach { item ->
//            item as OrderAdapterItemActiveOrder
//            var adapterOrder = item.order
//            newData.forEach {
//                if(it.id == adapterOrder.id){
//                    adapterOrder = it
//                }
//            }
//        }
//    }

    private fun updateListData() {
        val finalList = mutableListOf<OrderHistoryBaseItem>()
        orderListData[SECTION_ACTIVE]?.let {
            finalList.addAll(it)
            startSilentUpdate()
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
                Log.d(TAG,"fetching FromServer")
                getActiveOrders()
                delay(10000)
            }
        }
    }

    fun startSilentUpdate() {
        if(refreshRepeatedJob == null){
            refreshRepeatedJob = repeatRequest()
        }
    }

    private fun endUpdates() {
        refreshRepeatedJob?.cancel()
    }

    override fun onCleared() {
        Log.d(TAG,"onCleared")
        endUpdates()
        super.onCleared()
    }



    companion object {
        const val TAG = "wowOrderHistoryVM"
        const val SECTION_ACTIVE = 0
        const val SECTION_ARCHIVE = 1
        const val TYPE_ACTIVE_ORDER = "active"
        const val TYPE_ARCHIVE_ORDER = "archive"
    }

}
