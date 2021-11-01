package com.bupp.wood_spoon_eaters.views.resizeable_image

import com.bupp.wood_spoon_eaters.model.CloudinaryTransformationsType
import com.bupp.wood_spoon_eaters.repositories.AppSettingsRepository
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ResizeableImageViewModel: KoinComponent{

    val appSettingsRepository: AppSettingsRepository by inject()

    fun getByType(type: CloudinaryTransformationsType): String? {
        val cloudinery = appSettingsRepository.getCloudinaryTransformations()
        return cloudinery?.getByType(type)
    }


}