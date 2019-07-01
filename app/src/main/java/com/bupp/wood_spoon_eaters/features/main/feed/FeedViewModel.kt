package com.bupp.wood_spoon_eaters.features.main.feed

import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.utils.AppSettings

class FeedViewModel(val settings: AppSettings): ViewModel(){

    fun getEaterFirstName(): String?{
        return settings.currentEater?.firstName
    }
}