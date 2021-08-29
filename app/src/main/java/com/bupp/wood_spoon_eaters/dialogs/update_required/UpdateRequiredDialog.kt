package com.bupp.wood_spoon_eaters.dialogs.update_required

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.UpdateRequiredDialogBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class UpdateRequiredDialog : DialogFragment() {

    val binding: UpdateRequiredDialogBinding by viewBinding()

    val viewModel by viewModel<UpdateRequiredViewModel>()
    interface UpdateRequiredDialogListener {
        fun onUpdateApp(url: String)
    }

    private var listener: UpdateRequiredDialogListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        return inflater.inflate(R.layout.update_required_dialog, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserver()
        viewModel.getDialogData()
    }

    private fun initObserver() {
        with(binding){
            viewModel.updateRequiredEvent.observe(this@UpdateRequiredDialog, {event ->
                updateDialogTitle.text = event.title
                updateDialogBody.text = event.body

                updateDialogBtn.setOnClickListener {
                    listener?.onUpdateApp(event.redirectUrl)
                }
            })
        }
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
