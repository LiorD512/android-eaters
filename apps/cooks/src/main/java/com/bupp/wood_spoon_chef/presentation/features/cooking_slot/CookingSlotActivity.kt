package com.bupp.wood_spoon_chef.presentation.features.cooking_slot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.base.CookingSlotParentFragment
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityScope
import org.koin.core.scope.Scope

class CookingSlotActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cooking_slot)

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.cookingSlotActivityContainer, CookingSlotParentFragment())
                .commitAllowingStateLoss()
        }

    }
}
