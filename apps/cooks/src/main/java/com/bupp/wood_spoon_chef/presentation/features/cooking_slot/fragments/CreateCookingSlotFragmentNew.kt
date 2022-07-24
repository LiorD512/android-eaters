package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.FragmentCreateCookingSlotNewBinding
import com.bupp.wood_spoon_chef.presentation.custom_views.CreateCookingSlotTopBar
import com.bupp.wood_spoon_chef.presentation.features.base.BaseFragment
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs.OperatingHoursInfoBottomSheet
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs.TimePickerBottomSheet
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.base.CookingSlotParentFragment
import com.bupp.wood_spoon_chef.utils.DateUtils.prepareFormattedDateForHours
import com.bupp.wood_spoon_chef.utils.extensions.*
import com.eatwoodspoon.android_utils.binding.viewBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CreateCookingSlotFragmentNew : BaseFragment(R.layout.fragment_create_cooking_slot_new),
    CreateCookingSlotTopBar.CreateCookingSlotTopBarListener {

    private val binding by viewBinding(FragmentCreateCookingSlotNewBinding::bind)
    private val viewModel: CreateCookingSlotNewViewModel by viewModel {
        parametersOf(findParent(CookingSlotParentFragment::class.java)?.cookingSlotCoordinator)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        observeViewModelState()
        observeViewModelEvents()
    }

    private fun initUi() {
        binding.apply {
            createCookingSlotNewFragmentTopBar.setCookingSlotTopBarListener(this@CreateCookingSlotFragmentNew)
            createCookingSlotNewFragmentNextBtn.setOnClickListener { viewModel.onNextClick() }
            createCookingSlotNewFragmentOperatingHoursView.setOnSecondaryIconClickListener { openOperatingHoursInfoBottomSheet() }
            createCookingSlotNewFragmentOperatingHoursView.setAddClickListener { viewModel.onOperatingHoursClick() }
            createCookingSlotNewFragmentLastCallForOrderView.setForwardBtnClickListener { viewModel.onLastCallForOrderClick() }

        }
    }

    private fun observeViewModelState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        updateInputsWithState(state)
                    }
                }

                launch {
                    viewModel.state.map { it.errors }.distinctUntilChanged().collect {
                        updateErrors(it)
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
                            is CreateCookingSlotEvents.Error -> showErrorToast(
                                event.message ?: getString(R.string.generic_error),
                                binding.createCookingSlotNewFragmentMainLayout,
                                Toast.LENGTH_SHORT
                            )
                            is CreateCookingSlotEvents.ShowOperatingHours -> openTimePickerBottomSheet(
                                event.selectedDate,
                                event.operatingHours
                            )
                            is CreateCookingSlotEvents.ShowLastCallForOrder -> openLastCallForOrder(
                                event.lastCallForOrder
                            )
                            is CreateCookingSlotEvents.ShowRecurringRule -> openRecurringRule(
                                event.recurringRule
                            )
                        }
                    }
                }
            }
        }
    }

    private fun updateInputsWithState(state: CreateCookingSlotNewState) {
        binding.apply {
            setHeaderTitle(state.isInEditMode)
            showTitle(state.selectedDate)
            handleProgressBar(state.inProgress)

            createCookingSlotNewFragmentOperatingHoursView.setSubtitle(
                formatOperatingHours(
                    state.operatingHours.startTime, state.operatingHours.endTime
                )
            )
            createCookingSlotNewFragmentLastCallForOrderView.setSubtitle(
                formatLastCallForOrderDate(state.lastCallForOrder)
            )
            createCookingSlotNewFragmentMakeRecurringView.setSubtitle(
                formatRcs(state.recurringRule)
            )
        }
    }

    private fun showTitle(selectedDate: Long?) {
        binding.createCookingSlotNewFragmentTitle.text =
            DateTime(selectedDate).prepareFormattedDate()
    }

    private fun setHeaderTitle(isEditMode: Boolean){
        binding.apply {
            if (isEditMode){
                createCookingSlotNewFragmentTopBar.setTitle(getString(R.string.edit_cooking_slot))
            }else{
                createCookingSlotNewFragmentTopBar.setTitle(getString(R.string.create_cooking_slot))
            }
        }
    }

    private fun openOperatingHoursInfoBottomSheet() {
        OperatingHoursInfoBottomSheet().show(
            childFragmentManager,
            OperatingHoursInfoBottomSheet::class.simpleName
        )
    }

    private fun openTimePickerBottomSheet(selectedDate: Long?, operatingHours: OperatingHours?) {
        selectedDate?.let { date ->
            binding.createCookingSlotNewFragmentOperatingHoursError.show(false)
            TimePickerBottomSheet.show(this,date, operatingHours){
                viewModel.setOperatingHours(it)
            }
        }
    }

    private fun openLastCallForOrder(lastCallForOrder: Long?) {
        viewModel.setLastCallForOrders(lastCallForOrder)
    }

    private fun openRecurringRule(recurringRule: RecurringRule?) {
        viewModel.setRecurringRule(recurringRule)
    }

    override fun onBackClick() {
        requireActivity().finish()
    }

    private fun updateErrors(errors: List<Errors>) {
        errors.forEach { error ->
            when (error) {
                Errors.OperatingHours -> binding.createCookingSlotNewFragmentOperatingHoursError.show(
                    true
                )
            }
        }
    }


    override fun clearClassVariables() {}
}

private fun formatOperatingHours(startTime: Long?, endTime: Long?): String {
    if (startTime != null && endTime != null) {
        return "${DateTime(startTime).prepareFormattedDateForHours()} - ${DateTime(endTime).prepareFormattedDateForHours()}"
    }
    return ""
}

private fun formatLastCallForOrderDate(lastCallForOrder: Long?): String {
    if (lastCallForOrder != null) {
        return DateTime(lastCallForOrder).prepareFormattedDateForDateAndHour()
    }
    return ""
}

private fun formatRcs(recurringRule: RecurringRule?): String {
    if (recurringRule != null) {
        return "Cooking slot will occur ${recurringRule.frequency}, ${recurringRule.count} times"
    }
    return ""
}

private fun DateTime.prepareFormattedDateForDateAndHour(): String =
    DateTimeFormat.forPattern("EEEE, MMM dd, yyyy, hh:mm aa").print(this)