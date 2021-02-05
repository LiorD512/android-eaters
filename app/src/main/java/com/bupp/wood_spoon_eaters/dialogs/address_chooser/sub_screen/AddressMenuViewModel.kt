package com.example.matthias.mvvmcustomviewexample.custom

import android.util.Log
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddressMenuViewModel: KoinComponent {

    var listener: EditAddressVMListener? = null
    interface EditAddressVMListener{
        fun onRemoveAddressDone(isSuccess: Boolean)
    }

    val api: ApiService by inject()
    val eaterDataManager: EaterDataManager by inject()

    fun removeAddress(id: Long, listener: EditAddressVMListener?) {
        this.listener = listener
//        api.deleteAddress(id).enqueue(object: Callback<ServerResponse<Void>> {
//            override fun onResponse(call: Call<ServerResponse<Void>>, response: Response<ServerResponse<Void>>) {
//                Log.d("wowEditAddressVM","deleteAddress success: ${response.isSuccessful}")
//                eaterDataManager.removeAddressById(id)
//                listener?.onRemoveAddressDone(response.isSuccessful)
//
//            }
//
//            override fun onFailure(call: Call<ServerResponse<Void>>, t: Throwable) {
//                Log.d("wowEditAddressVM","deleteAddress failed: ${t.message}")
//                listener?.onRemoveAddressDone(false)
//            }
//        })
    }


}