package com.bupp.wood_spoon_eaters.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.LogoutDialogBinding

class LogoutDialog(val listener: LogoutDialogListener) : DialogFragment() {

    var binding: LogoutDialogBinding? = null

    interface LogoutDialogListener {
        fun logout()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.logout_dialog, null)
        binding = LogoutDialogBinding.bind(view)
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}