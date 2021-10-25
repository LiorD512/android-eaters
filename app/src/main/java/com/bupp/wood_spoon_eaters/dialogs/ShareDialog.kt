package com.bupp.wood_spoon_eaters.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView.BufferType
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.ShareDialogBinding


class ShareDialog(val listener: ShareDialogListener) : DialogFragment() {

    var binding: ShareDialogBinding? = null

    interface ShareDialogListener{
        fun onShareClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.share_dialog, null)
        binding = ShareDialogBinding.bind(view)
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        with(binding!!){
            shareDialogCloseBtn.setOnClickListener { dismiss() }
            shareDialogShareBtn.setOnClickListener {
                listener.onShareClick()
                dismiss()
            }
            shareDialogLayout.setOnClickListener { dismiss() }

            shareDialogText.setText(getString(R.string.share_dialog_share_text), BufferType.SPANNABLE)
            val s = shareDialogText.text as Spannable
            val start = 0
            val end = "Share ".length
            s.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(),R.color.teal_blue)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}