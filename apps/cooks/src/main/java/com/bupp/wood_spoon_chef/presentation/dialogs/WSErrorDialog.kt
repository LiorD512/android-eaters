package com.bupp.wood_spoon_chef.presentation.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.WsErrorDialogBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseDialogFragment

class WSErrorDialog(val body: String?, val listener: WSErrorListener?) : BaseDialogFragment(R.layout.ws_error_dialog){

    var binding : WsErrorDialogBinding? = null

    interface WSErrorListener{
        fun onWSErrorDone()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = WsErrorDialogBinding.bind(view)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
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

    override fun clearClassVariables() {
        binding = null
    }

}