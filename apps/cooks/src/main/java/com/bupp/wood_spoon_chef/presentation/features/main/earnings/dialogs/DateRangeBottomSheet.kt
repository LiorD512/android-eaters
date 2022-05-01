//package com.bupp.wood_spoon_chef.presentation.features.main.earnings.dialogs
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.RadioButton
//import com.bupp.wood_spoon_chef.R
//import com.bupp.wood_spoon_chef.common.FloatingBottomSheet
//import com.bupp.wood_spoon_chef.common.MTLogger
//import com.bupp.wood_spoon_chef.databinding.BottomSheetDateRangeBinding
//import com.bupp.wood_spoon_chef.model.EarningsDateRangeType
//
//class DateRangeBottomSheet(val listener: DateRangeBottomSheetListener) : FloatingBottomSheet() {
//
//    private var binding: BottomSheetDateRangeBinding? = null
//    private var selectedType: EarningsDateRangeType = EarningsDateRangeType.WEEKLY
//
//    interface DateRangeBottomSheetListener {
//        fun onDateRangeSelected(type: EarningsDateRangeType)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.bottom_sheet_date_range, container, false)
//        binding = BottomSheetDateRangeBinding.bind(view)
//        return view
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        arguments?.let { bundle ->
//            val type = bundle.getString(TYPE, null)
//            type?.let { it ->
//                selectedType = EarningsDateRangeType.valueOf(it)
//            }
//        }
//
//        initUi()
//    }
//
//    private fun initUi() {
//        binding?.apply {
//            when (selectedType) {
//                EarningsDateRangeType.WEEKLY ->
//                    dateRangeWeeklyBtn.isChecked = true
//                EarningsDateRangeType.MONTHLY ->
//                    dateRangeMonthlyBtn.isChecked = true
//                EarningsDateRangeType.YEARLY ->
//                    dateRangeYearlyBtn.isChecked = true
//            }
//
//            dateRangeWeeklyBtn.setOnClickListener { v -> onRadioButtonClick(v) }
//            dateRangeMonthlyBtn.setOnClickListener { v -> onRadioButtonClick(v) }
//            dateRangeYearlyBtn.setOnClickListener { v -> onRadioButtonClick(v) }
//            dateRangeSelectBtn.setOnClickListener {
//                listener.onDateRangeSelected(selectedType)
//                dismiss()
//            }
//        }
//    }
//
//    private fun onRadioButtonClick(v: View) {
//        binding?.apply {
//            if (v is RadioButton) {
//                val checked = v.isChecked
//
//                // Check which radio button was clicked
//                when (v.id) {
//                    dateRangeWeeklyBtn.id ->
//                        if (checked) {
//                            MTLogger.d("dateRangeWeeklyBtn")
//                            selectedType = EarningsDateRangeType.WEEKLY
//                        }
//                    dateRangeMonthlyBtn.id ->
//                        if (checked) {
//                            MTLogger.d("dateRangeMonthlyBtn")
//                            selectedType = EarningsDateRangeType.MONTHLY
//                        }
//                    dateRangeYearlyBtn.id -> {
//                        if (checked) {
//                            MTLogger.d("dateRangeYearlyBtn")
//                            selectedType = EarningsDateRangeType.YEARLY
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    override fun clearClassVariables() {
//        binding = null
//    }
//
//    companion object {
//        const val TAG = "DateRangeBottomSheet"
//        const val TYPE = "selected_type"
//
//        fun newInstance(
//            selectedType: EarningsDateRangeType,
//            listener: DateRangeBottomSheetListener
//        ): DateRangeBottomSheet {
//            val fragment = DateRangeBottomSheet(listener)
//            val args = Bundle()
//            args.putString(TYPE, selectedType.name)
//            fragment.arguments = args
//            return fragment
//        }
//
//    }
//
//}