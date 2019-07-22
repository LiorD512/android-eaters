package com.example.matthias.mvvmcustomviewexample.custom

import android.util.Log
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditAddressViewModel: KoinComponent {

    var listener: EditAddressVMListener? = null
    interface EditAddressVMListener{
        fun onDone(isSuccess: Boolean)
    }

    val api: ApiService by inject()

    fun removeAddress(id: Long, listener: EditAddressVMListener?) {
        this.listener = listener
        api.deleteAddress(id).enqueue(object: Callback<ServerResponse<Void>> {
            override fun onResponse(call: Call<ServerResponse<Void>>, response: Response<ServerResponse<Void>>) {
                Log.d("wowFavVM","likeDish success: ${response.isSuccessful}")
                    listener?.onDone(response.isSuccessful)

            }

            override fun onFailure(call: Call<ServerResponse<Void>>, t: Throwable) {
                Log.d("wowFavVM","likeDish failed: ${t.message}")
                listener?.onDone(false)
            }
        })
    }


}