package com.bupp.wood_spoon_chef.presentation.dialogs.update_required

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.UpdateRequiredDialogBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class UpdateRequiredDialog : BaseDialogFragment(R.layout.update_required_dialog) {

    var binding : UpdateRequiredDialogBinding? = null

    val viewModel by viewModel<UpdateRequiredViewModel>()
    interface UpdateRequiredDialogListener {
        fun onUpdateApp(url: String)
    }

    private var listener: UpdateRequiredDialogListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        return inflater.inflate(R.layout.update_required_dialog, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialogStyle)
    }

    private fun initUi() {
        viewModel.getDialogData()


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = UpdateRequiredDialogBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObserver()
    }

    private fun initObserver() {
        viewModel.updateRequiredEvent.observe(this, { event ->
            with(binding!!) {
                updateDialogTitle.text = event.title
                updateDialogBody.text = event.body

                updateDialogBtn.setOnClickListener {
                    listener?.onUpdateApp(event.redirectUrl)
                }
            }
        })
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
            throw RuntimeException("$context must implement UpdateRequiredDialogListener")
        }
    }

    override fun clearClassVariables() {
        binding = null
        listener = null
    }


}
