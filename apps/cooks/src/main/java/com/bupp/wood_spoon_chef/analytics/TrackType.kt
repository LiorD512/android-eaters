package com.bupp.wood_spoon_chef.analytics

import androidx.annotation.StringDef

@StringDef(
    TrackedArea.BOTTOM_TABS,
)
@Retention(AnnotationRetention.SOURCE)
annotation class TrackedArea {
    companion object {
        const val BOTTOM_TABS = "bottom_tabs"
        const val ONBOARDING = "onboarding"
        const val PHONE_VERIFICATION = "phone_verification"
        const val OTP_VERIFICATION = "otp_verification"
        const val BECOME_A_CHEF = "become_a_chef"
        const val PICK_UP_ADDRESS = "pick_up_address"
        const val BUILD_WOOD_SPOON_BRAND = "build_wood_spoon_brand"
        const val ADD_COVER_PHOTO = "add_cover_photo"
        const val SMS_VERIFICATION = "sms_verification"
        const val ORDERS = "orders"
        const val CALENDAR = "calendar"
        const val DISHES = "dishes"
        const val EARNINGS = "earnings"
        const val ACCOUNT = "account"
    }
}

@StringDef(
    TrackedEvents.Onboarding.SCREEN_OPENED,
    TrackedEvents.Onboarding.CLICK_ON_GET_STARTED_BUTTON,
    TrackedEvents.PhoneNumber.SCREEN_OPENED,
    TrackedEvents.PhoneNumber.CLICK_ON_COUNTRY_DROP_DOWN,
    TrackedEvents.PhoneNumber.CLICK_ON_TERMS_OF_USE,
    TrackedEvents.PhoneNumber.CLICK_ON_NEXT_BUTTON,
    TrackedEvents.SMSVerification.SCREEN_OPENED,
    TrackedEvents.SMSVerification.CLICK_ON_RESEND,
    TrackedEvents.SMSVerification.INVALID_PHONE_NUMBER_ENTERED,
    TrackedEvents.SMSVerification.CLICK_ON_NEXT,
    TrackedEvents.BottomTabs.CLICK_ON_BOTTOM_TAB_ORDERS,
    TrackedEvents.BottomTabs.CLICK_ON_BOTTOM_TAB_CALENDAR,
    TrackedEvents.BottomTabs.CLICK_ON_BOTTOM_TAB_DISHES,
    TrackedEvents.BottomTabs.CLICK_ON_BOTTOM_TAB_EARNINGS,
    TrackedEvents.BottomTabs.CLICK_ON_BOTTOM_TAB_ACCOUNT,
    TrackedEvents.Orders.SCREEN_OPENED,
    TrackedEvents.Orders.OPENED_LISTS,
    TrackedEvents.Orders.OPENED_EMPTY_LISTS,
    TrackedEvents.Orders.CLICK_ON_ITEM,
    TrackedEvents.Orders.CLICK_ON_ITEM_SHARE,
    TrackedEvents.Orders.CLICK_ON_ITEM_MENU,
    TrackedEvents.Orders.CLICK_ON_ITEM_MENU_EDIT_SLOT,
    TrackedEvents.Orders.CLICK_ON_ITEM_MENU_CANCEL_SLOT,
    TrackedEvents.Calendar.SCREEN_OPENED,
    TrackedEvents.Dishes.SCREEN_OPENED,
    TrackedEvents.Earnings.SCREEN_OPENED,
    TrackedEvents.Account.SCREEN_OPENED,

)
@Retention(AnnotationRetention.SOURCE)
annotation class TrackedEvent

object TrackedEvents {

    object Onboarding {
        const val SCREEN_OPENED = "onboarding_screen_opened"
        const val CLICK_ON_GET_STARTED_BUTTON = "click_get_started" // TODO: click_on_get_started_button 
    }

    object PhoneNumber {
        const val SCREEN_OPENED = "phone_number_verification_screen_opened"
        const val CLICK_ON_COUNTRY_DROP_DOWN = "click_on_country_drop_down"
        const val CLICK_ON_TERMS_OF_USE = "click_on_terms_of_use"
        const val CLICK_ON_NEXT_BUTTON = "send_otp" // TODO: click_on_next_button
    }

    object OTPVerification {
        const val SCREEN_OPENED = "otp_verification_screen_opened"
        const val CLICK_ON_NEXT_BUTTON = "verify_otp" // TODO: click_on_next_button
    }

    object BecomeChef {
        const val SCREEN_OPENED = "become_a_chef_screen_opened"
        const val CLICK_ON_NEED_HELP = "click_on_need_help"
        const val CLICK_ON_ADD_PHOTO = "click_on_add_photo"
        const val CLICK_ON_OPEN_CAMERA = "click_on_open_camera"
        const val CLICK_ON_TAKE_FROM_GALLERY = "click_on_take_from_gallery"
        const val CLICK_ON_INPUT_FIRST_NAME = "click_on_input_first_name"
        const val CLICK_ON_INPUT_SECOND_NAME = "click_on_input_second_name"
        const val CLICK_ON_INPUT_EMAIL = "click_on_input_email"
        const val CLICK_ON_INPUT_BIRTHDAY = "click_on_input_birthday"
        const val CLICK_ON_INPUT_BIRTHDAY_HINT = "click_on_input_birthday_hint"
        const val CLICK_ON_INPUT_SSN = "click_on_input_ssn"
        const val CLICK_ON_INPUT_SSN_HINT = "click_on_input_ssn_hint"
        const val CLICK_ON_NEXT_BUTTON = "click_on_next_button"
    }

    object PickUpAddress {
        const val SCREEN_OPENED = "pick_up_address_screen_opened"
        const val CLICK_ON_NEED_HELP = "click_on_need_help"
        const val CLICK_ON_INPUT_ADDRESS = "click_on_input_address"
        const val CLICK_ON_INPUT_DETAILS = "click_on_input_details"
        const val CLICK_ON_INPUT_NOTES = "click_on_input_notes"
        const val CLICK_ON_NEXT_BUTTON = "click_on_next_button"
        const val CLICK_ON_BACK_BUTTON = "click_on_back_button"
    }

    object BuildWoodSpoonBrand {
        const val SCREEN_OPENED = "build_wood_spoon_brand_screen_opened"
        const val CLICK_ON_INPUT_KITCHEN_NAME = "click_on_input_kitchen_name"
        const val CLICK_ON_INPUT_DESCRIPTION = "click_on_input_description"
        const val CLICK_ON_COUNTRY_DROP_DOWN = "click_on_country_drop_down"
    }

    object AddCoverPhoto {
        const val SCREEN_OPENED = "add_cover_photo_screen_opened"
        const val CLICK_ON_NEED_HELP = "click_on_need_help"
        const val CLICK_ON_ADD_PHOTO = "click_on_add_photo"
        const val CLICK_ON_ADD_VIDEO = "click_on_add_video"
        const val CLICK_ON_SUBMIT_RESTAURANT_BUTTON = "click_on_submit_restaurant_button"
    }

    object SMSVerification {
        const val SCREEN_OPENED = "sms_verification_screen_opened"
        const val CLICK_ON_RESEND = "click_on_resend"
        const val CLICK_ON_NEXT = "click_on_next"
        const val INVALID_PHONE_NUMBER_ENTERED = "invalid_phone_number_entered"
    }

    object BottomTabs {
        const val CLICK_ON_BOTTOM_TAB_ORDERS = "clicked_on_bottom_tab_orders"
        const val CLICK_ON_BOTTOM_TAB_CALENDAR = "clicked_on_bottom_tab_calendar"
        const val CLICK_ON_BOTTOM_TAB_DISHES = "click_on_bottom_tab_dishes"
        const val CLICK_ON_BOTTOM_TAB_EARNINGS = "click_on_bottom_tab_earnings"
        const val CLICK_ON_BOTTOM_TAB_ACCOUNT = "click_on_bottom_tab_account"
    }

    object Orders {
        const val SCREEN_OPENED = "orders_screen_opened"
        const val OPENED_LISTS = "lists_opened"
        const val OPENED_EMPTY_LISTS = "lists_opened_with_empty_data"
        const val OPENED_EMPTY_LISTS_CLICK_CALENDAR = "click_calendar" //TODO: empty_lists_click_calendar
        const val CLICK_ON_ITEM = "click_on_item"
        const val CLICK_ON_ITEM_SHARE = "click_on_item_share"
        const val CLICK_ON_ITEM_MENU = "click_on_item_menu"
        const val CLICK_ON_ITEM_MENU_EDIT_SLOT = "click_on_item_menu_edit_slot"
        const val CLICK_ON_ITEM_MENU_CANCEL_SLOT = "chef_canceled_order" // TODO: click_on_item_menu_cancel_slot
    }

    object Calendar {
        const val SCREEN_OPENED = "calendar_screen_opened"
        const val CLICK_ON_ITEM_CREATE_COOKING_SLOT = "create_new_cooking_slot" // TODO: click_on_item_create_cooking_slot
        const val CLICK_ON_NEED_HELP_BUTTON = "click_need_help" // TODO: click_on_need_help_button
    }

    object Dishes {
        const val SCREEN_OPENED = "dishes_screen_opened"
        const val CLICK_ON_CREATE_DISH_BUTTON = "click_add_dish" // TODO: click_on_create_dish_button
        const val CLICK_ON_SEARCH = "click_search_for_dish" // TODO: click_on_search
        const val CLICK_ON_ITEM_DISH = "click_on_item_dish"
        const val CLICK_ON_ITEM_DISH_MENU = "Click_on_item_dish_menu"
        const val CLICK_ON_ITEM_DISH_MENU_EDIT = "click_edit_dish" // TODO: click_on_item_dish_menu_edit
        const val CLICK_ON_ITEM_DISH_MENU_DUPLICATE = "click_duplicate_dish" // TODO: click_on_item_dish_menu_duplicate 
        const val CLICK_ON_ITEM_DISH_MENU_UNPUBLISH = "click_unpublish_dish" // TODO: click_on_item_dish_menu_unpublish 
        const val CLICK_ON_ITEM_DISH_MENU_DELETE = "click_delete_dish" // TODO: click_on_item_dish_menu_delete 
    }

    object Earnings {
        const val SCREEN_OPENED = "earnings_screen_opened"
    }

    object Account {
        const val SCREEN_OPENED = "account_screen_opened"
    }
}