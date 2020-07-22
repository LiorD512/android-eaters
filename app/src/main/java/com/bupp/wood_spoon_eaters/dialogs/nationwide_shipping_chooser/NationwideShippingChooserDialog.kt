package com.bupp.wood_spoon_eaters.dialogs.order_date_chooser

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
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.bupp.wood_spoon_eaters.model.ShippingMethod
import com.bupp.wood_spoon_eaters.utils.Utils
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.nationwide_shipping_chooser_dialog.*
import kotlinx.android.synthetic.main.order_date_chooser_dialog.*
import java.util.*
import kotlin.collections.ArrayList



class NationwideShippingChooserDialog(val currentMenuItem: MenuItem?, val allMenuItems: ArrayList<MenuItem>, val listener: OrderDateChooserDialogListener) : DialogFragment(),
    NationwideShippingChooserAdapter.NationwideShippingAdapterListener{

    private var newSelectedMenuItem: ShippingMethod? = null
    private lateinit var addressAdapter: NationwideShippingChooserAdapter

    interface OrderDateChooserDialogListener {
        fun onDateChoose(menuItem: MenuItem, newChosenDate: Date)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.nationwide_shipping_chooser_dialog, null)
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        nationwideShippingChooserDialogBkg.setOnClickListener {
            dismiss()
        }
        nationwideShippingChooserDialogClose.setOnClickListener {
            dismiss()
        }

//        addressAdapter = NationwideShippingChooserAdapter(context!!, allMenuItems, this)
//        nationwideShippingChooserDialogRecycler.layoutManager = LinearLayoutManager(context)
//        val dividerItemDecoration = DividerItemDecorator(ContextCompat.getDrawable(context!!, R.drawable.divider))
//        nationwideShippingChooserDialogRecycler.addItemDecoration(dividerItemDecoration)
//        nationwideShippingChooserDialogRecycler.adapter = addressAdapter

        if(currentMenuItem != null){
//            addressAdapter.setSelected(currentMenuItem)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    override fun onShippingMethodClick(selected: ShippingMethod) {
        this.newSelectedMenuItem = selected

//        val startDate: Date = selected.cookingSlot.startsAt
//        val startDate: Date = selected.cookingSlot.orderFrom
//        val endDate: Date = selected.cookingSlot.endsAt
//
//        val calStart = Calendar.getInstance()
//        calStart.time = startDate
//
//        val calEnd = Calendar.getInstance()
//        calEnd.time = endDate
//
//        if(Utils.isSameDay(calStart, calEnd)){
//            val now = Date()
//            calStart?.let{
//                if(now.time > it.time.time){
//                    calStart.time = now
//                }
//            }
//            openTimePicker(calStart, calEnd)
////            dismiss()
//        }else{
//            openDatePicker(calStart, calEnd)
//        }

    }








//    fun addAddress(selected: Address) {
//        addressAdapter.addAddress(selected)
//    }
}