package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import biweekly.util.DayOfWeek
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.databinding.BottomSheetCustomRecurringBinding
import com.bupp.wood_spoon_chef.presentation.custom_views.CreateCookingSlotOptionView
import com.bupp.wood_spoon_chef.presentation.custom_views.HeaderView
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.rrules.RRuleTextFormatter
import com.bupp.wood_spoon_chef.utils.extensions.show
import com.bupp.wood_spoon_chef.utils.extensions.showErrorToast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CustomRecurringBottomSheet(
    private val recurringRule: String?
) : TopCorneredBottomSheet(), HeaderView.HeaderViewListener {

    private var binding: BottomSheetCustomRecurringBinding? = null
    private val viewModel by viewModels<CustomRecurringViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_custom_recurring, container, false)
        binding = BottomSheetCustomRecurringBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFullScreenDialog()
        initUi()
        viewModel.init(recurringRule)
        observeViewModelState()
        observeViewModelEvents()
    }

    private fun initUi() {
        binding?.apply {
            customRecurringHeaderView.setHeaderViewListener(this@CustomRecurringBottomSheet)
            customRecurringDaysSection.children.forEach { dayView ->
                dayView.setOnClickListener {
                    viewModel.onDayClick(it.tag.toString())
                }
            }
            customRecurringFrequency.setOnClickListener {
                viewModel.onCustomClick()
            }

            customRecurringSaveBtn.setOnClickListener {
                viewModel.onSaveClick()
            }

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
            }
        }
    }

    private fun observeViewModelEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is CustomRecurringEvent.SaveRecurringRule -> {
                            onSaveClick(event.recurringRule)
                        }
                        is CustomRecurringEvent.ShowCustomFrequencyPicker -> {
                            CustomFrequencyPickerBottomSheet.show(
                                this@CustomRecurringBottomSheet,
                                event.interval,
                                event.frequency
                            ) { frequency, interval ->
                                viewModel.setFrequency(requireContext(), frequency)
                                viewModel.setInterval(interval)
                            }
                        }
                        is CustomRecurringEvent.Error -> {
                            binding?.let {
                                showErrorToast(
                                    event.message, it.customRecurringMainLayout, Toast.LENGTH_SHORT
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun onSaveClick(recurringRule: String?) {
        setFragmentResult(
            CUSTOM_RULE_KEY,
            bundleOf(CUSTOM_RULE_VALUE to recurringRule)
        )
        dismiss()
    }

    private fun setSelectedDays(selectedDays: List<DayOfWeek>) {
        binding?.apply {
            customRecurringDaysSection.children.forEach { dayView ->
                (dayView as? CreateCookingSlotOptionView)?.showEndDrawable(selectedDays.any {
                    it.name.equals(
                        dayView.tag.toString(),
                        true
                    )
                })
            }
        }
    }

    private fun updateInputsWithState(state: CustomRecurringState) {
        setSelectedDays(state.selectedDays)
        setFrequencyAndIntervalText(
            formatFrequencyString(
                state.customFrequency,
                state.customInterval
            )
        )
        when (state.customFrequency) {
            CustomFrequency.DAY -> showDaysSection(false)
            CustomFrequency.WEEK -> showDaysSection(true)
        }
    }

    private fun formatFrequencyString(
        customFrequency: CustomFrequency,
        interval: Int
    ): String {
        return when (customFrequency) {
            CustomFrequency.DAY -> "$interval ${
                resources.getQuantityString(
                    R.plurals.day_plurals,
                    interval
                )
            }"
            CustomFrequency.WEEK -> "$interval ${
                resources.getQuantityString(
                    R.plurals.week_plurals,
                    interval
                )
            }"
        }
    }


    private fun showDaysSection(show: Boolean) {
        binding?.apply {
            customRecurringDaysSection.show(show)
        }
    }

    private fun setFrequencyAndIntervalText(frequencyTxt: String?) {
        binding?.customRecurringFrequencyInput?.text = frequencyTxt
    }

    override fun onHeaderBackClick() {
        dismiss()
    }

    override fun clearClassVariables() {
        binding = null
    }

    companion object {

        const val CUSTOM_RULE_KEY = "customRuleKey"
        const val CUSTOM_RULE_VALUE = "customRuleValue"

        fun show(
            fragment: Fragment,
            recurringRule: String?,
            listener: ((String?) -> Unit)
        ) {
            CustomRecurringBottomSheet(recurringRule).show(
                fragment.childFragmentManager,
                CustomRecurringBottomSheet::class.simpleName
            )

            fragment.setRecurringRuleResultListener(listener)
        }
    }
}

private fun Fragment.setRecurringRuleResultListener(listener: ((String?) -> Unit)) {
    childFragmentManager.setFragmentResultListener(
        CustomRecurringBottomSheet.CUSTOM_RULE_KEY,
        this
    ) { _, bundle ->
        val result = bundle.getString(CustomRecurringBottomSheet.CUSTOM_RULE_VALUE)
        listener.invoke(result)
    }
}