package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.module

import com.bupp.wood_spoon_chef.data.repositories.CookingSlotRepository
import com.bupp.wood_spoon_chef.domain.GetIsCookingSlotNewFlowEnabledUseCase
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.CookingSlotMenuViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.CreateCookingSlotNewViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.base.CookingSlotParentViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator.CookingSlotFlowCoordinator
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.local.DishesWithCategoryMemoryDataSource
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs.FilterMenuViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs.MyDishesViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.mapper.CookingSlotStateMapper
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.network.CookingSlotApiService
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.network.DishesWithCategoryApiService
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.repository.CookingSlotsDraftMemoryDataSource
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.repository.CookingSlotsDraftRepository
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.repository.DishesWithCategoryRepository
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.mapper.OriginalCookingSlotToDraftCookingSlotMapper
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val cookingSlotModule = module {

    factory { CookingSlotFlowCoordinator() }
    factory { CookingSlotStateMapper() }
    factory { OriginalCookingSlotToDraftCookingSlotMapper() }

    single { GetIsCookingSlotNewFlowEnabledUseCase(get()) }
    single { CookingSlotRepository(get(), get(), get(), get()) }
    single { provideCookingSlotApiService(get()) }
    single { provideDishesWithCategoryApiService(get()) }
    single { DishesWithCategoryMemoryDataSource() }
    single { DishesWithCategoryRepository(get(), get()) }

    single { CookingSlotsDraftMemoryDataSource() }
    single { CookingSlotsDraftRepository(get()) }

    viewModel { params -> CookingSlotParentViewModel(params.get(), get(), get(), get()) }
    viewModel { params -> CreateCookingSlotNewViewModel(params.get(), get(), get(), get()) }
    viewModel { params -> CookingSlotMenuViewModel(params.get(), get(), get()) }
    viewModel { MyDishesViewModel(get(), get()) }
    viewModel { FilterMenuViewModel(get()) }

}

private fun provideCookingSlotApiService(retrofit: Retrofit): CookingSlotApiService =
    retrofit.create(CookingSlotApiService::class.java)

private fun provideDishesWithCategoryApiService(retrofit: Retrofit): DishesWithCategoryApiService =
    retrofit.create(DishesWithCategoryApiService::class.java)