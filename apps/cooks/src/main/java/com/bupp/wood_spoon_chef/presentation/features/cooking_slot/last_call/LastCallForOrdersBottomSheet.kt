package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.last_call

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.databinding.FragmentLastCallForOrderBinding
import com.bupp.wood_spoon_chef.presentation.custom_views.HeaderView
import com.shared.presentation.dialog.bottomsheet.ActionListBottomSheetFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LastCallForOrdersBottomSheet : TopCorneredBottomSheet(),
    HeaderView.HeaderViewListener {

    private var binding: FragmentLastCallForOrderBinding? = null
    private val viewModel: LastCallBottomSheetViewModel by viewModel()


    companion object {
        const val RESULT_KEY = "LastCallForOrdersBottomSheet_Result"
        const val RESULT_ARG_LAST_CALL = "LastCallForOrdersBottomSheet_LastCall"
        const val ARG_INITIAL_VALUE = "LastCallForOrdersBottomSheet_InitialValue"

        fun show(
            fragment: Fragment,
            initialsValue: LastCall?,
            listener: ((LastCall) -> Unit)
        ) {
            LastCallForOrdersBottomSheet().apply {
                arguments = bundleOf(ARG_INITIAL_VALUE to initialsValue)
            }.show(
                fragment.childFragmentManager,
                LastCallForOrdersBottomSheet::class.simpleName
            )
            fragment.setStartTimeResultListener(listener)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_last_call_for_order, container, false)
        binding = FragmentLastCallForOrderBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setDialogAdjustPan()
        setFullScreenDialog()
        setupUI()
        observeState()
        observeEvents()
        initViewModel()
    }

    private fun initViewModel() {
        arguments?.getParcelable<LastCall>(ARG_INITIAL_VALUE)?.let {
            viewModel.setInitialValue(it)
        }
    }

    private fun setupUI() {
        binding?.apply {
            btnSave.setOnClickListener {
                viewModel.onSaveClicked()
            }
            lastCallForOrderDay.setOnClickListener {
                viewModel.onSelectDayClicked()
            }
            lastCallForOrderTime.setOnClickListener {
                viewModel.onSelectTimeClicked()
            }
            toolbarView.setHeaderViewListener(this@LastCallForOrdersBottomSheet)
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding?.apply {
                        lastCallForOrderDay.setTitle(formatTextForDaysBefore(state.lastCall.daysBefore))
                        lastCallForOrderTimeSection.isVisible = state.timePickerVisible
                        lastCallForOrderTime.setTitle(formatTextForTimeBefore(state.lastCall.time) ?: "Choose time")
                    }
                }
            }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is LastCallEvents.SetResult -> setResultAndDismiss(event.result)
                        LastCallEvents.ShowDayPicker -> showDayPicker()
                        is LastCallEvents.ShowTimePicker -> showBottomTimePicker(event.time)
                        LastCallEvents.ShowWarningDialog -> showWarningBottomSheet()
                    }
                }
            }
        }
    }


    private fun formatTextForDaysBefore(daysBefore: Int?): String? {
        return LastCallForOrderFormatter.formatTextForDaysBefore(daysBefore)
    }

    private fun formatTextForTimeBefore(hoursAndMinutes: HoursAndMinutes?): String? {
        return LastCallForOrderFormatter.formatTextForTimeBefore(hoursAndMinutes)
    }

    private fun showWarningBottomSheet() {
        WarningLastCallBottomSheet(object : OnWarningLastCallBottomSheetListener {
            override fun onContinueClicked() {
                viewModel.onWarningAccepted()
            }

        }).show(
            childFragmentManager,
            WarningLastCallBottomSheet::class.java.simpleName
        )
    }

    private fun showDayPicker() {
        val actions = listOf(null, 0, 1, 2).map {
            ActionListBottomSheetFragment.Action(
                id = it,
                text = formatTextForDaysBefore(it)
            )
        }
        ActionListBottomSheetFragment.newInstance(
            ActionListBottomSheetFragment.ActionListArguments(
                actions
            )
        ).showWithResultListener(childFragmentManager, lifecycleOwner = this) { action ->
            viewModel.onDaysBeforeSelected(action.id)
        }
    }

    private fun setResultAndDismiss(result: LastCall) {
        setFragmentResult(
            RESULT_KEY, bundleOf(
                RESULT_ARG_LAST_CALL to result
            )
        )
        dismiss()
    }

    private fun showBottomTimePicker(hoursAndMinutes: HoursAndMinutes?) {
        LastCallPickerBottomSheet.show(this, hoursAndMinutes) {
            viewModel.setSelectedTime(it)
        }
    }

    override fun onHeaderBackClick() {
        dismiss()
    }

    override fun clearClassVariables() {
        binding = null
    }
}

private fun Fragment.setStartTimeResultListener(
    listener: ((LastCall) -> Unit)
) {
    childFragmentManager.setFragmentResultListener(
        LastCallForOrdersBottomSheet.RESULT_KEY,
        this
    ) { _, bundle ->
        val result =
            bundle.getParcelable<LastCall>(LastCallForOrdersBottomSheet.RESULT_ARG_LAST_CALL)
        result?.let {
            listener.invoke(it)
        }
    }
}