package com.bupp.wood_spoon_eaters.features.locations_and_address.address_verification_map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.common.AppSettings
import com.bupp.wood_spoon_eaters.di.abs.LiveEventData
import com.bupp.wood_spoon_eaters.model.AddressRequest
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.utils.LocationUtils
import com.google.android.gms.maps.model.LatLng

class AddressMapVerificationViewModel(val metaDataRepository: MetaDataRepository, val settings: AppSettings) : ViewModel() {

    var anchorLatLng: LatLng? = null
    fun setAnchorLocation(address: AddressRequest) {
        //this is the LatLng of the google autocomplete result
        address.lat?.let { lat ->
            address.lng?.let { lng ->
                anchorLatLng = LatLng(lat, lng)
            }
        }
    }

    enum class AddressMapVerificationStatus {
        CORRECT,
        WRONG,
        SHAKE,
    }

    val vibrateEvent = LiveEventData<Boolean>()
    val addressMapVerificationDoneEvent = LiveEventData<Boolean>()
    val addressMapVerificationStatus = MutableLiveData<AddressMapVerificationStatus>()
    fun checkCenterLatLngPosition(currentLatLng: LatLng) {
        anchorLatLng?.let { anchorLatLng ->
            val isInRadius = LocationUtils.isLocationsNear(
                anchorLatLng.latitude, anchorLatLng.longitude,
                currentLatLng.latitude, currentLatLng.longitude, metaDataRepository.getLocationDistanceThreshold()
            )
            if (isInRadius) {
                addressMapVerificationStatus.postValue(AddressMapVerificationStatus.CORRECT)
            } else if (addressMapVerificationStatus.value != AddressMapVerificationStatus.WRONG && addressMapVerificationStatus.value != AddressMapVerificationStatus.SHAKE) {
                addressMapVerificationStatus.postValue(AddressMapVerificationStatus.WRONG)
                vibrateEvent.postRawValue(true)
            }
        }
    }

    fun onMapVerificationDoneClick() {
        if (addressMapVerificationStatus.value == AddressMapVerificationStatus.CORRECT) {
            addressMapVerificationDoneEvent.postRawValue(true)
        } else {
            addressMapVerificationStatus.postValue(AddressMapVerificationStatus.SHAKE)
            vibrateEvent.postRawValue(true)
        }
    }

    val redirectToMyLocation = MutableLiveData<LatLng?>()
    fun redirectToMyLocation() {
        anchorLatLng?.let{
            redirectToMyLocation.postValue(it)
        }
    }


}
