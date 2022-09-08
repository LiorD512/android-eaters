package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.cooking_slot_menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
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
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.create_cooking_slot.OperatingHours
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.base.CookingSlotParentFragment
import com.bupp.wood_spoon_chef.utils.DateUtils.prepareFormattedDateForHours
import com.bupp.wood_spoon_chef.utils.extensions.prepareFormattedDate
import com.bupp.wood_spoon_chef.utils.extensions.show
import com.bupp.wood_spoon_chef.utils.extensions.showErrorToast
import com.eatwoodspoon.android_utils.binding.viewBinding
import com.eatwoodspoon.android_utils.fragments.findParent
import com.eatwoodspoon.android_utils.views.setSafeOnClickListener
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
            createCookingSlotMenuFragmentGoToReviewBtn.setSafeOnClickListener {
                viewModel.onOpenReviewFragmentClicked(requireContext())
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
                            is CookingSlotMenuEvents.Error -> {
                                showErrorToast(
                                    event.message,
                                    binding.cookingSlotMenuFragmentMainLayout,
                                    Toast.LENGTH_SHORT
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateInputsWithState(state: CookingSlotMenuState) {
        binding.apply {
            setHeaderTitle(state.isInEditMode)
            updateDishList(state.menuItemsByCategory)
            setTitle(state.operatingHours)
        }
    }

    private fun setHeaderTitle(isEditMode: Boolean) {
        binding.apply {
            if (isEditMode) {
                createCookingSlotMenuFragmentTopBar.setTitle(getString(R.string.edit_cooking_slot_menu))
            } else {
                createCookingSlotMenuFragmentTopBar.setTitle(getString(R.string.my_cooking_slot_menu))
            }
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
            viewModel.updateQuantity(dishId, quantity)
        }
    }
}