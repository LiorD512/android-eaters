package com.bupp.wood_spoon_chef.presentation.features.base

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_chef.di.abs.LiveEventData
import com.bupp.wood_spoon_chef.di.abs.ProgressData
import com.bupp.wood_spoon_chef.data.remote.network.base.MTError

abstract class BaseViewModel: ViewModel() {

    val errorEvent = LiveEventData<MTError>()
    val progressData = ProgressData()

}