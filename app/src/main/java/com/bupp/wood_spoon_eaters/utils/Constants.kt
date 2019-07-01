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

        //deliveryDetails Types
        const val DELIVERY_DETAILS_LOCATION = 0
        const val DELIVERY_DETAILS_TIME = 1

        //headerView types
        const val HEADER_VIEW_TYPE_FEED = 0
        const val HEADER_VIEW_TYPE_SEARCH = 1
        const val HEADER_VIEW_TYPE_SIGNUP = 2
        const val HEADER_VIEW_TYPE_BACK_TITLE = 3
        const val HEADER_VIEW_TYPE_BACK_TITLE_DONE = 4
        const val HEADER_VIEW_TYPE_BACK_TITLE_SAVE = 5
        const val HEADER_VIEW_TYPE_CLOSE_TITLE_SAVE = 6


        //input title view input type
        const val INPUT_TYPE_TEXT = 0
        const val INPUT_TYPE_NUMBER = 1
        const val INPUT_TYPE_MAIL = 2
        const val INPUT_TYPE_LONG_TEXT = 3
        const val INPUT_TYPE_FULL_NAME = 4
        const val INPUT_TYPE_TEXT_NO_TITLE = 5


        //tool tip type
        const val TOOL_TIP_SERVICE_FEE = 0


        //activity / dialog tags
        const val WELCOME_DIALOG_TAG = "welcome_dialog_tag"
        const val CREATE_ACCOUNT_TAG = "create_account_tag"

        const val FEED_TAG = "feed_tag"
        const val ADD_NEW_ADDRESS_TAG = "add_new_address_tag"
        const val DELIVERY_DETAILS_TAG = "delivery_details_tag"
        const val SETTINGS_TAG = "settings_tag"
        const val SUPPORT_TAG = "support_tag"





        const val ORDER_HISTORY_TAG = "order_history_tag"


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

    }
}


