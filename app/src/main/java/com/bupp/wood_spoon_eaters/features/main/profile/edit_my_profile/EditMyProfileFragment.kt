package com.bupp.wood_spoon_eaters.features.main.profile.edit_my_profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.MediaUtils
import com.bupp.wood_spoon_eaters.databinding.EditMyProfileFragmentBinding
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.views.UserImageView
import com.segment.analytics.Analytics
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditMyProfileFragment : Fragment(R.layout.edit_my_profile_fragment), UserImageView.UserImageViewListener {

    companion object {
        fun newInstance() = EditMyProfileFragment()
    }

    val binding: EditMyProfileFragmentBinding by viewBinding()
    private var photoUploaded: Boolean = false
    private var hasUpdated: Boolean = false
    val viewModel by viewModel<EditMyProfileViewModel>()
    val mainViewModel by sharedViewModel<MainViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Analytics.with(requireContext()).screen("Profile edit")

        initUi()
        initObservers()
    }

    private fun initUi() {
        binding.editMyProfileFragUserImageView.setUserImageViewListener(this)
        binding.editMyProfileFragUserImageBtn.setOnClickListener{
            mainViewModel.onUserImageClick()
        }
        binding.editMyProfileFragSave.setOnClickListener{
            saveEaterDetails()
        }
        (activity as MainActivity).setHeaderViewSaveBtnClickable(true)
    }

    private fun initObservers() {
        viewModel.userDetails.observe(viewLifecycleOwner, { eater -> setEaterDetails(eater) })

        viewModel.refreshThumbnailEvent.observe(viewLifecycleOwner,  { event -> handleSaveResponse(event) })

        viewModel.getEaterProfile()

        viewModel.progressData.observe(viewLifecycleOwner, {
            if (it) {
                binding.editMyProfileFragPb.show()
            } else {
                binding.editMyProfileFragPb.hide()
            }
        })
    }

    private fun handleSaveResponse(event: EditMyProfileViewModel.NavigationEvent?) {
        if (event!!.isSuccess) {
            (activity as MainActivity).refreshUserUi()
            activity?.onBackPressed()
        } else {
            Toast.makeText(context, "There was a problem accessing the server", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setEaterDetails(eater: Eater?) {
        with(binding) {
            if (eater != null) {
                if(eater.thumbnail != null){
                    editMyProfileFragUserImageView.setImage(eater.thumbnail)
                    editMyProfileFragUserImageBtn.setTitle("Change photo")
                }else{
                    editMyProfileFragUserImageBtn.setTitle("Add photo")
                }

                eater.firstName?.let {
                    editMyProfileFragFirstName.setText(it)
                }
                eater.lastName?.let {
                    editMyProfileFragLastName.setText(it)
                }

                eater.email?.let {
                    editMyProfileFragEmail.setText(it)
                }

                eater.phoneNumber.let {
                    editMyProfileFragPhone.setText(it)
                    editMyProfileFragPhone.alpha = 0.5f
                }

            }
        }
        this.photoUploaded = false
    }

    fun saveEaterDetails() {
        with(binding) {
            if (validateFields()) {
                val first = editMyProfileFragFirstName.getText()
                val last = editMyProfileFragLastName.getText()
                val email = editMyProfileFragEmail.getText()
                viewModel.saveEater(first, last, email, photoUploaded)
            }
        }
    }

    private fun validateFields(): Boolean {
        with(binding) {
            var isValid = true
            val first = editMyProfileFragFirstName.getText()
            val last = editMyProfileFragLastName.getText()
            val email = editMyProfileFragEmail.getText()
            if (first.isNullOrEmpty()) {
                editMyProfileFragFirstName.showError()
                isValid = false
            }
            if (last.isNullOrEmpty()) {
                editMyProfileFragLastName.showError()
                isValid = false
            }
            if (email.isNullOrEmpty()) {
                editMyProfileFragEmail.showError()
                isValid = false
            }
            return isValid
        }
    }

    override fun onUserImageClick(cook: Cook?) {
        mainViewModel.onUserImageClick()
    }


    fun onCameraUtilResult(result: MediaUtils.MediaUtilResult) {
        result.fileUri?.let {
            binding.editMyProfileFragUserImageView.setImage(it)
            viewModel.updateTempThumbnail(it)
            this.photoUploaded = true

            (activity as MainActivity).setHeaderViewSaveBtnClickable(true)
            this.hasUpdated = true
        }
    }

}
