package com.bupp.wood_spoon_eaters.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.MediaChooserDialogBinding

class MediaChooserDialog(val listener: MediaChooserListener) : DialogFragment() {

    lateinit var binding: MediaChooserDialogBinding
    interface MediaChooserListener {
        fun onMediaChoose(mediaType: Int)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.media_chooser_dialog, null)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = MediaChooserDialogBinding.bind(view)
        initUi()
    }

    private fun initUi() {
        with(binding){
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

}