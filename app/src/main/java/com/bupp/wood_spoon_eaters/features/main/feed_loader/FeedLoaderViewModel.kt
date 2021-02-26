package com.bupp.wood_spoon_eaters.features.main.feed_loader

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.model.WelcomeScreen
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository

class FeedLoaderViewModel(val metaDataRepository: MetaDataRepository): ViewModel() {

    val welcomeScreens = MutableLiveData<List<WelcomeScreen>?>()
    fun getWelcomeScreens(){
        welcomeScreens.postValue(metaDataRepository.getWelcomeScreens())
    }
}