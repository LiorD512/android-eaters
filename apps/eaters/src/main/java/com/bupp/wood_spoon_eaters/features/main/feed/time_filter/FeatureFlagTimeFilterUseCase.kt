package com.bupp.wood_spoon_eaters.features.main.feed.time_filter

import com.bupp.wood_spoon_eaters.domain.comon.UseCase
import com.bupp.wood_spoon_eaters.repositories.AppSettingsRepository
import com.bupp.wood_spoon_eaters.repositories.EatersFeatureFlags
import com.bupp.wood_spoon_eaters.repositories.featureFlag

class FeatureFlagTimeFilterUseCase(
    private val appSettingsRepository: AppSettingsRepository
): UseCase<Boolean, Nothing?> {

    override fun execute(params: Nothing?): Boolean = appSettingsRepository.featureFlag(
        EatersFeatureFlags.TimeFilterToday
    ) ?: false
}