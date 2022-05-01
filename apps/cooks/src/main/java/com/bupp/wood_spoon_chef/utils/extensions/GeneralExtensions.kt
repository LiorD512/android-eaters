package com.bupp.wood_spoon_chef.utils.extensions

import android.content.Intent

fun Intent.clearStack(){
    this.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
    this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}