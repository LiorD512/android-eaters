package com.bupp.wood_spoon_eaters.features.order_checkout.gift

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.bottom_sheets.action_list_bottom_sheet.ActionListBottomSheetFragment
import com.bupp.wood_spoon_eaters.databinding.FragmentGiftActionBinding
import com.bupp.wood_spoon_eaters.utils.showErrorToast
import com.eatwoodspoon.android_utils.binding.viewBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class GiftActionsDialogFragment : DialogFragment(R.layout.fragment_gift_action) {

    private val binding by viewBinding(FragmentGiftActionBinding::bind)
    private val viewModel by viewModel<GiftActionsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.transparent
                )
            )
        )
        super.onViewCreated(view, savedInstanceState)

        observeViewModelState()

        observeViewModelEvents()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    private fun observeViewModelState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.map { it.inProgress }.distinctUntilChanged().collect {
                        binding.giftProgress.setInProgress(it)
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
                            is GiftActionsViewModelEvents.Error -> {
                                showErrorToast(
                                    event.message ?: getString(R.string.generic_error_message),
                                    Toast.LENGTH_SHORT
                                )
                                dismiss()
                            }
                            GiftActionsViewModelEvents.NavigateDone -> {
                                dismiss()
                            }
                            GiftActionsViewModelEvents.NavigateEdit -> {
                                findNavController().navigate(R.id.action_giftActionsFragment_to_giftFragment)
                            }
                            GiftActionsViewModelEvents.ShowEditOptions -> {
                                showActionsBottomSheet()
                            }
                        }
                    }
                }
                viewModel.onStarted()
            }
        }
    }

    private fun showActionsBottomSheet() {
        val editAction = ActionListBottomSheetFragment.Action(
            textId = R.string.gift_edit_details_action
        )

        val clearAction = ActionListBottomSheetFragment.Action(
            textId = R.string.gift_delete_details_action
        )

        ActionListBottomSheetFragment.newInstance(editAction, clearAction)
            .showWithResultListener(parentFragmentManager, null, this) { action ->
                when (action) {
                    editAction -> {
                        dismiss()
                        viewModel.onEditSelected()
                    }
                    clearAction -> {
                        viewModel.onClearSelected()
                    }
                    ActionListBottomSheetFragment.defaultCancelAction -> {
                        dismiss()
                    }
                }
            }
    }

    companion object {
        @JvmStatic
        fun newInstance() = GiftActionsDialogFragment()
    }
}