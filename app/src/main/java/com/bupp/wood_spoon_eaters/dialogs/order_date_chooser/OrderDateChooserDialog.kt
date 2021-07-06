package com.bupp.wood_spoon_eaters.dialogs.order_date_chooser

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.databinding.OrderDateChooserDialogBinding
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.bupp.wood_spoon_eaters.utils.Utils
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.util.*
import kotlin.collections.ArrayList



class OrderDateChooserDialog(val currentMenuItem: MenuItem?, val allMenuItems: ArrayList<MenuItem>, val listener: OrderDateChooserDialogListener) : DialogFragment(),
    OrderDateChooserAdapter.OrderDateChooserAdapterListener, TimePickerDialog.OnTimeSetListener {

    val binding: OrderDateChooserDialogBinding by viewBinding()
    private var newSelectedMenuItem: MenuItem? = null
    private var addressAdapter: OrderDateChooserAdapter? = null

    interface OrderDateChooserDialogListener {
        fun onDateChoose(menuItem: MenuItem, newChosenDate: Date)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.order_date_chooser_dialog, null)
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        with(binding){
            orderDateChooserDialogBkg.setOnClickListener {
                dismiss()
            }

            addressAdapter = OrderDateChooserAdapter(requireContext(), allMenuItems, this@OrderDateChooserDialog)
            orderDateChooserDialogRecycler.layoutManager = LinearLayoutManager(context)
            val dividerItemDecoration = DividerItemDecorator(ContextCompat.getDrawable(requireContext(), R.drawable.divider))
            orderDateChooserDialogRecycler.addItemDecoration(dividerItemDecoration)
            orderDateChooserDialogRecycler.adapter = addressAdapter

            if(currentMenuItem != null){
                addressAdapter?.setSelected(currentMenuItem)
            }
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.d("wow", "WelcomeDialog dismiss")
    }

    override fun onOrderDateClick(selected: MenuItem) {
        this.newSelectedMenuItem = selected

//        val startDate: Date = selected.cookingSlot.startsAt
        val startDate: Date = selected.cookingSlot?.orderFrom ?: Date()
        val endDate: Date = selected.cookingSlot?.endsAt ?: Date()

        val calStart = Calendar.getInstance()
        calStart.time = startDate

        val calEnd = Calendar.getInstance()
        calEnd.time = endDate

        if(DateUtils.isSameDay(calStart, calEnd)){
            val now = Date()
            calStart?.let{
                if(now.time > it.time.time){
                    calStart.time = now
                }
            }
            if(calStart.before(calEnd)){
                openTimePicker(calStart, calEnd)
            }else{
                Toast.makeText(requireContext(), "Invalid Date selected", Toast.LENGTH_SHORT).show()
            }
//            dismiss()
        }else{
            if(calStart.before(calEnd)){
                openTimePicker(calStart, calEnd)
            }else{
                Toast.makeText(requireContext(), "Invalid Date selected", Toast.LENGTH_SHORT).show()
            }
        }

    }

//    fun openDatePicker(calStart: Calendar, calEnd: Calendar){
//        val c = Calendar.getInstance()
//        val year = calStart.get(Calendar.YEAR)
//        val month = calStart.get(Calendar.MONTH)
//        val day = calStart.get(Calendar.DAY_OF_MONTH)
//
//        val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//            val selectedDate = Calendar.getInstance()
//            selectedDate.set(year, monthOfYear, dayOfMonth)
//            if(DateUtils.isSameDay(selectedDate, calStart)){
//                selectedDate.set(year, monthOfYear, dayOfMonth, 23, 59, 59)
//                openTimePicker(calStart, selectedDate)
//            }else{
//                selectedDate.set(year, monthOfYear, dayOfMonth, 0, 0, 0)
//                openTimePicker(selectedDate, calEnd)
//            }
//
//
//        }, year, month, day)
//
//        dpd.datePicker.minDate = calStart.timeInMillis
//        dpd.datePicker.maxDate = calEnd.timeInMillis
//
//        dpd.show()
//    }

    private fun openTimePicker(calStart: Calendar, calEnd: Calendar) {
        val dpd = TimePickerDialog.newInstance(this, calStart.get(Calendar.HOUR_OF_DAY), calStart.get(Calendar.MINUTE), false)

        dpd.show(childFragmentManager, "Datepickerdialog")
        dpd.setMinTime(calStart.get(Calendar.HOUR_OF_DAY), calStart.get(Calendar.MINUTE), 0)
        dpd.setMaxTime(calEnd.get(Calendar.HOUR_OF_DAY), calEnd.get(Calendar.MINUTE), 0)
        dialog!!.show()
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        val newCal = Calendar.getInstance()
//        newCal.time = newSelectedMenuItem!!.cookingSlot.startsAt
        newCal.time = newSelectedMenuItem!!.cookingSlot?.orderFrom ?: Date()
        newCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
        newCal.set(Calendar.MINUTE, minute)
        var newChosenDate: Date = Date(newCal.timeInMillis)

        listener.onDateChoose(newSelectedMenuItem!!, newChosenDate)
        dismiss()
    }

    override fun onDestroyView() {
        addressAdapter = null
        super.onDestroyView()
    }

}