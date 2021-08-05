package com.bupp.wood_spoon_eaters.delete_me

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bupp.wood_spoon_eaters.databinding.ActivityTestBinding
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.cart_bottom_sheet.CartBottomSheet
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet.UpSaleNCartBottomSheet

class TestActivity : AppCompatActivity() {

    lateinit var binding: ActivityTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        UpSaleNCartBottomSheet().show(supportFragmentManager, "upsale")


        binding.btn.setOnClickListener {
            UpSaleNCartBottomSheet().show(supportFragmentManager, "upsale")
//            CartBottomSheet().show(supportFragmentManager, "upsale2")
        }

    }
}