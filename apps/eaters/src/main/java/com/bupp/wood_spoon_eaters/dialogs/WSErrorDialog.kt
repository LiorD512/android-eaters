package com.bupp.wood_spoon_eaters.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.WsErrorDialogBinding

class WSErrorDialog(val body: String?, val listener: WSErrorListener?) : DialogFragment() {

    var binding: WsErrorDialogBinding? = null

    interface WSErrorListener {
        fun onWSErrorDone()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.ws_error_dialog, null)
        binding = WsErrorDialogBinding.bind(view)
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        with(binding!!) {
            body?.let {
                errorDialogBody.text = it
            }
            errorDialogClose.setOnClickListener {
                dismiss()
                listener?.onWSErrorDone()
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}