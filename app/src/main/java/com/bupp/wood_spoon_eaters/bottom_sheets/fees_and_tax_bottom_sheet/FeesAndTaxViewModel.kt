package com.bupp.wood_spoon_eaters.bottom_sheets.fees_and_tax_bottom_sheet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bupp.wood_spoon_eaters.experiments.PricingExperimentParams
import com.bupp.wood_spoon_eaters.experiments.PricingExperimentUseCase
import com.bupp.wood_spoon_eaters.repositories.AppSettingsRepository
import com.bupp.wood_spoon_eaters.repositories.getMinOrderFeeStr

class FeesAndTaxViewModel(
    val appSettingsRepository: AppSettingsRepository,
    pricingExperimentUseCase: PricingExperimentUseCase
) : ViewModel() {

    val pricingExperimentParamsLiveData: LiveData<PricingExperimentParams> = MutableLiveData(pricingExperimentUseCase.getExperimentParams())

    fun getGlobalMinimumOrderFee(): String {
        return appSettingsRepository.getMinOrderFeeStr(false)
    }
}
