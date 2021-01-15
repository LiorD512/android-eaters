package com.bupp.wood_spoon_eaters.features.locations_and_address.delivery_details

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
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressViewModel
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.utils.DateUtils
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.delivery_details_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class DeliveryDetailsFragment : Fragment(R.layout.delivery_details_fragment), DeliveryDetailsView.DeliveryDetailsViewListener,
    TimeDeliveryDetailsView.TimeDeliveryDetailsViewListener {

    companion object {
        fun newInstance() = DeliveryDetailsFragment()
    }

    private lateinit var selectedTime: Date
    private val mainViewModel by sharedViewModel<LocationAndAddressViewModel>()
    val viewModel by viewModel<DeliveryDetailsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deliveryDetailsFragLocation.setDeliveryDetailsViewListener(this)
        deliveryDetailsFragTime.setTimeDeliveryDetailsViewListener(this)

        viewModel.lastDeliveryDetails.observe(this, Observer { details -> setDeliveryDetails(details) })
        viewModel.getLastDeliveryDetails()
    }

    private fun setDeliveryDetails(details: DeliveryDetailsViewModel.LastDataEvent) {
        deliveryDetailsFragLocation.updateDeliveryDetails("")
        details.address?.let{
            deliveryDetailsFragLocation.updateDeliveryDetails(details.address.streetLine1)
        }

        details.time?.let{
            val time = DateUtils.parseDateToDayDateHour(it)
            deliveryDetailsFragTime.updateDeliveryDetails(time)
        }
    }

    override fun onChangeLocationClick() {
        mainViewModel.onChangeLocationClick()
    }

    override fun onChangeTimeClick() {
        openDatePicker()
    }

    private fun openDatePicker(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            openTimePicker(year, monthOfYear, dayOfMonth)
        }, year, month, day)

        dpd.datePicker.minDate = Date().time

        dpd.show()
    }

    fun openTimePicker(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val cal = Calendar.getInstance()
        cal.set(year, monthOfYear, dayOfMonth)
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            deliveryDetailsFragTime.updateDeliveryDetails(DateUtils.parseDateToDayDateHour(cal.time))
            this.selectedTime = cal.time

            viewModel.setDeliveryTime(cal.time)

        }
        TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
    }

    override fun onChangeTimeAsap() {
        viewModel.setDeliveryTimeAsap()
    }

    fun onAddressChooserSelected() {
        viewModel.getLastDeliveryDetails()
    }
}
