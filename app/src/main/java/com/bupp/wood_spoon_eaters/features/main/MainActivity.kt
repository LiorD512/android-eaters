package com.bupp.wood_spoon_eaters.features.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.support.SupportDialog
import com.bupp.wood_spoon_eaters.utils.Constants


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showSupportDialog()
    }

    private fun showSupportDialog() {
        SupportDialog().show(supportFragmentManager, Constants.SUPPORT_TAG)
    }
}