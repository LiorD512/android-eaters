package com.bupp.wood_spoon_chef.presentation.features.main.calendar.create_cooking_slot

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.databinding.FragmentCreateCookingSlotBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseFragment
import com.bupp.wood_spoon_chef.presentation.features.main.MainActivity
import com.bupp.wood_spoon_chef.presentation.features.main.calendar.sub_screen.CookingSlotHelper
import com.bupp.wood_spoon_chef.presentation.features.main.calendar.sub_screen.calendar_menu_adapter.CookingSlotDetailsAdapter
import com.bupp.wood_spoon_chef.utils.AnimationUtil
import com.bupp.wood_spoon_chef.utils.DateUtils
import com.bupp.wood_spoon_chef.utils.extensions.prepareFormattedDate
import com.bupp.wood_spoon_chef.utils.hours
import com.bupp.wood_spoon_chef.utils.minutes
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.parcelize.Parcelize
import org.joda.time.DateTime
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

@Parcelize
@Keep
data class ArgumentModelCreateCookingSlot(
    var selectedDateMillis: Long,
    var editableCookingSlotId: Long? = null,
) : Parcelable

class CreateCookingSlotFragment : BaseFragment(R.layout.fragment_create_cooking_slot) {

    private var binding: FragmentCreateCookingSlotBinding? = null
    private val viewModel: CreateCookingSlotViewModel by viewModel()
    private val args: CreateCookingSlotFragmentArgs by navArgs()
    private var menuAdapter: CookingSlotDetailsAdapter? = null
    private var selectedDate: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_cooking_slot, container, false)
        binding = FragmentCreateCookingSlotBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreateCookingSlotBinding.bind(view)

        parsNavArguments()
        setupList()

        binding?.calendarFragCalBack?.setOnClickListener {
            findNavController().navigateUp()
        }
        binding?.btnSaveSlot?.let {
            it.setOnClickListener { saveSlot() }
        }

        viewModel.getMyDishes()
        observeViewModel()
    }

    private fun setupList() {
        binding?.calendarFragList?.let {
            it.layoutManager = LinearLayoutManager(context)
            it.isNestedScrollingEnabled = false
        }

        menuAdapter = CookingSlotDetailsAdapter(
            object : CookingSlotDetailsAdapter.CalendarAdapterListener {
                override fun onAddDishClick() {
                    (activity as MainActivity).addNewDishToCalendar()
                }

                override fun onTimeDialogClick(cookingSlotHelper: CookingSlotHelper) {
                    if (cookingSlotHelper.lastClickedView == CookingSlotDetailsAdapter.START_TIME) {
                        showOpenTimePicker(cookingSlotHelper)
                    } else {
                        showCloseTimePicker(cookingSlotHelper)
                    }
                }

                override fun onDateTimeDialogClick(cookingSlotHelper: CookingSlotHelper) {
                    openDatePicker(cookingSlotHelper)
                }

            })

        binding?.calendarFragList?.adapter = menuAdapter
    }

    private fun observeViewModel() {
        with(viewModel) {
            getDishesLiveData.observe(viewLifecycleOwner) { dishes ->
                menuAdapter?.submitList(dishes)
            }
            savedCookingSlotLiveData.observe(viewLifecycleOwner) {
                findNavController().popBackStack()
            }
            editingCookingSlotLiveData.observe(viewLifecycleOwner) {
                menuAdapter?.enableEditMode(it)
            }
            errorEvent.observe(viewLifecycleOwner) {
                handleErrorEvent(it, binding?.root)
            }
            progressData.observe(viewLifecycleOwner) {
                handleProgressBar(it)
                binding?.btnSaveSlot?.isVisible = it.not()
            }
        }
    }

    private fun parsNavArguments() {
        args.createCookingSlotFragmentArgument.let { argsSelectedDate ->
            showCalendarTitle(argsSelectedDate)
            fetchCookingSlotById(argsSelectedDate)
        }
    }

    private fun fetchCookingSlotById(argsSelectedDate: ArgumentModelCreateCookingSlot) {
        argsSelectedDate.editableCookingSlotId?.let {
            viewModel.fetchCookingSlotById(it)
        }
    }

    private fun showCalendarTitle(argsSelectedDate: ArgumentModelCreateCookingSlot) {
        selectedDate = argsSelectedDate.selectedDateMillis
        binding?.calendarFragCalTitle?.text = DateTime(selectedDate).prepareFormattedDate()
    }

    private fun showOpenTimePicker(cookingSlotHelper: CookingSlotHelper) {
        val cal = Calendar.getInstance()
        selectedDate?.let {
            cal.time = Date(it)
        }
        val picker: TimePickerDialog = TimePickerDialog.newInstance(
            { _, hour, minute, _ ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                menuAdapter?.updateTime(cal.time)
            },
            cal[Calendar.HOUR_OF_DAY],  // Initial year selection
            cal[Calendar.MINUTE],  // Initial month selection
            false
        )
        try {
            val endTime = cookingSlotHelper.finishTime
            endTime?.let { time ->
                picker.setMaxTime(time.hours() - 2, time.minutes(), 0)
            }
        } catch (ex: Exception) {
            Timber.e(ex)
        }
        picker.show(parentFragmentManager, Constants.DATE_PICKER_DIALOG)
    }

    private fun showCloseTimePicker(cookingSlotHelper: CookingSlotHelper) {
        val cal = Calendar.getInstance()
        selectedDate?.let {
            cal.time = Date(it)
        }
        val picker: TimePickerDialog = TimePickerDialog.newInstance(
            { _, hour, minute, _ ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                menuAdapter?.updateTime(cal.time)
            },
            cal[Calendar.HOUR_OF_DAY],  // Initial year selection
            cal[Calendar.MINUTE],  // Initial month selection
            false
        )
        try {
            val startTime = cookingSlotHelper.startTime
            startTime?.let { time ->
                picker.setMinTime(time.hours() + 2, time.minutes(), 0)
            }
        } catch (ex: Exception) {
            Timber.e(ex)
        }
        picker.show(parentFragmentManager, Constants.DATE_PICKER_DIALOG)
    }

    private fun openDatePicker(cookingSlotHelper: CookingSlotHelper) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            requireContext(), { _, _year, monthOfYear, dayOfMonth ->
                showLastCallTimePicker(_year, monthOfYear, dayOfMonth, cookingSlotHelper)
            },
            year,
            month,
            day
        )
        try {
            selectedDate?.let {
                dpd.datePicker.maxDate = Date(it).time
            }
            dpd.datePicker.minDate = Date().time
        } catch (ex: Exception) {
            Timber.e(ex)
        }
        dpd.show()
    }

    private fun showLastCallTimePicker(
        year: Int,
        monthOfYear: Int,
        dayOfMonth: Int,
        cookingSlotHelper: CookingSlotHelper
    ) {
        val cal = Calendar.getInstance()
        selectedDate?.let {
            cal.time = Date(it)
        }
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthOfYear)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val picker: TimePickerDialog = TimePickerDialog.newInstance(
            { _, hour, minute, _ ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                menuAdapter?.updateTime(cal.time)
            },
            cal[Calendar.HOUR_OF_DAY],  // Initial year selection
            cal[Calendar.MINUTE],  // Initial month selection
            false
        )
        try {
            val now = Calendar.getInstance().time
            selectedDate?.let {
                if (DateUtils.isSameDay(now, Date(it))) {
                    now.let { time ->
                        picker.setMinTime(time.hours(), roundUp(time.minutes()), 0)
                    }
                }

                if (DateUtils.isSameDay(cal.time, Date(it))) {
                    val endTime = cookingSlotHelper.finishTime
                    endTime?.let {
                        if (now.hours() < endTime.hours() + 1) {
                            picker.setMaxTime(endTime.hours(), endTime.minutes(), 0)
                        }
                    }
                }


            }
        } catch (ex: Exception) {
            Timber.e(ex)
        }
        picker.show(parentFragmentManager, Constants.DATE_PICKER_DIALOG)
    }

    private fun saveSlot() {
        if (menuAdapter?.allDataValid() == true) {
            var lastCallTime: Long? = null
            var startTime: Long? = null
            var endTime: Long? = null
            menuAdapter?.getStartTime()?.let {
                startTime = it.time / 1000
            }
            menuAdapter?.getFinishTime()?.let {
                endTime = it.time / 1000
            }
            menuAdapter?.getLastCallTime()?.let {
                lastCallTime = it.time / 1000
            }
            val menuItems = menuAdapter?.getMenuItems()
            val freeDelivery = menuAdapter?.getFreeDelivery() ?: false
            val isWorldwide = menuAdapter?.getWorldWide() ?: false
            handleProgressBar(true)
            menuItems?.let {
                viewModel.saveOrUpdateCookingSlot(
                    startTime,
                    endTime,
                    lastCallTime,
                    menuItems,
                    freeDelivery,
                    isWorldwide
                )
            }
        } else {
            binding?.apply {
                AnimationUtil().shakeView(btnSaveSlot, requireContext())
            }
        }
    }

    private fun roundUp(minutes: Int): Int {
        if (minutes in 1..14) {
            return 15
        }
        if (minutes in 16..29) {
            return 30
        }
        if (minutes in 31..44) {
            return 45
        }
        if (minutes in 46..59) {
            return 60
        }
        return minutes
    }

    override fun clearClassVariables() {
        binding = null
    }

}