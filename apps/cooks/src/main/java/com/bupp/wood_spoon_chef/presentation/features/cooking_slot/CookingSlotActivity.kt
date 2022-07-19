package com.bupp.wood_spoon_chef.presentation.features.cooking_slot

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.base.CookingSlotParentFragment

const val NAVIGATE_TO_OVERVIEW_COOKING_SLOT = "navigate_to_overview_cooking_slot"

const val KEY_NAVIGATION_DESTINATION = "key_navigation_destination"
const val KEY_SELECTED_DATE = "args_selected_date"

class CookingSlotActivity : AppCompatActivity() {

    fun newInstance(
        context: Context,
        destination: String? = null,
        selectedDate: Long?
    ): Intent = Intent(context, CookingSlotActivity::class.java).apply {
        if (selectedDate != null) {
            putExtra(KEY_SELECTED_DATE, selectedDate)
        }

        if (destination != null) {
            putExtra(KEY_NAVIGATION_DESTINATION, destination)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cooking_slot)

        if (savedInstanceState == null) {
            if (intent.getStringExtra(KEY_NAVIGATION_DESTINATION) != null) {
                when (intent.getStringExtra(KEY_NAVIGATION_DESTINATION)) {
                    NAVIGATE_TO_OVERVIEW_COOKING_SLOT -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.cookingSlotActivityContainer, CookingSlotParentFragment.newInstance())
                            .commitAllowingStateLoss()
                    }
                    else -> {}
                }
            }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.cookingSlotActivityContainer, CookingSlotParentFragment.newInstance())
                    .commitAllowingStateLoss()

        }
    }
}
