package com.bupp.wood_spoon_eaters.dialogs

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import com.bupp.wood_spoon_eaters.databinding.NewDishSuggestionDialogBinding

class NewDishSuggestionDialog(val listener: OfferDishDialogListener, val dishName: String? = null) :DialogFragment(), InputTitleView.InputTitleViewListener {

    val binding: NewDishSuggestionDialogBinding by viewBinding()

    interface OfferDishDialogListener{
        fun onNewDishSuggestion(dishName: String, dishDetails:String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.new_dish_suggestion_dialog, null)
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        with(binding){
            offerDishDialogDishName.setInputTitleViewListener(this@NewDishSuggestionDialog)
            offerDishDialogDishText.setInputTitleViewListener(this@NewDishSuggestionDialog)

            offerDishDialogCloseBtn.setOnClickListener{
                dismiss()}
            offerDishDialogSendBtn.setOnClickListener{
                listener.onNewDishSuggestion(offerDishDialogDishName.getText(), offerDishDialogDishText.getText())
                dismiss()
            }

            if(dishName!= null){
                offerDishDialogDishName.setText(dishName)
            }
        }
    }

    private fun validateFields(): Boolean {
        return binding.offerDishDialogDishName.isValid() && binding.offerDishDialogDishText.isValid()
    }

    override fun onInputTitleChange(str: String?) {
        with(binding){
            if(validateFields()){
                offerDishDialogSendBtn.isClickable = true
                offerDishDialogSendBtn.alpha = 1f
            }else{
                offerDishDialogSendBtn.isClickable = false
                offerDishDialogSendBtn.alpha = 0.5f
            }
        }
    }
}