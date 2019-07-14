package com.bupp.wood_spoon_eaters.features.main.delivery_details.sub_screens.add_new_address

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterAddressManager
import com.bupp.wood_spoon_eaters.managers.LocationManager
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.google.models.GoogleAddressResponse
import com.bupp.wood_spoon_eaters.utils.AppSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val DELIVERY_TO_DOOR_STRING = "Delivery to door"
const val PICK_UP_OUTSIDE_STRING = "Pick up outside"


class AddAddressViewModel(
    private val apiService: ApiService, private val appSettings: AppSettings,
    private val orderManager: OrderManager, private val eaterAddressManager: EaterAddressManager
) : ViewModel(),
    LocationManager.LocationManagerListener {


    data class MyLocationEvent(val myLocation: Address)
    data class NavigationEvent(val isSuccessful: Boolean = false, val address: Address?)

    val navigationEvent: SingleLiveEvent<NavigationEvent> = SingleLiveEvent()
    val myLocationEvent: SingleLiveEvent<MyLocationEvent> = SingleLiveEvent()

//    data class DeliveryDetailsEvent(var googleAddressResponse: GoogleAddressResponse, var address: Address, var isDelivery: Boolean)
//    val lastDeliveryDetails: SingleLiveEvent<DeliveryDetailsEvent> = SingleLiveEvent()

//    fun getCurrentDeliveryDetails(): CurrentDataEvent {
//        val googleAddressResponse = eaterAddressManager.getLastAddressResponse()
//        val address = orderManager.getLastOrderAddress()
//        val isDelivery = orderManager.isDelivery
//        return CurrentDataEvent(NewAddress(googleAddressResponse, address, isDelivery))
//    }

    fun fetchMyLocation() {
        val myLocation = eaterAddressManager.getCurrentAddress()
        if (myLocation != null) {
            appSettings.setUserChooseSpecificAddress(false)
            myLocationEvent.postValue(MyLocationEvent(myLocation))
        } else {
            eaterAddressManager.setLocationListener(this)
        }
    }

    override fun onLocationChanged(mLocation: Address) {
        myLocationEvent.postValue(MyLocationEvent(mLocation))
        eaterAddressManager.removeLocationListener(this)
    }

    fun postNewAddress(googleAddressResponse: GoogleAddressResponse? = null, myLocationAddress: Address? = null, address1stLine: String,
                       address2ndLine: String, deliveryNote: String, isDelivery: Boolean) {
        orderManager.updateOrder(googleAddressResponse = googleAddressResponse)
        var currentEater = appSettings.currentEater!!
        var notes = deliveryNote

        notes = prepareNotes(notes, isDelivery)

        var addressRequest = AddressRequest()
        if (googleAddressResponse != null) {
            addressRequest = parseGoogleResponse(googleAddressResponse, address1stLine, address2ndLine, notes)
        } else if (myLocationAddress != null){
            addressRequest = parseMyLocation(myLocationAddress)
        }

        var adresses = arrayListOf(addressRequest!!)

        var eaterRequest =
            EaterRequest(
                currentEater.firstName,
                currentEater.lastName,
                null,
                currentEater.email,
                adresses
            )

        //post EaterRequest
        postEater(eaterRequest)
    }

    private fun parseMyLocation(myLocationAddress: Address): AddressRequest {
        var addressRequest = AddressRequest()

        addressRequest.streetLine1 = myLocationAddress.streetLine1

        return addressRequest
    }

    private fun postEater(eater: EaterRequest) {
        apiService.postMe(eater).enqueue(object : Callback<ServerResponse<Eater>> {
            override fun onResponse(call: Call<ServerResponse<Eater>>, response: Response<ServerResponse<Eater>>) {
                if (response.isSuccessful) {
                    Log.d("wowAddNewAddressVM", "on success! ")
                    var eater = response.body()?.data!!
                    appSettings.currentEater = eater
                    eaterAddressManager.setLastChosenAddress(eater.addresses[0])
//                    orderManager.updateOrder(orderAddress = eater.addresses[0])
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

    private fun parseGoogleResponse(googleAddress: GoogleAddressResponse, streetLine1: String, streetLine2: String, notes: String): AddressRequest {
        var userAddress = AddressRequest()

        var addressComponents: List<GoogleAddressResponse.AddressComponentsItem>?
        if (googleAddress.results?.addressComponents != null) {
            addressComponents = googleAddress.results!!.addressComponents
        } else {
            addressComponents = googleAddress.ResultsItem().addressComponents
        }

        if (!addressComponents.isNullOrEmpty()) {
            var lat: Double? = 0.0
            var lng: Double? = 0.0

            if (googleAddress.Location().lat != 0.0 && googleAddress.Location().lng != 0.0) {
                lat = googleAddress.Location().lat
                lng = googleAddress.Location().lng
            } else {
                lat = googleAddress.results?.geometry?.location?.lat
                lng = googleAddress.results?.geometry?.location?.lng
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
        } else {

        }
        userAddress.notes = notes
        return userAddress
    }

    private fun getCityName(addrComponents: List<GoogleAddressResponse.AddressComponentsItem>?): String? {
        for (i in 0 until addrComponents!!.size) {
            if (addrComponents[i].types!![0] == "locality") {
                return addrComponents[i].longName!!
            }
        }
        return null
    }

    private fun getStateName(
        addrComponents: List<GoogleAddressResponse.AddressComponentsItem>?,
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

    private fun getCountry(addrComponents: List<GoogleAddressResponse.AddressComponentsItem>?): Pair<String?, String?> {
        for (i in 0 until addrComponents!!.size) {
            if (addrComponents[i].types!![0] == "country") {
                return Pair(addrComponents[i].longName!!, addrComponents[i].shortName!!)
            }
        }
        return Pair(null, null)
    }

    private fun getStreet(
        addrComponents: List<GoogleAddressResponse.AddressComponentsItem>?,
        streetName: String
    ): String {
        for (i in 0 until addrComponents!!.size) {
            if (addrComponents[i].types!![0] == "route") {
                return addrComponents[i].longName!!
            }
        }
        return streetName
    }

    private fun getZipCode(addrComponents: List<GoogleAddressResponse.AddressComponentsItem>?): String? {
        for (i in 0 until addrComponents!!.size) {
            if (addrComponents[i].types!![0] == "postal_code") {
                return addrComponents[i].longName!!
            }
        }
        return null
    }

    fun prepareNotes(notes: String, isDelivery: Boolean): String {
        if (notes.isNotEmpty()) {
            when {
                notes.endsWith(DELIVERY_TO_DOOR_STRING) -> {
                    val indexOfSuffix = notes.indexOf(DELIVERY_TO_DOOR_STRING)
                    return if (isDelivery) {
                        notes.replaceRange(indexOfSuffix, notes.length, DELIVERY_TO_DOOR_STRING)
                    } else {
                        notes.replaceRange(indexOfSuffix, notes.length, PICK_UP_OUTSIDE_STRING)
                    }
                }
                notes.endsWith(PICK_UP_OUTSIDE_STRING) -> {
                    val indexOfSuffix = notes.indexOf(PICK_UP_OUTSIDE_STRING)
                    return if (isDelivery) {
                        notes.replaceRange(indexOfSuffix, notes.length, DELIVERY_TO_DOOR_STRING)
                    } else {
                        notes.replaceRange(indexOfSuffix, notes.length, PICK_UP_OUTSIDE_STRING)
                    }
                }
                else -> return if (isDelivery) {
                    ", $DELIVERY_TO_DOOR_STRING"
                } else {
                    ", $PICK_UP_OUTSIDE_STRING"
                }
            }

        } else {
            return if (isDelivery) {
                DELIVERY_TO_DOOR_STRING
            } else {
                PICK_UP_OUTSIDE_STRING
            }
        }
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