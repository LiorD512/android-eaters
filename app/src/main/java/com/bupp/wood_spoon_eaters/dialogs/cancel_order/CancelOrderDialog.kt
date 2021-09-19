package com.bupp.wood_spoon_eaters.dialogs.cancel_order

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.CancelOrderDialogLayoutBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class CancelOrderDialog(val type: Int, val orderId: Long?) : DialogFragment() {

    interface CancelOrderDialogListener{
        fun onOrderCanceled()
    }

    val binding: CancelOrderDialogLayoutBinding by viewBinding()
    var listener: CancelOrderDialogListener? = null
    val viewModel: CancelOrderViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.cancel_order_dialog_layout, null)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_43)))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
    }

    private fun initUi() {
        with(binding) {
            cancelOrderDialogCloseBtn.setOnClickListener { dismiss() }
            cancelOrderDialogKeepBtn.setOnClickListener { dismiss() }
            cancelOrderDialogCancelBtn.setOnClickListener { cancelOrder() }

            when(type){
                Constants.CANCEL_ORDER_STAGE_1 -> {
                    cancelOrderDialogTitle.text = resources.getString(R.string.cancel_dialog_stage_1_title)
                    cancelOrderDialogBody.text = resources.getString(R.string.cancel_dialog_stage_1_body)
                    //                cancelOrderDialogReason.visibility = View.VISIBLE
                }
                Constants.CANCEL_ORDER_STAGE_2 -> {
                    cancelOrderDialogTitle.text = resources.getString(R.string.cancel_dialog_stage_2_title)
                    cancelOrderDialogBody.text = resources.getString(R.string.cancel_dialog_stage_2_body)
                    //                cancelOrderDialogReason.visibility = View.GONE
                }
                Constants.CANCEL_ORDER_STAGE_3 -> {
                    cancelOrderDialogTitle.text = resources.getString(R.string.cancel_dialog_stage_3_title)
                    cancelOrderDialogBody.text = resources.getString(R.string.cancel_dialog_stage_3_body)
                    //                cancelOrderDialogReason.visibility = View.GONE
                }
            }

            viewModel.cancelOrder.observe(viewLifecycleOwner, {cancelOrderEvent ->
                cancelOrderPb.hide()
                if(cancelOrderEvent){
                    listener?.onOrderCanceled()
                    dismiss()
                }else{
                    Toast.makeText(context, "Problem canceling order", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun cancelOrder() {
        binding.cancelOrderPb.show()
        viewModel.cancelOrder(orderId)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CancelOrderDialogListener) {
            listener = context
        } else if (parentFragment is CancelOrderDialogListener) {
            this.listener = parentFragment as CancelOrderDialogListener
        } else {
            throw RuntimeException("$context must implement CancelOrderDialogListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}