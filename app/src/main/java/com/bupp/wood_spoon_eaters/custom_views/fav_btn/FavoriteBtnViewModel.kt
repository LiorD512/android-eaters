package com.bupp.wood_spoon_eaters.custom_views.fav_btn

import android.util.Log
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteBtnViewModel: KoinComponent {

    var listener: FavVMListener? = null
    interface FavVMListener{
        fun onFail()
    }

    fun setFavListener(listener: FavVMListener?){
        this.listener = listener
    }

    val api: ApiService by inject()

    fun onClick(dishId: Long?, favSelected: Boolean) {
        Log.d("wow","fav btn clicked ! dishId: $dishId")
        if(dishId == null)
            return

        if(favSelected){
            likeDish(dishId)
        }else{
            unlikeDish(dishId)
        }
    }

    fun likeDish(id: Long) {
        api.likeDish(id).enqueue(object: Callback<ServerResponse<Any>> {
            override fun onResponse(call: Call<ServerResponse<Any>>, response: Response<ServerResponse<Any>>) {
                Log.d("wowFavVM","likeDish success: ${response.isSuccessful}")
                if(!response.isSuccessful){
                    listener?.onFail()
                }
            }

            override fun onFailure(call: Call<ServerResponse<Any>>, t: Throwable) {
                Log.d("wowFavVM","likeDish failed: ${t.message}")
                listener?.onFail()
            }
        })
    }

    fun unlikeDish(id: Long) {
        api.unlikeDish(id).enqueue(object: Callback<ServerResponse<Any>> {
            override fun onResponse(call: Call<ServerResponse<Any>>, response: Response<ServerResponse<Any>>) {
                Log.d("wowFavVM","unlikeDish success: ${response.isSuccessful}")
                if(!response.isSuccessful){
                    listener?.onFail()
                }
            }

            override fun onFailure(call: Call<ServerResponse<Any>>, t: Throwable) {
                Log.d("wowFavVM","unlikeDish failed: ${t.message}")
                listener?.onFail()
            }

        })
    }
}