package com.bupp.wood_spoon_chef.presentation.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.presentation.custom_views.HeaderView
import com.bupp.wood_spoon_chef.databinding.NewDishDialogBinding
import com.bupp.wood_spoon_chef.databinding.VideoViewDialogBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseDialogFragment
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util


class VideoViewDialog(val url: String?, val title: String? = null) : BaseDialogFragment(R.layout.video_view_dialog), HeaderView.HeaderViewListener, Player.EventListener {

    private var player: SimpleExoPlayer? = null
    var binding: VideoViewDialogBinding? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = VideoViewDialogBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(requireActivity())
                .setView(binding!!.root)
                .create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        initUi()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        player = null
        binding = null
    }

    private fun initUi() {
        with(binding!!){
            videoViewHeaderView.setHeaderViewListener(this@VideoViewDialog)
            title?.let{
                videoViewHeaderView.setTitle(it)
            }

            url.let{
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

    override fun clearClassVariables() {
        binding = null
    }
}