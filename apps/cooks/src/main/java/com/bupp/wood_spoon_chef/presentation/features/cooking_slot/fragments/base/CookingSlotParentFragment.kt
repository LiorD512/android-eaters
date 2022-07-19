package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.FragmentCookingSlotParentBinding
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator.CookingSlotFlowCoordinator
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.CookingSlotMenuFragmentDirections
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.CookingSlotReviewFragmentDirections
import com.eatwoodspoon.android_utils.binding.viewBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

interface CookingSlotParent {
    val cookingSlotCoordinator: CookingSlotFlowCoordinator
}

class CookingSlotParentFragment : Fragment(R.layout.fragment_cooking_slot_parent),
    CookingSlotParent {

    private lateinit var navController: NavController
    private val binding by viewBinding(FragmentCookingSlotParentBinding::bind)
    override val cookingSlotCoordinator: CookingSlotFlowCoordinator by inject()
    private val viewModel: CookingSlotParentViewModel by viewModel {
        parametersOf(cookingSlotCoordinator)
    }

    companion object {
        private const val KEY_SCREEN = "key_starting_point"

        fun newInstance(screen: CookingSlotFlowCoordinator.Step? = null) =
            CookingSlotParentFragment().apply {
                screen?.let {
                    arguments = bundleOf(
                        KEY_SCREEN to screen.name
                    )
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(KEY_SCREEN)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!navController.popBackStack()) {
                        // In case this flow will be used in SingleActivity app we will have undesired behaviour
                        activity?.finish()
                    }
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavController()
        initObservers()
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.stepStateFlow.collect {
                    when (it.step) {
                        CookingSlotFlowCoordinator.Step.OPEN_MENU_FRAGMENT -> {
                            CookingSlotMenuFragmentDirections.toCookingSlotMenuFragment(it.cookingSlot)
                        }
                        CookingSlotFlowCoordinator.Step.OPEN_REVIEW_FRAGMENT -> {
                            CookingSlotReviewFragmentDirections.toCookingSlotReviewFragment(it.cookingSlot)
                        }
                        else -> { null }
                    }?.let { action ->
                        navController.navigate(action)
                    }
                }
            }
        }
    }

    private fun setNavController() {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.cookingSlotParentContainer) as NavHostFragment
        navController = navHostFragment.navController
    }
}