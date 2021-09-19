package com.bupp.wood_spoon_eaters.bottom_sheets.address_menu

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressViewModel
import com.bupp.wood_spoon_eaters.managers.EaterDataManager
import com.bupp.wood_spoon_eaters.managers.FeedDataManager
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.ErrorEventType
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import kotlinx.coroutines.launch


class AddressMenuViewModel(
    private val userRepository: UserRepository,
    private val eaterDataManager: EaterDataManager,
    private val feedDataManager: FeedDataManager
) : ViewModel() {

    val errorEvents: MutableLiveData<ErrorEventType> = MutableLiveData()
    val navigationEvent = MutableLiveData<NavigationEventType>()
    enum class NavigationEventType {
        ADDRESS_MENU_DONE
    }
    val currentAddress = MutableLiveData<Address>()

    fun setCurrentAddress(address: Address) {
        currentAddress.postValue(address)
    }

    fun deleteCurrentAddress() {
        currentAddress.value?.let{
            viewModelScope.launch {
                val userRepoResult = userRepository.deleteAddress(it)
    //            progressData.endProgress()
                when (userRepoResult.type) {
                    UserRepository.UserRepoStatus.SERVER_ERROR -> {
                        Log.d(LocationAndAddressViewModel.TAG, "NetworkError")
                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                    }
                    UserRepository.UserRepoStatus.SOMETHING_WENT_WRONG -> {
                        Log.d(LocationAndAddressViewModel.TAG, "GenericError")
                        errorEvents.postValue(ErrorEventType.SOMETHING_WENT_WRONG)
                    }
                    UserRepository.UserRepoStatus.SUCCESS -> {
                        Log.d(LocationAndAddressViewModel.TAG, "Success")
                        eaterDataManager.updateSelectedAddress(null)
                        feedDataManager.initFeedDataManager()
                        navigationEvent.postValue(NavigationEventType.ADDRESS_MENU_DONE)
                    }
                    else -> {
                        Log.d(LocationAndAddressViewModel.TAG, "NetworkError")
                        errorEvents.postValue(ErrorEventType.SERVER_ERROR)
                    }
                }
            }
        }
    }
}