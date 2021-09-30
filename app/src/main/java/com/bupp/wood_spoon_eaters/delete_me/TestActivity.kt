package com.bupp.wood_spoon_eaters.delete_me

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.reviews.BottomSheetReviews

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

//        UpSaleBottomSheet().show(supportFragmentManager, "upsale")

        BottomSheetReviews().show(supportFragmentManager, "bottomsheet")


    }
}