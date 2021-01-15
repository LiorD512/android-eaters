package com.bupp.wood_spoon_eaters.features.locations_and_address

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.bupp.wood_spoon_eaters.features.base.SingleLiveEvent
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.EventsManager
import com.bupp.wood_spoon_eaters.common.AppSettings
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.model.Address
import androidx.lifecycle.ViewModel
import java.util.*

class LocationAndAddressViewModel : ViewModel() {

    val actionEvent = MutableLiveData<ActionEvent>()

    enum class ActionEvent {
        SAVE_NEW_ADDRESS,
    }

    data class NavigationEvent(val type: NavigationEventType, val address: Address? = null)

    val navigationEvent = MutableLiveData<NavigationEvent>()

    enum class NavigationEventType {
        OPEN_EDIT_ADDRESS_SCREEN,
        OPEN_ADDRESS_LIST_CHOOSER,
        OPEN_ADD_NEW_ADDRESS_SCREEN,
        DONE_WITH_LOCATION_AND_ADDRESS
    }

    fun onChangeLocationClick() {
        navigationEvent.postValue(NavigationEvent(NavigationEventType.OPEN_ADDRESS_LIST_CHOOSER))
    }

    fun onAddressChooserDone() {
        navigationEvent.postValue(NavigationEvent(NavigationEventType.DONE_WITH_LOCATION_AND_ADDRESS))
    }

    fun onEditAddressClick(address: Address) {
        navigationEvent.postValue(NavigationEvent(NavigationEventType.OPEN_EDIT_ADDRESS_SCREEN, address))
    }

    fun onAddNewAddressClick() {
        navigationEvent.postValue(NavigationEvent(NavigationEventType.OPEN_ADD_NEW_ADDRESS_SCREEN))
    }

    fun onSaveNewAddressClick() {
        actionEvent.postValue(ActionEvent.SAVE_NEW_ADDRESS)
    }

    fun checkIntentParam(intent: Intent?) {
        intent?.let {
            if (it.hasExtra(Constants.START_WITH)) {
                when (it.getIntExtra(Constants.START_WITH, Constants.NOTHING)) {
                    Constants.START_WITH_ADDRESS_CHOOSER -> {
                        navigationEvent.postValue(NavigationEvent(NavigationEventType.OPEN_ADDRESS_LIST_CHOOSER))
                    }
                }
            }
        }
    }


}
