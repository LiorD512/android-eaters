package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.last_call

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.databinding.FragmentLastCallForOrderBinding
import com.bupp.wood_spoon_chef.presentation.custom_views.HeaderView
import it.sephiroth.android.library.xtooltip.ClosePolicy
import it.sephiroth.android.library.xtooltip.Tooltip
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LastCallForOrdersBottomSheet : TopCorneredBottomSheet(),
    HeaderView.HeaderViewListener{

    private var binding: FragmentLastCallForOrderBinding? = null
    private val viewModel: LastCallBottomSheetViewModel by viewModel()


    companion object {
        const val TIME_KEY = "timeKey"
        const val TIME_VALUE = "timeValue"

        fun show(
            fragment: Fragment,
            listener: ((SelectedHoursAndMinutes) -> Unit)
        ) {
            LastCallForOrdersBottomSheet().show(
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

        binding?.apply {
            btnSave.setOnClickListener {
                lifecycleScope.launch {
                    if (viewModel.shouldShowWarningBottomSheetFlow.first()) {
                        showWarningBottomSheet()
                    }
                }
            }
            toolbarView.setHeaderViewListener(this@LastCallForOrdersBottomSheet)

            viewCallForOrder.apply {
                setAddClickListener {
                    showBottomTimePicker()
                }
                setOnSecondaryIconClickListener {
                    showToolTip(it)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.formattedSelectedHoursAndMinutesFlow.collect { formatSubtitle ->
                binding?.viewCallForOrder?.setSubtitle(formatSubtitle)
            }
        }
    }

    private fun showWarningBottomSheet() {
        WarningLastCallBottomSheet(object : OnWarningLastCallBottomSheetListener {
            override fun onContinueClicked() {
                val selectedValue: SelectedHoursAndMinutes = viewModel.selectedHoursAndMinutes.value

                setFragmentResult(
                    TIME_KEY, bundleOf(
                        TIME_VALUE to SelectedHoursAndMinutes(
                            hours = selectedValue.hours,
                            minutes = selectedValue.minutes
                        )
                    )
                )
                dismiss()
            }

        }).show(
            childFragmentManager,
            WarningLastCallBottomSheet::class.java.simpleName
        )
    }

    private fun showToolTip(anchorView: View) {
        binding?.viewCallForOrder?.apply {
            post {
                Tooltip.Builder(requireContext())
                    .anchor(anchorView, 0, -20, true)
                    .text(getString(R.string.tool_tip_last_call))
                    .arrow(true)
                    .closePolicy(ClosePolicy.TOUCH_INSIDE_NO_CONSUME)
                    .fadeDuration(250)
                    .showDuration(10000)
                    .overlay(false)
                    .maxWidth(this.measuredWidth)
                    .create()
                    .doOnHidden { }
                    .doOnFailure { }
                    .doOnShown { }
                    .show(this, Tooltip.Gravity.TOP, false)
            }
        }
    }

    private fun showBottomTimePicker() {
        LastCallPickerBottomSheet.show(this) {
            viewModel.setSelectedHoursAndMinutes(it)
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
    listener: ((SelectedHoursAndMinutes) -> Unit)
) {
    childFragmentManager.setFragmentResultListener(
        LastCallForOrdersBottomSheet.TIME_KEY,
        this
    ) { _, bundle ->
        val result =
            bundle.getParcelable<SelectedHoursAndMinutes>(LastCallForOrdersBottomSheet.TIME_VALUE)
        result?.let {
            listener.invoke(it)
        }
    }
}