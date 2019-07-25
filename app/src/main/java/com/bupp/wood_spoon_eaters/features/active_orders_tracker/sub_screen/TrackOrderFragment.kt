package com.bupp.wood_spoon_eaters.features.active_orders_tracker.sub_screen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.features.active_orders_tracker.ActiveOrderTrackerViewModel
import com.bupp.wood_spoon_eaters.model.Order
import com.bupp.wood_spoon_eaters.utils.Constants
import com.bupp.wood_spoon_eaters.utils.Utils
import kotlinx.android.synthetic.main.track_order_dialog.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrackOrderFragment(val curOrder: Order, val listener: TrackOrderDialogListener) : Fragment() {

    val viewModel by viewModel<ActiveOrderTrackerViewModel>()

    private lateinit var progressList: ArrayList<CheckBox>

    interface TrackOrderDialogListener {
        fun onContactUsClick(order: Order)
        fun onMessageClick(order: Order)
        fun onShareImageClick(order: Order)
        fun onCancelOrderClick(order: Order)
        fun onCloseClick()
    }

    companion object {
        fun newInstance(order: Order, listener: TrackOrderDialogListener) = TrackOrderFragment(order, listener)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.track_order_dialog, null)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBtns()
        initUi(curOrder)
        initUpdateObserver()
    }

    private fun initUpdateObserver() {
        viewModel.startSilentUpdate()

        viewModel.getActiveOrders.observe(this, Observer { result ->
            if(result.isSuccess && result.orders?.size!! > 0){
                Log.d("wowActiveOrderTracker","updating orders")
                for(order in result.orders){
                    if(order.id == curOrder.id){
                        initUi(order)
                    }
                }
            }
        })
    }

    private fun initBtns() {
        trackOrderDialogMessageBtn.setOnClickListener {
            listener.onMessageClick(curOrder)
        }

        trackOrderDialogContactUsBtn.setOnClickListener {
            listener.onContactUsClick(curOrder)
        }

        trackOrderDialogCancelBtn.setOnClickListener {
            listener.onCancelOrderClick(curOrder)
//            dismiss()
        }

        trackOrderDialogShareImageBtn.setOnClickListener {
            listener.onShareImageClick(curOrder)
        }

        trackOrderDialogCloseBtn.setOnClickListener {
            listener.onCloseClick()
        }

    }

    private fun initUi(order: Order) {
        Log.d("wowTrackOrderFragment","initUing now")
        progressList = arrayListOf<CheckBox>(trackOrderDialogCb1, trackOrderDialogCb2, trackOrderDialogCb3, trackOrderDialogCb4)

        trackOrderDialogArrivalTime.text = Utils.parseDateToTime(order.estDeliveryTime)
        when(order.preparationStatus){
            "idle", "received", "in_progress" -> {
                trackOrderDialogCb2.isChecked = true
            }
        }

        when(order.deliveryStatus){
            "idle", "awaiting" -> {
                trackOrderDialogCb2.isChecked = true
            }
            "on_the_way" -> {
                trackOrderDialogCb3.isChecked = true
            }
            "shipped" -> {
                trackOrderDialogCb4.isChecked = true
            }
        }


    }

//    private fun handleOrderDetails(details: ActiveOrderTrackerViewModel.OrderDetailsEvent) {
//        setProgress(details.orderProgress)
//        setMessagesIcon(details.isNewMsgs)
//        setArrivalTime(details.arrivalTime)
//    }

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


}