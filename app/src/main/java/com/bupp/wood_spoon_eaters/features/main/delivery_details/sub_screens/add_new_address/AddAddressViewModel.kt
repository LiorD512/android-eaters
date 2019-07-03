package com.bupp.wood_spoon_eaters.features.main.delivery_details.sub_screens.add_new_address

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.utils.AppSettings
import com.bupp.wood_spoon_eaters.network.google.models.AddressResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddAddressViewModel(
    private val apiService: ApiService,
    private val appSettings: AppSettings,
    private val orderManager: OrderManager
) : ViewModel() {

    data class CurrentDataEvent(val currentNewAddress: NewAddress)
    //    data class CurrentDataEvent(val address: Address?, val isDelivery: Boolean?)
    data class NavigationEvent(val isSuccessful: Boolean = false, val address: Address?)

    val navigationEvent: SingleLiveEvent<NavigationEvent> = SingleLiveEvent()

//    data class DeliveryDetailsEvent(var addressResponse: AddressResponse, var address: Address, var isDelivery: Boolean)
//    val lastDeliveryDetails: SingleLiveEvent<DeliveryDetailsEvent> = SingleLiveEvent()

    fun getCurrentDeliveryDetails(): CurrentDataEvent {
        val addressResponse = orderManager.getLastAddressResponse()
        val address = orderManager.getLastOrderAddress()
        val isDelivery = orderManager.isDelivery
        return CurrentDataEvent(NewAddress(addressResponse, address, isDelivery))
    }

    fun postNewAddress(
        addressResponse: AddressResponse,
        address1stLine: String,
        address2ndLine: String,
        deliveryNote: String,
        isDelivery: Boolean
    ) {
        orderManager.updateOrder(addressResponse = addressResponse)
        var currentEater = appSettings.currentEater!!
        var notes = deliveryNote
        notes += if (isDelivery) {
            ", Delivery to door"
        } else {
            ", Pick up outside"
        }

        var addressRequest = getAddress(addressResponse, address1stLine, address2ndLine, notes)

        var adresses = arrayListOf(addressRequest!!)

        //todo - thumbnail
        var eaterRequest =
            EaterRequest(currentEater.firstName, currentEater.lastName, "empty", currentEater.email, adresses)

        //post EaterRequest
        postEater(eaterRequest)
    }

    private fun postEater(eater: EaterRequest) {
        apiService.postMe(eater).enqueue(object : Callback<ServerResponse<Eater>> {
            override fun onResponse(call: Call<ServerResponse<Eater>>, response: Response<ServerResponse<Eater>>) {
                if (response.isSuccessful) {
                    Log.d("wowAddNewAddressVM", "on success! ")
                    var eater = response.body()?.data!!
                    appSettings.currentEater = eater
                    orderManager.updateOrder(orderAddress = eater.addresses[0])
                    navigationEvent.postValue(NavigationEvent(true, eater.addresses[0]))
                } else {
                    Log.d("wowAddNewAddressVM", "on Failure! ")
                    navigationEvent.postValue(NavigationEvent(false, null))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Eater>>, t: Throwable) {
                Log.d("wowAddNewAddressVM", "on big Failure! " + t.message)
                navigationEvent.postValue(NavigationEvent(false, null))
            }
        })
    }

    private fun getAddress(address: AddressResponse, streetLine1: String, streetLine2: String, notes: String): AddressRequest? {
        var userAddress: AddressRequest = AddressRequest()

        if (address != null) {

            var addressComponents: List<AddressResponse.AddressComponentsItem>?
            if (address.results?.addressComponents != null) {
                addressComponents = address.results!!.addressComponents
            } else {
                addressComponents = address.ResultsItem().addressComponents
            }

            if (!addressComponents.isNullOrEmpty()) {
                var lat: Double? = 0.0
                var lng: Double? = 0.0

                if (address.Location().lat != 0.0 && address.Location().lng != 0.0) {
                    lat = address.Location().lat
                    lng = address.Location().lng
                } else {
                    lat = address.results?.geometry?.location?.lat
                    lng = address.results?.geometry?.location?.lng
                }

                var countryNames = getCountry(addressComponents)
                var countryName = countryNames.first
                var countryIso = countryNames.second


                var stateNames = getStateName(addressComponents, countryName, countryIso)
                var stateIso = stateNames.second

                var streetLine1 = getStreet(addressComponents, streetLine1)
                var cityName = getCityName(addressComponents)
                var zipCode = getZipCode(addressComponents)

                userAddress.streetLine1 = streetLine1
                userAddress.streetLine2 = streetLine2
                userAddress.countryIso = countryIso
                userAddress.stateIso = stateIso
                userAddress.cityName = cityName
                userAddress.zipCode = zipCode
                userAddress.lat = lat
                userAddress.lng = lng
            }else{

            }
            userAddress.notes = notes
            return userAddress
        } else return null
    }

    private fun getCityName(addrComponents: List<AddressResponse.AddressComponentsItem>?): String? {
        for (i in 0 until addrComponents!!.size) {
            if (addrComponents[i].types!![0] == "locality") {
                return addrComponents[i].longName!!
            }
        }
        return null
    }

    private fun getStateName(
        addrComponents: List<AddressResponse.AddressComponentsItem>?,
        countryName: String?,
        countryIso: String?
    ): Pair<String?, String?> {
        if (countryIso == "US") {
            for (i in 0 until addrComponents!!.size) {
                if (addrComponents[i].types!![0] == "administrative_area_level_1") {
                    return Pair(addrComponents[i].longName!!, addrComponents[i].shortName!!)
                }
            }
        }
        return Pair(countryName, countryIso)
    }

    private fun getCountry(addrComponents: List<AddressResponse.AddressComponentsItem>?): Pair<String?, String?> {
        for (i in 0 until addrComponents!!.size) {
            if (addrComponents[i].types!![0] == "country") {
                return Pair(addrComponents[i].longName!!, addrComponents[i].shortName!!)
            }
        }
        return Pair(null, null)
    }

    private fun getStreet(addrComponents: List<AddressResponse.AddressComponentsItem>?, streetName: String): String {
        for (i in 0 until addrComponents!!.size) {
            if (addrComponents[i].types!![0] == "route") {
                return addrComponents[i].longName!!
            }
        }
        return streetName
    }

    private fun getZipCode(addrComponents: List<AddressResponse.AddressComponentsItem>?): String? {
        for (i in 0 until addrComponents!!.size) {
            if (addrComponents[i].types!![0] == "postal_code") {
                return addrComponents[i].longName!!
            }
        }
        return null
    }

//    fun getDeliveryTime(): String? {
//        if(orderManager.orderTime != null){
//            return Utils.parseTime(orderManager.orderTime)
//        }
//        return "ASAP"
//    }
//
//    fun getDeliveryAddress(): String? {
//        return ""//orderManager.selectedAddress?.results!!.formattedAddress!!
//    }


}