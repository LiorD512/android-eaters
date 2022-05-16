package com.bupp.wood_spoon_eaters.domain

import com.bupp.wood_spoon_eaters.domain.comon.UseCase
import com.bupp.wood_spoon_eaters.repositories.AppSettingsRepository
import com.bupp.wood_spoon_eaters.repositories.getOnboardingSlideDelay

private var DEFAULT_FLIP_SLIDE_INTERVAL = 6000L

class GetOnboardingAppSettingsSlidesDelayUseCase(
    private val appSettingsRepository: AppSettingsRepository
) : UseCase<Long, Nothing?> {

    override fun execute(params: Nothing?): Long {
        val result = appSettingsRepository.getOnboardingSlideDelay()

        return if (result in 0.0..1.0) {
            DEFAULT_FLIP_SLIDE_INTERVAL
        } else {
            result.toLong() * 1000
        }
    }
}