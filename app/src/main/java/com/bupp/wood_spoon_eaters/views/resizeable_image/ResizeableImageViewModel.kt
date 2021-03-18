package com.bupp.wood_spoon_eaters.views.resizeable_image

import com.bupp.wood_spoon_eaters.model.CloudinaryTransformationsType
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ResizeableImageViewModel: KoinComponent{

    val metaDataRepository: MetaDataRepository by inject()

    fun getByType(type: CloudinaryTransformationsType): String? {
        val cloudinery = metaDataRepository.getCloudinaryTransformations()
        return cloudinery?.getByType(type)
    }


}