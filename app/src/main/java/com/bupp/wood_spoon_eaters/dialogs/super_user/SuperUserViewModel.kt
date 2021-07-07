package com.bupp.wood_spoon_eaters.dialogs.super_user

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.common.FlavorConfigManager
import com.bupp.wood_spoon_eaters.repositories.UserRepository

class SuperUserViewModel(
    private val flavorConfig: FlavorConfigManager,
    private val userRepository: UserRepository
) : ViewModel() {

    fun setEnvironment(env: String) {
        flavorConfig.setEnvironment(env)
        userRepository.logout()
    }

}