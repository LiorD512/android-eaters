package com.bupp.wood_spoon_eaters.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.model.Cook
import kotlinx.android.synthetic.main.start_new_cart_dialog_layout.*

class StartNewCartDialog(val cookInCartName: String = "", val currentShowingCookName: String = "", val listener: StartNewCartDialogListener) : DialogFragment() {

    interface StartNewCartDialogListener {
        fun onNewCartClick()
//        fun onCancelClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.start_new_cart_dialog_layout, null)
        dialog!!.window.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        startNewCartDialogNewCartBtn.setOnClickListener {
            listener.onNewCartClick()
            dismiss()
        }
        startNewCartDialogCloseBtn.setOnClickListener { dismiss()}
        startNewCartDialogCancelBtn.setOnClickListener {
//            listener.onCancelClick()
            dismiss()
        }


        startNewCartDialogText.text = getString(R.string.start_new_cart_dialog_your_cart_already_contains, cookInCartName, currentShowingCookName)
    }

}