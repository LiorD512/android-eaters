package com.bupp.wood_spoon_eaters.utils

import android.Manifest

class Constants {
    companion object {


        //client pressigned url types
        const val PRESIGNED_URL_THUMBNAIL = "thumbnail"
        const val PRESIGNED_URL_VIDEO = "video"

        //settings stuff
        const val COOKING_TIME_PRE_COOKED = "pre_cooked"
        const val COOKING_TIME_ON_THE_SPOT = "made_to_order"
        const val SINGLE_SELECTION = 1
        const val MULTI_SELECTION = 5

        //signup stages
        const val SIGNUP_STAGE_KEY = "signup_level_key"
        const val SIGNUP_STAGE_ACCOUNT = 1
        const val SIGNUP_STAGE_STORY = 2

        //prefs
        const val PREFS_KEY_TOKEN = "key_token"
        const val PREFS_KEY_IS_FIRST_TIME = "is_first_time"
        const val SHOW_NOT_AVAILABLE_DIALOG = "show_not_available_dialog"

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


        //tool tip type
        const val TOOL_TIP_SERVICE_FEE = 0


        //activity / dialog tags
        const val WELCOME_DIALOG_TAG = "welcome_dialog_tag"
        const val HIDE_DISH_DIALOG_TAG = "hide_dish_dialog"
        const val DISH_CHOOSER_DIALOG_TAG = "dish_chooser_dialog"
        const val COOKING_SLOT_CHOOSER_TAG = "cooking_slot_chooser_dialog"
        const val CUSTOM_CHOOSER_DIALOG_TAG = "custom_chooser_dialog_tag"
        const val NOT_AVAILABLE_DIALOG_TAG = "not_available_dialog_tag"
        const val CANCEL_ORDER_DIALOG_TAG = "cancel_order_dialog_tag"

        const val CHOOSER_FRAG_TAG = "chooser_frag_tag"
        const val MEDIA_CHOOSER_TAG = "media_chooser_tag"



        const val CREATE_ACCOUNT_TAG = "create_account_tag"

        const val FEED_TAG = "my_dishes_tag"
        const val DELIVERY_DETAILS_TAG = "delivery_details_tag"
        const val ORDER_HISTORY_TAG = "order_history_tag"

//        const val MY_DISHES_TAG = "my_dishes_tag"
//        const val CALENDAR_TAG = "calendar_tag"
//        const val ORDERS_TAG = "orders_tag"
//        const val EARNINGS_TAG = "earnings_tag"
//        const val PROFILE_TAG = "profile_tag"
//        const val EDIT_PROFILE_TAG = "edit_profile_tag"
//        const val NOTIFICATIONS_TAG = "notifications_tag"
//        const val SINGLE_DISH_TAG = "single_dish_tag"
//        const val SUPPORT_TAG = "support_tag"
//        const val ORDERS_HISTORY_TAG = "orders_history_tag"


        //your story permissions reuest code
        const val YOUR_STORY_PERMISSION_REQUEST = 10

        //cooking slot adapter types
//        const val TYPE_ORDERS = 12
//        const val TYPE_CALENDAR = 13

        //activity request code
        const val CHOOSER_ACT_RC = 901
        const val NEW_DISH_TO_MY_DISHES_RC = 902
        const val NEW_DISH_TO_CALENDAR_RC = 903


        //img put action
        val PUT_ACTION_DISH_THUMBNAIL: Int = 1
        val PUT_ACTION_COOK_THUMBNAIL: Int = 2
        val PUT_ACTION_COOK_VIDEO: Int = 3


        //media chooser dialog
        const val MEDIA_TYPE_CAMERA = 111
        const val MEDIA_TYPE_GALLERY = 112
        const val MEDIA_TYPE_VIDEO = 169

        //dish chooser dialog
        const val DISH_CHOOSER_HISTORY = 211
        const val DISH_CHOOSER_EDIT = 212
        const val DISH_CHOOSER_HIDE = 213

        //changeable settings
        const val ADDRESS_SETTING = 441
        const val PAYMENT_SETTING = 442

        //UserImageView Image Size
        const val SMALL_IMAGE_SIZE = 1
        const val BIG_IMAGE_SIZE = 2

        //UserImageView types
        const val USER_IMAGE_VIEW_SHORT_VIDEO = 419
        const val USER_IMAGE_VIEW_USER_PICTURE = 420

        //permission const
        const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        const val READ_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
        const val WRITE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE

        const val CAMERA_PERMISSION_REQUEST_CODE = 444
        const val READ_PERMISSION_REQUEST_CODE = 445
        const val WRITE_PERMISSION_REQUEST_CODE = 446
    }
}


