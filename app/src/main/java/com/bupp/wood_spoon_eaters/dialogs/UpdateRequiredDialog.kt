package com.bupp.wood_spoon_eaters.dialogs

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import kotlinx.android.synthetic.main.update_required_dialog.*


class UpdateRequiredDialog : DialogFragment() {


    interface UpdateRequiredDialogListener {
        fun onUpdate()
    }

    private var listener: UpdateRequiredDialogListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)))
        return inflater.inflate(R.layout.update_required_dialog, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialogStyle)
    }

    private fun initUi() {

        updateDialogBtn.setOnClickListener {
            listener?.onUpdate()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is UpdateRequiredDialogListener) {
            listener = context
        }
        else if (parentFragment is UpdateRequiredDialogListener){
            this.listener = parentFragment as UpdateRequiredDialogListener
        }
        else {
            throw RuntimeException(context.toString() + " must implement UpdateRequiredDialogListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }



}
