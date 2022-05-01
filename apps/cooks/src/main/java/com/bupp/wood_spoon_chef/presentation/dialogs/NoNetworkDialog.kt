package com.bupp.wood_spoon_chef.presentation.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.NoNetworkDialogBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseDialogFragment


class NoNetworkDialog(val listener: NoNetworkListener) :
    BaseDialogFragment(R.layout.no_network_dialog) {

    var binding: NoNetworkDialogBinding? = null

    interface NoNetworkListener {
        fun onRetryConnectionClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = NoNetworkDialogBinding.bind(view)
        dialog?.window?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.dark_43
                )
            )
        )
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        with(binding!!) {
            noNetworkDialogTry.setOnClickListener {
                dismiss()
                listener.onRetryConnectionClick()
            }
            noNetworkDialogBkg.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun clearClassVariables() {
        binding = null
    }

}