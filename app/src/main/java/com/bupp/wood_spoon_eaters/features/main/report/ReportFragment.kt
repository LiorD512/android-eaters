package com.bupp.wood_spoon_eaters.features.main.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.InputTitleView
import kotlinx.android.synthetic.main.report_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel


class ReportFragment : Fragment(), InputTitleView.InputTitleViewListener {

    private var enteredReport: Boolean = false

    companion object {
        fun newInstance() = ReportFragment()
    }

    val viewModel by viewModel<ReportViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.report_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        reportFragDeliveryNotes.setInputTitleViewListener(this)
        reportFragDishNotes.setInputTitleViewListener(this)

        reportFragReportBtn.setBtnEnabled(false)

        reportFragWrongDishCb.setOnClickListener {
            reportFragWrongDishCb.isSelected = !reportFragWrongDishCb.isSelected

            validateFields()
        }
        reportFragNoStandardFoodCb.setOnClickListener {
            reportFragNoStandardFoodCb.isSelected = !reportFragNoStandardFoodCb.isSelected

            validateFields()
        }
        reportFragMissingFoodCb.setOnClickListener {
            reportFragMissingFoodCb.isSelected = !reportFragMissingFoodCb.isSelected

            validateFields()
        }


        reportFragArrivedLateCb.setOnClickListener {
            reportFragArrivedLateCb.isSelected = !reportFragArrivedLateCb.isSelected
            if (reportFragNeverDeliveredCb.isSelected) {
                reportFragNeverDeliveredCb.isSelected = false
            }

            validateFields()
        }
        reportFragNeverDeliveredCb.setOnClickListener {
            reportFragNeverDeliveredCb.isSelected = !reportFragNeverDeliveredCb.isSelected
            if (reportFragArrivedLateCb.isSelected) {
                reportFragArrivedLateCb.isSelected = false
            }

            validateFields()
        }

        reportFragReportBtn.setOnClickListener {
            sendReport()
        }

    }

    private fun sendReport() {
        if (isValidReport()) {
            viewModel.postReport(null)
        }else{
            Toast.makeText(context, "Invalid Report form", Toast.LENGTH_LONG).show()
        }
    }

    private fun isValidReport(): Boolean {
        return enteredReport && (reportFragWrongDishCb.isSelected || reportFragNoStandardFoodCb.isSelected
                || reportFragMissingFoodCb.isSelected || reportFragArrivedLateCb.isSelected || reportFragNeverDeliveredCb.isSelected)
    }

    private fun validateFields() {
        if (isValidReport()) {
            reportFragReportBtn.setBtnEnabled(true)
            reportFragReportBtn.alpha = 1f
        } else {
            reportFragReportBtn.setBtnEnabled(false)
            reportFragReportBtn.alpha = 0.3f
        }
    }

    override fun onInputTitleChange(str: String?) {
        this.enteredReport = !str.isNullOrEmpty()
        validateFields()
    }
}
