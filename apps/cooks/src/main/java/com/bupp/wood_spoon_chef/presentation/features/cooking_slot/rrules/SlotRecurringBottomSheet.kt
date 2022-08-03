package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.rrules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.databinding.BottomSheetSlotRecurringBinding
import com.bupp.wood_spoon_chef.presentation.custom_views.CreateCookingSlotOptionView
import com.bupp.wood_spoon_chef.presentation.custom_views.HeaderView
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.common.RecurringFrequency
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.common.RecurringFrequencyName
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.common.SlotRecurringEvent
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.common.SlotRecurringViewModel
import com.bupp.wood_spoon_chef.utils.DateUtils
import com.bupp.wood_spoon_chef.utils.extensions.showErrorToast
import com.google.android.material.datepicker.*
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SlotRecurringBottomSheet(
    private val recurringRule: String?,
    private val selectedDate: Long
) : TopCorneredBottomSheet(), HeaderView.HeaderViewListener, DatePickerDialog.OnDateSetListener {

    private var binding: BottomSheetSlotRecurringBinding? = null
    private val viewModel by viewModel<SlotRecurringViewModel>()
    private var optionViewList = listOf<CreateCookingSlotOptionView>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_slot_recurring, container, false)
        binding = BottomSheetSlotRecurringBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFullScreenDialog()
        initUi()
        viewModel.init(recurringRule, selectedDate)
        observeViewModelState()
        observeViewModelEvents()
    }

    private fun initUi() {
        binding?.apply {
            makeSlotRecurringHeaderView.setHeaderViewListener(this@SlotRecurringBottomSheet)

            optionViewList = listOf(
                makeSlotRecurringOneTime,
                makeSlotRecurringEveryDay,
                makeSlotRecurringEveryWeek
            )

            optionViewList.forEach {
                it.setOnClickListener { optionView ->
                    viewModel.onItemClicked(RecurringFrequencyName.valueOf(optionView.tag.toString()))
                }
            }

            makeSlotRecurringCustom.setOnClickListener {
                viewModel.onItemClicked(RecurringFrequencyName.valueOf(it.tag.toString()))
            }
            makeSlotRecurringSaveBtn.setOnClickListener {
                viewModel.onSaveClick()
            }

            makeSlotRecurringEndsAt.setOnClickListener {
                viewModel.onEndsAtClick()
            }


        }
    }

    private fun observeViewModelState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        setSelectedFrequencyItem(state.selectedFrequency)
                        setRruleText(state.humanReadableText)
                        setEndsAtText(state.endsAt)
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
                        is SlotRecurringEvent.ShowCustomPicker -> openCustomRecurringBottomSheet(
                            event.recurringRule
                        )
                        is SlotRecurringEvent.OnSave -> onSaveClick(event.recurringRule)
                        is SlotRecurringEvent.Error -> {
                            binding?.let {
                                showErrorToast(
                                    event.message,
                                    it.makeSlotRecurringMainLayout,
                                    Toast.LENGTH_SHORT
                                )
                            }
                        }
                        is SlotRecurringEvent.ShowEndAtDatePicker -> {
                            event.selectedDate?.let {
                                showEndsAtDatePicker(it)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setSelectedFrequencyItem(selectedFrequency: RecurringFrequency) {
        binding?.apply {
            optionViewList.forEach {
                it.apply {
                    showEndDrawable(tag.equals(selectedFrequency.name.name))
                }
            }

            makeSlotRecurringCustom.apply {
                if (tag.equals(selectedFrequency.name.name)) {
                    setEndIcon(R.drawable.ic_check_v)
                } else {
                    setEndIcon(R.drawable.ic_arrow_right)
                }
            }
        }
    }

    private fun openCustomRecurringBottomSheet(recurringRule: SimpleRRule?) {
        CustomRecurringBottomSheet.show(this, recurringRule) { simpleRule ->
            simpleRule?.let {
                viewModel.setRecurringFrequencyToCustom(it)
            }
        }
    }

    private fun setRruleText(humanReadableText: String?) {
        binding?.makeSlotRecurringRuleTxt?.text = humanReadableText
    }

    private fun showEndsAtDatePicker(selectedDate: Long) {
        val validator = CompositeDateValidator.allOf(
            listOf(
                DateValidatorPointForward.from(selectedDate),
                DateValidatorPointBackward.before(DateTime(selectedDate).plusMonths(3).millis)
            )
        )
        val calendarConstraints = CalendarConstraints.Builder().setValidator(validator).build()

        val picker =
            MaterialDatePicker.Builder.datePicker().setTitleText(getString(R.string.ends_at))
                .setPositiveButtonText(getString(R.string.save))
                .setNegativeButtonText(getString(R.string.cancel))
                .setTheme(R.style.MaterialCalendarTheme)
                .setCalendarConstraints(calendarConstraints)
                .setSelection(selectedDate).build()

        picker.addOnPositiveButtonClickListener {
            viewModel.setEndDate(Date(it))
        }
        picker.addOnNegativeButtonClickListener {
            dismiss()
        }
        picker.show(childFragmentManager, "EndsAtDatePicker")
    }

    override fun onHeaderBackClick() {
        dismiss()
    }

    private fun setEndsAtText(endsAt: Date?) {
        binding?.apply {
            endsAt?.let {
                makeSlotRecurringEndsAt.setTitle("Ends at ${DateUtils.parseDateToDayMonthDay(it)}")
            } ?: makeSlotRecurringEndsAt.setTitle("Ends at")
        }
    }

    private fun onSaveClick(recurringRule: String?) {
        setFragmentResult(
            SELECTED_RULE_KEY,
            bundleOf(SELECTED_RULE_VALUE to recurringRule)
        )
        dismiss()
    }

    override fun clearClassVariables() {
        binding = null
    }

    companion object {
        const val SELECTED_RULE_KEY = "selectedRuleKey"
        const val SELECTED_RULE_VALUE = "selectedRuleValue"

        fun show(
            fragment: Fragment,
            recurringRule: String?,
            selectedDate: Long,
            listener: ((String?) -> Unit)
        ) {
            SlotRecurringBottomSheet(recurringRule, selectedDate).show(
                fragment.childFragmentManager,
                SlotRecurringBottomSheet::class.simpleName
            )
            fragment.setRecurringRuleResultListener(listener)
        }
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        TODO("Not yet implemented")
    }
}

private fun Fragment.setRecurringRuleResultListener(listener: ((String?) -> Unit)) {
    childFragmentManager.setFragmentResultListener(
        SlotRecurringBottomSheet.SELECTED_RULE_KEY,
        this
    ) { _, bundle ->
        val result = bundle.getString(SlotRecurringBottomSheet.SELECTED_RULE_VALUE)
        listener.invoke(result)
    }
}
