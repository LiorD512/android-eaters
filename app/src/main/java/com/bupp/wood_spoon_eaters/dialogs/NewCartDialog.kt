package com.bupp.wood_spoon_eaters.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.bupp.wood_spoon_eaters.R





class NewCartDialog : DialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_new_cart)
        dialog.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialog.window?.attributes?.gravity = Gravity.BOTTOM
        dialog.window?.attributes?.windowAnimations = R.style.NewCartDialogAnimation

        return dialog
    }






}