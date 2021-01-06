package com.bupp.wood_spoon_eaters.features.main.profile.video_view

import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.util.Util.getUserAgent
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util


class VideoViewDialog(val cook: Cook) : DialogFragment(), HeaderView.HeaderViewListener, Player.EventListener {


    private var player: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.video_view_dialog, null)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        videoViewHeaderView.setHeaderViewListener(this)
        videoViewHeaderView.setTitle("Story by ${cook.getFullName()}")

        Log.d("wowVideoView","video url: ${cook.video}")
        player = ExoPlayerFactory.newSimpleInstance(requireContext())
        videoView.setPlayer(player)
        val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(requireContext(), "WoodSpoonEaters"))
        val uri = Uri.parse(cook.video)
        val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
        player?.prepare(videoSource)
//        player?.playWhenReady = true
//        player?.addListener(this)

//        videoViewPb.show()

    }

//    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//        if (playWhenReady && playbackState == Player.STATE_READY) {
//            // Active playback.
////            videoViewPb.hide()
//        } else if (playWhenReady) {
//            // Not playing because playback ended, the player is buffering, stopped or
//            // failed. Check playbackState and player.getPlaybackError for details.
//        } else {
//            // Paused by app.
//        }
//    }

    override fun onHeaderBackClick() {
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        player?.release()
        player?.clearVideoSurface()

    }
}