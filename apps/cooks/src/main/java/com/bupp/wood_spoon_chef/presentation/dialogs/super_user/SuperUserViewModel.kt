package com.bupp.wood_spoon_chef.presentation.dialogs.super_user

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_chef.common.FlavorConfigManager

class SuperUserViewModel(
    private val flavorConfig: FlavorConfigManager,
) : ViewModel() {

    fun setEnvironment(env: String) {
        flavorConfig.setEnvironment(env)
    }

}