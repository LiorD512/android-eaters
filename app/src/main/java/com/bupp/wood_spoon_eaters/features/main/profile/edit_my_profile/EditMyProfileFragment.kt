package com.bupp.wood_spoon_eaters.features.main.profile.edit_my_profile

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.views.UserImageView
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.features.main.MainViewModel
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.utils.CameraUtils
import com.segment.analytics.Analytics
import kotlinx.android.synthetic.main.edit_my_profile_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditMyProfileFragment : Fragment(), UserImageView.UserImageViewListener, CameraUtils.CameraUtilListener {

    companion object {
        fun newInstance() = EditMyProfileFragment()
    }

    private var photoUploaded: Boolean = false
    private var hasUpdated: Boolean = false
    val viewModel by viewModel<EditMyProfileViewModel>()
    val mainViewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.edit_my_profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Analytics.with(requireContext()).screen("Profile edit")

        initUi()
        initObservers()
    }

    private fun initUi() {
        editMyProfileFragUserImageView.setUserImageViewListener(this)
        (activity as MainActivity).setHeaderViewSaveBtnClickable(true)
    }

    private fun initObservers() {
        viewModel.userDetails.observe(this, Observer { eater -> setEaterDetails(eater) })

        viewModel.refreshThumbnailEvent.observe(this, Observer { event -> handleSaveResponse(event) })

        viewModel.getEaterProfile()

        viewModel.progressData.observe(viewLifecycleOwner, {
            if(it){
                editMyProfileFragPb.show()
            }else{
                editMyProfileFragPb.hide()
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

        if (eater != null) {
            editMyProfileFragUserImageView.setImage(eater.thumbnail)

            eater.firstName?.let{
                editMyProfileFragFirstName.setText(it)
            }
            eater.lastName?.let{
                editMyProfileFragLastName.setText(it)
            }

            eater.email?.let{
                editMyProfileFragEmail.setText(it)
            }

            eater.phoneNumber.let{
                editMyProfileFragPhone.setText(it)
            }

        }
        this.photoUploaded = false
    }

    fun saveEaterDetails() {
        if(validateFields()){
            val first = editMyProfileFragFirstName.getText()
            val last = editMyProfileFragLastName.getText()
            val email = editMyProfileFragEmail.getText()
            viewModel.saveEater(first, last, email, photoUploaded)
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true
        val first = editMyProfileFragFirstName.getText()
        val last = editMyProfileFragLastName.getText()
        val email = editMyProfileFragEmail.getText()
        if(first.isNullOrEmpty()){
            editMyProfileFragFirstName.showError()
            isValid = false
        }
        if(last.isNullOrEmpty()){
            editMyProfileFragLastName.showError()
            isValid = false
        }
        if(email.isNullOrEmpty()){
            editMyProfileFragEmail.showError()
            isValid = false
        }
        return isValid
    }

    override fun onUserImageClick(cook: Cook?) {
        CameraUtils.openCameraForResult(activity as Activity, this)
    }

//    fun onMediaCaptureResult(resultUri: Uri?) {
//        editMyProfileFragUserImageView.setImage(resultUri!!)
//        viewModel.updateTempThumbnail(resultUri)
//
//    }

    override fun onCameraUtilResult(result: CameraUtils.CameraUtilResult) {
        result.fileUri?.let{
            editMyProfileFragUserImageView.setImage(it)
            viewModel.updateTempThumbnail(it)
            this.photoUploaded = true

            (activity as MainActivity).setHeaderViewSaveBtnClickable(true)
            this.hasUpdated = true
        }
    }

}
