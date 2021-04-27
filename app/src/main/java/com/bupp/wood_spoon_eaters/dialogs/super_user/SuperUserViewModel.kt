package com.bupp.wood_spoon_chef.dialogs.super_user

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.common.FlavorConfigManager

class SuperUserViewModel(
    val flavorConfig: FlavorConfigManager,
) : ViewModel() {

    fun setEnvironment(env: String) {
        flavorConfig.setEnvironment(env)
    }

}