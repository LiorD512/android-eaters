package com.bupp.wood_spoon_eaters.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import kotlinx.android.synthetic.main.new_dish_suggestion_dialog.*

class NewDishSuggestionDialog(val listener: OfferDishDialogListener, val dishName: String? = null) :DialogFragment(), InputTitleView.InputTitleViewListener {

    interface OfferDishDialogListener{
        fun onNewDishSuggestion(dishName: String, dishDetails:String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.new_dish_suggestion_dialog, null)
        dialog!!.window.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        offerDishDialogDishName.setInputTitleViewListener(this)
        offerDishDialogDishText.setInputTitleViewListener(this)

        offerDishDialogCloseBtn.setOnClickListener{
            dismiss()}
        offerDishDialogSendBtn.setOnClickListener{
            listener.onNewDishSuggestion(offerDishDialogDishName.getText(),offerDishDialogDishText.getText())
            dismiss()
        }

        if(dishName!= null){
            offerDishDialogDishName.setText(dishName)
        }
    }

    private fun validateFields(): Boolean {
        return offerDishDialogDishName.isValid() && offerDishDialogDishText.isValid()
    }

    override fun onInputTitleChange(str: String?) {
        if(validateFields()){
            offerDishDialogSendBtn.isClickable = true
            offerDishDialogSendBtn.alpha = 1f
        }else{
            offerDishDialogSendBtn.isClickable = false
            offerDishDialogSendBtn.alpha = 0.5f
        }
    }
}