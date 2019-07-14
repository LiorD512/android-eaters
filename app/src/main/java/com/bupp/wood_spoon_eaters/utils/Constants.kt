package com.bupp.wood_spoon_eaters.utils

import android.Manifest

class Constants {
    companion object {


        //client pressigned url types
        const val PRESIGNED_URL_THUMBNAIL = "thumbnail"
        const val PRESIGNED_URL_VIDEO = "video"

        //settings stuff
        const val SINGLE_SELECTION = 1
        const val MULTI_SELECTION = 5

        //prefs
        const val PREFS_KEY_TOKEN = "key_token"
        const val PREFS_KEY_IS_FIRST_TIME = "is_first_time"
        const val SHOW_NOT_AVAILABLE_DIALOG = "show_not_available_dialog"
        const val ENABLE_USER_LOCATION = "enable_user_location"
        const val ENABLE_STATUS_ALERTS = "enable_status_alerts"
        const val ENABLE_COMMERCIAL_EMAILS = "enable_commercial_emails"


        //action title view actionType:
        const val CALENDER_ACTION = 0
        const val NEW_INGREDIENT_ACTION = 1
        const val COUNTRY_CHOOSER_ACTION = 2
        const val LOCATION_CHOOSER_ACTION = 3

        //lastDeliveryDetails Types
        const val DELIVERY_DETAILS_LOCATION = 0
        const val DELIVERY_DETAILS_TIME = 1
        const val DELIVERY_DETAILS_PAYMENT = 2

        //headerView types
        const val HEADER_VIEW_TYPE_FEED = 0
        const val HEADER_VIEW_TYPE_SEARCH = 1
        const val HEADER_VIEW_TYPE_SIGNUP = 2
        const val HEADER_VIEW_TYPE_BACK_TITLE = 3
        const val HEADER_VIEW_TYPE_BACK_TITLE_DONE = 4
        const val HEADER_VIEW_TYPE_BACK_TITLE_SAVE = 5
        const val HEADER_VIEW_TYPE_CLOSE_TITLE_SAVE = 6
        const val HEADER_VIEW_TYPE_BACK_TITLE_SETTINGS = 7


        //input title view input type
        const val INPUT_TYPE_TEXT = 0
        const val INPUT_TYPE_NUMBER = 1
        const val INPUT_TYPE_MAIL = 2
        const val INPUT_TYPE_LONG_TEXT = 3
        const val INPUT_TYPE_FULL_NAME = 4
        const val INPUT_TYPE_TEXT_NO_TITLE = 5
//        const val INPUT_TYPE_LOCATION = 6


        //Price Range View
        const val PRICE_NOT_SELECTED = 0
        const val PRICE_GROUP_1 = 1
        const val PRICE_GROUP_2 = 2
        const val PRICE_GROUP_3 = 3
        const val PRICE_GROUP_4 = 4

        //UserImageView Image Size
        const val HEADER_IMAGE_SIZE = 0
        const val SMALL_IMAGE_SIZE = 1
        const val BIG_IMAGE_SIZE = 2
        const val BIGGEST_IMAGE_SIZE = 3

        //tool tip type
        const val TOOL_TIP_SERVICE_FEE = 0


        //activity / dialog tags
        const val WELCOME_DIALOG_TAG = "welcome_dialog_tag"
        const val CREATE_ACCOUNT_TAG = "create_account_tag"
        const val OFFER_DISH_TAG = "offer_dish_tag"
        const val DISH_OFFERED_TAG = "dish_offered_tag"


        const val FEED_TAG = "feed_tag"
        const val SEARCH_TAG = "search_tag"
        const val ADD_NEW_ADDRESS_TAG = "add_new_address_tag"
        const val DELIVERY_DETAILS_TAG = "delivery_details_tag"
        const val SETTINGS_TAG = "settings_tag"
        const val SUPPORT_TAG = "support_tag"
        const val LOCATION_CHOOSER_TAG = "location_chooser_tag"
        const val PICK_FILTERS_TAG = "pick_filters_tag"
        const val MY_PROFILE_TAG = "my_profile_tag"
        const val EDIT_MY_PROFILE_TAG = "edit_my_profile_tag"

        const val SINGLE_DISH_DIALOG = "single_dish_dialog"
        const val ADDRESS_DIALOG_TAG = "address_dialog"
        const val DELIVERY_TO_ADDRESS_DIALOG_TAG = "delivery_to_address_dialog"
        const val TIP_COURIER_DIALOG_TAG = "tip_courier_dialog"
        const val CONTACT_US_DIALOG_TAG = "contact_us_dialog"
        const val THANK_YOU_DIALOG_TAG = "thank_you_dialog"
        const val START_NEW_CART_DIALOG_TAG = "start_new_cart_dialog"
        const val DISH_SOLD_OUT_DIALOG_TAG = "dish_sold_out_dialog"
        const val ORDER_DATE_CHOOSER_DIALOG_TAG = "order_date_chooser_dialog"
        const val CERTIFICATES_DIALOG_TAG = "certificated_dialog"



        const val ORDER_HISTORY_TAG = "order_history_tag"


        //Search resource type
        const val RESOURCE_TYPE_COOK = "Cook"
        const val RESOURCE_TYPE_DISH = "Dish"



        //activity request code
        const val CHOOSER_ACT_RC = 901


        //img put action
        val PUT_ACTION_DISH_THUMBNAIL: Int = 1
        val PUT_ACTION_COOK_THUMBNAIL: Int = 2
        val PUT_ACTION_COOK_VIDEO: Int = 3


        //media chooser dialog
        const val MEDIA_TYPE_CAMERA = 111
        const val MEDIA_TYPE_GALLERY = 112
        const val MEDIA_TYPE_VIDEO = 169


        //permission const
        const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        const val READ_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
        const val WRITE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
        const val FINE_LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
        const val COARSE_LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION


        const val CAMERA_PERMISSION_REQUEST_CODE = 444
        const val READ_PERMISSION_REQUEST_CODE = 445
        const val WRITE_PERMISSION_REQUEST_CODE = 446
        const val LOCATION_PERMISSION_REQUEST_CODE = 420
        const val REQUEST_CHECK_SETTINGS = 121


    }
}


