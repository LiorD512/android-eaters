package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SlotRecurringBottomSheet(
    private val recurringRule: String?
) : TopCorneredBottomSheet(), HeaderView.HeaderViewListener {

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
        viewModel.init(recurringRule)
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
                it.setOnClickListener{ optionView->
                    viewModel.onItemClicked(RecurringFrequencyName.valueOf(optionView.tag.toString()))
                }
            }

            makeSlotRecurringCustom.setOnClickListener {
                viewModel.onItemClicked(RecurringFrequencyName.valueOf(it.tag.toString()))
            }
            makeSlotRecurringSaveBtn.setOnClickListener {
                viewModel.onSaveClick()
            }

        }
    }

    private fun observeViewModelState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        setSelectedFrequencyItem(state.selectedFrequency)
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
                if (tag.equals(selectedFrequency.name.name)){
                    setRightArrowIcon(R.drawable.ic_check_v)
                }else{
                    setRightArrowIcon(R.drawable.ic_arrow_right)
                }
            }
        }
    }

    private fun openCustomRecurringBottomSheet(recurringRule: String?) {
        CustomRecurringBottomSheet.show(this, recurringRule){
            if (!it.isNullOrEmpty()){
                viewModel.setRecurringFrequencyToCustom(it)
            }
        }
    }

    override fun onHeaderBackClick() {
        dismiss()
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
            listener: ((String?) -> Unit)
        ) {
            SlotRecurringBottomSheet(recurringRule).show(
                fragment.childFragmentManager,
                SlotRecurringBottomSheet::class.simpleName
            )
            fragment.setRecurringRuleResultListener(listener)
        }
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
