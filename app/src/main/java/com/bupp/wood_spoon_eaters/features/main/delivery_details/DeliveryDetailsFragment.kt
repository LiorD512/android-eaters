package com.bupp.wood_spoon_eaters.features.main.delivery_details

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.DeliveryDetailsView
import com.bupp.wood_spoon_eaters.custom_views.TimeDeliveryDetailsView
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.delivery_details_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class DeliveryDetailsFragment : Fragment(), DeliveryDetailsView.DeliveryDetailsViewListener,
    TimeDeliveryDetailsView.TimeDeliveryDetailsViewListener {

    companion object {
        fun newInstance() = DeliveryDetailsFragment()
    }

    private lateinit var selectedTime: Date
    val viewModel by viewModel<DeliveryDetailsViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.delivery_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        deliveryDetailsFragLocation.setDeliveryDetailsViewListener(this)
        deliveryDetailsFragTime.setTimeDeliveryDetailsViewListener(this)

//        viewModel.initLocationListener()

        viewModel.lastDeliveryDetails.observe(this, Observer { details -> setDeliveryDetails(details) })
        viewModel.getLastDeliveryDetails()
    }

    private fun setDeliveryDetails(details: DeliveryDetailsViewModel.LastDataEvent) {
        if (details.address != null && details.address.id != null) {
            deliveryDetailsFragLocation.updateDeliveryDetails(details.address.streetLine1)
        }
        if (details.time != null && details.time.toString().isNotEmpty()) {
            val time = Utils.parseDateToDayDateHour(details.time)
            deliveryDetailsFragTime.updateDeliveryDetails(time)
        }
    }

    override fun onChangeLocationClick() {
        (activity as MainActivity).loadAddressesDialog()
    }

    override fun onChangeTimeClick() {
        openDatePicker()
    }

    fun openDatePicker(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            openTimePicker(year, monthOfYear, dayOfMonth)
        }, year, month, day)
        dpd.show()
    }

    fun openTimePicker(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val cal = Calendar.getInstance()
        cal.set(year, monthOfYear, dayOfMonth)
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            deliveryDetailsFragTime.updateDeliveryDetails(Utils.parseDateToDayDateHour(cal.time))
            this.selectedTime = cal.time

            viewModel.setDeliveryTime(cal.time)

        }
        TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
    }

    override fun onChangeTimeAsap() {
        viewModel.setDeliveryTimeAsap()
    }

    fun onAddressChooserSelected() {
        viewModel.getLastDeliveryDetails()
    }
}
