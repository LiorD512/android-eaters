package com.bupp.wood_spoon_eaters.features.active_orders_tracker

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import io.reactivex.disposables.Disposable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ActiveOrderTrackerViewModel(val api: ApiService, val eaterDataManager: EaterDataManager) : ViewModel() {


    private val compositeDisposable: ArrayList<Disposable> = arrayListOf()
    private var disposable: Disposable? = null
    val orderDetails: SingleLiveEvent<OrderDetailsEvent> = SingleLiveEvent()

    data class OrderDetailsEvent(/*val order: Order,*/val orderProgress: Int, val arrivalTime:String, val isNewMsgs:Boolean)

    fun getShareText(): String {
        val inviteUrl = eaterDataManager.currentEater?.shareCampaign?.inviteUrl
        val text = eaterDataManager.currentEater?.shareCampaign?.shareText
        return "$text \n $inviteUrl"
    }

    private fun fetchFromServer(emitter: ObservableEmitter<String>) {
        Log.d("wowActiveOrderTrackerVM", "fetchFromServer start")
        api.getTrackableOrders().enqueue(object : Callback<ServerResponse<ArrayList<Order>>> {
            override fun onResponse(call: Call<ServerResponse<ArrayList<Order>>>, response: Response<ServerResponse<ArrayList<Order>>>) {
                if (response.isSuccessful) {
                    Log.d("wowActiveOrderTrackerVM", "getTrackableOrders success")
                    val activeOrders = response.body()!!.data
                    if(activeOrders != null && activeOrders.size > 0){
                        getActiveOrders.postValue(GetActiveOrdersEvent(true, activeOrders))
                    }else{
                        getActiveOrders.postValue(GetActiveOrdersEvent(false, null))
                    }
                    emitter.onNext("onNext Update")
                    emitter.onComplete()
                } else {
                    Log.d("wowActiveOrderTrackerVM", "getTrackableOrders fail")
                    getActiveOrders.postValue(GetActiveOrdersEvent(false, null))
                }
            }

            override fun onFailure(call: Call<ServerResponse<ArrayList<Order>>>, t: Throwable) {
                Log.d("wowActiveOrderTrackerVM", "getTrackableOrders big fail")
                getActiveOrders.postValue(GetActiveOrdersEvent(false, null))
            }
        })
    }


    fun startSilentUpdate() {
        val disposable =
            Observable.interval(0, 20, TimeUnit.SECONDS)
                .flatMap {
                    return@flatMap Observable.create<String> { emitter ->
                        Log.d("wowActiveOrderTrackerVM", "looking for order updates...")
                        fetchFromServer(emitter)
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("wowActiveOrderTrackerVM", it)
                }
        compositeDisposable.add(disposable)
    }

    fun endUpdates(){
        for(item in compositeDisposable){
            item.dispose()
        }
    }




    val getActiveOrders: SingleLiveEvent<GetActiveOrdersEvent> = SingleLiveEvent()
    data class GetActiveOrdersEvent(val isSuccess: Boolean, val orders: ArrayList<Order>?)

}