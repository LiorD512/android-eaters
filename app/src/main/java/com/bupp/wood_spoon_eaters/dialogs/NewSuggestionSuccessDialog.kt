package com.bupp.wood_spoon_eaters.dialogs

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.NewSuggestionSuccessDialogBinding

class NewSuggestionSuccessDialog : DialogFragment() {

    lateinit var binding: NewSuggestionSuccessDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.new_suggestion_success_dialog, null)
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = NewSuggestionSuccessDialogBinding.bind(view)
        initUi()
    }

    private fun initUi() {
        with(binding){
            dishOfferedDialogBkg.setOnClickListener {
                dismiss()
            }
            dishOfferedDialogLayout.setOnClickListener {
                dismiss()
            }
        }
    }
}