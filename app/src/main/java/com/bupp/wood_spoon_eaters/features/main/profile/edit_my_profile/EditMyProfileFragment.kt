package com.bupp.wood_spoon_eaters.features.main.profile.edit_my_profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import com.bupp.wood_spoon_eaters.custom_views.UserImageView
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.model.Cook
import com.bupp.wood_spoon_eaters.model.Eater
import com.bupp.wood_spoon_eaters.utils.CameraUtils
import kotlinx.android.synthetic.main.edit_my_profile_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditMyProfileFragment : Fragment(), InputTitleView.InputTitleViewListener, UserImageView.UserImageViewListener {

    companion object {
        fun newInstance() = EditMyProfileFragment()
    }

    private var photoUploaded: Boolean = false
    private var hasUpdated: Boolean = false
    val viewModel by viewModel<EditMyProfileViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.edit_my_profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editMyProfileFragUserImageView.setUserImageViewListener(this)

        editMyProfileFragFullName.setInputTitleViewListener(this)
        editMyProfileFragEmail.setInputTitleViewListener(this)


        viewModel.userDetails.observe(this, Observer { eater -> setEaterDetails(eater) })

        viewModel.navigationEvent.observe(this, Observer { event -> handleSaveResponse(event) })

        viewModel.getEaterProfile()

        (activity as MainActivity).handlePb(true)
    }

    private fun handleSaveResponse(event: EditMyProfileViewModel.NavigationEvent?) {
        (activity as MainActivity).handlePb(false)
        if (event!!.isSuccess) {
            (activity as MainActivity).loadMyProfile()
        } else {
            Toast.makeText(context, "There was a problem accessing the server", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setEaterDetails(eater: Eater?) {
        (activity as MainActivity).handlePb(false)

        if (eater != null) {
            editMyProfileFragUserImageView.setImage(eater.thumbnail)

            if (!eater.getFullName().isNullOrBlank()) {
                editMyProfileFragFullName.setText(eater.getFullName())
            }

            editMyProfileFragEmail.setText(eater.email)
            editMyProfileFragMobileNum.setText(eater.phoneNumber)
        }
        hasUpdated = false
        this.photoUploaded = false
        (activity as MainActivity).setHeaderViewSaveBtnClickable(false)
    }


    override fun onInputTitleChange(str: String?) {
        (activity as MainActivity).setHeaderViewSaveBtnClickable(true)
        this.hasUpdated = true
    }

    fun saveEaterDetails() {
        if (hasUpdated) {
            viewModel.saveEater(editMyProfileFragFullName.getText(), editMyProfileFragEmail.getText(), photoUploaded)
        }
    }

    override fun onUserImageClick(cook: Cook?) {
        CameraUtils.openCamera(activity as MainActivity)
    }

    fun onMediaCaptureResult(resultUri: Uri?) {
        editMyProfileFragUserImageView.setImage(resultUri!!)
        viewModel.updateTempThumbnail(resultUri)
        this.photoUploaded = true

        (activity as MainActivity).setHeaderViewSaveBtnClickable(true)
        this.hasUpdated = true
    }

}
