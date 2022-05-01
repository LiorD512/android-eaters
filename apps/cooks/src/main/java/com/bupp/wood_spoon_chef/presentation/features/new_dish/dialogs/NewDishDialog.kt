package com.bupp.wood_spoon_chef.presentation.features.new_dish.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.NewDishDialogBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseDialogFragment


class NewDishDialog : BaseDialogFragment(R.layout.new_dish_dialog) {

    var binding: NewDishDialogBinding? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = NewDishDialogBinding.inflate(LayoutInflater.from(context))
        return AlertDialog.Builder(requireActivity())
            .setView(binding!!.root)
            .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        with(binding!!) {
            newDishDialogGotIt.setOnClickListener {
                dismiss()
            }
            newDishBkg.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun clearClassVariables() {
        binding = null
    }

}