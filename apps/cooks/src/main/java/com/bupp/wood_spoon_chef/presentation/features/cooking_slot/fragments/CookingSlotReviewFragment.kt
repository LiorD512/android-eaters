package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.FragmentCookingSlotReviewBinding
import com.bupp.wood_spoon_chef.presentation.custom_views.CreateCookingSlotTopBar
import com.bupp.wood_spoon_chef.presentation.features.base.BaseFragment
import com.bupp.wood_spoon_chef.utils.DateUtils.prepareFormattedDateForHours
import com.bupp.wood_spoon_chef.utils.extensions.prepareFormattedDate
import com.bupp.wood_spoon_chef.utils.extensions.prepareFormattedDateForDateAndHour
import com.eatwoodspoon.android_utils.binding.viewBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.koin.androidx.viewmodel.ext.android.viewModel

class CookingSlotReviewFragment : BaseFragment(R.layout.fragment_cooking_slot_review),
    CreateCookingSlotTopBar.CreateCookingSlotTopBarListener {

    private val binding by viewBinding(FragmentCookingSlotReviewBinding::bind)
    private var menuAdapter: CookingSlotMenuAdapter? = null
    private val viewModel: CookingSlotReviewViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        ReviewCookingSlotState.Idle -> {
                            handleProgressBar(false)
                        }
                        is ReviewCookingSlotState.Loading -> {
                            handleProgressBar(isLoading = state.isLoading)
                        }
                        is ReviewCookingSlotState.ScreenDataState -> {
                            handleProgressBar(false)
                            updateInputsWithState(state)
                        }
                        is ReviewCookingSlotState.Error -> {
                            handleProgressBar(false)
                            handleErrorEvent(state.error, binding.root)
                        }
                        ReviewCookingSlotState.SlotCreatedSuccess -> {
                            handleProgressBar(false)
                            activity?.finish()
                        }
                    }
                }
            }
        }
    }

    private fun updateInputsWithState(state: ReviewCookingSlotState.ScreenDataState) {
        binding.apply {
            showTitle(state.selectedDate)
            showSubTitle(
                startTime = state.operatingHours.startTime,
                endTime = state.operatingHours.endTime
            )

            state.btnActionStringRes?.let {
                showButtonText(it)
            }

            createCookingSlotNewFragmentLastCallForOrderView.setSubtitle(
                formatLastCallForOrderDate(state.lastCallForOrder)
            )
            createCookingSlotNewFragmentMakeRecurringView.setSubtitle(
                state.recurringRule)

            menuAdapter?.submitList(state.menuItems)
        }
    }

    private fun showButtonText(
        @StringRes resId: Int
    ) {
        binding.apply {
            context?.resources?.getString(resId)?.let { string ->
                btnSaveSlot.setText(string)
            }
        }
    }

    private fun showTitle(selectedDate: Long?) {
        binding.tvHeaderTitle.text = DateTime(selectedDate).prepareFormattedDate()
    }

    private fun showSubTitle(startTime: Long?, endTime: Long?) {
        binding.apply {
            tvHeaderSubtitle.text = getString(
                R.string.selected_date_format,
                DateTime(startTime).prepareFormattedDateForHours(),
                DateTime(endTime).prepareFormattedDateForHours()
            )
        }
    }

    private fun initUi() {
        binding.apply {
            reviewFragmentTopBar.setCookingSlotTopBarListener(this@CookingSlotReviewFragment)
            initList(this)

            btnSaveSlot.setOnClickListener {
                viewModel.saveOrUpdateCookingSlot()
            }
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

    private fun formatLastCallForOrderDate(lastCallForOrder: Long?): String {
        if (lastCallForOrder != null) {
            return DateTime(lastCallForOrder).prepareFormattedDateForDateAndHour()
        }
        return ""
    }

    override fun onBackClick() {
        findNavController().navigateUp()
    }

    override fun clearClassVariables() {}
}