package com.bupp.wood_spoon_eaters.features.locations_and_address.address_list_chooser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.model.*
import com.bupp.wood_spoon_eaters.network.ApiService
import com.bupp.wood_spoon_eaters.common.AppSettings
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent

class AddressChooserViewModel(val api: ApiService, val settings: AppSettings, val eaterDataManager: EaterDataManager): ViewModel(), EaterDataManager.EaterDataMangerListener {

    data class AdapterAddress(val address: Address, val isSelected: Boolean = false)

    val addressChooserData = MutableLiveData<List<AdapterAddress>?>()
    fun initAddressChooser() {
        val data = mutableListOf<AdapterAddress>()
        val selectedAddress = eaterDataManager.getLastChosenAddress()
        eaterDataManager.currentEater?.addresses?.forEach { address ->
            if(address.id == selectedAddress?.id){
                data.add(AdapterAddress(address, true))
            }else{
                data.add(AdapterAddress(address))
            }
        }
        addressChooserData.postValue(data)
    }

    fun setChosenAddress(address: Address){
        eaterDataManager.setUserChooseSpecificAddress(true)
        eaterDataManager.setPreviousChosenAddress(eaterDataManager.getLastChosenAddress())
        eaterDataManager.setLastChosenAddress(address)
    }

    fun setNoAddress(){
        eaterDataManager.setUserChooseSpecificAddress(false)
        eaterDataManager.setLastChosenAddress(null)
    }

    data class AddressUpdateEvent(val currentAddress: Address?)
    val addressUpdateEvent: SingleLiveEvent<AddressUpdateEvent> = SingleLiveEvent()
    override fun onAddressChanged(updatedAddress: Address?) {
        addressUpdateEvent.postValue(AddressUpdateEvent(updatedAddress))
    }


}