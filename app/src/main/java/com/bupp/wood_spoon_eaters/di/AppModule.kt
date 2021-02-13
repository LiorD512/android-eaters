package com.bupp.wood_spoon_eaters.di

import com.bupp.wood_spoon_eaters.common.AppSettings
import com.bupp.wood_spoon_eaters.dialogs.RateLastOrderViewModel
import com.bupp.wood_spoon_eaters.dialogs.cancel_order.CancelOrderViewModel
import com.bupp.wood_spoon_eaters.dialogs.payment_methods.PaymentMethodsViewModel
import com.bupp.wood_spoon_eaters.dialogs.update_required.UpdateRequiredViewModel
import com.bupp.wood_spoon_eaters.dialogs.web_docs.CookProfileViewModel
import com.bupp.wood_spoon_eaters.dialogs.web_docs.WebDocsViewModel
import com.bupp.wood_spoon_eaters.fcm.FcmManager
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.ActiveOrderTrackerViewModel
import com.bupp.wood_spoon_eaters.bottom_sheets.address_menu.AddressMenuViewModel
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.TimePickerViewModel
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressViewModel
import com.bupp.wood_spoon_eaters.features.locations_and_address.address_verification_map.AddressMapVerificationViewModel
import com.bupp.wood_spoon_eaters.features.locations_and_address.select_address.SelectAddressViewModel
import com.bupp.wood_spoon_eaters.features.login.LoginViewModel
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.features.main.feed.FeedViewModel
import com.bupp.wood_spoon_eaters.features.main.filter.PickFiltersViewModel
import com.bupp.wood_spoon_eaters.features.main.order_details.OrderDetailsViewModel
import com.bupp.wood_spoon_eaters.features.main.order_history.OrdersHistoryViewModel
import com.bupp.wood_spoon_eaters.features.main.profile.edit_my_profile.EditMyProfileViewModel
import com.bupp.wood_spoon_eaters.features.main.profile.my_profile.MyProfileViewModel
import com.bupp.wood_spoon_eaters.features.main.report_issue.ReportIssueViewModel
import com.bupp.wood_spoon_eaters.features.main.search.SearchViewModel
import com.bupp.wood_spoon_eaters.features.main.settings.SettingsViewModel
import com.bupp.wood_spoon_eaters.features.main.support_center.SupportViewModel
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderMainViewModel
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderViewModel
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.checkout.CheckoutViewModel
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.promo_code.PromoCodeViewModel
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.sub_screen.single_dish_info.SingleDishInfoViewModel
import com.bupp.wood_spoon_eaters.features.splash.SplashViewModel
import com.bupp.wood_spoon_eaters.managers.*
import com.bupp.wood_spoon_eaters.managers.delivery_date.DeliveryTimeManager
import com.bupp.wood_spoon_eaters.managers.location.LocationManager
import com.bupp.wood_spoon_eaters.network.base_repos.FeedRepositoryImpl
import com.bupp.wood_spoon_eaters.network.base_repos.UserRepositoryImpl
import com.bupp.wood_spoon_eaters.repositories.FeedRepository
import com.bupp.wood_spoon_eaters.repositories.MetaDataRepository
import com.bupp.wood_spoon_eaters.repositories.NewOrderRepository
import com.bupp.wood_spoon_eaters.repositories.UserRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {

    //global
    single { FcmManager(get()) }
    factory { PermissionManager() }
    single { DeliveryTimeManager() }
    single { PaymentManager(get()) }
    single { AppSettings(get(), get()) }
    single { MetaDataRepository(get()) }
    single { LocationManager(get(), get()) }

    //repos
    single { UserRepositoryImpl(get()) }
    single { UserRepository(get(), get()) }
    single { FeedRepositoryImpl(get()) }
    single { FeedRepository(get()) }
    single { NewOrderRepository(get(), get()) }

    //managers
    single { OrderManager(get(), get(), get()) }
    single { CartManager(get(), get(), get(), get()) }
    single { EventsManager(get(), get(), get(), get()) }
    single { SearchManager(get(), get(), get(), get()) }
    single { FeedDataManager(get(), get()) }
    single { EaterDataManager(get(), get(), get(), get()) }



    //VIEW MODELS

    //bottom sheet
    viewModel { AddressMenuViewModel(get()) }

    //splash
    viewModel { SplashViewModel(get(), get(), get(), get()) }

    //login
    viewModel { LoginViewModel(get(), get(), get(), get(), get(), get()) }


    //location
    viewModel { LocationAndAddressViewModel(get(), get()) }
    viewModel { SelectAddressViewModel(get(), get(), get(), get()) }
    viewModel { AddressMapVerificationViewModel(get(), get()) }

    //time
    viewModel { TimePickerViewModel(get()) }

    //New Order
    viewModel { NewOrderMainViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { SingleDishInfoViewModel(get(), get(), get(), get(), get(), get()) }


    //main
    viewModel { MainViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { FeedViewModel(get(), get(), get()) }
    viewModel { SearchViewModel(get(), get(), get(), get(), get()) }
    viewModel { PickFiltersViewModel(get(), get()) }
    viewModel { CheckoutViewModel(get(), get(), get()) }
    viewModel { PromoCodeViewModel(get(),get())}
    viewModel { RateLastOrderViewModel(get(),get()) }
    viewModel { ReportIssueViewModel(get(), get()) }
    viewModel { OrderDetailsViewModel(get()) }
    viewModel { CookProfileViewModel(get(), get())}

    viewModel { PaymentMethodsViewModel(get()) }
    viewModel { UpdateRequiredViewModel(get()) }

    viewModel { NewOrderViewModel(get(), get(), get()) }
    viewModel { ActiveOrderTrackerViewModel(get(), get(), get()) }
    viewModel { CancelOrderViewModel(get()) }

    //Profile
    viewModel { MyProfileViewModel(get(), get(), get(), get(), get()) }
    viewModel { EditMyProfileViewModel(get(), get(), get()) }
    viewModel { OrdersHistoryViewModel(get()) }

    //support
    viewModel { SupportViewModel(get())}
    viewModel { WebDocsViewModel(get()) }


    //settings
    viewModel { SettingsViewModel(get(), get(), get(), get()) }



}

