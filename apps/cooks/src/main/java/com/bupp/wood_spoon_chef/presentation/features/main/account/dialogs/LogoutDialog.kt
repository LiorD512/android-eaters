package com.bupp.wood_spoon_chef.presentation.features.main.account.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.LogoutDialogBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseDialogFragment

class LogoutDialog(val listener: LogoutDialogListener) : BaseDialogFragment(R.layout.logout_dialog){

    var binding : LogoutDialogBinding? =null


    interface LogoutDialogListener{
        fun logout()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = LogoutDialogBinding.bind(view)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        super.onViewCreated(view, savedInstanceState)
        initUi()
}

    private fun initUi() {
        with(binding!!) {
            logoutDialogBkg.setOnClickListener {
                dismiss()
            }
            logoutDialogLogout.setOnClickListener {
                listener.logout()
            }
            logoutDialogCancel.setOnClickListener {
                dismiss()
            }
        }
    }


    override fun clearClassVariables() {
        binding = null
    }


}