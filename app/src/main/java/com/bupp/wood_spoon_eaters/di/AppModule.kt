package com.bupp.wood_spoon_eaters.di

import com.bupp.wood_spoon_eaters.features.login.code.CodeViewModel
import com.bupp.wood_spoon_eaters.features.login.verification.PhoneVerificationViewModel
import com.bupp.wood_spoon_eaters.features.login.welcome.WelcomeViewModel
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.features.main.feed.FeedViewModel
import com.bupp.wood_spoon_eaters.features.main.search.SearchViewModel
import com.bupp.wood_spoon_eaters.features.main.sub_features.settings.SettingsViewModel
import com.bupp.wood_spoon_eaters.features.sign_up.create_account.CreateAccountViewModel
import com.bupp.wood_spoon_eaters.features.splash.SplashViewModel
import com.bupp.wood_spoon_eaters.features.main.support_center.SupportViewModel
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
    single { OrderManager(get()) }
    single { SearchManager(get()) }
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
    viewModel { MainViewModel(get(), get()) }
    viewModel { FeedViewModel(get()) }
    viewModel { SearchViewModel(get(), get(), get()) }

    //support
    viewModel { SupportViewModel(get()) }


    //settings
    viewModel { SettingsViewModel(get()) }

}

