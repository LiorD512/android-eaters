package com.bupp.wood_spoon_eaters.features.main

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.utils.AppSettings

class MainViewModel(val settings: AppSettings): ViewModel(){

    fun isFirstTime(): Boolean{
        return settings.isFirstTime()
    }
}