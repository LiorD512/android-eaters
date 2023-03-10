package com.bupp.wood_spoon_eaters.di

import android.content.Context
import com.bupp.wood_spoon_eaters.EatersAuthConfigurationProvider
import com.bupp.wood_spoon_eaters.dialogs.super_user.SuperUserViewModel
import com.bupp.wood_spoon_eaters.common.UserSettings
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
import com.bupp.wood_spoon_eaters.bottom_sheets.reviews.ReviewsBSViewModel
import com.bupp.wood_spoon_eaters.features.main.settings.SettingsViewModel
import com.bupp.wood_spoon_eaters.bottom_sheets.support_center.SupportViewModel
import com.bupp.wood_spoon_eaters.custom_views.cuisine_chooser.CuisineChooserViewModel
import com.bupp.wood_spoon_eaters.data.data_sorce.memory.MemoryAppReviewDataSource
import com.bupp.wood_spoon_eaters.features.upsale.data_source.memory.MemoryUpSaleItemsDataSource
import com.bupp.wood_spoon_eaters.domain.*
import com.bupp.wood_spoon_eaters.experiments.PricingExperimentUseCase
import com.bupp.wood_spoon_eaters.features.create_profile.EditProfileActivity
import com.bupp.wood_spoon_eaters.features.create_profile.PhoneNumberVerificationRequestCodeUseCase
import com.bupp.wood_spoon_eaters.features.create_profile.SendPhoneVerificationUseCase
import com.bupp.wood_spoon_eaters.features.create_profile.code.EditProfileCodeViewModel
import com.bupp.wood_spoon_eaters.features.main.feed.time_filter.FeatureFlagTimeFilterUseCase
import com.bupp.wood_spoon_eaters.features.main.feed.time_filter.FeedTimeFilterViewModel
import com.bupp.wood_spoon_eaters.features.main.search.SearchViewModel
import com.bupp.wood_spoon_eaters.features.onboarding.OnboardingViewModel
import com.bupp.wood_spoon_eaters.features.order_checkout.checkout.CheckoutViewModel
import com.bupp.wood_spoon_eaters.features.order_checkout.promo_code.PromoCodeViewModel
import com.bupp.wood_spoon_eaters.features.order_checkout.OrderCheckoutViewModel
import com.bupp.wood_spoon_eaters.features.order_checkout.gift.GiftActionsViewModel
import com.bupp.wood_spoon_eaters.features.order_checkout.gift.GiftConfigUseCase
import com.bupp.wood_spoon_eaters.features.order_checkout.gift.GiftStateMapper
import com.bupp.wood_spoon_eaters.features.order_checkout.gift.GiftViewModel
import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.UpSaleNCartViewModel
import com.bupp.wood_spoon_eaters.features.restaurant.RestaurantMainViewModel
import com.bupp.wood_spoon_eaters.features.restaurant.dish_page.DishPageViewModel
import com.bupp.wood_spoon_eaters.features.restaurant.restaurant_page.RestaurantPageViewModel
import com.bupp.wood_spoon_eaters.features.reviews.ReviewsViewModel
import com.bupp.wood_spoon_eaters.features.splash.SplashViewModel
import com.bupp.wood_spoon_eaters.features.upsale.cart.CartViewModel
import com.bupp.wood_spoon_eaters.features.upsale.upsale.UpSaleViewModel
import com.bupp.wood_spoon_eaters.features.upsale.data_source.repository.UpSaleRepository
import com.bupp.wood_spoon_eaters.managers.*
import com.bupp.wood_spoon_eaters.managers.location.LocationManager
import com.bupp.wood_spoon_eaters.network.base_repos.*
import com.bupp.wood_spoon_eaters.network.result_handler.ErrorManger
import com.bupp.wood_spoon_eaters.network.result_handler.ResultManager
import com.bupp.wood_spoon_eaters.repositories.*
import com.eatwoodspoon.logsender.Logger
import com.eatwoodspoon.analytics.AnalyticsEventReporter
import com.eatwoodspoon.auth.WoodSpoonAuthConfigurationProvider
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module


val appModule = module {

    factory { get<Context>().resources }
    single<WoodSpoonAuthConfigurationProvider> { EatersAuthConfigurationProvider(get()) }

    //dataSource
    single { MemoryAppReviewDataSource() }
    single { MemoryUpSaleItemsDataSource() }

    single { Logger.instance }
    //global
    single { FcmManager(get()) }
    single { UserSettings(get(), get()) }
    single { FlowEventsManager(get(), get()) }
    single { ErrorManger(get()) }
    single { ResultManager(get()) }

    //repos
    single { MetaDataRepository(get()) }
    single { MetaDataRepositoryImpl(get(), get()) }
    factory<FeatureFlagLocalDataSource> { FeatureFlagLocalDataSourceImpl(get()) }
    single<AppSettingsRepository> {
        AppSettingsRepositoryImpl(
            get(),
            get(),
            StaticFeatureFlagsListProvider(),
            get(),
            get(),
            get()
        )
    }
    single { FeedRepository(get(), get(), get(), get()) }
    single { FeedRepositoryImpl(get(), get()) }
    single { UserRepositoryImpl(get(), get()) }
    single { RestaurantRepository(get()) }
    single { RestaurantRepositoryImpl(get(), get()) }
    single { UserRepository(get(), get(), get(), get(), get(), get()) }
    single { OrderRepository(get(), get()) }
    single { OrderRepositoryImpl(get(), get()) }
    single { EaterDataRepository(get()) }
    single { EaterDataRepositoryImpl(get(), get()) }
    single { CampaignRepository(get(), get()) }
    single { CampaignRepositoryImpl(get(), get()) }
    single { UpSaleRepository(get(), get()) }

    //useCase
    single { PricingExperimentUseCase(get(), get()) }
    single { FeatureFlagDynamicContentUseCase(get()) }
    single { GetOnboardingVideoPathUseCase(get()) }
    single { GetOnboardingSlideListUseCase() }
    single { GetOnboardingAppSettingsSlidesDelayUseCase(get()) }
    single { GiftConfigUseCase(get()) }
    single { FeatureFlagLongFeedUseCase(get()) }
    single { FeatureFlagNewAuthUseCase(get()) }
    single { FeatureFlagFreeDeliveryUseCase(get()) }
    single { FeatureFlagTimeFilterUseCase(get()) }
    factory { PhoneNumberVerificationRequestCodeUseCase(get(), get()) }
    factory { SendPhoneVerificationUseCase(get(), get()) }


    //managers
    single { GlobalErrorManager() }
    single { PaymentManager(get(), get()) }
    single { LocationManager(get(), get()) }
    single { CampaignManager(get()) }
    single { FeatureFlagManager(get(), get()) }
    single { MediaUploadManager(get(), get()) }
    single { FeedDataManager(get(), get(), get()) }
    single { CartManager(get(), get(), get(), get()) }
    single { EaterDataManager(get(), get(), get(), get(), get()) }

    //mappers
    factory { GiftStateMapper() }

    //VIEW MODELS

    // analytics
    single { Firebase.analytics }
    single { EatersAnalyticsTracker(get(), get(), get()) }.bind(AnalyticsEventReporter::class)

    //bottom sheet
    viewModel { AddressMenuViewModel(get(), get(), get()) }

    //splash
    viewModel { SplashViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { SuperUserViewModel(get(), get()) }

    //login
    viewModel { LoginViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { OnboardingViewModel(get(), get(), get(), get(), get()) }

    //location
    viewModel { LocationAndAddressViewModel(get(), get(), get(), get(), get()) }
    viewModel { SelectAddressViewModel(get(), get(), get()) }
    viewModel { AddressMapVerificationViewModel(get(), get(), get()) }

    //time
    viewModel { TimePickerViewModel(get()) }
    viewModel { FeedTimeFilterViewModel(get()) }

    //New Order
    viewModel { CheckoutViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { PromoCodeViewModel(get(), get()) }
    viewModel { FeesAndTaxViewModel(get(), get()) }
    viewModel { GiftViewModel(get(), get(), get()) }
    viewModel { GiftActionsViewModel(get(), get()) }


    viewModel { UpSaleNCartViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { CartViewModel(get(), get(), get(), get(), get()) }
    viewModel { UpSaleViewModel(get(), get(), get()) }

    //main
    viewModel {
        MainViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    viewModel { FeedViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { SearchViewModel(get(), get(), get()) }
    viewModel { ReportIssueViewModel(get(), get(), get()) }

    viewModel { UpdateRequiredViewModel(get()) }

    viewModel { ActiveOrderTrackerViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { CancelOrderViewModel(get()) }

    //Profile
    viewModel { MyProfileViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { EditProfileViewModel(get(), get(), get()) }

    //New Edit Profile
    scope<EditProfileActivity> {

    }
    viewModel {
        com.bupp.wood_spoon_eaters.features.create_profile.details.EditProfileViewModel(
            get(),
            get(),
            get(),
            get()
        )
    }
    viewModel { EditProfileCodeViewModel(get(), get(), get()) }
    viewModel { SingleOrderDetailsViewModel(get(), get(), get()) }
    viewModel { OrdersHistoryViewModel(get(), get(), get()) }
    viewModel { CuisineChooserViewModel(get(), get()) }


    //support
    viewModel { SupportViewModel(get(), get(), get()) }
    viewModel { WebDocsViewModel(get(), get()) }


    //settings
    viewModel { SettingsViewModel(get(), get(), get(), get(), get()) }

    //RestaurantPage
    viewModel { RestaurantMainViewModel(get(), get(), get(), get()) }
    viewModel { RestaurantPageViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { DishPageViewModel(get(), get(), get(), get()) }
    viewModel { OrderCheckoutViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { ReviewsBSViewModel(get(), get()) }

    //Review Activity
    viewModel { ReviewsViewModel(get(), get(), get(), get()) }
}

