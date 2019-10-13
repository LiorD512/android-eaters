package com.bupp.wood_spoon_eaters.di

import com.bupp.wood_spoon_eaters.dialogs.locationAutoComplete.LocationChooserViewModel
import com.bupp.wood_spoon_eaters.dialogs.RateLastOrderViewModel
import com.bupp.wood_spoon_eaters.dialogs.cancel_order.CancelOrderViewModel
import com.bupp.wood_spoon_eaters.dialogs.payment_methods.PaymentMethodsViewModel
import com.bupp.wood_spoon_eaters.dialogs.web_docs.CookProfileViewModel
import com.bupp.wood_spoon_eaters.dialogs.web_docs.WebDocsViewModel
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.ActiveOrderTrackerViewModel
import com.bupp.wood_spoon_eaters.features.address_and_location.AddressChooserViewModel
import com.bupp.wood_spoon_eaters.features.login.code.CodeViewModel
import com.bupp.wood_spoon_eaters.features.login.verification.PhoneVerificationViewModel
import com.bupp.wood_spoon_eaters.features.login.welcome.WelcomeViewModel
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.checkout.CheckoutViewModel
import com.bupp.wood_spoon_eaters.features.main.delivery_details.DeliveryDetailsViewModel
import com.bupp.wood_spoon_eaters.features.main.delivery_details.sub_screens.add_new_address.AddAddressViewModel
import com.bupp.wood_spoon_eaters.features.main.profile.edit_my_profile.EditMyProfileViewModel
import com.bupp.wood_spoon_eaters.features.main.feed.FeedViewModel
import com.bupp.wood_spoon_eaters.features.main.search.SearchViewModel
import com.bupp.wood_spoon_eaters.features.main.profile.my_profile.MyProfileViewModel
import com.bupp.wood_spoon_eaters.features.main.filter.PickFiltersViewModel
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.single_dish.SingleDishViewModel
import com.bupp.wood_spoon_eaters.features.main.order_details.OrderDetailsViewModel
import com.bupp.wood_spoon_eaters.features.main.order_history.OrdersHistoryViewModel
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.promo_code.PromoCodeViewModel
import com.bupp.wood_spoon_eaters.features.main.report_issue.ReportIssueViewModel
import com.bupp.wood_spoon_eaters.features.main.settings.SettingsViewModel
import com.bupp.wood_spoon_eaters.features.main.support_center.SupportViewModel
import com.bupp.wood_spoon_eaters.features.new_order.NewOrderViewModel
import com.bupp.wood_spoon_eaters.features.sign_up.create_account.CreateAccountViewModel
import com.bupp.wood_spoon_eaters.features.splash.SplashViewModel
import com.bupp.wood_spoon_eaters.managers.*
import com.bupp.wood_spoon_eaters.utils.AppSettings
import org.koin.android.viewmodel.dsl.viewModel

import org.koin.dsl.module


val appModule = module {

    single { AppSettings(get()) }
    single { OrderManager(get(), get(), get()) }
    single { LocationManager(get(), get()) }
    single { MetaDataManager() }
    single { SearchManager(get(), get(), get()) }
    single { EaterDataManager(get(), get(), get()) }
    single { FcmManager(get()) }

    factory { PermissionManager() }

    //VIEW MODELS


    //splash
    viewModel { SplashViewModel(get(), get(), get(), get(), get()) }

    //login
    viewModel { WelcomeViewModel(get()) }
    viewModel { CodeViewModel(get(), get(), get(), get()) }
    viewModel { PhoneVerificationViewModel(get()) }

    //sign up
    viewModel { CreateAccountViewModel(get(), get()) }

    //main
    viewModel { MainViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { FeedViewModel(get(), get(), get(), get()) }
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { SingleDishViewModel(get(), get(), get(), get(), get()) }
    viewModel { AddressChooserViewModel(get(), get(), get()) }
    viewModel { AddAddressViewModel(get(), get(), get()) }
    viewModel { PickFiltersViewModel(get(), get()) }
    viewModel { CheckoutViewModel(get(), get(), get()) }
    viewModel { PromoCodeViewModel(get(),get())}
    viewModel { RateLastOrderViewModel(get(),get()) }
    viewModel { ReportIssueViewModel(get(), get()) }
    viewModel { OrderDetailsViewModel(get()) }
    viewModel { CookProfileViewModel(get(), get())}

    viewModel { PaymentMethodsViewModel(get()) }

    viewModel { NewOrderViewModel(get(), get(), get()) }
    viewModel { ActiveOrderTrackerViewModel(get(), get()) }
    viewModel { CancelOrderViewModel(get()) }

    //Profile
    viewModel { MyProfileViewModel(get(), get(), get(), get()) }
    viewModel { EditMyProfileViewModel(get(), get(), get()) }
    viewModel { OrdersHistoryViewModel(get()) }

    //support
    viewModel { SupportViewModel(get())}
    viewModel { WebDocsViewModel(get())}


    //settings
    viewModel { SettingsViewModel(get(), get(), get(), get()) }

    //chooser fragment
    viewModel { LocationChooserViewModel(get()) }
    viewModel { DeliveryDetailsViewModel(get(), get(), get()) }
    

}

