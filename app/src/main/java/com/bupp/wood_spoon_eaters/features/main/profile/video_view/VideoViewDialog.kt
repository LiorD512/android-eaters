package com.bupp.wood_spoon_eaters.features.main.profile.video_view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.model.Cook
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.video_view_dialog.*


class VideoViewDialog(val cook: Cook) : DialogFragment(), HeaderView.HeaderViewListener, Player.EventListener {


    private var player: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.video_view_dialog, null)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        videoViewHeaderView.setHeaderViewListener(this)
        videoViewHeaderView.setTitle("Story by ${cook.getFullName()}")

        Log.d("wowVideoView", "video url: ${cook.video}")
        player = SimpleExoPlayer.Builder(requireContext()).build()

        videoView.player = player
        val dataSourceFactory = DefaultDataSourceFactory(requireContext(), Util.getUserAgent(requireContext(), "WoodSpoonEaters"))
        val uri = Uri.parse(cook.video)
        val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
        player?.prepare(videoSource)
    }

    override fun onHeaderBackClick() {
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        player?.release()
        player?.clearVideoSurface()
        player = null
        super.onDismiss(dialog)
    }
}