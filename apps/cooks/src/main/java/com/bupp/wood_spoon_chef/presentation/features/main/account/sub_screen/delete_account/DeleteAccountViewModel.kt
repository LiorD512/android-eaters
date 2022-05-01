package com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.delete_account

import android.app.Activity
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.di.abs.LiveEventData
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.data.repositories.UserRepository
import com.bupp.wood_spoon_chef.utils.UserSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeleteAccountViewModel(
    private val userRepository: UserRepository,
    private val userSettings: UserSettings,
) : BaseViewModel() {

    val deleteSuccessEvent = LiveEventData<Boolean>()
    fun deleteAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = userRepository.deleteAccount()
            deleteSuccessEvent.postRawValue(true)
        }
    }

    fun logout(activity: Activity) {
        userSettings.logout(activity)
    }

}