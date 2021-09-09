package com.bupp.wood_spoon_eaters.bottom_sheets.time_picker

import android.app.Dialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.SingleTimePickerBottomSheetBinding
import com.bupp.wood_spoon_eaters.model.CookingSlot
import com.bupp.wood_spoon_eaters.model.DeliveryDates
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SingleColumnTimePickerBottomSheet(val listener: TimePickerListener? = null) : BottomSheetDialogFragment() {

    data class DeliveryTimeParam(
    val deliveryTimeType: DeliveryType,
    val date: Date? = null
    )
    enum class DeliveryType{
        TODAY,
        FUTURE
    }
    interface TimePickerListener{
        fun onTimerPickerChange(deliveryTimeParam: DeliveryTimeParam?){}
        fun onTimerPickerCookingSlotChange(cookingSlot: CookingSlot){}
    }

    private val binding: SingleTimePickerBottomSheetBinding by viewBinding()
    private val viewModel by viewModel<TimePickerViewModel>()
    private var isTemporary: Boolean = false

    private var daysFromNow: Int? = null
    private var cookingSlots: List<CookingSlot>? = null
    private var deliveryDates: List<DeliveryDates>? = null

    private var selectedDeliveryDate: Date? = null
    private var selectedCookingSlot: CookingSlot? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.single_time_picker_bottom_sheet, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FloatingBottomSheetStyle)
    }


    private lateinit var behavior: BottomSheetBehavior<View>
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val sheet = d.findViewById<View>(R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(sheet!!)
            behavior.isDraggable = false
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resources = resources

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val parent = view.parent as View
            val layoutParams = parent.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.setMargins(
                resources.getDimensionPixelSize(R.dimen.bottom_sheet_horizontal_margin), // LEFT
                0,
                resources.getDimensionPixelSize(R.dimen.bottom_sheet_horizontal_margin), // RIGHT
                0
            )
            parent.layoutParams = layoutParams
            parent.setBackgroundResource(R.drawable.floating_bottom_sheet_bkg)
        }


        initUi()
    }

    private fun initUi() {
        with(binding){

            cookingSlots?.let{
                timePickerTimePicker.setDatesByCookingSlots(it)
                timePickerTimePicker.setSelectedCookingSlot(selectedCookingSlot)
                timePickerAsapBtn.visibility = View.GONE
                timePickerTitle.text = "Change menu"
                timePickerScheduleBtn.setTitle("Select")

                timePickerScheduleBtn.setOnClickListener {
                    val selectedCookingSlot = timePickerTimePicker.getChosenCookingSlot()
                    selectedCookingSlot?.let{
                        listener?.onTimerPickerCookingSlotChange(it)
                    }
                    dismiss()
                }
            }

            deliveryDates?.let{
                timePickerTimePicker.setDatesByDeliveryDates(it, selectedDeliveryDate)
                timePickerAsapBtn.visibility = View.GONE
                timePickerSubtitle.visibility = View.VISIBLE
                timePickerSubtitle.text = DateUtils.parseDateToFullDayDate(it[0].from)

                binding.timePickerScheduleBtn.setOnClickListener {
                    listener?.onTimerPickerChange(DeliveryTimeParam(DeliveryType.FUTURE, timePickerTimePicker.getChosenDate()))
                    dismiss()
                }
            }

            daysFromNow?.let{
                val selectedDate = viewModel.getSelectedData()

                binding.timePickerTimePicker.initSimpleDatesData(it, selectedDate)

                binding.timePickerAsapBtn.setOnClickListener {
                    listener?.onTimerPickerChange(DeliveryTimeParam(DeliveryType.TODAY))
                    dismiss()
                }

                binding.timePickerScheduleBtn.setOnClickListener {
                    listener?.onTimerPickerChange(DeliveryTimeParam(DeliveryType.FUTURE, timePickerTimePicker.getChosenDate()))
                    dismiss()
                }
            }
        }
    }


    fun setCookingSlots(selectedCookingSlot: CookingSlot, cookingSlots: MutableList<CookingSlot>) {
        //restaurant
        this.cookingSlots = cookingSlots
        val curCookingSlot = cookingSlots.find { it.id == selectedCookingSlot.id }
        this.selectedCookingSlot = curCookingSlot
    }

    fun setDeliveryDates(selectedDeliveryDate: Date?, deliveryDates: List<DeliveryDates>){
        //checkout
        this.isTemporary = true
        this.deliveryDates = deliveryDates
        this.selectedDeliveryDate = selectedDeliveryDate
    }

    fun setDatesFromNow(daysFromNow: Int = 7){
        //feed
        this.isTemporary = false
        this.daysFromNow = daysFromNow
    }

}