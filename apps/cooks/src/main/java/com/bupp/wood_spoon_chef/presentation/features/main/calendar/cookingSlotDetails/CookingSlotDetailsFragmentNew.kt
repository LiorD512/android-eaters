package com.bupp.wood_spoon_chef.presentation.features.main.calendar.cookingSlotDetails

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.annotation.Keep
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.databinding.FragmentDetailsCookingSlotNewBinding
import com.bupp.wood_spoon_chef.presentation.custom_views.CreateCookingSlotTopBar
import com.bupp.wood_spoon_chef.presentation.features.base.BaseFragment
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.CookingSlotActivity
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.fragments.CookingSlotMenuAdapter
import com.bupp.wood_spoon_chef.utils.extensions.prepareFormattedDate
import com.eatwoodspoon.android_utils.binding.viewBinding
import com.shared.presentation.dialog.bottomsheet.ActionListBottomSheetFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.koin.androidx.viewmodel.ext.android.viewModel

@Parcelize
@Keep
data class ArgumentModelCookingSlotDetailsNew(
    var cookingSlitsId: Long
) : Parcelable

class CookingSlotDetailsFragmentNew : BaseFragment(R.layout.fragment_details_cooking_slot_new),
    CreateCookingSlotTopBar.CreateCookingSlotTopBarListener {

    private val binding by viewBinding(FragmentDetailsCookingSlotNewBinding::bind)
    private val args: CookingSlotDetailsFragmentNewArgs by navArgs()

    private val viewModel: CookingSlotDetailsViewModelNew by viewModel()
    private var menuAdapter: CookingSlotMenuAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parsNavArguments()
        observeViewModelState()

        binding.apply {
            toolbar.setCookingSlotTopBarListener(this@CookingSlotDetailsFragmentNew)
            initList(this)
        }
    }

    private fun initList(binding: FragmentDetailsCookingSlotNewBinding) {
        binding.rvMenu.let {
            it.layoutManager = LinearLayoutManager(context)
            it.isNestedScrollingEnabled = false
        }
        menuAdapter = CookingSlotMenuAdapter()
        binding.rvMenu.adapter = menuAdapter
    }

    private fun observeViewModelState() {
        viewLifecycleOwner.lifecycleScope.launch() {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        CookingSlotDetailsState.Idle -> {
                            handleProgressBar(false)
                        }
                        is CookingSlotDetailsState.Error -> {
                            handleProgressBar(false)
                            handleErrorEvent(state.error, binding.root)
                        }
                        CookingSlotDetailsState.Loading -> {
                            handleProgressBar(true)
                        }
                        is CookingSlotDetailsState.Success -> {
                            viewModel.selectedCookingSlot = state.cookingSlot

                            handleProgressBar(false)
                            setupToolBar(state.cookingSlot)

                            binding.createCookingSlotNewFragmentLastCallForOrderView.setSubtitle(
                                DateTime(state.cookingSlot.lastCallAt).prepareFormattedDate()
                            )
                            menuAdapter?.submitList(state.categoriesWithMenu)
                        }
                        CookingSlotDetailsState.SlotCanceled -> {
                            onBackClick()
                        }
                    }
                }
            }
        }
    }

    private fun setupToolBar(slot: CookingSlot) {
        binding.toolbar.apply {
            setTitle(DateTime(slot.startsAt).prepareFormattedDate())

            val dateTimeFormat: DateTimeFormatter = DateTimeFormat.forPattern("h:mm a")
            val fromDateTimeFormatted: String =
                dateTimeFormat.print(DateTime(slot.startsAt)).uppercase()
            val toDateTimeFormatted: String =
                dateTimeFormat.print(DateTime(slot.endsAt)).uppercase()

            setSubTitle("$fromDateTimeFormatted - $toDateTimeFormatted")
        }
    }

    private fun parsNavArguments() {
        args.cookingSlot.let { args ->
            viewModel.fetchSlotById(args.cookingSlitsId)
        }
    }

    override fun onBackClick() {
        findNavController().navigateUp()
    }

    override fun onMenuClick() {
        showDialogBottom()
    }

    private fun showDialogBottom() {

        val editAction = ActionListBottomSheetFragment.Action(
            textId = R.string.slot_bottomsheet_action_edit
        )

        val duplicateAction = ActionListBottomSheetFragment.Action(
            textId = R.string.slot_bottomsheet_action_duplicate
        )

        val deleteAction = ActionListBottomSheetFragment.Action(
            textId = R.string.slot_bottomsheet_action_delete
        )

        ActionListBottomSheetFragment.newInstance(
            editAction,
            duplicateAction,
            deleteAction
        ).showWithResultListener(parentFragmentManager, null, this) { action ->
            when (action) {
                editAction -> {
                    doOnEditAction()
                }
                duplicateAction -> {
                    // todo duplicateAction
                }
                deleteAction -> {
                    doOnDeleteAction()
                }
            }
        }
    }

    private fun doOnDeleteAction() {
        viewModel.selectedCookingSlot?.id?.let { id ->
            viewModel.cancelCookingSlot(id)
        }
    }

    private fun doOnEditAction() {
        viewModel.selectedCookingSlot?.let { slot ->
            startActivity(
                CookingSlotActivity().newInstance(
                    requireContext(),
                    selectedDate = slot.startsAt.time,
                    cookingSlotId = slot.id
                )
            )
        }
    }

    override fun clearClassVariables() {}
}