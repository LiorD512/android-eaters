package com.bupp.wood_spoon_chef.di

import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.data.local.CookingSlotsDraftMemoryDataSource
import com.bupp.wood_spoon_chef.data.remote.model.request.CookingSlotRequestMapper
import com.bupp.wood_spoon_chef.data.repositories.CookingSlotRepository
import com.bupp.wood_spoon_chef.domain.GetFormattedSelectedHoursAndMinutesUseCase
import com.bupp.wood_spoon_chef.domain.GetIsCookingSlotNewFlowEnabledUseCase
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.base.CookingSlotParentViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.rrules.SlotRecurringViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.cooking_slot_menu.FilterMenuViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.cooking_slot_menu.MyDishesViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.CookingSlotReportEventUseCase
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.cooking_slot_menu.CookingSlotMenuViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.create_cooking_slot.CreateCookingSlotNewViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator.CookingSlotFlowCoordinator
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.network.CookingSlotApiService
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.network.DishesWithCategoryApiService
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.repository.CookingSlotsDraftRepository
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.review_cooking_slot.CookingSlotReviewViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.last_call.LastCallBottomSheetViewModel
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.mapper.*
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val cookingSlotModule = module {

    factory { CookingSlotFlowCoordinator() }

    //UseCase
    factory {
        GetFormattedSelectedHoursAndMinutesUseCase(androidContext().resources.getString(R.string.format_last_call_subtitle))
    }

    //mapper
    factory { CookingSlotRequestMapper() }
    factory { OriginalCookingSlotToDraftCookingSlotMapper(get()) }
    factory { MenuItemToMenuDishItemMapper() }
    factory { MenuDishItemToAdapterModelMapper() }
    factory { AdapterModelCategoriesListMapper() }
    factory { CookingSlotReportEventUseCase(get()) }

    single { GetIsCookingSlotNewFlowEnabledUseCase(get()) }
    single { CookingSlotRepository(get(), get(), get(), get()) }
    single { provideCookingSlotApiService(get()) }
    single { provideDishesWithCategoryApiService(get()) }

    single { CookingSlotsDraftMemoryDataSource() }
    single { CookingSlotsDraftRepository(get()) }

    viewModel { params -> CookingSlotParentViewModel(params.get(), get(), get(), get(), get()) }
    viewModel { params -> CreateCookingSlotNewViewModel(params.get(), get(), get(), get(), get(), get()) }
    viewModel { params -> CookingSlotMenuViewModel(params.get(), get(), get(), get()) }
    viewModel { MyDishesViewModel(get(), get(), get()) }
    viewModel { FilterMenuViewModel(get(),get(), get()) }
    viewModel { CookingSlotReviewViewModel(get(), get(), get(), get(), get(),get(), get()) }
    viewModel { SlotRecurringViewModel(get(), get()) }
    viewModel { LastCallBottomSheetViewModel() }

}

private fun provideCookingSlotApiService(retrofit: Retrofit): CookingSlotApiService =
    retrofit.create(CookingSlotApiService::class.java)

private fun provideDishesWithCategoryApiService(retrofit: Retrofit): DishesWithCategoryApiService =
    retrofit.create(DishesWithCategoryApiService::class.java)