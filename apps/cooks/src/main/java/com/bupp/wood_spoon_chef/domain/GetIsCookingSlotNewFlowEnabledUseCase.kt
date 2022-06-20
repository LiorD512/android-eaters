package com.bupp.wood_spoon_chef.domain

import com.bupp.wood_spoon_chef.data.repositories.AppSettingsRepository
import com.bupp.wood_spoon_chef.data.repositories.CooksFeatureFlags
import com.bupp.wood_spoon_chef.domain.comon.UseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetIsCookingSlotNewFlowEnabledUseCase(
    private val appSettingsRepository: AppSettingsRepository
): UseCase<Flow<Boolean>, Nothing?> {

    override fun execute(params: Nothing?): Flow<Boolean> = flow {
        emit(
            appSettingsRepository.featureFlag(
                CooksFeatureFlags.mobile_cooking_slot_new_flow_enabled.name
            ) ?: false
        )
    }
}