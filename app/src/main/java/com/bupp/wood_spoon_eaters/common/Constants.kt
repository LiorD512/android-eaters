package com.bupp.wood_spoon_eaters.common

class Constants {
    companion object {

        const val FEED_SECTION_TYPE_COUPONS = "available_coupons"
        const val FEED_SECTION_TYPE_RESTAURANT = "restaurant_overview"
        const val FEED_EMPTY_NO_CHEF = "feed_empty_no_chefs"
        const val SECTION_EMPTY_NO_CHEF = "section_empty_no_chefs"
        const val RESTAURANT_SECTION_TYPE_DISH = "dish"
        const val RESTAURANT_SECTION_TYPE_SEE_MORE = "see_more"

        //feed banners
        const val NO_BANNER = -1
        const val BANNER_NO_GPS = 0
        const val BANNER_MY_LOCATION = 1
        const val BANNER_KNOWN_ADDRESS = 2
        const val BANNER_NO_AVAILABLE_DISHES = 3

        //login activity state
        const val LOGIN_STATE = "login_state"
        const val LOGIN_STATE_WELCOME = 0
        const val LOGIN_STATE_VERIFICATION = 1
        const val LOGIN_STATE_CREATE_ACCOUNT = 2

        const val ENABLE_USER_LOCATION = "enable_user_location"


        //lastDeliveryDetails Types
        const val DELIVERY_DETAILS_LOCATION = 0
        const val DELIVERY_DETAILS_TIME = 1
        const val DELIVERY_DETAILS_PAYMENT = 2
        const val DELIVERY_DETAILS_CHECKOUT_DELIVERY = 3
        const val DELIVERY_DETAILS_LOCATION_PROFILE = 4
        const val DELIVERY_DETAILS_NATIONWIDE_SHIPPING = 5
        const val DELIVERY_DETAILS_PROMO_CODE = 6

        //headerView types
        const val HEADER_VIEW_TYPE_FEED = 0
        const val HEADER_VIEW_TYPE_SEARCH = 1
        const val HEADER_VIEW_TYPE_SIGNUP = 2
        const val HEADER_VIEW_TYPE_BACK_TITLE = 3
        const val HEADER_VIEW_TYPE_CLOSE_TITLE = 11
        const val HEADER_VIEW_TYPE_BACK_TITLE_DONE = 4
        const val HEADER_VIEW_TYPE_BACK_TITLE_SAVE = 5
        const val HEADER_VIEW_TYPE_CLOSE_TITLE_SAVE = 6
        const val HEADER_VIEW_TYPE_CLOSE_NO_TITLE = 7
        const val HEADER_VIEW_TYPE_CLOSE_TITLE_DONE = 8
        const val HEADER_VIEW_TYPE_CLOSE_TITLE_NEXT = 9

        //Checkout header icon types
        const val HEADER_ICON_BACK = 0
        const val HEADER_ICON_CLOSE = 1

        //Resizeable image sizes
        const val RESIZEABLE_IMAGE_SMALL = 0
        const val RESIZEABLE_IMAGE_MEDIUM = 1
        const val RESIZEABLE_IMAGE_LARGE = 2

        //Lottie animation types
        const val LOTTIE_ANIM_PB = 0
        const val LOTTIE_ANIM_SELECT_ADDRESS = 1
        const val LOTTIE_ANIM_LOCATION_PERMISSION = 2
        const val LOTTIE_ANIM_EMPTY_FEED = 3

        //TagView types
        const val TAG_VIEW_FEED = 0
        const val TAG_VIEW_DISH = 1


        //input title view input type
        const val INPUT_TYPE_TEXT = 0
        const val INPUT_TYPE_TEXT_WITH_NUMBER = 7
        const val INPUT_TYPE_NUMBER = 1
        const val INPUT_TYPE_MAIL = 2
        const val INPUT_TYPE_LONG_TEXT = 3
        const val INPUT_TYPE_FULL_NAME = 4
        const val INPUT_TYPE_TEXT_NO_TITLE = 5
        const val INPUT_TYPE_DONE_BTN = 6
        const val INPUT_TYPE_PHONE = 8
        const val INPUT_TYPE_CAPITAL_TEXT = 9

        //UserImageView Image Size
        const val HEADER_IMAGE_SIZE = 0
        const val SMALL_IMAGE_SIZE = 1
        const val BIG_IMAGE_SIZE = 2
        const val BIGGEST_IMAGE_SIZE = 3
        const val MANY_COOKS_VIEW = 4

        //tool tip type
        const val TOOL_TIP_SERVICE_FEE = 0
        const val TOOL_TIP_CHECKOUT_SERVICE_FEE = 2
        const val TOOL_TIP_CHECKOUT_DELIVERY_FEE = 3
        const val TOOL_TIP_MINMUM_ORDER_FEE = 4
        const val TOOL_TIP_COURIER_TIP = 5
        const val FEES_AND_ESTIMATED_TAX = 6

        //Floating button type
        const val VIEW_CART = 0
        const val ADD_TO_CART = 1
        const val PLACE_ORDER = 2


        const val ARG_RESTAURANT = "args_restaurant"
        const val ARG_DISH = "args_dish"


        const val TIP_COURIER_DIALOG_TAG = "tip_courier_dialog"
        const val TRACK_ORDER_DIALOG_TAG = "track_order_dialog_tag"
        const val RATE_LAST_ORDER_DIALOG_TAG = "rate_last_order_dialog_tag"
        const val RATINGS_DIALOG_TAG = "ratings_dialog_tag"
        const val CLEAR_CART_RESTAURANT_DIALOG_TAG = "clear_cart_rest_dialog"
        const val CLEAR_CART_COOKING_SLOT_DIALOG_TAG = "clear_cart_cooking_slot_dialog"
        const val LOGOUT_DIALOG_TAG = "logout_dialog_tag"
        const val WEB_DOCS_DIALOG_TAG = "web_docs_dialog_tga"
        const val VIDEO_VIEW_DIALOG = "video_view_dialog_tag"
        const val WEB_DOCS_DIALOG = "web_docs_dialog_tag"
        const val UPDATE_REQUIRED_DIALOG = "update_required_dialog"
        const val ERROR_DIALOG = "error_dialog"
        const val NATIONWIDE_SHIPPING_SELECT_DIALOG = "nationwide_shipping_select_dialog"
        const val WS_ERROR_DIALOG = "ws_error_dialog"
        const val WRONG_ADDRESS_DIALOG = "wrong_address_dialog"
        const val SUPER_USER_DIALOG = "super_user_dialog"
        const val MEDIA_CHOOSER_DIALOG = "media_chooser_dialog"
        const val TITLE_BODY_DIALOG = "title_body_dialog"


        //bottom sheet
        const val TIME_PICKER_BOTTOM_SHEET = "time_picker_bottom_sheet"
        const val ADDRESS_MENU_BOTTOM_SHEET = "address_menu_bottom_sheet"
        const val COUNTRY_CODE_BOTTOM_SHEET = "country_code_bottom_sheet"
        const val LOCATION_PERMISSION_BOTTOM_SHEET = "location_permission_bottom_sheet"
        const val CAMPAIGN_BOTTOM_SHEET = "campaign_bottom_sheet"
        const val SETTINGS_BOTTOM_SHEET = "settings_bottom_sheet"
        const val SUPPORT_CENTER_BOTTOM_SHEET = "support_center_bottom_sheet"
        const val JOIN_AS_CHEF_BOTTOM_SHEET = "join_as_chef_bottom_sheet"
        const val SINGLE_ORDER_DETAILS_BOTTOM_SHEET = "single_order_details_bottom_sheet"
        const val REPORT_ISSUE_BOTTOM_SHEET = "report_issue_bottom_sheet"
        const val FREE_TEXT_BOTTOM_SHEET = "free_text_bottom_sheet"
        const val DELETE_ACCOUNT_BOTTOM_SHEET = "delete_account_bottom_sheet"
        const val EDIT_PROFILE_BOTTOM_SHEET = "edit_profile_bottom_sheet"
        const val FEES_AND_tAX_BOTTOM_SHEET = "fees_and_tax_bottom_sheet"
        const val UPSALE_AND_CART_BOTTOM_SHEET = "upsale_and_cart_bottom_sheet"
        const val TRACK_ORDER_MENU_BOTTOM_SHEET = "track_order_menu_bottom_sheet"

        //Location navigation destination labels
        const val LOCATION_DESTINATION_SELECT_ADDRESS = "select_address"
        const val LOCATION_DESTINATION_MAP_VERIFICATION = "address_verification_map"
        const val LOCATION_DESTINATION_FINAL_DETAILS = "final_address_details"


        //cancel order dialog stage
        const val CANCEL_ORDER_DIALOG_TAG = "cancel_order_dialog_tag"
        const val CANCEL_ORDER_STAGE_1 = 1
        const val CANCEL_ORDER_STAGE_2 = 2
        const val CANCEL_ORDER_STAGE_3 = 3


        //Search resource type
        const val RESOURCE_TYPE_COOK = "Cook"
        const val RESOURCE_TYPE_DISH = "Dish"


        //Tip courier Selection arg
        const val TIP_NOT_SELECTED = 0
        const val TIP_10_PERCENT_SELECTED = 10
        const val TIP_15_PERCENT_SELECTED = 15
        const val TIP_20_PERCENT_SELECTED = 20
        const val TIP_CUSTOM_SELECTED = 666


        //media chooser dialog
        const val MEDIA_TYPE_CAMERA = 111
        const val MEDIA_TYPE_GALLERY = 112

        const val WEB_DOCS_PRIVACY = 0
        const val WEB_DOCS_TERMS = 1
        const val WEB_DOCS_QA = 2


        const val LOCATION_PERMISSION_REQUEST_CODE = 420
        const val PHONE_CALL_PERMISSION_REQUEST_CODE = 430

        //UXCAM Events
        const val EVENT_SEND_OTP = "send_otp"
        const val EVENT_VERIFY_OTP = "verify_otp"
        const val EVENT_CREATE_ACCOUNT = "create_account"
        const val EVENT_CLICK_GET_STARTED = "click_get_started"
        const val EVENT_ON_EXISTING_USER_LOGIN_SUCCESS = "existing_user_login_success"
        const val EVENT_SWIPE_BETWEEN_DISHES = "swipe_between_dishes_feed"
        const val EVENT_CLICK_RESTAURANT = "click_on_home_chef"
        const val EVENT_CLICK_ON_DISH = "click_on_dish"
        const val EVENT_CAMPAIGN_INVITE = "campaign_invite"
        const val EVENT_ORDER_PLACED = "order_placed"
        const val EVENT_CLICK_VIDEO_IN_RESTAURANT = "click_video_in_home_chef"
        const val EVENT_CLICK_TRACK_YOUR_ORDER = "click_track_your_order"
        const val EVENT_LOCATION_PERMISSION = "location_persuasion"
        const val EVENT_SWIPED_ADD_DISH = "swipe_add_dish_in_chef_page"
        const val EVENT_SWIPED_REMOVE_DISH = "swipe_remove_dish_in_chef_page"
        const val EVENT_LIKE_RESTAURANT = "add_chef_to_favorites"
        const val EVENT_SHARE_RESTAURANT = "share_home_chef"
        const val EVENT_CHANGE_DELIVERY_DATE = "change_feed_date" //feed
        const val EVENT_CHANGE_COOKING_SLOT_DATE = "change_cooking_slot_date" //restaurant header
        const val EVENT_CHANGE_COOKING_SLOT = "change_cooking_slot" //restaurant picker
        const val EVENT_CHANGE_DELIVERY_TIME = "change_delivery_time" //checkout
        const val EVENT_CLICK_VIEW_MORE = "click_view_more_home_chef_description"
        const val EVENT_OPEN_DEEP_LINK = "open_app_deeplink_home_chef"
        const val EVENT_ADD_DISH = "add_dish_to_cart"
        const val EVENT_CHANGE_DISH_QUANTITY = "change_dish_quantity"
        const val EVENT_SWIPE_ADD_DISH_IN_CART = "swipe_add_dish_in_cart"
        const val EVENT_SWIPE_REMOVE_DISH_IN_CART = "swipe_remove_dish_in_cart"
        const val EVENT_CLICK_DISH_IN_CART = "click_on_dish_from_cart"
        const val EVENT_UPDATE_DISH = "update_dish"
        const val EVENT_UPDATE_DELIVERY_ADDRESS = "change_delivery_address"
        const val EVENT_CLICK_EDIT_ORDER = "click_edit_order"
        const val EVENT_CLICK_BACK_FROM_CHECKOUT = "click_back_from_checkout"
        const val EVENT_CLICK_ON_PROMO_CODE = "click_on_promo_code_section"
        const val EVENT_SUBMIT_PROMO_CODE = "submit_promo_code"
        const val EVENT_CLEAR_CART = "clear_cart"
        const val EVENT_CLICK_PAYMENT = "click_payment"
        const val EVENT_CLICK_SIGN_OUT = "sign_out"

        const val EVENT_ADD_ADDITIONAL_DISH = "add_upsale_dish_to_cart"
        const val EVENT_TRACK_ORDER_CLICK = "clicked_track_your_order"
        const val EVENT_PROCEED_TO_CHECKOUT = "proceed_checkout"
        const val EVENT_CLICK_TIP = "click_tip"



        const val MINIMUM_LOCATION_DISTANCE = 100
    }

}


