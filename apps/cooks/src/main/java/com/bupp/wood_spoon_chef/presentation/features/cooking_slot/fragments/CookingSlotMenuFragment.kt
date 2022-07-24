package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.FragmentCookingSlotMenuBinding
import com.bupp.wood_spoon_chef.presentation.custom_views.CreateCookingSlotTopBar
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.data.models.DishesMenuAdapterModel
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
    DishMenuAdapter.DishesMenuAdapterListener {

    private val binding by viewBinding(FragmentCookingSlotMenuBinding::bind)
    private lateinit var dishMenuAdapter: DishMenuAdapter

    private val viewModel: CookingSlotMenuViewModel by viewModel {
        parametersOf(findParent(CookingSlotParentFragment::class.java)?.cookingSlotCoordinator)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                viewModel.onOpenReviewFragmentClicked()
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
                        setAddDishesView(state.menuItemsByCategory)
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
                                openMyDishesBottomSheet(event.selectedDishes)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateInputsWithState(state: CookingSlotMenuState) {
        binding.apply {
            updateDishList(state.menuItemsByCategory)
            setTitle(state.operatingHours)
        }
    }

    private fun openMyDishesBottomSheet(selectedDishes: List<Long>) {
        MyDishesBottomSheet.show(this, selectedDishes) {
            viewModel.addDishesByIds(it)
        }
    }

    private fun setupList() {
        binding.apply {
            createCookingSlotMenuFragmentDishesRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                dishMenuAdapter = DishMenuAdapter(this@CookingSlotMenuFragment)
                adapter = dishMenuAdapter
                itemAnimator = object : DefaultItemAnimator() {
                    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder) =
                        true

                    override fun canReuseUpdatedViewHolder(
                        viewHolder: RecyclerView.ViewHolder,
                        payloads: MutableList<Any>
                    ) = true
                }
            }
        }
    }

    private fun updateDishList(data: List<DishesMenuAdapterModel>?) {
        data?.let {
            dishMenuAdapter.submitSections(it)
        }
    }

    private fun setTitle(operatingHours: OperatingHours) {
        binding.apply {
            createCookingSlotMenuFragmentTitle.text =
                DateTime(operatingHours.startTime).prepareFormattedDate()
            createCookingSlotMenuFragmentOpeningHours.text =
                getString(
                    R.string.selected_date_format,
                    DateTime(operatingHours.startTime).prepareFormattedDateForHours(),
                    DateTime(operatingHours.endTime).prepareFormattedDateForHours()
                )
        }
    }

    private fun setAddDishesView(dishList: List<DishesMenuAdapterModel>?) {
        binding.apply {
            createCookingSlotMenuFragmentAddDishesEmpty.show(dishList.isNullOrEmpty())
            createCookingSlotMenuFragmentAddDishesFull.show(!dishList.isNullOrEmpty())
        }
    }

    override fun onBackClick() {
        findNavController().navigateUp()
    }

    override fun onDeleteClick(dishId: Long?) {
        viewModel.onDeleteDishClick(dishId)
    }

    override fun onQuantityChange(dishId: Long?, quantity: Int) {
        dishId?.let {
            if (quantity >= 1) {
                viewModel.updateQuantity(dishId, quantity)
            } else {
                viewModel.updateQuantity(dishId, 1)
            }
        }
    }
}