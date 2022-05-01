package com.bupp.wood_spoon_chef.common

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.bupp.wood_spoon_chef.R

fun Activity.navigateToCallApp(tel: String) {
    Intent(
        Intent.ACTION_DIAL,
        Uri.parse("tel:$tel")
    ).also(this::startActivity)
}

fun Activity.navigateToSMSApp(phone: String) {
    Intent(
        Intent.ACTION_SENDTO,
        Uri.parse("smsto:$phone")
    ).apply {
        putExtra("sms_body", getString(R.string.support_frag_sms_sentence))
    }.also(this::startActivity)
}