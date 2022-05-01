package com.bupp.wood_spoon_chef.presentation.features.main.my_dishes.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.DishUpdatedDialogBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseDialogFragment


class DishUpdatedDialog : BaseDialogFragment(R.layout.tooltip_dialog) {

    var listener: DishUpdateListener? = null

    interface DishUpdateListener {
        fun onDishUpdateDialogDismiss()
    }

    var title = ""
    var body = ""
    var binding: DishUpdatedDialogBinding? = null

    companion object {
        private const val MSG_PARAM_TITLE = "newDishDoneTitle"
        private const val MSG_PARAM_BODY = "newDishDoneBody"

        fun newInstance(title: String, body: String?): DishUpdatedDialog {
            val fragment = DishUpdatedDialog()
            val args = Bundle()
            args.putString(MSG_PARAM_TITLE, title)
            body?.let {
                args.putString(MSG_PARAM_BODY, body)
            }
            fragment.arguments = args
            return fragment
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            title = requireArguments().getString(MSG_PARAM_TITLE) ?: ""
            body = requireArguments().getString(MSG_PARAM_BODY) ?: ""
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DishUpdatedDialogBinding.inflate(LayoutInflater.from(context))
        return AlertDialog.Builder(requireActivity())
            .setView(binding!!.root)
            .create()
    }

    override fun onStart() {
        super.onStart()
        initUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun initUi() {
        with(binding!!) {
            newDishDoneDialogGotIt.setOnClickListener {
                dismiss()
            }
            newDishDoneDialogTitle.text = title
            if (body.isNotEmpty()) {
                newDishDoneDialogBody.text = body
                newDishDoneDialogBody.visibility = View.VISIBLE
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener?.onDishUpdateDialogDismiss()
        super.onDismiss(dialog)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DishUpdateListener) {
            listener = context
        } else if (parentFragment is DishUpdateListener) {
            this.listener = parentFragment as DishUpdateListener
        } else {
            throw RuntimeException("$context must implement DishUpdateListener")
        }
    }

    override fun clearClassVariables() {
        binding = null
        listener = null
    }


}