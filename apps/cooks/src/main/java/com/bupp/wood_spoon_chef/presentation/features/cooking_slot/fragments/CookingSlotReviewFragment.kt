package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.FragmentCookingSlotReviewBinding
import com.bupp.wood_spoon_chef.presentation.custom_views.CreateCookingSlotTopBar
import com.bupp.wood_spoon_chef.presentation.features.main.calendar.sub_screen.calendar_menu_adapter.CookingSlotDetailsAdapter
import com.eatwoodspoon.android_utils.binding.viewBinding

class CookingSlotReviewFragment : Fragment(R.layout.fragment_cooking_slot_review),
    CreateCookingSlotTopBar.CreateCookingSlotTopBarListener {

    private val binding by viewBinding(FragmentCookingSlotReviewBinding::bind)
    private var menuAdapter: CookingSlotMenuAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        binding.apply {
            reviewFragmentTopBar.setCookingSlotTopBarListener(this@CookingSlotReviewFragment)
            initList(this)
        }
    }

    private fun initList(binding: FragmentCookingSlotReviewBinding) {
        binding.rvMenu.let {
            it.layoutManager = LinearLayoutManager(context)
            it.isNestedScrollingEnabled = false
        }
        menuAdapter = CookingSlotMenuAdapter()
        binding.rvMenu.adapter = menuAdapter
    }

    override fun onBackClick() {
        findNavController().navigateUp()
    }
}