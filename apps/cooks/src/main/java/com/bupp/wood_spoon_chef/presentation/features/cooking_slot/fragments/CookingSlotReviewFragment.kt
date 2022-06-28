package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.FragmentCookingSlotReviewBinding
import com.bupp.wood_spoon_chef.presentation.custom_views.CreateCookingSlotTopBar
import com.eatwoodspoon.android_utils.binding.viewBinding

class CookingSlotReviewFragment : Fragment(R.layout.fragment_cooking_slot_review),
    CreateCookingSlotTopBar.CreateCookingSlotTopBarListener {

    private val binding by viewBinding(FragmentCookingSlotReviewBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        binding.apply {
            createCookingSlotReviewFragmentTopBar.setCookingSlotTopBarListener(this@CookingSlotReviewFragment)
        }
    }

    override fun onBackClick() {
        findNavController().navigateUp()
    }
}