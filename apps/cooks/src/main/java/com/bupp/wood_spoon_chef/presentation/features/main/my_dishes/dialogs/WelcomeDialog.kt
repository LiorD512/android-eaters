package com.bupp.wood_spoon_chef.presentation.features.main.my_dishes.dialogs

import android.os.Bundle
import android.view.View
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.DialogWelcomeBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseDialogFragment

class WelcomeDialog(val listener: WelcomeDialogListener) : BaseDialogFragment(R.layout.dialog_welcome) {

    var binding: DialogWelcomeBinding? = null

    interface WelcomeDialogListener {
        fun onContinueClick()
        fun onNotNowClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = DialogWelcomeBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        with(binding!!) {
            welcomeDialogContinue.setOnClickListener {
                listener.onContinueClick()
                dismiss()
            }

            welcomeDialogNotNow.setOnClickListener {
                listener.onNotNowClick()
                dismiss()
            }
        }
    }

    override fun clearClassVariables() {
        binding = null
    }

}