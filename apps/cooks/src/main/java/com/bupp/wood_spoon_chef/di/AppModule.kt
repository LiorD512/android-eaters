package com.bupp.wood_spoon_chef.di

import com.bupp.wood_spoon_chef.data.local.MemoryCalendarDataSource
import com.bupp.wood_spoon_chef.presentation.dialogs.super_user.SuperUserViewModel
import com.bupp.wood_spoon_chef.presentation.dialogs.update_required.UpdateRequiredViewModel
import com.bupp.wood_spoon_chef.presentation.dialogs.web_docs.WebDocsViewModel
import com.bupp.wood_spoon_chef.fcm.FcmManager
import com.bupp.wood_spoon_chef.presentation.features.main.MainViewModel
import com.bupp.wood_spoon_chef.presentation.features.main.account.AccountViewModel
import com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.delete_account.DeleteAccountViewModel
import com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.edit_account_and_kitchen.UpdateAccountViewModel
import com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.payment.PaymentViewModel
import com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.reviews.ReviewsBSViewModel
import com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.support_center.SupportViewModel
import com.bupp.wood_spoon_chef.presentation.features.main.calendar.CalendarViewModel
import com.bupp.wood_spoon_chef.presentation.features.main.calendar.create_cooking_slot.CreateCookingSlotViewModel
import com.bupp.wood_spoon_chef.presentation.features.main.calendar.cookingSlotDetails.CookingSlotDetailsViewModel
import com.bupp.wood_spoon_chef.presentation.features.main.earnings.EarningsViewModel
import com.bupp.wood_spoon_chef.presentation.features.main.my_dishes.MyDishesViewModel
import com.bupp.wood_spoon_chef.presentation.features.main.orders.OrdersViewModel
import com.bupp.wood_spoon_chef.presentation.features.main.orders.order_details.OrderDetailsViewModel
import com.bupp.wood_spoon_chef.presentation.features.main.orders.order_details.dialogs.cancel_order.CancelOrderViewModel
import com.bupp.wood_spoon_chef.presentation.features.main.single_dish.SingleDishViewModel
import com.bupp.wood_spoon_chef.presentation.features.new_dish.NewDishViewModel
import com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account.CreateAccountViewModel
import com.bupp.wood_spoon_chef.presentation.features.onboarding.login.LoginViewModel
import com.bupp.wood_spoon_chef.presentation.features.splash.SplashViewModel
import com.bupp.wood_spoon_chef.analytics.ChefAnalyticsTracker
import com.bupp.wood_spoon_chef.data.local.MemoryCategoriesWithDishDataSource
import com.bupp.wood_spoon_chef.managers.MediaUploadManager
import com.bupp.wood_spoon_chef.data.remote.network.ErrorManger
import com.bupp.wood_spoon_chef.data.remote.network.ResponseHandler
import com.bupp.wood_spoon_chef.data.repositories.*
import com.bupp.wood_spoon_chef.data.repositories.AppSettingsRepositoryImpl
import com.bupp.wood_spoon_chef.domain.*
import com.bupp.wood_spoon_chef.presentation.features.main.calendar.cookingSlotDetails.CookingSlotDetailsViewModelNew
import com.bupp.wood_spoon_chef.utils.UserSettings
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // DataSource
    single { MemoryCalendarDataSource() }
    single { MemoryCategoriesWithDishDataSource() }

    //UseCase
    single { GetSupportNumberUseCase(get()) }
    single { IsCallSupportByCancelingOrderUseCase(get()) }
    single { FetchCookingSlotByIdUseCase(get()) }
    single { CancelCookingSlotUseCase(get()) }
    single { GetSectionsWithDishesUseCase(get()) }

    //Interactor
    single { CookingSlotWithCategoriesInteractor(get(), get()) }

    //Repo
    single { UserRepository(get(), get(), get(), get(), get()) }
    single { MetaDataRepository(get(), get(), StaticFeatureFlagsListProvider(), get()) }
    single<AppSettingsRepository> { AppSettingsRepositoryImpl(get(), get()) }
    single { DishRepository(get(), get(), get(), get(), get()) }
    single { CookingSlotRepository(get(), get(), get(), get()) }
    single { OrderRepository(get(), get()) }
    single { EventRepository(get(), get()) }
    single { EarningsRepository(get(), get()) }

    //Managers
    single { UserSettings(get(), get()) }
    single { FcmManager(get()) }
    single { MediaUploadManager(get(), get(), get()) }
    single { ResponseHandler(get()) }
    single { ErrorManger(get()) }

    // analytics
    single { Firebase.analytics }
    single { ChefAnalyticsTracker(get(), get()) }

    //VIEW MODELS
    //splash and getCode
    viewModel { SplashViewModel(get(), get(), get(), get()) }
    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { CreateAccountViewModel(get(), get(), get(), get()) }
    viewModel { UpdateRequiredViewModel(get()) }
    viewModel { SuperUserViewModel(get()) }
    viewModel { WebDocsViewModel(get()) }

    //main
    viewModel { MainViewModel(get(), get(), get(), get(), get()) }

    //tabs
    viewModel { MyDishesViewModel(get(), get(), get(), get()) }
    viewModel { OrdersViewModel(get(), get(), get()) }
    viewModel { OrderDetailsViewModel(get(), get(), get(), get(), get()) }

    //new Dish
    viewModel { NewDishViewModel(get(), get(), get()) }

    //calendar
    viewModel { CalendarViewModel(get(), get(), get(), get()) }

    //calendar CookingSlotDetailsBottomSheet
    viewModel { CreateCookingSlotViewModel(get(), get()) }

    //calendar CookingSlotDetailsViewModel
    viewModel { CookingSlotDetailsViewModel(get(), get()) }

    //calendar CookingSlotDetailsViewModel
    viewModel { CookingSlotDetailsViewModelNew(get(), get(), get(), get(), get()) }

    //orders
    viewModel { CancelOrderViewModel(get(), get()) }

    //earnings
    viewModel { EarningsViewModel(get(), get()) }

    //profile
    viewModel { AccountViewModel(get(), get(), get()) }
    viewModel { SupportViewModel(get(), get()) }
    viewModel { UpdateAccountViewModel(get(), get(), get()) }
    viewModel { ReviewsBSViewModel(get()) }
    viewModel { PaymentViewModel(get()) }
    viewModel { DeleteAccountViewModel(get(), get()) }

    //SingleDishFragment
    viewModel { SingleDishViewModel(get(), get()) }

}

