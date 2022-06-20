package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.module

import com.bupp.wood_spoon_chef.domain.GetIsCookingSlotNewFlowEnabledUseCase
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.CookingSlotMenuViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.CreateCookingSlotNewViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.base.CookingSlotParentViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.navigation.CookingSlotFlowNavigator
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val cookingSlotModule = module {

    factory { CookingSlotFlowNavigator() }

    single { GetIsCookingSlotNewFlowEnabledUseCase(get()) }

    viewModel { params -> CookingSlotParentViewModel(params.get()) }
    viewModel { params -> CreateCookingSlotNewViewModel(params.get()) }
    viewModel { params -> CookingSlotMenuViewModel(params.get()) }

}