package com.bupp.wood_spoon_eaters.features.create_profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import kotlinx.parcelize.Parcelize


interface EditProfileParent {
    fun onProfileFragmentDismissed()

    val startArgs: EditProfileStartArgs
}

@Parcelize
data class EditProfileStartArgs(
    val alternativeReasonDescription: String? = null
) : Parcelable


class EditProfileActivity : AppCompatActivity(R.layout.activity_edit_profile), EditProfileParent {
    override val startArgs: EditProfileStartArgs by lazy {
        intent.getParcelableExtra<EditProfileStartArgs>(EXTRA_START_ARGS)
            ?: throw IllegalStateException("No start args provided")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, EditProfileParentFragment())
                .commit()
        }
    }

    override fun onProfileFragmentDismissed() {
        finish()
    }

    companion object {
        const val EXTRA_START_ARGS = "start_args"

        fun createIntent(context: Context, alternativeReasonDescription: String? = null): Intent {
            return Intent(context, EditProfileActivity::class.java).apply {
                putExtra(EXTRA_START_ARGS, EditProfileStartArgs(alternativeReasonDescription))
            }
        }
    }
}

class EditProfileParentFragment : Fragment(R.layout.fragment_edit_profile_parent)
