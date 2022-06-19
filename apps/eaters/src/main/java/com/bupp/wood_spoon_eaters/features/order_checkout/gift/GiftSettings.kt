package com.bupp.wood_spoon_eaters.features.order_checkout.gift

import com.bupp.wood_spoon_eaters.repositories.AppSettingsRepository
import com.bupp.wood_spoon_eaters.repositories.EatersFeatureFlags
import com.bupp.wood_spoon_eaters.repositories.featureFlag

fun AppSettingsRepository.isGiftEnabled() = featureFlag(EatersFeatureFlags.GiftIsEnabled) ?: false