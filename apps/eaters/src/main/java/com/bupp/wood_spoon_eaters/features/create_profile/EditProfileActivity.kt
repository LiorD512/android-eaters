package com.bupp.wood_spoon_eaters.features.create_profile

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.bupp.wood_spoon_eaters.R


interface EditProfileFragmentParent {
    fun onProfileFragmentDismissed()
}


class EditProfileActivity : AppCompatActivity(R.layout.activity_edit_profile),
    EditProfileFragmentParent {

    val navigation by lazy { findNavController(R.id.editProfileNav) }

    override fun onProfileFragmentDismissed() {
        finish()
    }

}

