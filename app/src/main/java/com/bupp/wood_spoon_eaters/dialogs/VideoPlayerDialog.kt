package com.bupp.wood_spoon_eaters.dialogs

import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.HeaderView
import com.bupp.wood_spoon_eaters.databinding.VideoPlayerDialogBinding
import com.bupp.wood_spoon_eaters.dialogs.web_docs.WebDocsViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import org.koin.androidx.viewmodel.ext.android.viewModel


class VideoPlayerDialog(val uri: Uri) : DialogFragment(), HeaderView.HeaderViewListener, Player.EventListener {

    lateinit var binding: VideoPlayerDialogBinding
    private var player: SimpleExoPlayer? = null
    val viewModel by viewModel<WebDocsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.video_player_dialog, null)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = VideoPlayerDialogBinding.bind(view)

        initUi()


    }

    private fun initUi() {
        with(binding){
            videoPlayerHeaderView.setHeaderViewListener(this@VideoPlayerDialog)

            player = SimpleExoPlayer.Builder(requireContext()).build()
//            player = ExoPlayerFactory.newSimpleInstance(requireContext())
            videoPlayer.setPlayer(player)

            val dataSourceFactory = DefaultDataSourceFactory(requireContext(), Util.getUserAgent(requireContext(), "XRHealth"))
            val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            player?.prepare(videoSource)
            player?.playWhenReady = true

            player?.addListener(this@VideoPlayerDialog)
        }
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if(playbackState == ExoPlayer.STATE_ENDED){
            dismiss()
        }
    }
    

    override fun onHeaderBackClick() {
        dismiss()
    }

}