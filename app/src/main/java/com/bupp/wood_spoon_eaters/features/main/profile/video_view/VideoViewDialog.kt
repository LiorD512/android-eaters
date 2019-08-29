package com.bupp.wood_spoon_eaters.features.main.profile.video_view

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.model.Cook
import kotlinx.android.synthetic.main.contact_us_dialog.*
import kotlinx.android.synthetic.main.video_view_dialog.*

class VideoViewDialog(val cook: Cook) : DialogFragment(), HeaderView.HeaderViewListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.video_view_dialog, null)
        dialog.window.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        videoViewHeaderView.setHeaderViewListener(this)
        videoViewHeaderView.setTitle("Video by ${cook.getFullName()}")

        videoView.setVideoPath(cook.video)

        val mediaController = MediaController(context)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)
        videoView.start()
    }

    override fun onHeaderBackClick() {
        dismiss()
    }
}