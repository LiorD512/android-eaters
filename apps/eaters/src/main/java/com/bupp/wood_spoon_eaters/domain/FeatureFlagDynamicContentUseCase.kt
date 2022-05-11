package com.bupp.wood_spoon_eaters.domain

import com.bupp.wood_spoon_eaters.domain.comon.UseCase
import com.bupp.wood_spoon_eaters.repositories.AppSettingsRepository
import com.bupp.wood_spoon_eaters.repositories.EatersFeatureFlags
import com.bupp.wood_spoon_eaters.repositories.featureFlag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FeatureFlagDynamicContentUseCase(
    private val appSettingsRepository: AppSettingsRepository
) : UseCase<Flow<Boolean>, Nothing?> {

    override fun execute(params: Nothing?): Flow<Boolean> = flow {
        emit(
            appSettingsRepository.featureFlag(
                EatersFeatureFlags.OnboardingDynamicScrollableContent
            ) ?: false
        )
    }
}