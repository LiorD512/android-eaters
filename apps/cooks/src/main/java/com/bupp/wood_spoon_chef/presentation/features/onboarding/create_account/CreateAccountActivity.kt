package com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.findNavController
import com.bupp.wood_spoon_chef.BuildConfig
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.databinding.ActivityCreateAccountBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseActivity
import com.bupp.wood_spoon_chef.presentation.features.main.MainActivity
import com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account.dialogs.CreateAccountHelpBottomSheet
import com.bupp.wood_spoon_chef.data.remote.network.base.CustomError
import com.bupp.wood_spoon_chef.utils.extensions.clearStack
import com.bupp.wood_spoon_chef.utils.media_utils.MediaUtils
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateAccountActivity : BaseActivity(), MediaUtils.MediaUtilListener {

    val viewModel by viewModel<CreateAccountViewModel>()
    var binding: ActivityCreateAccountBinding? = null

    private val mediaUtil = MediaUtils(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        initUi()
        initObservers()
    }

    private fun initUi() {
        initializePlaces()
        with(binding!!) {
            createAccountActHelp.setOnClickListener {
                CreateAccountHelpBottomSheet().show(
                    supportFragmentManager,
                    Constants.CREATE_ACCOUNT_HELP_BOTTOM_SHEET
                )
            }

            createAccountActClose.setOnClickListener {
                finish()
            }

            createAccountActFeaturePb.nextStep()
        }
    }

    private fun initObservers() {
        viewModel.navigationEvent.observe(this, {
            it?.let {
                when (it) {
                    CreateAccountViewModel.NavigationEventType.OPEN_ADDRESS_AUTO_COMPLETE -> {
                        redirectToAutoCompleteSearch()
                    }
                    CreateAccountViewModel.NavigationEventType.DETAILS_TO_ADDRESS -> {
                        binding!!.createAccountActFeaturePb.nextStep()
                        findNavController(R.id.createAccountActContainer).navigate(R.id.action_detailsFragment_to_setupAddressFragment)
                    }
                    CreateAccountViewModel.NavigationEventType.ADDRESS_TO_PROFILE -> {
                        binding!!.createAccountActFeaturePb.nextStep()
                        findNavController(R.id.createAccountActContainer).navigate(R.id.action_setupAddressFragment_to_setupProfileFragment)
                    }
                    CreateAccountViewModel.NavigationEventType.PROFILE_TO_VIDEO -> {
                        binding!!.createAccountActFeaturePb.nextStep()
                        findNavController(R.id.createAccountActContainer).navigate(R.id.action_setupProfileFragment_to_setupVideoFragment)
                    }
                    CreateAccountViewModel.NavigationEventType.FINISH -> {
                        //redirect to main
                        val intent = Intent(this, MainActivity::class.java)
                        intent.clearStack()
                        startActivity(intent)
                        finish()
                    }
                    else -> {}
                }
            }
        })
        viewModel.progressData.observe(this, {
            handleProgressBar(it)
        })
        viewModel.navigationEvent.observe(this, {
            when (it) {
                CreateAccountViewModel.NavigationEventType.START_VIDEO_CAPTURE -> {
                    mediaUtil.startVideoFetcher(Constants.MEDIA_TYPE_VIDEO)
                }
                CreateAccountViewModel.NavigationEventType.START_IMAGE_CAPTURE -> {
                    mediaUtil.startPhotoFetcher(Constants.MEDIA_TYPE_COOK_IMAGE)
                }
                CreateAccountViewModel.NavigationEventType.START_COVER_CAPTURE -> {
                    mediaUtil.startPhotoFetcher(Constants.MEDIA_TYPE_COVER_IMAGE)
                }
                else -> {
                }
            }
        })
        viewModel.errorEvent.observe(this, {
            handleErrorEvent(it, binding?.root)
        })
    }

    private fun initializePlaces() {
        Places.initialize(this, BuildConfig.GOOGLE_API_KEY)
    }

    override fun onMediaUtilResult(result: MediaUtils.MediaUtilResult) {
        Log.d(TAG, "onCameraUtilResult: ${result.fileUri} ${result.file} ${result.path}")
        when (result.mediaType) {
            Constants.MEDIA_TYPE_VIDEO -> {
                result.fileUri?.let { viewModel.onVideoCaptureDone(it) }
            }
            Constants.MEDIA_TYPE_COOK_IMAGE -> {
                viewModel.onCameraUtilResult(result)
            }
            Constants.MEDIA_TYPE_COVER_IMAGE -> {
                viewModel.onCoverResult(result)
            }
        }
    }

    override fun fileToBigError() {
        val error = CustomError("Sorry, your file is too large to upload. It needs to be 100 MB or smaller in size")
        handleErrorEvent(error, binding?.root)
    }

    private fun redirectToAutoCompleteSearch() {
        val fields = listOf(Place.Field.NAME, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG)
        val intent =
            Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this)
        autoCompleteAddressSearchForResult.launch(intent)
    }

    override fun onBackPressed() {
        binding?.apply { createAccountActFeaturePb.previousStep() }
        super.onBackPressed()
    }

    private val autoCompleteAddressSearchForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            Log.d(TAG, "Activity For Result")
            val data = result.data
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i(
                            TAG,
                            "Place: ${place.name}, ${place.addressComponents}, ${place.latLng}"
                        )
                        viewModel.updateAutoCompleteAddressFound(this, place)
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i(TAG, status.statusMessage ?: "")
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }

        }

    companion object {
        const val TAG = "wowCreateAccountAct"
    }

}