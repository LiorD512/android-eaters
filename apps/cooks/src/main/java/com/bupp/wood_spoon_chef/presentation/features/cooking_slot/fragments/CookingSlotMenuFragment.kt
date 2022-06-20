package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.FragmentCookingSlotMenuBinding
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.base.CookingSlotParentFragment
import com.bupp.wood_spoon_chef.utils.extensions.findParent
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
class CookingSlotMenuFragment : Fragment(R.layout.fragment_cooking_slot_menu) {

    private var binding: FragmentCookingSlotMenuBinding? = null
    private val viewModel: CookingSlotMenuViewModel by viewModel {
        parametersOf(findParent(CookingSlotParentFragment::class.java)?.cookingSlotNavigator)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCookingSlotMenuBinding.bind(view)

        initUi()
    }

    private fun initUi() {
        binding?.apply {
            createCookingSlotMenuFragmentNextBtn.setOnClickListener { viewModel.openReviewFragment() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}