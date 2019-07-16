package com.bupp.wood_spoon_eaters.dialogs

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.utils.Constants
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.track_order_dialog.*
import org.koin.android.viewmodel.ext.android.viewModel

class TrackOrderDialog(val listener: TrackOrderDialogListener) : DialogFragment() {

    val viewModel by viewModel<TrackOrderViewModel>()

    private lateinit var progressList: ArrayList<CheckBox>

    interface TrackOrderDialogListener {
        fun onContactUsClick()
        fun onMessageClick()
        fun onShareImageClick()
        fun onCancelOrderClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.track_order_dialog, null)
        dialog.window.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context!!, R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        progressList = arrayListOf<CheckBox>(trackOrderDialogCb1, trackOrderDialogCb2, trackOrderDialogCb3, trackOrderDialogCb4)

        trackOrderDialogCloseBtn.setOnClickListener { dismiss() }
        trackOrderDialogMessageBtn.setOnClickListener {
            listener.onMessageClick()
            dismiss()
        }

        trackOrderDialogContactUsBtn.setOnClickListener {
            listener.onContactUsClick()
            dismiss()
        }

        trackOrderDialogCancelBtn.setOnClickListener {
            listener.onCancelOrderClick()
//            dismiss()
        }

        trackOrderDialogShareImageBtn.setOnClickListener {
            listener.onShareImageClick()
            dismiss()
        }

        viewModel.orderDetails.observe(this, Observer { details ->
            if (details != null) {
                handleOrderDetails(details)
            }
        })

        viewModel.getDumbOrderDetails()
    }

    private fun handleOrderDetails(details: TrackOrderViewModel.OrderDetailsEvent) {
        setProgress(details.orderProgress)
        setMessagesIcon(details.isNewMsgs)
        setArrivalTime(details.arrivalTime)
    }

    private fun setArrivalTime(arrivalTime: String){
        trackOrderDialogArrivalTime.text = arrivalTime
    }

    private fun setMessagesIcon(isNewMessages: Boolean) {
        trackOrderDialogMessageBtn.isSelected = isNewMessages
    }

    private fun setProgress(stepNum: Int) {
        clearProgress()
        setOrderProgress(stepNum)
    }

    private fun clearProgress() {
        for (cb in progressList){
            cb.isSelected = false
            cb.text = Utils.setCustomFontTypeSpan(context!!,cb.text.toString(),0,cb.text.toString().length,R.font.open_sans_reg)
            cb.setTextColor(ContextCompat.getColor(context!!,R.color.dark_50))
        }
    }

    private fun setOrderProgress(stepNum: Int) {
        if (stepNum == Constants.ORDER_PROGRESS_NO_PROGRESS) {
            return
        }

        var cbItem = progressList[stepNum]

        cbItem.isSelected = true
        cbItem.text = Utils.setCustomFontTypeSpan(context!!,cbItem.text.toString(),0,cbItem.text.toString().length,R.font.open_sans_semi_bold)
        cbItem.setTextColor(ContextCompat.getColor(context!!,R.color.dark))

        setOrderProgress(stepNum - 1)
    }

//    private fun setOrderProgress(stepNum: Int){
//        clearProgress()
//        when(stepNum){
//            1 ->{
//                trackOrderDialogCb1.isSelected = true
//                trackOrderDialogCb1.text = Utils.setCustomFontTypeSpan(context!!, trackOrderDialogCb1.text.toString(), 0, trackOrderDialogCb1.text.toString().length, R.font.open_sans_semi_bold)
//            }
//            2 ->{
//                trackOrderDialogCb1.isSelected = true
//                trackOrderDialogCb1.text = Utils.setCustomFontTypeSpan(context!!, trackOrderDialogCb1.text.toString(), 0, trackOrderDialogCb1.text.toString().length, R.font.open_sans_semi_bold)
//
//                trackOrderDialogCb2.isSelected = true
//                trackOrderDialogCb2.text = Utils.setCustomFontTypeSpan(context!!, trackOrderDialogCb2.text.toString(), 0, trackOrderDialogCb2.text.toString().length, R.font.open_sans_semi_bold)
//            }
//            3 ->{
//                trackOrderDialogCb1.isSelected = true
//                trackOrderDialogCb1.text = Utils.setCustomFontTypeSpan(context!!, trackOrderDialogCb1.text.toString(), 0, trackOrderDialogCb1.text.toString().length, R.font.open_sans_semi_bold)
//
//                trackOrderDialogCb2.isSelected = true
//                trackOrderDialogCb2.text = Utils.setCustomFontTypeSpan(context!!, trackOrderDialogCb2.text.toString(), 0, trackOrderDialogCb2.text.toString().length, R.font.open_sans_semi_bold)
//
//                trackOrderDialogCb3.isSelected = true
//                trackOrderDialogCb3.text = Utils.setCustomFontTypeSpan(context!!, trackOrderDialogCb3.text.toString(), 0, trackOrderDialogCb3.text.toString().length, R.font.open_sans_semi_bold)
//
//            }
//            4 ->{
//                trackOrderDialogCb1.isSelected = true
//                trackOrderDialogCb1.text = Utils.setCustomFontTypeSpan(context!!, trackOrderDialogCb1.text.toString(), 0, trackOrderDialogCb1.text.toString().length, R.font.open_sans_semi_bold)
//
//                trackOrderDialogCb2.isSelected = true
//                trackOrderDialogCb2.text = Utils.setCustomFontTypeSpan(context!!, trackOrderDialogCb2.text.toString(), 0, trackOrderDialogCb2.text.toString().length, R.font.open_sans_semi_bold)
//
//                trackOrderDialogCb3.isSelected = true
//                trackOrderDialogCb3.text = Utils.setCustomFontTypeSpan(context!!, trackOrderDialogCb3.text.toString(), 0, trackOrderDialogCb3.text.toString().length, R.font.open_sans_semi_bold)
//
//                trackOrderDialogCb4.isSelected = true
//                trackOrderDialogCb4.text = Utils.setCustomFontTypeSpan(context!!, trackOrderDialogCb4.text.toString(), 0, trackOrderDialogCb4.text.toString().length, R.font.open_sans_semi_bold)
//
//            }
//        }

}