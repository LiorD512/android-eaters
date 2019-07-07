package com.bupp.wood_spoon_eaters.features.main.delivery_details

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.DeliveryDetailsView
import com.bupp.wood_spoon_eaters.features.main.MainActivity
import com.bupp.wood_spoon_eaters.utils.Constants
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.delivery_details_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class DeliveryDetailsFragment : Fragment(), DeliveryDetailsView.DeliveryDetailsViewListener {


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
        deliveryDetailsFragTime.setDeliveryDetailsViewListener(this)

        viewModel.lastDeliveryDetails.observe(this, Observer { details -> setDeliveryDetails(details) })
        viewModel.getLastDeliveryDetails()
    }

    private fun setDeliveryDetails(details: DeliveryDetailsViewModel.LastDataEvent) {
        if (details.address != null && details.address.streetLine1.isNotEmpty()) {
            deliveryDetailsFragLocation.updateDeliveryDetails(details.address.streetLine1)
        }
        if (details.time != null && details.time.toString().isNotEmpty()) {
            val time = Utils.parseTime(details.time)
            deliveryDetailsFragTime.updateDeliveryDetails(time)
        }
    }

    override fun onChangeLocationClick() {
        (activity as MainActivity).loadAddressesDialog()
    }

    override fun onChangeTimeClick() {
        val cal = Calendar.getInstance()
        cal.time = Date()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            deliveryDetailsFragTime.updateDeliveryDetails(Utils.parseTime(cal.time))
            this.selectedTime = cal.time

            viewModel.setDeliveryTime(cal.time)

        }
        TimePickerDialog(context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()

    }
}
