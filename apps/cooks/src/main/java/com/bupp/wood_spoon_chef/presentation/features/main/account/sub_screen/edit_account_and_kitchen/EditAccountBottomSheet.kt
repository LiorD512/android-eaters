package com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.edit_account_and_kitchen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.presentation.custom_views.UserImageView
import com.bupp.wood_spoon_chef.databinding.BottomSheetEditAccountBinding
import com.bupp.wood_spoon_chef.data.remote.model.Cook
import com.bupp.wood_spoon_chef.data.remote.model.MediaRequests
import com.bupp.wood_spoon_chef.presentation.views.WSEditText
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class EditAccountBottomSheet: TopCorneredBottomSheet(),UserImageView.UserImageViewListener, WSEditText.WSEditTextListener {

    private var binding: BottomSheetEditAccountBinding? = null
    private val viewModel: UpdateAccountViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_edit_account, container, false)
        binding = BottomSheetEditAccountBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFullScreenDialog()
        viewModel.initCookData()
        initUi()
        initObservers()
    }


    private fun initUi() {
        with(binding!!){
            editAccountUserImageView.setUserImageViewListener(this@EditAccountBottomSheet, Constants.USER_IMAGE_VIEW_USER_PICTURE)
            editAccountSave.setOnClickListener {
                validateAndContinue()
            }
            editAccountChangePicture.setOnClickListener {
                viewModel.onChangeThumbnailClick()
            }
            editAccountHeader.setOnIconClickListener {
                dismiss()
            }
        }
    }

    private fun initObservers() {
        viewModel.cookData.observe(viewLifecycleOwner, {
            loadCookData(it)
        })
        viewModel.mediaRequestData.observe(viewLifecycleOwner, {
            loadMediaRequestData(it)
        })
        viewModel.postAccountSuccessEvent.observe(viewLifecycleOwner,{
            it.getContentIfNotHandled()?.let{ success->
                if(success){
                    dismiss()
                }
            }
        })
        viewModel.progressData.observe(viewLifecycleOwner, {
            handleProgressBar(it)
        })
        viewModel.errorEvent.observe(viewLifecycleOwner,{
            handleErrorEvent(it, binding?.root)
        })
    }

    private fun validateAndContinue() {
        if(allFieldsValid()){
            with(binding!!){
                val firstName = editAccountFirstName.getTextOrNull() ?: ""
                val lastName = editAccountLastName.getTextOrNull() ?: ""
                val email = editAccountEmail.getTextOrNull() ?: ""
                viewModel.updateCookAccount(firstName, lastName, email)
            }
        }
    }

    private fun loadCookData(cook: Cook?) {
        with(binding!!){
            cook?.let{ it ->
                it.firstName?.let{
                    editAccountFirstName.setText(it)
                }
                it.lastName?.let{
                    editAccountLastName.setText(it)
                }
                it.email?.let{
                    editAccountEmail.setText(it)
                }
                it.thumbnail?.let{
                    editAccountUserImageView.setImage(it.url?:"")
                }
            }
        }
    }

    private fun loadMediaRequestData(mediaRequests: MediaRequests?) {
        with(binding!!){
            mediaRequests?.let{ it ->
                it.tempThumbnail?.let{
                    editAccountUserImageView.setImage(it)
                }
            }
        }
    }

    private fun allFieldsValid(): Boolean {
        with(binding!!){
            val validFirst = editAccountFirstName.checkIfValidAndSHowError()
            val validLast = editAccountLastName.checkIfValidAndSHowError()
            val validEmail = editAccountEmail.checkIfValidEmailAndSHowError()
            return  validFirst && validLast && validEmail
        }
    }

    override fun onUserImageClick(type: Int) {
        if (type == Constants.USER_IMAGE_VIEW_USER_PICTURE) {
            viewModel.onChangeThumbnailClick()
        }
    }

    override fun clearClassVariables() {
        binding = null
        viewModel.resetData()
    }

}