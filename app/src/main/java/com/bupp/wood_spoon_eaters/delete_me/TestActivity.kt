package com.bupp.wood_spoon_eaters.delete_me

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.bupp.wood_spoon_eaters.databinding.ActivityTestBinding

class TestActivity : ComponentActivity() {

    lateinit var binding: ActivityTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setContent{
//            Text("hello world")
//        }
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
//
//        UpSaleNCartBottomSheet().show(supportFragmentManager, "upsale")
//
//
//        binding.btn.setOnClickListener {
//            UpSaleNCartBottomSheet().show(supportFragmentManager, "upsale")
//            CartBottomSheet().show(supportFragmentManager, "upsale2")
//        }

        binding.btn.setOnClickListener {
//            binding.orderPb.next()
        }
        binding.clearBtn.setOnClickListener {
//            binding.orderPb.setProgress(0)
        }
    }

}