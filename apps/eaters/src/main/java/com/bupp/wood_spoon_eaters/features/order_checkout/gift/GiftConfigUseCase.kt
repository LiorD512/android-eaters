package com.bupp.wood_spoon_eaters.features.order_checkout.gift

import com.bupp.wood_spoon_eaters.domain.comon.UseCase
import com.bupp.wood_spoon_eaters.repositories.AppSettingsRepository

data class GiftConfig(
    val isGiftEnabled: Boolean
)

class GiftConfigUseCase(val appSettingsRepository: AppSettingsRepository) :
    UseCase<GiftConfig, Nothing?> {
    override fun execute(params: Nothing?) = GiftConfig(
        isGiftEnabled = appSettingsRepository.isGiftEnabled()
    )
}