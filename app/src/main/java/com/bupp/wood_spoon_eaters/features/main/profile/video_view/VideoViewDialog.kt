package com.bupp.wood_spoon_eaters.features.main.profile.video_view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.databinding.VideoViewDialogBinding
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util


class VideoViewDialog(val cookFullName: String, val video: String) : DialogFragment(), HeaderView.HeaderViewListener, Player.EventListener {


    private var player: SimpleExoPlayer? = null
    val binding: VideoViewDialogBinding by viewBinding()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.video_view_dialog, null)
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
    }

    private fun initUi() {
        with(binding) {
            videoViewExitBtn.setOnClickListener {
                dismiss()
            }

            video.let {
                Log.d("wowVideoView", "video url: $it")
                player = SimpleExoPlayer.Builder(requireContext()).build()

                videoView.player = player
                val dataSourceFactory = DefaultDataSourceFactory(requireContext(), Util.getUserAgent(requireContext(), "WoodSpoonEaters"))
                val uri = Uri.parse(it)
                val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
                player?.prepare(videoSource)
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