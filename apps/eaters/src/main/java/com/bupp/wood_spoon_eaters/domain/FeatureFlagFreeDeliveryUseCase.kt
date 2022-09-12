package com.bupp.wood_spoon_eaters.domain

import com.bupp.wood_spoon_eaters.custom_views.onboarding.SliderContentPage
import com.bupp.wood_spoon_eaters.domain.comon.UseCase
import com.bupp.wood_spoon_eaters.repositories.AppSettingsRepository
import com.bupp.wood_spoon_eaters.repositories.EatersFeatureFlags
import com.bupp.wood_spoon_eaters.repositories.featureFlag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FeatureFlagFreeDeliveryUseCase(
    private val appSettingsRepository: AppSettingsRepository
) : UseCase<Boolean, Nothing?> {

    override fun execute(params: Nothing?): Boolean = appSettingsRepository.featureFlag(
        EatersFeatureFlags.FreeDeliveryEnabled
    ) ?: false

}