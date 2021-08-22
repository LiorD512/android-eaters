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
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SingleColumnTimePickerBottomSheet(val listener: TimePickerListener? = null) : BottomSheetDialogFragment() {

//    var listener: TimePickerListener? = null
    interface TimePickerListener{
        fun onTimerPickerChange(){}
        fun onTimerPickerCookingSlotChange(cookingSlot: CookingSlot){}
    }

    private val binding: SingleTimePickerBottomSheetBinding by viewBinding()
    val viewModel by viewModel<TimePickerViewModel>()
    private var isTemporary: Boolean = false

//    private var menuItems: List<MenuItem>? = null
    private var daysFromNow: Int? = null
    private var cookingSlots: List<CookingSlot>? = null
    private var deliveryDates: List<DeliveryDates>? = null
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
            timePickerAsapBtn.setOnClickListener {
                viewModel.setDeliveryTime(Date(), isTemporary)
                listener?.onTimerPickerChange()
                dismiss()
            }
            timePickerScheduleBtn.setOnClickListener {
                viewModel.setDeliveryTime(timePickerTimePicker.getChosenDate(), isTemporary)
                listener?.onTimerPickerChange()
                dismiss()
            }

//            menuItems?.let{
//                timePickerTimePicker.setDatesByMenuItems(it)
//                timePickerAsapBtn.visibility = View.GONE
//            }

            cookingSlots?.let{
                timePickerTimePicker.setDatesByCookingSlots(it)
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
                timePickerTimePicker.setDatesByDeliveryDates(it)
                timePickerAsapBtn.visibility = View.GONE
                timePickerSubtitle.visibility = View.VISIBLE
                timePickerSubtitle.text = DateUtils.parseDateToFullDayDate(it[0].from)
            }

            daysFromNow?.let{
                binding.timePickerTimePicker.initSimpleDatesData(it)
            }



        }
    }

//    fun setMenuItems(menuItems: List<MenuItem>){
//        val menuItemsData = menuItems.filter { it.quantity > 0 }
//        this.isTemporary = true
//        this.menuItems = menuItemsData
//    }

    fun setCookingSlots(selectedCookingSlot: CookingSlot, cookingSlots: MutableList<CookingSlot>) {
        this.isTemporary = true
        this.cookingSlots = cookingSlots
        val curCookingSlot = cookingSlots.find { it.id == selectedCookingSlot.id }
        this.selectedCookingSlot = curCookingSlot
    }

    fun setDeliveryDates(deliveryDates: List<DeliveryDates>){
        this.isTemporary = true
        this.deliveryDates = deliveryDates
    }

    fun setDatesFromNow(daysFromNow: Int = 7){
        this.isTemporary = false
        this.daysFromNow = daysFromNow

    }

}