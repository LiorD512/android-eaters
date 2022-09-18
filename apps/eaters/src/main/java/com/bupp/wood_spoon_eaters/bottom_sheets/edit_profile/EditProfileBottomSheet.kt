package com.bupp.wood_spoon_eaters.bottom_sheets.edit_profile

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.abs.FullScreenBottomSheetBase
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.common.MediaUtils
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.databinding.EditProfileBottomSheetBinding
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.views.UserImageVideoView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class EditProfileBottomSheet: FullScreenBottomSheetBase(), UserImageVideoView.UserImageVideoViewListener, HeaderView.HeaderViewListener {

    var binding: EditProfileBottomSheetBinding? = null
    private var photoUploaded: Boolean = false
    val viewModel by viewModel<EditProfileViewModel>()
    val mainViewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.edit_profile_bottom_sheet, container, false)
        binding = EditProfileBottomSheetBinding.bind(view)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        Analytics.with(requireContext()).screen("Profile edit")
        mainViewModel.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_EDIT_ACCOUNT)

        val parent = view.parent as View
        parent.setBackgroundResource(R.drawable.top_cornered_bkg)

        initUi()
        initObservers()
    }

    private fun initUi() {
//        mediaUtils = MediaUtils(requireActivity(), this)
        with(binding!!){
            editMyProfileFragUserImageView.setUserImageVideoViewListener(this@EditProfileBottomSheet)
            editMyProfileFragUserImageBtn.setOnClickListener {
                mainViewModel.onUserImageClick()
            }
            editMyProfileFragSave.setOnClickListener {
                saveEaterDetails()
            }

            editMyProfileFragHeader.setHeaderViewListener(this@EditProfileBottomSheet)
        }
    }

    private fun initObservers() {
        viewModel.userDetails.observe(viewLifecycleOwner, { eater -> setEaterDetails(eater) })

        viewModel.refreshThumbnailEvent.observe(viewLifecycleOwner, { event -> handleSaveResponse(event) })

        viewModel.getEaterProfile()

        viewModel.progressData.observe(viewLifecycleOwner, {
            if (it) {
                binding!!.editMyProfileFragPb.show()
            } else {
                binding!!.editMyProfileFragPb.hide()
            }
        })

        mainViewModel.mediaUtilsResultLiveData.observe(viewLifecycleOwner, {
            onMediaUtilResult(it)
        })
    }

    private fun handleSaveResponse(event: EditProfileViewModel.NavigationEvent?) {
        if (event!!.isSuccess) {
            dismiss()
        } else {
            Toast.makeText(context, "There was a problem accessing the server", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setEaterDetails(eater: Eater?) {
        with(binding!!) {
            if (eater != null) {
                if (eater.thumbnail != null) {
                    editMyProfileFragUserImageView.setImage(eater.thumbnail)
                    editMyProfileFragUserImageBtn.setTitle("Change photo")
                } else {
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

    private fun saveEaterDetails() {
        with(binding!!) {
            if (validateFields()) {
                val first = editMyProfileFragFirstName.getText()
                val last = editMyProfileFragLastName.getText()
                val email = editMyProfileFragEmail.getText()
                viewModel.saveEater(first, last, email, photoUploaded)
            }
        }
    }

    private fun validateFields(): Boolean {
        with(binding!!) {
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

    fun onMediaUtilResult(result: MediaUtils.MediaUtilResult) {
        result.fileUri?.let {
            binding!!.editMyProfileFragUserImageView.setImage(it)
            binding!!.editMyProfileFragUserImageBtn.setTitle("Change photo")
            viewModel.updateTempThumbnail(it)
            this.photoUploaded = true
        }
    }

    override fun onHeaderCloseClick() {
        dismiss()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}