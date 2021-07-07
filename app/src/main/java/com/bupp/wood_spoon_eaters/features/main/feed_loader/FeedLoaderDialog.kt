package com.bupp.wood_spoon_eaters.features.main.feed_loader

import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.FragmentFeedLoaderBinding
import com.bupp.wood_spoon_eaters.utils.updateScreenUi
import org.koin.android.ext.android.bind
import org.koin.androidx.viewmodel.ext.android.viewModel


class FeedLoaderDialog : DialogFragment() {

    private var timer: CountDownTimer? = null
    val viewModel by viewModel<FeedLoaderViewModel>()
    val binding: FragmentFeedLoaderBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_feed_loader, null)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initObservers()


        viewModel.getWelcomeScreens()
    }

    private fun handleProgress(imagesCount: Int) {

        val totalTime = imagesCount * 2000
        binding.feedLoaderPb.max = totalTime

        timer = object : CountDownTimer(totalTime.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.feedLoaderPb.progress = (totalTime - millisUntilFinished).toInt()
            }

            override fun onFinish() {
                dismissAllowingStateLoss()
            }
        }
        timer?.start()
    }

    private fun initUi() {

    }

    private fun initObservers() {
        viewModel.welcomeScreens.observe(viewLifecycleOwner, {
            it?.let {
                binding.feedLoaderImageView.init(it)
                handleProgress(it.size)
            }
        })
    }

    override fun onDismiss(dialog: DialogInterface) {
        binding.feedLoaderImageView.stopAnimation()
        super.onDismiss(dialog)
    }


    override fun onResume() {
        super.onResume()
        updateScreenUi()
    }
}