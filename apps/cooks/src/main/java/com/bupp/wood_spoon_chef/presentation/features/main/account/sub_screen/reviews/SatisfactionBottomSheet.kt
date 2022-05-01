package com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.FloatingBottomSheet
import com.bupp.wood_spoon_chef.databinding.BottomSheetSatisfactionBinding

class SatisfactionBottomSheet: FloatingBottomSheet() {

    private var binding: BottomSheetSatisfactionBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.bottom_sheet_satisfaction, container, false)
        binding = BottomSheetSatisfactionBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        binding?.root?.setOnClickListener{
            dismiss()
        }
    }

    override fun clearClassVariables() {
        binding = null
    }

}