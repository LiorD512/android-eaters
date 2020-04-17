package com.bupp.wood_spoon_eaters.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import kotlinx.android.synthetic.main.contact_us_dialog.*

class ContactUsDialog(val listener: ContactUsDialogListener) : DialogFragment() {

    interface ContactUsDialogListener{
        fun onCallSupportClick()
        fun onSmsSupportClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.contact_us_dialog, null)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        contactUsDialogCloseBtn.setOnClickListener { dismiss() }
        contactUsDialogCallBtn.setOnClickListener {
            listener.onCallSupportClick()
            dismiss()
        }
        contactUsDialogSmsBtn.setOnClickListener {
            listener.onSmsSupportClick()
            dismiss()
        }
        contactUsDialogLayout.setOnClickListener { dismiss() }
    }
}