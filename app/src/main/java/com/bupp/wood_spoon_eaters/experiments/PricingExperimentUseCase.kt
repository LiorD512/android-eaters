package com.bupp.wood_spoon_eaters.experiments

import android.content.res.Resources
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.repositories.AppSettingsRepository
import com.bupp.wood_spoon_eaters.repositories.EatersFeatureFlags
import com.bupp.wood_spoon_eaters.repositories.featureFlag

/**
 * https://woodspoon.atlassian.net/wiki/spaces/BACKEND/pages/728727573/New+pricing+modal+mechanism
 */

data class PricingExperimentParams(
    val shouldHideFee: Boolean,
    val feeAndTaxTitle: String
)

class PricingExperimentUseCase(
    private val appSettingsRepository: AppSettingsRepository,
    private val resource: Resources
) {

    fun getExperimentParams() = PricingExperimentParams(
        shouldHideFee = isEatersFeeInPrice(),
        feeAndTaxTitle = getFeeAndTaxTitle()
    )

    private fun isEatersFeeInPrice() = appSettingsRepository.featureFlag(EatersFeatureFlags.EatersFeeInPrice) == true

    private fun getFeeAndTaxTitle() = resource.getString(
        if (isEatersFeeInPrice()) {
            R.string.estimated_tax_title
        } else {
            R.string.fees_and_estimated_tax_title
        }
    )
}
