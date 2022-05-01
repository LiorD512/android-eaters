package com.bupp.wood_spoon_chef.presentation.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.databinding.MediaChooserDialogBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseDialogFragment

class MediaChooserDialog(val listener: MediaChooserListener) : BaseDialogFragment(R.layout.media_chooser_dialog) {

    var binding : MediaChooserDialogBinding?= null

    interface MediaChooserListener {
        fun onMediaChoose(mediaType: Int)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = MediaChooserDialogBinding.bind(view)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        with(binding!!) {
            mediaChooserDialogBkg.setOnClickListener {
                dismiss()
            }
            mediaChooserDialogClose.setOnClickListener {
                dismiss()
            }
            mediaChooserDialogCamera.setOnClickListener {
                listener.onMediaChoose(Constants.MEDIA_TYPE_CAMERA)
                dismiss()
            }
            mediaChooserDialogGallery.setOnClickListener {
                listener.onMediaChoose(Constants.MEDIA_TYPE_GALLERY)
                dismiss()
            }
        }
    }

    override fun clearClassVariables() {
        binding = null
    }

}