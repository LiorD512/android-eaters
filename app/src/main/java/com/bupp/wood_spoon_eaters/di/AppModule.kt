package com.bupp.wood_spoon_eaters.di

import com.bupp.wood_spoon_eaters.dialogs.super_user.SuperUserViewModel
import com.bupp.wood_spoon_eaters.common.AppSettings
import com.bupp.wood_spoon_eaters.dialogs.rate_last_order.RateLastOrderViewModel
import com.bupp.wood_spoon_eaters.dialogs.cancel_order.CancelOrderViewModel
import com.bupp.wood_spoon_eaters.dialogs.update_required.UpdateRequiredViewModel
import com.bupp.wood_spoon_eaters.features.main.cook_profile.CookProfileViewModel
import com.bupp.wood_spoon_eaters.dialogs.web_docs.WebDocsViewModel
import com.bupp.wood_spoon_eaters.fcm.FcmManager
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.ActiveOrderTrackerViewModel
import com.bupp.wood_spoon_eaters.bottom_sheets.address_menu.AddressMenuViewModel
import com.bupp.wood_spoon_eaters.bottom_sheets.single_order_details.SingleOrderDetailsViewModel
//import com.bupp.wood_spoon_eaters.bottom_sheets.edit_profile.EditMyProfileViewModel
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.TimePickerViewModel
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressViewModel
import com.bupp.wood_spoon_eaters.features.locations_and_address.address_verification_map.AddressMapVerificationViewModel
import com.bupp.wood_spoon_eaters.features.locations_and_address.select_address.SelectAddressViewModel
import com.bupp.wood_spoon_eaters.features.login.LoginViewModel
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.features.main.feed.FeedViewModel
import com.bupp.wood_spoon_eaters.features.main.feed_loader.FeedLoaderViewModel
import com.bupp.wood_spoon_eaters.features.main.filter.PickFiltersViewModel
import com.bupp.wood_spoon_eaters.features.main.order_history.OrdersHistoryViewModel
import com.bupp.wood_spoon_eaters.features.main.profile.edit_my_profile.EditMyProfileViewModel
import com.bupp.wood_spoon_eaters.features.main.profile.my_profile.MyProfileViewModel
import com.bupp.wood_spoon_eaters.bottom_sheets.report_issue.ReportIssueViewModel
import com.bupp.wood_spoon_eaters.features.main.search.SearchViewModel
import com.bupp.wood_spoon_eaters.features.main.settings.SettingsViewModel
import com.bupp.wood_spoon_eaters.bottom_sheets.support_center.SupportViewModel
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderMainViewModel
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.checkout.CheckoutViewModel
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.promo_code.PromoCodeViewModel
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_info.SingleDishInfoViewModel
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_ingredients.SingleDishIngredientViewModel
import com.bupp.wood_spoon_eaters.features.splash.SplashViewModel
import com.bupp.wood_spoon_eaters.managers.*
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import com.bupp.wood_spoon_eaters.managers.location.LocationManager
import com.bupp.wood_spoon_eaters.network.base_repos.*
import com.bupp.wood_spoon_eaters.repositories.EaterDataRepository
import com.bupp.wood_spoon_eaters.repositories.FeedRepository
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.repositories.OrderRepository
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {

    //global
    single { FcmManager(get()) }
    single { AppSettings(get(), get()) }

    //repos
    single { MetaDataRepository(get()) }
    single { MetaDataRepositoryImpl(get()) }
    single { FeedRepository(get()) }
    single { FeedRepositoryImpl(get()) }
    single { UserRepositoryImpl(get()) }
    single { UserRepository(get(), get(), get()) }
    single { OrderRepository(get(), get()) }
    single { OrderRepositoryImpl(get()) }
    single { EaterDataRepository(get()) }
    single { EaterDataRepositoryImpl(get()) }

    //managers
    single { DeliveryTimeManager() }
    single { PaymentManager(get(), get()) }
    single { MediaUploadManager(get(), get()) }
    single { LocationManager(get(), get()) }
    single { OrderManager(get(), get(), get()) }
    single { FeedDataManager(get(), get(), get()) }
    single { CartManager(get(), get(), get(), get(), get()) }
    single { EventsManager(get(), get()) }
    single { SearchManager(get(), get(), get(), get()) }
    single { EaterDataManager(get(), get(), get(), get(), get(), get()) }
    single { CampaignManager(get(), get()) }



    //VIEW MODELS

    //bottom sheet
    viewModel { AddressMenuViewModel(get(), get(), get()) }

    //splash
    viewModel { SplashViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { SuperUserViewModel(get()) }

    //login
    viewModel { LoginViewModel(get(), get(), get(), get(), get(), get()) }


    //location
    viewModel { LocationAndAddressViewModel(get(), get(), get()) }
    viewModel { SelectAddressViewModel(get(), get(), get(), get()) }
    viewModel { AddressMapVerificationViewModel(get(), get()) }

    //time
    viewModel { TimePickerViewModel(get(), get()) }

    //New Order
    viewModel { NewOrderMainViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { SingleDishInfoViewModel(get(), get()) }
    viewModel { SingleDishIngredientViewModel(get()) }
    viewModel { CheckoutViewModel(get(), get(), get(), get()) }
    viewModel { PromoCodeViewModel(get()) }


    //main
    viewModel { MainViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { FeedLoaderViewModel(get()) }
    viewModel { FeedViewModel(get(), get()) }
    viewModel { SearchViewModel(get(), get(), get(), get()) }
    viewModel { PickFiltersViewModel(get(), get()) }
    viewModel { RateLastOrderViewModel(get()) }
    viewModel { ReportIssueViewModel(get(), get()) }
    viewModel { CookProfileViewModel(get(), get(), get()) }

    viewModel { UpdateRequiredViewModel(get()) }

    viewModel { ActiveOrderTrackerViewModel(get(), get(), get()) }
    viewModel { CancelOrderViewModel(get()) }

    //Profile
    viewModel { MyProfileViewModel(get(), get(), get(), get(), get()) }
    viewModel { EditMyProfileViewModel(get(), get(), get()) }
    viewModel { OrdersHistoryViewModel(get()) }
    viewModel { SingleOrderDetailsViewModel(get(), get()) }

    //support
    viewModel { SupportViewModel(get(), get()) }
    viewModel { WebDocsViewModel(get()) }


    //settings
    viewModel { SettingsViewModel(get(), get(), get(), get()) }



}

