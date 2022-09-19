package com.bupp.wood_spoon_eaters.features.create_profile.details

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.country_code_chooser.CountryChooserBottomSheetFragmentResult
import com.bupp.wood_spoon_eaters.custom_views.phone_number.PhoneSelectorView
import com.bupp.wood_spoon_eaters.custom_views.phone_number.setCountryCode
import com.bupp.wood_spoon_eaters.databinding.FragmentCreateAccount2Binding
import com.bupp.wood_spoon_eaters.features.create_profile.EditProfileParent
import com.bupp.wood_spoon_eaters.features.create_profile.code.EditProfileCodeFragmentParams
import com.bupp.wood_spoon_eaters.model.CountriesISO
import com.bupp.wood_spoon_eaters.utils.hideKeyboard
import com.bupp.wood_spoon_eaters.views.WSEditText
import com.eatwoodspoon.android_utils.binding.viewBinding
import com.eatwoodspoon.android_utils.fragments.findParent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class EditProfileFragment : Fragment(R.layout.fragment_create_account_2) {

    private val binding by viewBinding(FragmentCreateAccount2Binding::bind)
    private val viewModel by viewModel<EditProfileViewModel>()

    val navigation by lazy { findNavController(this) }

    private val editProfileParent by lazy {
        findParent(EditProfileParent::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initObservers()
    }

    private fun initUi() {
        initPhoneSelector()
        with(binding) {
//                editMyProfileFragUserImageView.setUserImageVideoViewListener(this@EditProfileFragment)
//                editMyProfileFragUserImageBtn.setOnClickListener {
//                    mainViewModel.onUserImageClick()
//                }
            editProfileParent?.startArgs?.alternativeReasonDescription?.let {
                subtitle.text = it
            }

            createAccountFragNext.setOnClickListener {
                saveEaterDetails()
            }
            val listener  =  object: WSEditText.WSEditTextListener {
                override fun onWSEditTextActionDone() {
                    saveEaterDetails()
                }
            }
            createAccountFragFirstName.listener = listener
            createAccountFragLastName.listener = listener
            createAccountFragEmail.listener = listener
            createAccountFragPhoneNumber.phoneInput.listener = listener

            createAccountFragCloseBtn.setOnClickListener {
                onHeaderCloseClick()
            }
        }
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        updateUIWithState(state)
                    }
                }

                launch {
                    viewModel.events.collect { event ->
                        when (event) {
                            is EditProfileEvents.NavigateToPhoneValidation -> navigation.navigate(
                                EditProfileFragmentDirections.actionToPhoneVerification(
                                    EditProfileCodeFragmentParams(phoneNumber = event.phoneNumber)
                                )
                            )
                            is EditProfileEvents.InputError -> handleInputError(event)
                            EditProfileEvents.NavigateEditProfileDone -> {
                                activity?.setResult(Activity.RESULT_OK)
                                activity?.finish()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleInputError(event: EditProfileEvents.InputError) = with(binding){
        val errorMessage = event.message ?: getString(R.string.default_input_error_message)
        when(event.field) {
            EditProfileEvents.InputError.Field.FirstName -> createAccountFragFirstName.showError(errorMessage)
            EditProfileEvents.InputError.Field.LastName -> createAccountFragLastName.showError(errorMessage)
            EditProfileEvents.InputError.Field.Email -> createAccountFragEmail.showError(errorMessage)
            EditProfileEvents.InputError.Field.PhoneNumber -> createAccountFragPhoneNumber.showError(errorMessage)
        }
    }

    private fun initPhoneSelector() {
        binding.createAccountFragPhoneNumber.onCountryClickListener =
            PhoneSelectorView.OnCountryClickListener { phoneView ->
                childFragmentManager.setFragmentResultListener(
                    CountryChooserBottomSheetFragmentResult.requestKey,
                    this
                ) { _, result ->
                    result.getParcelable<CountriesISO>(
                        CountryChooserBottomSheetFragmentResult.resultKey
                    )?.let {
                        phoneView.setCountryCode(it)
                    }
                }
                CountryChooserBottomSheetFragmentResult().show(childFragmentManager, null)
            }
    }

    private fun updateUIWithState(state: EditProfileState) {
        with(binding) {
            progress.setProgress(state.progress)
//                editMyProfileFragUserImageSection.isVisible = state.photoSectionEnabled
        }
        setEaterDetails(state.user)
    }

    private fun setEaterDetails(eater: EditProfileState.User?) = with(binding) {
        val eater = eater ?: return
//            editMyProfileFragUserImageView.setImage(eater.thumbnail)
//            editMyProfileFragUserImageBtn.setTitle(
//                if (eater.thumbnail != null) {
//                    "Change photo"
//                } else {
//                    "Add photo"
//                }
//            )

        createAccountFragFirstName.setText(eater.firstName)
        createAccountFragLastName.setText(eater.lastName)


        fun alphaForIsEditable(value: Boolean) = if (value) 1f else 0.5f

        createAccountFragEmail.setIsEditable(eater.canEditEmail, null)
        createAccountFragEmail.setText(eater.email)
        createAccountFragEmail.alpha = alphaForIsEditable(eater.canEditEmail)


        createAccountFragPhoneNumber.isEnabled = eater.canEditPhoneNumber
        createAccountFragPhoneNumber.setPhoneString(eater.phoneNumber)
        createAccountFragPhoneNumber.alpha = alphaForIsEditable(eater.canEditPhoneNumber)
    }

    private fun saveEaterDetails() = with(binding) {

        activity?.hideKeyboard()

        val first = createAccountFragFirstName.getText()
        val last = createAccountFragLastName.getText()
        val email = createAccountFragEmail.getText()
        val phoneNumber = createAccountFragPhoneNumber.getPhoneString()
        viewModel.onSaveUserClicked(first, last, email, phoneNumber)
    }

//    override fun onUserImageClick(cook: Cook?) {
//        mainViewModel.onUserImageClick()
//    }

//    fun onMediaUtilResult(result: MediaUtils.MediaUtilResult) {
//        result.fileUri?.let {
//            binding.editMyProfileFragUserImageView.setImage(it)
//            binding.editMyProfileFragUserImageBtn.setTitle("Change photo")
//            viewModel.updateTempThumbnail(it)
//        }
//    }

    private fun onHeaderCloseClick() {
        editProfileParent?.onProfileFragmentDismissed()
    }
}
