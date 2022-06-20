package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.FragmentCreateCookingSlotNewBinding
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.base.CookingSlotParentFragment
import com.bupp.wood_spoon_chef.utils.extensions.findParent
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
class CreateCookingSlotFragmentNew : Fragment(R.layout.fragment_create_cooking_slot_new) {

    private var binding: FragmentCreateCookingSlotNewBinding? = null
    private val viewModel: CreateCookingSlotNewViewModel by viewModel {
        parametersOf(findParent(CookingSlotParentFragment::class.java)?.cookingSlotNavigator)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreateCookingSlotNewBinding.bind(view)

        initUi()
    }

    private fun initUi(){
        viewModel.let {  }
        binding?.apply {
            createCookingSlotNewFragmentNextBtn.setOnClickListener { viewModel.openMenuFragment()}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}