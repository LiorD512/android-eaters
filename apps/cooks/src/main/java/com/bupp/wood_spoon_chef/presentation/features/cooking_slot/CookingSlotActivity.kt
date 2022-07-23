package com.bupp.wood_spoon_chef.presentation.features.cooking_slot

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.base.CookingSlotParentFragment

const val NAVIGATE_TO_VIEW_COOKING_SLOT = "navigate_to_view_cooking_slot"
const val NAVIGATE_TO_CREATE_COOKING_SLOT = "navigate_to_create_cooking_slot"

const val KEY_NAVIGATION_DESTINATION = "key_navigation_destination"
const val KEY_SELECTED_DATE = "args_selected_date"
const val KEY_COOKING_SLOT_ID = "args_cooking_slot_id"

class CookingSlotActivity : AppCompatActivity() {

    fun newInstance(
        context: Context,
        destination: String = "",
        selectedDate: Long? = null,
        cookingSlotId: Long? = null
    ): Intent = Intent(context, CookingSlotActivity::class.java).apply {
        if (selectedDate != null) {
            putExtra(KEY_SELECTED_DATE, selectedDate)
        }

        if (cookingSlotId != null) {
            putExtra(KEY_COOKING_SLOT_ID, cookingSlotId)
        }

        putExtra(KEY_NAVIGATION_DESTINATION, destination)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cooking_slot)

        if (savedInstanceState == null) {
            val destination = intent.getStringExtra(KEY_NAVIGATION_DESTINATION)
            val cookingSlotId = intent.getLongExtra(KEY_COOKING_SLOT_ID, 0).takeIf { it > 0 }
            val selectedDate = intent.getLongExtra(KEY_SELECTED_DATE, 0).takeIf { it > 0 }

            supportFragmentManager.beginTransaction()
                .replace(R.id.cookingSlotActivityContainer, CookingSlotParentFragment.newInstance(
                    cookingSlotId = cookingSlotId,
                    selectedDate = selectedDate
                ))
                .commit()
        }
    }
}
