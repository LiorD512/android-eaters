package com.bupp.wood_spoon_chef.common

class Constants {
    companion object {

        const val PHONE_CALL_PERMISSION_REQUEST_CODE = 430


        const val PREFS_KEY_IS_FIRST_TIME = "is_first_time"
        const val SHOW_NOT_AVAILABLE_DIALOG = "show_not_available_dialog"

        const val HEADER_VIEW_TYPE_TITLE = 0
        const val HEADER_VIEW_TYPE_TITLE_BACK = 1
        const val HEADER_VIEW_TYPE_TITLE_BACK_SAVE = 2
        const val HEADER_VIEW_TYPE_TITLE_CLOSE_DONE = 3
        const val HEADER_VIEW_TYPE_TITLE_CLOSE_NEXT = 4
        const val HEADER_VIEW_TYPE_TITLE_NOTIFICATIONS_SETTINGS = 6
        const val HEADER_VIEW_TYPE_TITLE_CLOSE = 7
        const val HEADER_VIEW_TYPE_TITLE_BACK_NEXT = 8
        const val HEADER_VIEW_TYPE_TITLE_ADD = 9

        //bottomBar types
        const val BOTTOM_VIEW_ORDERS = 0
        const val BOTTOM_VIEW_CALENDER = 1
        const val BOTTOM_VIEW_DISHES = 2
        const val BOTTOM_VIEW_EARNINGS = 3
        const val BOTTOM_VIEW_PROFILE = 4

        //bottom sheet
        const val COUNTRY_CODE_BOTTOM_SHEET = "country_code_bottom_sheet"
        const val CREATE_ACCOUNT_HELP_BOTTOM_SHEET = "create_account_help_bottom_sheet"
        const val CUSTOM_STRING_CHOOSER_BOTTOM_SHEET = "custom_string_chooser_bottom_sheet"
        const val SAVE_AS_DRAFT_BOTTOM_SHEET = "save_as_draft_bottom_sheet"
        const val DISH_CATEGORIES_BOTTOM_SHEET_TAG = "dish_categories_bottom_sheet"
        const val SUPPORT_BOTTOM_SHEET = "support_bottom_sheet"
        const val DELETE_ACCOUNT_BOTTOM_SHEET = "support_bottom_sheet"

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
        const val INPUT_TYPE_TEXT_CAP_SENTENCE = 9
        const val INPUT_TOOL_TIP_SSN = 1


        //tool tip type
        const val TOOL_TIP_SERVICE_FEE = 0
        const val TOOL_TIP_SSN = 1
        const val TOOL_TIP_PROFILE_FLAG = 2


        const val HIDE_DISH_DIALOG_TAG = "hide_dish_dialog"
        const val DISH_CHOOSER_DIALOG_TAG = "dish_chooser_dialog"
        const val COOKING_SLOT_CHOOSER_TAG = "cooking_slot_chooser_dialog"
        const val NOT_AVAILABLE_DIALOG_TAG = "not_available_dialog_tag"
        const val CANCEL_ORDER_DIALOG_TAG = "cancel_order_dialog_tag"
        const val NOT_YET_APPROVED_DIALOG = "not_yet_approved_dialog_tag"
        const val SINGLE_ORDER_DIALOG = "single_order_dialog"
        const val LOGOUT_DIALOG_TAG = "logout_dialog"
        const val PAYMENT_METHOD_DIALOG = "payment_method_dialog_tag"
        const val WEB_DOCS_DIALOG = "web_docs_dialog_tag"
        const val VIDEO_PLAYER_DIALOG = "video_player_dialog"
        const val UPDATE_REQUIRED_DIALOG = "update_required_dialog"
        const val NATIONWIDE_SHIPPING_INFO_DIALOG = "nationwide_dialog"
        const val WS_ERROR_DIALOG = "ws_error_dialog"
        const val TOOL_TIP_DIALOG = "tooltip_dialog"
        const val NO_NETWORK_DIALOG = "no_network_dialog"
        const val NEW_DISH_DIALOG = "new_dish_dialog"
        const val NEW_DISH_DONE_DIALOG = "new_dish_done_dialog"
        const val SUPER_USER_DIALOG = "super_user_dialog"
        const val DATE_PICKER_DIALOG = "date_picker_dialog"
        const val EDIT_ACCOUNT_DIALOG = "edit_account_dialog"
        const val EDIT_KITCHEN_DIALOG = "edit_kitchen_dialog"
        const val REVIEWS_DIALOG = "edit_kitchen_dialog"
        const val SATISFACTION_DIALOG = "satisfaction_dialog"
        const val COOKING_SLOT_DIALOG = "cooking_slot_dialog"


        const val CHOOSER_FRAG_TAG = "chooser_frag_tag"
        const val MEDIA_CHOOSER_TAG = "media_chooser_tag"

        const val SINGLE_DISH_TAG = "single_dish_tag"

        //Analytics Events
        const val EVENTS_EDIT_COOKING_SLOT = "edit_cooking_slot"
        const val EVENTS_CREATED_COOKING_SLOT = "created_cooking_slot"
        const val EVENTS_EDIT_DISH = "edit_existing_dish"
        const val EVENTS_NEW_DISH = "create_new_dish"

        //WEB DOCS TYPE
        const val WEB_DOCS_PRIVACY = 0
        const val WEB_DOCS_TERMS = 1
        const val WEB_DOCS_QA = 2

        //Lottie animation types
        const val LOTTIE_ANIM_PB = 0
        const val LOTTIE_ANIM_SELECT_ADDRESS = 1
        const val LOTTIE_ANIM_PAYMENTS = 2
        const val LOTTIE_ANIM_NEW_DISH_PRICE = 3
        const val LOTTIE_ANIM_NEW_DISH_VIDEO = 4
        const val LOTTIE_ANIM_NEW_DISH_NAME = 5


        //order status
        const val PREPARATION_STATUS_NULL = -1
        const val PREPARATION_STATUS_IDLE = 0
        const val PREPARATION_STATUS_RECEIVED = 1
        const val PREPARATION_STATUS_IN_PROGRESS = 2
        const val PREPARATION_STATUS_COMPLETED = 3
        const val PREPARATION_STATUS_DELIVERED = 5
        const val PREPARATION_STATUS_SHIPPED = 6
        const val PREPARATION_STATUS_CANCELLED = 7



        //media chooser dialog
        const val MEDIA_TYPE_CAMERA = 111
        const val MEDIA_TYPE_GALLERY = 112
        const val MEDIA_TYPE_VIDEO = 169

        //dishPricing chooser dialog
        const val DISH_CHOOSER_HISTORY = 211
        const val DISH_CHOOSER_EDIT = 212
        const val DISH_CHOOSER_HIDE = 213
        const val DISH_CHOOSER_ACTIVATE = 214
        const val DISH_CHOOSER_UNPUBLISH = 215
        const val DISH_CHOOSER_DUPLICATE = 216

        //UserImageView Image Size
        const val SMALL_IMAGE_SIZE = 1
        const val BIG_IMAGE_SIZE = 2

        const val USER_IMAGE_VIEW_USER_PICTURE = 420

        //Upload Manager
        const val MEDIA_TYPE_COOK_IMAGE = 100
        const val MEDIA_TYPE_COOK_STORY = 200
        const val MEDIA_TYPE_DISH_IMAGE = 300
        const val MEDIA_TYPE_DISH_MAIN_IMAGE = 301
        const val MEDIA_TYPE_DISH_VIDEO = 400
        const val MEDIA_TYPE_COVER_IMAGE = 500

        //cook presigned url types
        const val PRESIGNED_URL_THUMBNAIL = "thumbnail"
        const val PRESIGNED_URL_COVER = "cover_photo"
        const val PRESIGNED_URL_VIDEO = "video"

        //args
        const val ARG_SELECTED_DATE = "args_selected_date"

        //        const val MEDIA_THUMBNAIL_TYPE = 50

//        const val CAMERA_PERMISSION_REQUEST_CODE = 444
//        const val READ_PERMISSION_REQUEST_CODE = 445
//        const val WRITE_PERMISSION_REQUEST_CODE = 446
    }
}


