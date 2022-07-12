package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.model.Dish
import com.bupp.wood_spoon_chef.databinding.FragmentCookingSlotMenuBinding
import com.bupp.wood_spoon_chef.presentation.custom_views.CreateCookingSlotTopBar
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs.MyDishesBottomSheet
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.base.CookingSlotParentFragment
import com.bupp.wood_spoon_chef.utils.DateUtils.prepareFormattedDateForHours
import com.bupp.wood_spoon_chef.utils.extensions.findParent
import com.bupp.wood_spoon_chef.utils.extensions.prepareFormattedDate
import com.bupp.wood_spoon_chef.utils.extensions.show
import com.eatwoodspoon.android_utils.binding.viewBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CookingSlotMenuFragment :
    Fragment(R.layout.fragment_cooking_slot_menu),
    CreateCookingSlotTopBar.CreateCookingSlotTopBarListener,
    DishMenuAdapter.DishesMenuAdapterListener{

    private val binding by viewBinding(FragmentCookingSlotMenuBinding::bind)
    private lateinit var dishMenuAdapter: DishMenuAdapter

    private val viewModel: CookingSlotMenuViewModel by viewModel {
        parametersOf(findParent(CookingSlotParentFragment::class.java)?.cookingSlotCoordinator)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parseArguments()
        initUi()
        setupList()
        observeViewModelState()
        observeViewModelEvents()
    }

    private fun initUi() {
        binding.apply {
            createCookingSlotMenuFragmentTopBar.setCookingSlotTopBarListener(
                this@CookingSlotMenuFragment
            )
            createCookingSlotMenuFragmentGoToReviewBtn.setOnClickListener {
                viewModel.openReviewFragment()
            }
            createCookingSlotMenuFragmentAddDishesEmpty.setOnClickListener {
                viewModel.onAddDishesClick()
            }
            createCookingSlotMenuFragmentAddDishesFull.setOnClickListener {
                viewModel.onAddDishesClick()
            }
        }
    }

    private fun observeViewModelState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        updateInputsWithState(state)
                        setAddDishesView(state.dishList)
                    }
                }
            }
        }
    }

    private fun observeViewModelEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.events.collect { event ->
                        when (event) {
                            is CookingSlotMenuEvents.ShowMyDishesBottomSheet -> {
                                openMyDishesBottomSheet(event.dishList)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateInputsWithState(state: CookingSlotMenuState) {
        binding.apply {
            updateDishList(state.dishList)
            setTitle(state.cookingSlot)
        }
    }

    private fun openMyDishesBottomSheet(selectedDishes: List<Dish>?) {
        MyDishesBottomSheet.show(this, selectedDishes) {
            viewModel.setDishList(it)
        }
    }

    private fun setupList() {
        binding.apply {
            createCookingSlotMenuFragmentDishesRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                dishMenuAdapter = DishMenuAdapter(this@CookingSlotMenuFragment)
                adapter = dishMenuAdapter
            }
        }
    }

    private fun updateDishList(data: List<Dish>?) {
        dishMenuAdapter.submitList(data)
    }

    private fun parseArguments() {
        val args : CookingSlotMenuFragmentArgs by navArgs()
        viewModel.setCookingSlot(cookingSlot = args.cookingSlot)
    }

    private fun setTitle(cookingSlot: CookingSlot?) {
        binding.apply {
            createCookingSlotMenuFragmentTitle.text =
                DateTime(cookingSlot?.startsAt).prepareFormattedDate()
            createCookingSlotMenuFragmentOpeningHours.text =
                getString(R.string.selected_date_format,
                    DateTime(cookingSlot?.startsAt).prepareFormattedDateForHours(),
                    DateTime(cookingSlot?.endsAt).prepareFormattedDateForHours())
        }
    }

    private fun setAddDishesView(dishList: List<Dish>?){
        binding.apply {
            createCookingSlotMenuFragmentAddDishesEmpty.show(dishList.isNullOrEmpty())
            createCookingSlotMenuFragmentAddDishesFull.show(!dishList.isNullOrEmpty())
        }
    }

    override fun onBackClick() {
        findNavController().navigateUp()
    }

    override fun onDeleteClick(dish: Dish, position: Int) {
        viewModel.removeDishFromList(dish)
        dishMenuAdapter.notifyItemRemoved(position)
    }
}