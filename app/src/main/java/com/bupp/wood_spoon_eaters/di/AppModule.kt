package com.bupp.wood_spoon_eaters.di

import com.bupp.wood_spoon_eaters.dialogs.LocationChooserViewModel
import com.bupp.wood_spoon_eaters.dialogs.RateLastOrderViewModel
import com.bupp.wood_spoon_eaters.dialogs.rating_dialog.RatingsViewModel
import com.bupp.wood_spoon_eaters.dialogs.TrackOrderViewModel
import com.bupp.wood_spoon_eaters.features.login.code.CodeViewModel
import com.bupp.wood_spoon_eaters.features.login.verification.PhoneVerificationViewModel
import com.bupp.wood_spoon_eaters.features.login.welcome.WelcomeViewModel
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.features.main.checkout.CheckoutViewModel
import com.bupp.wood_spoon_eaters.features.main.delivery_details.DeliveryDetailsViewModel
import com.bupp.wood_spoon_eaters.features.main.delivery_details.sub_screens.add_new_address.AddAddressViewModel
import com.bupp.wood_spoon_eaters.features.main.edit_my_profile.EditMyProfileViewModel
import com.bupp.wood_spoon_eaters.features.main.feed.FeedViewModel
import com.bupp.wood_spoon_eaters.features.main.search.SearchViewModel
import com.bupp.wood_spoon_eaters.features.main.my_profile.MyProfileViewModel
import com.bupp.wood_spoon_eaters.features.main.filter.PickFiltersViewModel
import com.bupp.wood_spoon_eaters.features.main.order_details.OrderDetailsViewModel
import com.bupp.wood_spoon_eaters.features.main.promo_code.PromoCodeViewModel
import com.bupp.wood_spoon_eaters.features.main.report.ReportViewModel
import com.bupp.wood_spoon_eaters.features.main.sub_features.settings.SettingsViewModel
import com.bupp.wood_spoon_eaters.features.main.support_center.SupportViewModel
import com.bupp.wood_spoon_eaters.features.sign_up.create_account.CreateAccountViewModel
import com.bupp.wood_spoon_eaters.features.splash.SplashViewModel
import com.bupp.wood_spoon_eaters.managers.MetaDataManager
import com.bupp.wood_spoon_eaters.managers.OrderManager
import com.bupp.wood_spoon_eaters.managers.PermissionManager
import com.bupp.wood_spoon_eaters.managers.SearchManager
import com.bupp.wood_spoon_eaters.utils.AppSettings
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {

    single { MetaDataManager() }
    single { AppSettings(get()) }
    single { SearchManager(get()) }
    single { OrderManager(get(), get()) }
    factory { PermissionManager() }

    //VIEW MODELS

    //splash
    viewModel { SplashViewModel(get(), get(), get(), get()) }

    //login
    viewModel { WelcomeViewModel(get()) }
    viewModel { CodeViewModel(get(), get()) }
    viewModel { PhoneVerificationViewModel(get()) }

    //sign up
    viewModel { CreateAccountViewModel(get(), get()) }

    //main
    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { FeedViewModel(get()) }
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { AddAddressViewModel(get(), get(), get()) }
    viewModel { PickFiltersViewModel(get()) }
    viewModel { CheckoutViewModel(get(), get()) }
    viewModel { PromoCodeViewModel(get(),get())    }
    viewModel { TrackOrderViewModel(get(), get()) }
    viewModel { RateLastOrderViewModel(get(),get()) }
    viewModel { RatingsViewModel(get(), get()) }
    viewModel { ReportViewModel(get()) }
    viewModel { OrderDetailsViewModel(get(),get()) }

    //Profile
    viewModel { MyProfileViewModel(get(), get(), get()) }
    viewModel { EditMyProfileViewModel(get(), get(), get()) }


    //support
    viewModel { SupportViewModel(get()) }


    //settings
    viewModel { SettingsViewModel(get()) }

    //chooser fragment
    viewModel { LocationChooserViewModel(get()) }
    viewModel { DeliveryDetailsViewModel(get()) }

}

