package com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account.sub_screen

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.presentation.features.base.BaseFragment
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.presentation.custom_views.UserImageView
import com.bupp.wood_spoon_chef.databinding.FragmentSetupDetailsBinding
import com.bupp.wood_spoon_chef.presentation.dialogs.TooltipDialog
import com.bupp.wood_spoon_chef.presentation.features.onboarding.create_account.CreateAccountViewModel
import com.bupp.wood_spoon_chef.data.remote.model.CookRequest
import com.bupp.wood_spoon_chef.utils.AnimationUtil
import com.bupp.wood_spoon_chef.presentation.views.WSEditText
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class SetupDetailsFragment : BaseFragment(R.layout.fragment_setup_details),
    UserImageView.UserImageViewListener, WSEditText.WSEditTextListener {

    private var binding: FragmentSetupDetailsBinding? = null
    val viewModel by sharedViewModel<CreateAccountViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSetupDetailsBinding.bind(view)

        initUi()
        initObservers()

    }

    private fun initUi() {
        binding?.apply {
            setupDetailsUserImageView.setUserImageViewListener(
                this@SetupDetailsFragment,
                Constants.USER_IMAGE_VIEW_USER_PICTURE
            )

            setupDetailsSave.setOnClickListener {
                validateAndContinue()
            }
            setupDetailsChangePicture.setOnClickListener {
                viewModel.onChangePhotoClick()
            }

            setupDetailsBirthdate.setIsEditable(false, this@SetupDetailsFragment)
            setupDetailsBirthdate.setToolTip(
                getString(R.string.tooltip_birthdate),
                this@SetupDetailsFragment
            )
            setupDetailsSsn.setToolTip(getString(R.string.tooltip_ssn), this@SetupDetailsFragment)
        }
    }

    private fun validateAndContinue() {
        if (allFieldsValid()) {
            with(binding!!) {
                val firstName = setupDetailsFirstName.getTextOrNull() ?: ""
                val lastName = setupDetailsLastName.getTextOrNull() ?: ""
                val email = setupDetailsEmail.getTextOrNull() ?: ""
                val birthdate = setupDetailsBirthdate.getTextOrNull()
                val ssn = setupDetailsSsn.getTextOrNull()!!
                viewModel.saveDetails(firstName, lastName, email, birthdate, ssn)
            }
        }
    }

    private fun initObservers() {
        viewModel.currentUserLiveData.observe(viewLifecycleOwner, {
            loadUnSavedData(it)
        })
    }

    private fun loadUnSavedData(cookRequest: CookRequest?) {
        binding?.apply {
            cookRequest?.let { it ->
                it.firstName?.let {
                    setupDetailsFirstName.setText(it)
                }
                it.lastName?.let {
                    setupDetailsLastName.setText(it)
                }
                it.email?.let {
                    setupDetailsEmail.setText(it)
                }
                it.tempThumbnail?.let {
                    setupDetailsUserImageView.setImage(it)
                    setupDetailsChangePicture.text = "Edit Photo"
                }
                it.birthdate.let {
                    setupDetailsBirthdate.setText(it)
                }
                it.ssn?.let {
                    setupDetailsSsn.setText(it)
                }
            }

        }
    }

    private fun allFieldsValid(): Boolean {
        with(binding!!) {
            val validFirst = setupDetailsFirstName.checkIfValidAndSHowError()
            val validLast = setupDetailsLastName.checkIfValidAndSHowError()
            val validEmail = setupDetailsEmail.checkIfValidEmailAndSHowError()
            val hasPicture = viewModel.hasPicture()
            if (!hasPicture) {
                AnimationUtil().shakeView(setupDetailsUserImageView, requireContext())
            }
            val hasBirthdate = setupDetailsBirthdate.checkIfValidAndSHowError()
            val hasSsn = setupDetailsSsn.checkIfLongerThenAndSHowError(4)
            return validFirst && validLast && validEmail && hasPicture && hasBirthdate && hasSsn
        }
    }

    override fun onUserImageClick(type: Int) {
        if (type == Constants.USER_IMAGE_VIEW_USER_PICTURE) {
            viewModel.onChangePhotoClick()
        }
    }

    override fun onWSEditUnEditableClick() {
        val cal = Calendar.getInstance()
        val timeSetListener =
            DatePickerDialog.OnDateSetListener { timePicker, year, month, dayOfMonth ->
                val monthToDisplay = month + 1
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthToDisplay)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                binding!!.setupDetailsBirthdate.setText("$monthToDisplay/$dayOfMonth/$year")
            }
        DatePickerDialog(
            requireContext(),
            timeSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onTooltipClick(text: String) {
        TooltipDialog.newInstance(text).show(childFragmentManager, Constants.TOOL_TIP_DIALOG)
    }

    override fun clearClassVariables() {
        binding = null
    }

}