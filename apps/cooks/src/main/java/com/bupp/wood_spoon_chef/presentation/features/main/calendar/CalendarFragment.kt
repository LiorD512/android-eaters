package com.bupp.wood_spoon_chef.presentation.features.main.calendar

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.analytics.event.calendar.CalendarClickOnNeedHelpEvent
import com.bupp.wood_spoon_chef.analytics.event.calendar.CalendarCreateCookingSlotEvent
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.presentation.custom_views.WowCalendarView
import com.bupp.wood_spoon_chef.databinding.FragmentCalendarBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseFragment
import com.bupp.wood_spoon_chef.presentation.features.main.account.sub_screen.support_center.SupportBottomSheet
import com.bupp.wood_spoon_chef.presentation.features.main.calendar.adapters.CookingSlotSlimAdapter
import com.bupp.wood_spoon_chef.presentation.features.main.calendar.cookingSlotDetails.ArgumentModelCookingSlotDetails
import com.bupp.wood_spoon_chef.presentation.features.main.calendar.create_cooking_slot.ArgumentModelCreateCookingSlot
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlotSlim
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.CookingSlotActivity
import com.bupp.wood_spoon_chef.presentation.features.main.calendar.cookingSlotDetails.ArgumentModelCookingSlotDetailsNew
import com.bupp.wood_spoon_chef.utils.extensions.monthOfYearAsShortText
import com.bupp.wood_spoon_chef.utils.extensions.prepareFormattedDate
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class CalendarFragment : BaseFragment(R.layout.fragment_calendar),
    WowCalendarView.CalendarViewListener,
    CookingSlotSlimAdapter.CalendarCookingSlotAdapterListener {

    private var binding: FragmentCalendarBinding? = null
    private var selectedDate: Date? = null
    private var cookingSlotSlimAdapter: CookingSlotSlimAdapter? = null

    val viewModel by viewModel<CalendarViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCalendarBinding.bind(view)
        initObservers()

        initUi()
    }

    override fun onResume() {
        super.onResume()
        getData(DateTime(viewModel.selectedDateFlow.value))
    }

    private fun getData(
        byDate: DateTime = DateTime.now()
    ) {
        viewModel.calendarEventsLaveData.value = hashMapOf()
        viewModel.fetchCookingSlotsPaging(byDate.toDate(), true)
        selectedDate = byDate.toDate()
    }

    private fun initObservers() {
        viewModel.calendarEventsLaveData.observe(viewLifecycleOwner) { events ->
            if (events.isNotEmpty()) {
                events.forEach {
                    binding?.calendarFragCalView?.addEvents(it.value, it.key)
                }
            }
            onDateSelected(selectedDate)
        }

        viewModel.errorEvent.observe(viewLifecycleOwner) {
            handleErrorEvent(it, binding?.root)
        }

        viewModel.progressData.observe(viewLifecycleOwner) {
            handleProgressBar(it)
        }

        lifecycleScope.launch {
            viewModel.selectedDateFlow.collect {
                binding?.calendarFragCalView?.setSelectedDate(Date(it))
            }
        }
    }

    private fun initUi() {
        binding?.apply {
            calendarFragCalView.setCalendarViewListener(this@CalendarFragment)

            initAdapter()

            calendarSupportCenterButton.setOnClickListener {
                viewModel.trackAnalyticsEvent(CalendarClickOnNeedHelpEvent())
                SupportBottomSheet().show(childFragmentManager, Constants.SUPPORT_BOTTOM_SHEET)
            }
        }
    }

    private fun initAdapter() {
        binding?.calendarFragList?.layoutManager = LinearLayoutManager(context)

        lifecycleScope.launch {
            viewModel.isCookingSlotNewFlowEnable.collectLatest { isFFEnabled ->
                cookingSlotSlimAdapter = CookingSlotSlimAdapter(
                    requireContext(), mutableListOf(), this@CalendarFragment, isFFEnabled
                )
            }
        }
    }

    override fun onMonthScroll(firstDayOfMonth: Date?) {
        firstDayOfMonth?.let {
            viewModel.fetchCookingSlotsPaging(firstDayOfMonth)
        }
    }

    override fun onDateSelected(dateClicked: Date?) {
        this.selectedDate = dateClicked

        binding?.apply {
            tvSelectedSlotDate.apply {
                text = DateTime(dateClicked).prepareFormattedDate()
            }

            refreshCookingSlotsAdapter(getCookingSlotsByDate(dateClicked))

            dateClicked?.let {
                viewModel.setSelectedDate(it)
            }
        }
    }

    private fun getCookingSlotsByDate(dateClicked: Date?): List<CookingSlotSlim> =
        viewModel.calendarEventsLaveData.value?.get(
            DateTime(dateClicked).monthOfYearAsShortText()
        )?.filter {
            DateTime(it.startsAt).dayOfMonth().equals(DateTime(dateClicked).dayOfMonth())
        } ?: emptyList()


    private fun refreshCookingSlotsAdapter(cookingSlotsByDate: List<CookingSlotSlim>) {
        cookingSlotSlimAdapter?.updateList(cookingSlotsByDate)
        binding?.calendarFragList?.adapter = cookingSlotSlimAdapter
    }

    override fun onCreateCookingSlotClick() {
        viewModel.trackAnalyticsEvent(CalendarCreateCookingSlotEvent())

        lifecycleScope.launch {
            viewModel.isCookingSlotNewFlowEnable.collectLatest {
                handleCookingSlotNewFlowEnable(it)
            }
        }
    }

    private fun handleCookingSlotNewFlowEnable(isEnabled: Boolean) {
        selectedDate?.let {
            if (isEnabled) {
                openCookingSlotActivity(it.time)
            } else {
                findNavController().apply {
                    val action =
                        CalendarFragmentDirections.actionCalendarFragmentToCreateCookingSlotFragment(
                            ArgumentModelCreateCookingSlot(
                                selectedDateMillis = it.time
                            )
                        )
                    navigate(action)
                }
            }
        }
    }

    private fun openCookingSlotActivity(selectedDate: Long) {
        startActivity(
            CookingSlotActivity().newInstance(
                context = requireContext(),
                selectedDate = selectedDate
            )
        )
    }

    override fun onCookingSlotClick(cookingSlotIds: List<Long>, selectedCookingSlotDate: Long) {
        findNavController().apply {
            val action = CalendarFragmentDirections
                .actionCalendarFragmentToCookingSlotDetailsFragment(
                    ArgumentModelCookingSlotDetails(
                        cookingSlitsIds = cookingSlotIds,
                        cookingSlitsDateMillis = selectedCookingSlotDate
                    )
                )
            navigate(action)
        }
    }

    override fun onCookingSlotClickNew(selectedCookingSlotId: Long) {
        findNavController().apply {
            val action = CalendarFragmentDirections
                .actionCalendarFragmentToCookingSlotDetailsFragmentNew(
                    ArgumentModelCookingSlotDetailsNew(
                        cookingSlitsId = selectedCookingSlotId
                    )
                )
            navigate(action)
        }
    }

    override fun clearClassVariables() {
        binding = null
        selectedDate = null
        cookingSlotSlimAdapter = null
    }
}
