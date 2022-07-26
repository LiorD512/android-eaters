package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.TopCorneredBottomSheet
import com.bupp.wood_spoon_chef.databinding.BottomSheetSlotRecurringBinding
import com.bupp.wood_spoon_chef.presentation.custom_views.HeaderView
import org.koin.androidx.viewmodel.ext.android.viewModel

class SlotRecurringBottomSheet : TopCorneredBottomSheet(), HeaderView.HeaderViewListener {

    private var binding: BottomSheetSlotRecurringBinding? = null
    private val viewModel by viewModel<SlotRecurringViewModel>()

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
    }

    private fun initUi() {
        binding?.apply {
            makeSlotRecurringHeaderView.setHeaderViewListener(this@SlotRecurringBottomSheet)
        }
    }

    override fun onHeaderBackClick() {
        dismiss()
    }

    override fun clearClassVariables() {
        binding = null
    }

    companion object {

        fun show(
            fragment: Fragment
        ) {
            SlotRecurringBottomSheet().show(
                fragment.childFragmentManager,
                SlotRecurringBottomSheet::class.simpleName
            )
        }
    }
}
