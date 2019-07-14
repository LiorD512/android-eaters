package com.bupp.wood_spoon.dialogs

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
import com.bupp.wood_spoon_eaters.custom_views.adapters.AddressChooserAdapter
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.dialogs.abs.OrderDateChooserAdapter
import com.bupp.wood_spoon_eaters.model.Address
import com.bupp.wood_spoon_eaters.model.MenuItem
import kotlinx.android.synthetic.main.order_date_chooser_dialog.*

class OrderDateChooserDialog(val currentMenuItem: MenuItem?, val allMenuItems: ArrayList<MenuItem>, val listener: OrderDateChooserDialogListener) : DialogFragment(),
    OrderDateChooserAdapter.OrderDateChooserAdapterListener {


    private lateinit var addressAdapter: OrderDateChooserAdapter

    interface OrderDateChooserDialogListener {
        fun onDateChoose(menuItem: MenuItem)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.order_date_chooser_dialog, null)
        dialog.window.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)))
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

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        Log.d("wow", "WelcomeDialog dismiss")
    }

    override fun onOrderDateClick(selected: MenuItem) {
        listener?.onDateChoose(selected)
        dismiss()
    }



//    fun addAddress(selected: Address) {
//        addressAdapter.addAddress(selected)
//    }
}