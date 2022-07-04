package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.databinding.FragmentCreateCookingSlotNewBinding
import com.bupp.wood_spoon_chef.presentation.custom_views.CreateCookingSlotTopBar
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs.OperatingHoursInfoBottomSheet
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs.TimePickerBottomSheet
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.base.CookingSlotParentFragment
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

class CreateCookingSlotFragmentNew : Fragment(R.layout.fragment_create_cooking_slot_new),
    CreateCookingSlotTopBar.CreateCookingSlotTopBarListener {

    private val binding by viewBinding(FragmentCreateCookingSlotNewBinding::bind)
    private val viewModel: CreateCookingSlotNewViewModel by viewModel {
        parametersOf(findParent(CookingSlotParentFragment::class.java)?.cookingSlotCoordinator)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getArgs()
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
            createCookingSlotNewFragmentMakeRecurringView.setForwardBtnClickListener { viewModel.onRecurringSlotClick() }

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
                            is CreateCookingSlotEvents.ShowOperatingHours -> openTimePickerBottomSheetStart()
                            is CreateCookingSlotEvents.ShowLastCallForOrder -> openLastCallForOrder(
                                event.lastCallForOrder
                            )
                            is CreateCookingSlotEvents.ShowRecurringRule -> openRecurringRule(
                                event.recurringRule
                            )
                            is CreateCookingSlotEvents.ShowEndTimePicker -> openTimePickerBottomSheetEnd(
                                event.startTime
                            )
                        }
                    }
                }
            }
        }
    }

    private fun updateInputsWithState(state: CreateCookingSlotNewState) {
        binding.apply {
            showTitle(state.selectedDate)

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

    private fun getArgs() {
        val selectedDate = requireActivity().intent.getLongExtra(Constants.ARG_SELECTED_DATE, 0)
        viewModel.setSelectedDate(selectedDate)
    }

    private fun showTitle(selectedDate: Long?) {
        binding.createCookingSlotNewFragmentTitle.text =
            DateTime(selectedDate).prepareFormattedDate()
    }

    private fun openOperatingHoursInfoBottomSheet() {
        OperatingHoursInfoBottomSheet().show(
            childFragmentManager,
            OperatingHoursInfoBottomSheet::class.simpleName
        )
    }

    private fun openTimePickerBottomSheetStart() {
        binding.createCookingSlotNewFragmentOperatingHoursError.show(false)
        TimePickerBottomSheet.show(this, TimePickerBottomSheet.TimePickerState.START_TIME, null){
            viewModel.setStartTime(it)
            viewModel.openOperatingHoursEndTime()
        }
    }

    private fun openTimePickerBottomSheetEnd(startTime: Long?) {
        TimePickerBottomSheet.show(this, TimePickerBottomSheet.TimePickerState.END_TIME, startTime){
            viewModel.setOperatingHours(OperatingHours(startTime, it))
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

private fun DateTime.prepareFormattedDateForHours(): String =
    DateTimeFormat.forPattern("hh:mm aa").print(this)

private fun DateTime.prepareFormattedDateForDateAndHour(): String =
    DateTimeFormat.forPattern("EEEE, MMM dd, yyyy, hh:mm aa").print(this)