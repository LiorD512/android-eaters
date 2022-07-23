package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.base

import android.os.Bundle
import android.os.Parcelable
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
import com.bupp.wood_spoon_chef.common.MTLogger
import com.bupp.wood_spoon_chef.databinding.FragmentCookingSlotParentBinding
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator.CookingSlotFlowCoordinator
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator.CookingSlotFlowNavigationEvent
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.coordinator.CookingSlotFlowStep
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.CookingSlotMenuFragmentDirections
import com.eatwoodspoon.android_utils.binding.viewBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

interface CookingSlotParent {
    val cookingSlotCoordinator: CookingSlotFlowCoordinator
}

@Parcelize
data class CookingSlotParentFragmentArgs(
    val cookingSlotId: Long?,
    val selectedDate: Long?
) : Parcelable

class CookingSlotParentFragment : Fragment(R.layout.fragment_cooking_slot_parent),
    CookingSlotParent {

    private lateinit var navController: NavController
    private val binding by viewBinding(FragmentCookingSlotParentBinding::bind)
    override val cookingSlotCoordinator: CookingSlotFlowCoordinator by inject()
    private val viewModel: CookingSlotParentViewModel by viewModel {
        parametersOf(cookingSlotCoordinator)
    }

    companion object {
        private const val ARGUMENTS = "arguments"

        fun newInstance(cookingSlotId: Long?, selectedDate: Long?) =
            CookingSlotParentFragment().apply {
                arguments = bundleOf(
                    ARGUMENTS to CookingSlotParentFragmentArgs(
                        cookingSlotId = cookingSlotId,
                        selectedDate = selectedDate
                    )
                )
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        val startArguments =
            arguments?.getParcelable<CookingSlotParentFragmentArgs>(ARGUMENTS) ?: run {
                MTLogger.e("No Arguments Provided To `CookingSlotParentFragment`")
                return
            }
        viewModel.initWith(startArguments.cookingSlotId, startArguments.selectedDate)
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.navigationEvent.collect { event ->
                    when (event) {
                        CookingSlotFlowNavigationEvent.NavigateDone -> activity?.finish()
                        is CookingSlotFlowNavigationEvent.NavigateToStep -> navigateToStep(event.step)
                    }
                }
            }
        }
    }

    private fun navigateToStep(step: CookingSlotFlowStep) {
        when (step) {

            CookingSlotFlowStep.EDIT_DETAILS -> CookingSlotMenuFragmentDirections.toCookingSlotEditDetails()
            CookingSlotFlowStep.EDIT_MENU -> CookingSlotMenuFragmentDirections.toCookingSlotMenuFragment()
            CookingSlotFlowStep.PREVIEW_DETAILS -> CookingSlotMenuFragmentDirections.toCookingSlotReviewFragment()
        }?.let { action ->
            navController.navigate(action)
        }
    }

    private fun setNavController() {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.cookingSlotParentContainer) as NavHostFragment
        navController = navHostFragment.navController
    }
}