package com.bupp.wood_spoon_chef.presentation.features.main.account

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bupp.wood_spoon_chef.data.remote.model.Cook
import com.bupp.wood_spoon_chef.presentation.features.base.BaseViewModel
import com.bupp.wood_spoon_chef.data.repositories.MetaDataRepository
import com.bupp.wood_spoon_chef.data.repositories.UserRepository
import com.bupp.wood_spoon_chef.utils.UserSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountViewModel(
    private val appSettings: UserSettings,
    val metaDataRepository: MetaDataRepository,
    val userRepository: UserRepository
) : BaseViewModel(){

    val cookData: MutableLiveData<Cook?> = userRepository.getUserData()

    fun initData() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getCurrentChef(fetchFromServer = true)
        }
    }

    fun logout(activity: Activity) {
        appSettings.logout(activity)
    }

    fun getAdminMailAddress(): String {
        return metaDataRepository.getSupportEmailAddress()
    }

}