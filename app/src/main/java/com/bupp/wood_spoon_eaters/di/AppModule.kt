package com.bupp.wood_spoon_eaters.di

import com.bupp.wood_spoon_eaters.dialogs.super_user.SuperUserViewModel
import com.bupp.wood_spoon_eaters.common.AppSettings
import com.bupp.wood_spoon_eaters.dialogs.rate_last_order.RateLastOrderViewModel
import com.bupp.wood_spoon_eaters.dialogs.cancel_order.CancelOrderViewModel
import com.bupp.wood_spoon_eaters.dialogs.update_required.UpdateRequiredViewModel
import com.bupp.wood_spoon_eaters.dialogs.web_docs.WebDocsViewModel
import com.bupp.wood_spoon_eaters.fcm.FcmManager
import com.bupp.wood_spoon_eaters.features.track_your_order.ActiveOrderTrackerViewModel
import com.bupp.wood_spoon_eaters.bottom_sheets.address_menu.AddressMenuViewModel
import com.bupp.wood_spoon_eaters.bottom_sheets.single_order_details.SingleOrderDetailsViewModel
import com.bupp.wood_spoon_eaters.bottom_sheets.time_picker.TimePickerViewModel
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressViewModel
import com.bupp.wood_spoon_eaters.features.locations_and_address.address_verification_map.AddressMapVerificationViewModel
import com.bupp.wood_spoon_eaters.features.locations_and_address.select_address.SelectAddressViewModel
import com.bupp.wood_spoon_eaters.features.login.LoginViewModel
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.features.main.feed.FeedViewModel
import com.bupp.wood_spoon_eaters.features.main.order_history.OrdersHistoryViewModel
import com.bupp.wood_spoon_eaters.bottom_sheets.edit_profile.EditProfileViewModel
import com.bupp.wood_spoon_eaters.bottom_sheets.fees_and_tax_bottom_sheet.FeesAndTaxViewModel
import com.bupp.wood_spoon_eaters.features.main.profile.my_profile.MyProfileViewModel
import com.bupp.wood_spoon_eaters.bottom_sheets.report_issue.ReportIssueViewModel
import com.bupp.wood_spoon_eaters.features.main.settings.SettingsViewModel
import com.bupp.wood_spoon_eaters.bottom_sheets.support_center.SupportViewModel
import com.bupp.wood_spoon_eaters.custom_views.cuisine_chooser.CuisineChooserViewModel
import com.bupp.wood_spoon_eaters.features.order_checkout.checkout.CheckoutViewModel
import com.bupp.wood_spoon_eaters.features.order_checkout.promo_code.PromoCodeViewModel
import com.bupp.wood_spoon_eaters.features.order_checkout.OrderCheckoutViewModel
import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.UpSaleNCartViewModel
import com.bupp.wood_spoon_eaters.features.restaurant.RestaurantMainViewModel
import com.bupp.wood_spoon_eaters.features.restaurant.dish_page.DishPageViewModel
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.RestaurantPageViewModel
import com.bupp.wood_spoon_eaters.features.splash.SplashViewModel
import com.bupp.wood_spoon_eaters.managers.*
import com.bupp.wood_spoon_eaters.managers.location.LocationManager
import com.bupp.wood_spoon_eaters.network.base_repos.*
import com.bupp.wood_spoon_eaters.network.result_handler.ErrorManger
import com.bupp.wood_spoon_eaters.network.result_handler.ResultManager
import com.bupp.wood_spoon_eaters.repositories.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {

    //global
    single { FcmManager(get()) }
    single { AppSettings(get(), get()) }
    single { FlowEventsManager(get(),get()) }
    single { ErrorManger()}
    single { ResultManager(get()) }

    //repos
    single { MetaDataRepository(get()) }
    single { MetaDataRepositoryImpl(get(),get()) }
    single { FeedRepository(get(), get(), get()) }
    single { FeedRepositoryImpl(get(),get()) }
    single { UserRepositoryImpl(get(),get()) }
    single { RestaurantRepository(get())}
    single { RestaurantRepositoryImpl(get(),get())}
    single { UserRepository(get(), get(), get(), get(), get()) }
    single { OrderRepository(get(), get()) }
    single { OrderRepositoryImpl(get(),get()) }
    single { EaterDataRepository(get()) }
    single { EaterDataRepositoryImpl(get(),get()) }
    single { CampaignRepository(get(), get()) }
    single { CampaignRepositoryImpl(get(),get()) }

    //managers
    single { GlobalErrorManager() }
    single { EventsManager(get()) }
    single { PaymentManager(get(), get()) }
    single { LocationManager(get(), get()) }
    single { CampaignManager(get()) }
    single { MediaUploadManager(get(), get()) }
    single { FeedDataManager(get(), get(), get()) }
    single { CartManager(get(), get(), get()) }
    single { EaterDataManager(get(), get(), get(), get(), get()) }



    //VIEW MODELS

    //bottom sheet
    viewModel { AddressMenuViewModel(get(), get(), get()) }

    //splash
    viewModel { SplashViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { SuperUserViewModel(get(), get()) }

    //login
    viewModel { LoginViewModel(get(), get(), get(), get(), get(), get()) }


    //location
    viewModel { LocationAndAddressViewModel(get(), get(), get(), get()) }
    viewModel { SelectAddressViewModel(get(), get(), get()) }
    viewModel { AddressMapVerificationViewModel(get(), get()) }

    //time
    viewModel { TimePickerViewModel(get()) }

    //New Order
    viewModel { CheckoutViewModel(get(), get(), get(), get()) }
    viewModel { PromoCodeViewModel(get(), get()) }
    viewModel { FeesAndTaxViewModel(get()) }

    viewModel { UpSaleNCartViewModel(get(), get(), get(), get()) }

    //main
    viewModel { MainViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { FeedViewModel(get(), get(), get(), get(), get()) }
    viewModel { ReportIssueViewModel(get(), get()) }
    viewModel { RateLastOrderViewModel(get()) }

    viewModel { UpdateRequiredViewModel(get()) }

    viewModel { ActiveOrderTrackerViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { CancelOrderViewModel(get()) }

    //Profile
    viewModel { MyProfileViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { EditProfileViewModel(get(), get(), get()) }
    viewModel { SingleOrderDetailsViewModel(get()) }
    viewModel { OrdersHistoryViewModel(get(), get()) }
    viewModel { CuisineChooserViewModel(get(), get()) }

    //support
    viewModel { SupportViewModel(get(), get(), get()) }
    viewModel { WebDocsViewModel(get(), get()) }


    //settings
    viewModel { SettingsViewModel(get(), get(), get(), get(), get()) }

    //RestaurantPage
    viewModel { RestaurantMainViewModel(get(), get()) }
    viewModel { RestaurantPageViewModel(get(), get(), get(), get()) }
    viewModel { DishPageViewModel(get(), get(), get(), get()) }
    viewModel { OrderCheckoutViewModel(get(), get(), get(), get()) }


}

