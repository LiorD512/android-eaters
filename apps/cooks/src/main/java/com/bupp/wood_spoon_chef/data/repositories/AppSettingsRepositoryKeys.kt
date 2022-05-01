package com.bupp.wood_spoon_chef.data.repositories


fun AppSettingsRepository.getUpdateDialogTitle() = stringAppSetting("android_version_control_title")
        ?: ""

fun AppSettingsRepository.getUpdateDialogBody() = stringAppSetting("android_version_control_body")
        ?: ""

fun AppSettingsRepository.getUpdateDialogUrl() = stringAppSetting("android_version_control_link_chefs")
        ?: ""

// TODO("Move all the convenient methods from MetaDataRepository)