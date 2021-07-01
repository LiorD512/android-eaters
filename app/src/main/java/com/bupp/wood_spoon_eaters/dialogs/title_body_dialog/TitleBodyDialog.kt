package com.bupp.wood_spoon_eaters.dialogs.title_body_dialog

import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.single_order_details.SingleOrderDetailsBottomSheet
import com.bupp.wood_spoon_eaters.databinding.LogoutDialogBinding
import com.bupp.wood_spoon_eaters.databinding.TitleBodyDialogBinding

class TitleBodyDialog() : DialogFragment(){

    private lateinit var listener: TitleBodyDialogListener
    lateinit var binding: TitleBodyDialogBinding
    interface TitleBodyDialogListener{
        fun onTitleBodyDialogDismiss()
    }

    companion object{
        private const val TITLE_BODY_DIALOG_TITLE = "title_body_dialog_title"
        private const val TITLE_BODY_DIALOG_BODY = "title_body_dialog_body"
        fun newInstance(title: String, body: String): TitleBodyDialog{
            return TitleBodyDialog().apply {
                arguments = Bundle().apply {
                    putString(TITLE_BODY_DIALOG_TITLE, title)
                    putString(TITLE_BODY_DIALOG_BODY, body)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.title_body_dialog, null)
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = TitleBodyDialogBinding.bind(view)

        arguments?.let {
            val title = it.getString(TITLE_BODY_DIALOG_TITLE)
            val body = it.getString(TITLE_BODY_DIALOG_BODY)
            binding.titleBodyDialogTitle.text = title
            binding.titleBodyDialogBody.text = body
        }

        binding.titleBodyDialogClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener.onTitleBodyDialogDismiss()
        super.onDismiss(dialog)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TitleBodyDialogListener) {
            listener = context
        }
        else if (parentFragment is TitleBodyDialogListener){
            this.listener = parentFragment as TitleBodyDialogListener
        }
        else {
            throw RuntimeException("$context must implement TitleBodyDialogListener")
        }
    }

}