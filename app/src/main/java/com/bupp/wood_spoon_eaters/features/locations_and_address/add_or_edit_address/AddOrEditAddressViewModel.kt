package com.bupp.wood_spoon_eaters.features.locations_and_address.add_or_edit_address

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.network.google.models.GoogleAddressResponse
import com.bupp.wood_spoon_eaters.common.AppSettings
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


const val DELIVERY_TO_DOOR_STRING = "Delivery to door"
const val PICK_UP_OUTSIDE_STRING = "Pick up outside"


class AddOrEditAddressViewModel(private val apiService: ApiService, private val eaterDataManager: EaterDataManager, private val appSettings: AppSettings) :
    ViewModel(), EaterDataManager.EaterDataMangerListener {

    companion object{
        const val TAG = "wowAddOrEditAddressVM"
    }

    val editAddressEvent = MutableLiveData<Address>()
    fun initAddOrEdit(address: Address?) {
        address?.let{
            editAddressEvent.postValue(it)
        }
    }

    fun getLocationLiveData() = eaterDataManager.getFinalAddressLiveData()

    data class MyLocationEvent(val myLocation: Address)
    val myLocationEvent: SingleLiveEvent<MyLocationEvent> = SingleLiveEvent()

    fun fetchMyLocation() {
        val myLocation = eaterDataManager.getFinalAddressLiveData().value
        if (myLocation != null) {
            eaterDataManager.setUserChooseSpecificAddress(false)
            myLocationEvent.postValue( MyLocationEvent(myLocation) )
        } else {
            Log.d(TAG, "no location found")
        }
    }

    fun saveNewAddress() {

    }
















    data class NavigationEvent(val isSuccessful: Boolean = false, val addressStreetStr: String?)
    val updateAddressEvent: SingleLiveEvent<NavigationEvent> = SingleLiveEvent()

    override fun onAddressChanged(currentAddress: Address?) {
        if (currentAddress != null) {
            myLocationEvent.postValue(MyLocationEvent(currentAddress))
        }
    }

    fun postNewAddress(
        googleAddressResponse: GoogleAddressResponse? = null, myLocationAddress: Address? = null, address1stLine: String,
        address2ndLine: String, deliveryNote: String, isDelivery: Boolean, currentAddressId: Long? = null
    ) {
        val currentEater = eaterDataManager.currentEater
        Log.d("wowAddressBug", "googleAddresRes: ${googleAddressResponse?.status} ")
        currentEater?.let {
            val notes = deliveryNote

            var addressRequest = AddressRequest()

            addressRequest.dropoffLocation = isDelivery.getDropoffLocationStr()

            if (googleAddressResponse != null) {
                Log.d("wowAddressBug", "googleAddresRes1")

                addressRequest = parseGoogleResponse(googleAddressResponse, address1stLine, address2ndLine, notes)
            } else if (myLocationAddress != null) {
                Log.d("wowAddressBug", "googleAddresRes2")
                addressRequest = parseMyLocation(myLocationAddress)
            }

            val adresses = arrayListOf(addressRequest)
            Log.d("wowAddressBug", "googleAddresRes3 ${adresses}")


            if (currentAddressId != null) {
                //update Address
                Log.d("wowAddressBug", "googleAddresRes 4")
                updateAddress(currentAddressId, addressRequest)
            } else {
                Log.d("wowAddressBug", "googleAddresRes 5")
                //post EaterRequest
                var eaterRequest =
                    EaterRequest(
                        it.firstName,
                        it.lastName,
                        null,
                        it.email,
                        adresses
                    )
                postEater(eaterRequest)
            }
        }

    }

    //weird example of kotlin structure. behold.
    private fun Boolean.getDropoffLocationStr(): String? = if (this) {
        "delivery_to_door"
    } else {
        "pickup_outside"
    }

    private fun updateAddress(currentAddressId: Long, addressRequest: AddressRequest) {
        apiService.updateAddress(currentAddressId, addressRequest).enqueue(object : Callback<ServerResponse<Address>> {
            override fun onResponse(call: Call<ServerResponse<Address>>, response: Response<ServerResponse<Address>>) {
                if (response.isSuccessful) {
                    Log.d("wowAddNewAddressVM", "updateAddress success! ")
                    val newAddress = response.body()?.data
                    eaterDataManager.updateAddressById(currentAddressId, newAddress)
                    eaterDataManager.setLastChosenAddress(newAddress)
                    updateAddressEvent.postValue(NavigationEvent(true, newAddress?.streetLine1))
                } else {
                    Log.d("wowAddNewAddressVM", "updateAddress Failure! ")
                    updateAddressEvent.postValue(NavigationEvent(false, null))
                }
            }

            override fun onFailure(call: Call<ServerResponse<Address>>, t: Throwable) {
                Log.d("wowAddNewAddressVM", "updateAddress big Failure! " + t.message)
                updateAddressEvent.postValue(NavigationEvent(false, null))
            }
        })
    }

    private fun parseMyLocation(myLocationAddress: Address): AddressRequest {
        var addressRequest = AddressRequest()

        addressRequest.lat = myLocationAddress.lat
        addressRequest.lng = myLocationAddress.lng
        addressRequest.streetLine1 = myLocationAddress.streetLine1
        addressRequest.streetLine2 = myLocationAddress.streetLine2

        return addressRequest
    }

    private fun postEater(eater: EaterRequest) {
        //todo - nyc change
//        apiService.postMe(eater).enqueue(object : Callback<ServerResponse<Eater>> {
//            override fun onResponse(call: Call<ServerResponse<Eater>>, response: Response<ServerResponse<Eater>>) {
//                if (response.isSuccessful) {
//                    Log.d("wowAddNewAddressVM", "on success! ")
//                    var eater = response.body()?.data!!
//                    eaterDataManager.currentEater = eater
//                    eaterDataManager.setLastChosenAddress(eater.addresses.last())
////                    orderManager.updateOrder(orderAddress = eater.addresses[0])
//                    val address = eater.addresses.last()
//                    updateAddressEvent.postValue(NavigationEvent(true, address.streetLine1))
//                } else {
//                    Log.d("wowAddNewAddressVM", "on Failure! ")
//                    updateAddressEvent.postValue(NavigationEvent(false, null))
//                }
//            }
//
//            override fun onFailure(call: Call<ServerResponse<Eater>>, t: Throwable) {
//                Log.d("wowAddNewAddressVM", "on big Failure! " + t.message)
//                updateAddressEvent.postValue(NavigationEvent(false, null))
//            }
//        })
    }

    private fun parseGoogleResponse(googleAddress: GoogleAddressResponse, streetLine1: String, streetLine2: String, notes: String): AddressRequest {
        var userAddress = AddressRequest()

        var addressComponents: List<GoogleAddressResponse.AddressComponentsItem>?
        if (googleAddress.results?.addressComponents != null) {
            Log.d("wowAddressBug", "googleAddresRes 1.1")
            addressComponents = googleAddress.results!!.addressComponents
        } else {
            Log.d("wowAddressBug", "googleAddresRes 1.2")
            addressComponents = googleAddress.ResultsItem().addressComponents
        }

        if (!addressComponents.isNullOrEmpty()) {
            Log.d("wowAddressBug", "googleAddresRes 1.3")
            var lat: Double? = 0.0
            var lng: Double? = 0.0

            Log.d("wowAddressBug", "googleAddresRes 1.3.1 - ")
            Log.d("wowAddressBug", "googleAddress.Location().lat - ${googleAddress.Location().lat} ")
            Log.d("wowAddressBug", "googleAddress.Location().lng - ${googleAddress.Location().lng} ")
            val gson = Gson()
            val jsonString = gson.toJson(googleAddress.results)
            Log.d("wowAddressBug", "googleAddress.results - ${jsonString} ")
            val locationString = gson.toJson(googleAddress.Location())
            Log.d("wowAddressBug", "googleAddress.Location - ${locationString} ")
//            Log.d("wowAddressBug","googleAddress.results?.geometry?.location?.lat - ${googleAddress.results?.geometry?.location?.lat} ")
//            Log.d("wowAddressBug","googleAddress.results?.geometry?.location?.lng - ${googleAddress.results?.geometry?.location?.lng} ")
            if (googleAddress.Location().lat != 0.0 && googleAddress.Location().lng != 0.0) {
                Log.d("wowAddressBug", "googleAddresRes 1.4")
                lat = googleAddress.Location().lat
                lng = googleAddress.Location().lng
            } else {
                Log.d("wowAddressBug", "googleAddresRes 1.5")
                googleAddress.results?.let {
                    val geometry = it.geometry
                    val geometryStr = gson.toJson(geometry).toString()
                    Log.d("wowAddressBug", "geometry ${geometryStr}")
                    val convertedObject: GoogleAddressResponse.Geometry = gson.fromJson(geometryStr, GoogleAddressResponse.Geometry::class.java)
                    Log.d("wowAddressBug", "geometry ${convertedObject}")
                    convertedObject.location?.let {
                        Log.d("wowAddressBug", "googleAddresRes 1.5.1")
                        it.let {
                            Log.d("wowAddressBug", "googleAddresRes 1.5.2")
                            lat = it.lat
                            lng = it.lng
                        }
                    }
                    Log.d("wowAddressBug", "googleAddresRes 1.5.3")
                }
            }

//            var lat: Double? = 0.0
//            var lng: Double? = 0.0
//
//            if (googleAddress.Location().lat != 0.0 && googleAddress.Location().lng != 0.0) {
//                lat = googleAddress.Location().lat
//                lng = googleAddress.Location().lng
//            } else {
//                lat = googleAddress.results?.geometry?.location?.lat
//                lng = googleAddress.results?.geometry?.location?.lng
//            }


            var countryNames = getCountry(addressComponents)
            var countryName = countryNames.first
            var countryIso = countryNames.second


            var stateNames = getStateName(addressComponents, countryName, countryIso)
            var stateIso = stateNames.second

//            var streetLine1 = streetLine1
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
            Log.d("wowAddressBug", "googleAddresRes 1.6")

        }
        userAddress.notes = notes
        return userAddress
    }

    private fun getCityName(addrComponents: List<GoogleAddressResponse.AddressComponentsItem>?): String? {
        for (i in 0 until addrComponents!!.size) {
            if (addrComponents[i].types!![0] == "locality") {
                return addrComponents[i].longName!!
            } else {
                addrComponents[i].types?.let {
                    if (it.size > 1) {
                        if (addrComponents[i].types!![1] == "sublocality") {
                            return addrComponents[i].longName!!
                        }
                    }
                }
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

    private fun getStreet(addrComponents: List<GoogleAddressResponse.AddressComponentsItem>?, streetName: String): String {
        var streetNumber = ""
        var route = ""
        var locality = ""
        var country = ""
        for (i in 0 until addrComponents!!.size) {
            val component = addrComponents[i]
            if (component != null) {
                when (component.types!![0]) {
                    "street_number" -> {
                        streetNumber = component.longName!!
                    }
                    "route" -> {
                        route = component.longName!!
                    }
                    "locality" -> {
                        locality = component.longName!!
                    }
                    "country" -> {
                        country = component.longName!!
                    }
                }
            }
        }
        return "$streetNumber $route"//, $locality, $country"
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

    fun isLocationEnabled(): Boolean {
        return appSettings.shouldEnabledUserLocation
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