package com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.databinding.FragmentCreateCookingSlotNewBinding
import com.bupp.wood_spoon_chef.presentation.custom_views.CreateCookingSlotTopBar
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs.LastCallForOrdersBottomSheet
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs.OperatingHoursInfoBottomSheet
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.dialogs.TimePickerBottomSheet
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.base.CookingSlotParentFragment
import com.bupp.wood_spoon_chef.utils.extensions.findParent
import com.bupp.wood_spoon_chef.utils.extensions.prepareFormattedDate
import com.eatwoodspoon.android_utils.binding.viewBinding
import org.joda.time.DateTime
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
    }

    private fun initUi() {
        binding.apply {
            createCookingSlotNewFragmentTopBar.setCookingSlotTopBarListener(this@CreateCookingSlotFragmentNew)
            createCookingSlotNewFragmentNextBtn.setOnClickListener { viewModel.openMenuFragment() }
            createCookingSlotNewFragmentOperatingHoursView.setOnSecondaryIconClickListener { openOperatingHoursInfoBottomSheet() }
            createCookingSlotNewFragmentOperatingHoursView.setAddClickListener { openTimePickerBottomSheet() }
            createCookingSlotNewFragmentLastCallForOrderView.setForwardBtnClickListener { openLastCallForOrderBs() }
        }
    }

    private fun getArgs() {
        val selectedDate = requireActivity().intent.getLongExtra(Constants.ARG_SELECTED_DATE, 0)
        showTitle(selectedDate)
    }

    private fun showTitle(selectedDate: Long) {
        binding.createCookingSlotNewFragmentTitle.text =
            DateTime(selectedDate).prepareFormattedDate()
    }

    private fun openOperatingHoursInfoBottomSheet() {
        OperatingHoursInfoBottomSheet().show(
            childFragmentManager,
            OperatingHoursInfoBottomSheet::class.simpleName
        )
    }

    private fun openTimePickerBottomSheet() {
        TimePickerBottomSheet.show(this) { time ->
            viewModel.setStartTime(time)
        }
    }

    override fun onBackClick() {
        requireActivity().finish()
    }

    private fun openLastCallForOrderBs() {
        LastCallForOrdersBottomSheet().show(childFragmentManager, LastCallForOrdersBottomSheet::class.simpleName)
    }
}