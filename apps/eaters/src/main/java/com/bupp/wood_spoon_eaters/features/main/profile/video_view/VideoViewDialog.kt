package com.bupp.wood_spoon_eaters.features.main.profile.video_view

import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.databinding.LogoutDialogBinding
import com.bupp.wood_spoon_eaters.databinding.VideoViewDialogBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util


class VideoViewDialog(val video: String) : DialogFragment(), HeaderView.HeaderViewListener, Player.EventListener {


    private var player: SimpleExoPlayer? = null
    var binding: VideoViewDialogBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.video_view_dialog, null)
        binding = VideoViewDialogBinding.bind(view)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player = null
        binding = null
    }

    private fun initUi() {
        with(binding!!) {
            videoViewExitBtn.setOnClickListener {
                dismiss()
            }

            video.let {
                Log.d("wowVideoView", "video url: $it")
                player = SimpleExoPlayer.Builder(requireContext()).build()

                videoView.player = player
                val dataSourceFactory = DefaultDataSourceFactory(requireContext(), Util.getUserAgent(requireContext(), "WoodSpoonEaters"))
                val uri = Uri.parse(it)
                val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri))
                player?.setMediaSource(videoSource)
                player?.prepare()
            }

        }
    }

    override fun onHeaderBackClick() {
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        player?.release()
        player?.clearVideoSurface()
        super.onDismiss(dialog)
    }

}