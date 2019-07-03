package com.bupp.wood_spoon_eaters.dialogs

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.network.google.interfaces.GoogleApi
import com.taliazhealth.predictix.network_google.models.google_api.AddressIdResponse
import com.bupp.wood_spoon_eaters.network.google.models.AddressResponse
import retrofit2.Call
import retrofit2.Response

class LocationChooserViewModel(val googleApi: GoogleApi) : ViewModel() {

    val addressIdResponse: MutableLiveData<AddressIdResponse> = SingleLiveEvent()
    val addressResponse: MutableLiveData<AddressResponse> = SingleLiveEvent()

    val queryEvent: SingleLiveEvent<QueryEvent> = SingleLiveEvent()

    data class QueryEvent(val response: AddressIdResponse?, val query: String)

    fun search(inputStr: String) {
        googleApi?.getAddressId(inputStr)!!.enqueue(object : retrofit2.Callback<AddressIdResponse> {

            override fun onResponse(call: Call<AddressIdResponse>, response: Response<AddressIdResponse>) {
                if (response.isSuccessful) {
                    queryEvent.postValue(QueryEvent(response.body(), inputStr))
                }
            }

            override fun onFailure(call: Call<AddressIdResponse>, t: Throwable) {
                Log.d("wowGoogleApi", "onFailure! " + t.message)
            }
        })

    }

    fun fetchAddress(placeId: String?) {
        googleApi.getAddress(placeId!!).enqueue(object : retrofit2.Callback<AddressResponse> {

            override fun onResponse(call: Call<AddressResponse>, response: Response<AddressResponse>) {
                if (response.isSuccessful) {
                    addressResponse.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<AddressResponse>, t: Throwable) {
                Log.d("wowGoogleApi", "onFailure! " + t.message)
            }
        })
    }

}