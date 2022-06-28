package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.FragmentCookingSlotMenuBinding
import com.bupp.wood_spoon_chef.presentation.custom_views.CreateCookingSlotTopBar
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.base.CookingSlotParentFragment
import com.bupp.wood_spoon_chef.utils.extensions.findParent
import com.eatwoodspoon.android_utils.binding.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CookingSlotMenuFragment :
    Fragment(R.layout.fragment_cooking_slot_menu),
    CreateCookingSlotTopBar.CreateCookingSlotTopBarListener {

    private val binding by viewBinding(FragmentCookingSlotMenuBinding::bind)

    private val viewModel: CookingSlotMenuViewModel by viewModel {
        parametersOf(findParent(CookingSlotParentFragment::class.java)?.cookingSlotCoordinator)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        binding.apply {
            createCookingSlotMenuFragmentTopBar.setCookingSlotTopBarListener(this@CookingSlotMenuFragment)
            createCookingSlotMenuFragmentGoToReviewBtn.setOnClickListener { viewModel.openReviewFragment() }
        }
    }

    override fun onBackClick() {
        findNavController().navigateUp()
    }
}