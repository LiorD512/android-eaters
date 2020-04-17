package com.bupp.wood_spoon_eaters.dialogs

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.dialogs.abs.OrderDateChooserAdapter
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.utils.Utils
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.order_date_chooser_dialog.*
import java.util.*
import kotlin.collections.ArrayList



class OrderDateChooserDialog(val currentMenuItem: MenuItem?, val allMenuItems: ArrayList<MenuItem>, val listener: OrderDateChooserDialogListener) : DialogFragment(),
    OrderDateChooserAdapter.OrderDateChooserAdapterListener, TimePickerDialog.OnTimeSetListener {

    private var newSelectedMenuItem: MenuItem? = null
    private lateinit var addressAdapter: OrderDateChooserAdapter

    interface OrderDateChooserDialogListener {
        fun onDateChoose(menuItem: MenuItem, newChosenDate: Date)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.order_date_chooser_dialog, null)
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        orderDateChooserDialogBkg.setOnClickListener {
            dismiss()
        }

        addressAdapter = OrderDateChooserAdapter(context!!, allMenuItems, this)
        orderDateChooserDialogRecycler.layoutManager = LinearLayoutManager(context)
        val dividerItemDecoration = DividerItemDecorator(ContextCompat.getDrawable(context!!, R.drawable.divider))
        orderDateChooserDialogRecycler.addItemDecoration(dividerItemDecoration)
        orderDateChooserDialogRecycler.adapter = addressAdapter

        if(currentMenuItem != null){
            addressAdapter.setSelected(currentMenuItem)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.d("wow", "WelcomeDialog dismiss")
    }

    override fun onOrderDateClick(selected: MenuItem) {
        this.newSelectedMenuItem = selected

        val startDate: Date = selected.cookingSlot.startsAt
        val endDate: Date = selected.cookingSlot.endsAt

        val calStart = Calendar.getInstance()
        calStart.time = startDate

        val calEnd = Calendar.getInstance()
        calEnd.time = endDate

        if(Utils.isSameDay(calStart, calEnd)){
            val now = Date()
            calStart?.let{
                if(now.time > it.time.time){
                    calStart.time = now
                }
            }
            openTimePicker(calStart, calEnd)
//            dismiss()
        }else{
            openDatePicker(calStart, calEnd)
        }

    }

    fun openDatePicker(calStart: Calendar, calEnd: Calendar){
        val c = Calendar.getInstance()
        val year = calStart.get(Calendar.YEAR)
        val month = calStart.get(Calendar.MONTH)
        val day = calStart.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(context!!, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, monthOfYear, dayOfMonth)
            if(Utils.isSameDay(selectedDate, calStart)){
                selectedDate.set(year, monthOfYear, dayOfMonth, 23, 59, 59)
                openTimePicker(calStart, selectedDate)
            }else{
                selectedDate.set(year, monthOfYear, dayOfMonth, 0, 0, 0)
                openTimePicker(selectedDate, calEnd)
            }


        }, year, month, day)

        dpd.datePicker.minDate = calStart.timeInMillis
        dpd.datePicker.maxDate = calEnd.timeInMillis

        dpd.show()
    }

    fun openTimePicker(calStart: Calendar, calEnd: Calendar) {
        val dpd = TimePickerDialog.newInstance(this, calStart.get(Calendar.HOUR_OF_DAY), calStart.get(Calendar.MINUTE), false)

        dpd.show(childFragmentManager, "Datepickerdialog")
        dpd.setMinTime(calStart.get(Calendar.HOUR_OF_DAY), calStart.get(Calendar.MINUTE), 0)
        dpd.setMaxTime(calEnd.get(Calendar.HOUR_OF_DAY), calEnd.get(Calendar.MINUTE), 0)
        dialog!!.show()
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        val newCal = Calendar.getInstance()
        newCal.time = newSelectedMenuItem!!.cookingSlot.startsAt
        newCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
        newCal.set(Calendar.MINUTE, minute)
        var newChosenDate: Date = Date(newCal.timeInMillis)

        listener.onDateChoose(newSelectedMenuItem!!, newChosenDate)
        dismiss()
    }



//    fun addAddress(selected: Address) {
//        addressAdapter.addAddress(selected)
//    }
}