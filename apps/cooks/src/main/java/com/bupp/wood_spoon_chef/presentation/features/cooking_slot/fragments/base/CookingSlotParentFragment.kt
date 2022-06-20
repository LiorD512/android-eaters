package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.FragmentCookingSlotParentBinding
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.navigation.CookingSlotFlowNavigator
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

interface CookingSlotParent {
    val cookingSlotNavigator: CookingSlotFlowNavigator
}

class CookingSlotParentFragment : Fragment(R.layout.fragment_cooking_slot_parent), CookingSlotParent {

    private lateinit var navController: NavController
    private var binding: FragmentCookingSlotParentBinding? = null
    override val cookingSlotNavigator: CookingSlotFlowNavigator by inject()
    private val viewModel: CookingSlotParentViewModel by viewModel {
        parametersOf(cookingSlotNavigator)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(!navController.popBackStack()) {
                    // In case this flow will be used in SingleActivity app we will have undesired behaviour
                    activity?.finish()
                }
            }
        })
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCookingSlotParentBinding.bind(view)


        setNavController()
        initObservers()
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.whenStarted {
                viewModel.stepStateFlow.collect {
                    when (it) {
                        CookingSlotFlowNavigator.Step.OPEN_MENU_FRAGMENT -> {
                            navController.navigate(R.id.to_cookingSlotMenuFragment)
                        }
                        CookingSlotFlowNavigator.Step.OPEN_REVIEW_FRAGMENT -> {
                            navController.navigate(R.id.to_cookingSlotReviewFragment)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun setNavController(){
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.cookingSlotParentContainer) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}