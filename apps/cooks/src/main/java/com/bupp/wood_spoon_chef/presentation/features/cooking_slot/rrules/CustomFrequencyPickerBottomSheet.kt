package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.rrules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.databinding.BottomSheetCustomeFreqPickerBinding

class CustomFrequencyPickerBottomSheet: TopCorneredBottomSheet() {

    private var binding: BottomSheetCustomeFreqPickerBinding? = null
    private var frequencyValues = listOf<String>()
    private var interval: Int? = null
    private var frequency: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_custome_freq_picker, container, false)
        binding = BottomSheetCustomeFreqPickerBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        interval = requireArguments().getInt(INTERVAL_ARGS_KEY)
        frequency = requireArguments().getString(FREQUENCY_ARGS_KEY)
        initUi()

    }

    private fun initUi() {
        initPickers()
        binding?.apply {
            customFreqBsSaveBtn.setOnClickListener {
                val frequency = frequencyValues[customFreqBsFrequencyPicker.value]
                val interval = customFreqBsIntervalPicker.value
                setFragmentResult(
                    CUSTOM_FREQUENCY_PICKER_KEY, bundleOf(
                        CUSTOM_FREQUENCY_VALUE to frequency, CUSTOM_INTERVAL_VALUE to interval
                    )
                )
                dismiss()
            }
        }
    }


    private fun initPickers() {
        binding?.apply {
            customFreqBsIntervalPicker.minValue = 1
            customFreqBsIntervalPicker.maxValue = 6
            interval?.let { currentInterval ->
                customFreqBsIntervalPicker.value = currentInterval
            }
            customFreqBsFrequencyPicker.setOnValueChangedListener { _, _, newVal ->
                when (newVal) {
                    0 -> {
                        customFreqBsIntervalPicker.minValue = 1
                        customFreqBsIntervalPicker.maxValue = 6
                    }

                    1 -> {
                        customFreqBsIntervalPicker.minValue = 1
                        customFreqBsIntervalPicker.maxValue = 12
                    }
                }
            }

            setFrequencyList(interval)

                customFreqBsIntervalPicker.setOnValueChangedListener { _, _, newVal ->
                    setFrequencyList(newVal)
                    initFrequencyPicker()
                }
        }

        initFrequencyPicker()
    }

    private fun setFrequencyList(interval: Int?) {
        interval?.let { currentInterval ->
            frequencyValues = if (currentInterval == 1) {
                listOf(
                    resources.getQuantityString(R.plurals.day_plurals, 1),
                    resources.getQuantityString(R.plurals.week_plurals, 1)
                )
            } else {
                listOf(
                    resources.getQuantityString(R.plurals.day_plurals, currentInterval),
                    resources.getQuantityString(R.plurals.week_plurals, currentInterval)
                )
            }
        }
    }

    private fun initFrequencyPicker() {
        binding?.apply {
            customFreqBsFrequencyPicker.minValue = 0
            customFreqBsFrequencyPicker.maxValue = frequencyValues.size - 1
            customFreqBsFrequencyPicker.wrapSelectorWheel = true
            customFreqBsFrequencyPicker.displayedValues = frequencyValues.toTypedArray()
            frequency?.let { currentFrequency ->
                val index = frequencyValues.indices.find {
                    frequencyValues[it].contains(
                        currentFrequency.dropLast(currentFrequency.length.minus(2)), true
                    )
                } ?: 0

                customFreqBsFrequencyPicker.value = index
            }
        }
    }

    override fun clearClassVariables() {
        binding = null
    }

    companion object {
        const val CUSTOM_FREQUENCY_PICKER_KEY = "customPickerFreqKey"
        const val CUSTOM_FREQUENCY_VALUE = "customFrequencyValue"
        const val CUSTOM_INTERVAL_VALUE = "customIntervalValue"
        const val INTERVAL_ARGS_KEY = "intervalArgsKey"
        const val FREQUENCY_ARGS_KEY = "frequencyArgsKey"

        fun show(
            fragment: Fragment,
            interval: Int?,
            frequency: String?,
            listener: ((String, Int) -> Unit)
        ) {
            CustomFrequencyPickerBottomSheet().apply {
                arguments = bundleOf(INTERVAL_ARGS_KEY to interval, FREQUENCY_ARGS_KEY to frequency)
            }.show(
                fragment.childFragmentManager,
                CustomFrequencyPickerBottomSheet::class.simpleName
            )
            fragment.setCustomFrequencyPickerResultListener(listener)
        }
    }
}

private fun Fragment.setCustomFrequencyPickerResultListener(listener: ((String, Int) -> Unit)) {
    childFragmentManager.setFragmentResultListener(
        CustomFrequencyPickerBottomSheet.CUSTOM_FREQUENCY_PICKER_KEY,
        this
    ) { _, bundle ->
        val frequency =
            bundle.getString(CustomFrequencyPickerBottomSheet.CUSTOM_FREQUENCY_VALUE) ?: ""
        val interval = bundle.getInt(CustomFrequencyPickerBottomSheet.CUSTOM_INTERVAL_VALUE)
        listener.invoke(frequency, interval)
    }
}