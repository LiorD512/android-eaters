package com.bupp.wood_spoon_chef.presentation.features.main.orders.order_details.dialogs.cancel_order

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.databinding.CancelOrderDialogBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseDialogFragment
import com.bupp.wood_spoon_chef.data.remote.model.Order
import org.koin.androidx.viewmodel.ext.android.viewModel

class CancelOrderDialog(
    private val curOrder: Order,
    val listener: CancelOrderDialogListener
) : BaseDialogFragment(R.layout.cancel_order_dialog),
    CancelOrderDialogAdapter.CancelOrderDialogListener {

    var binding: CancelOrderDialogBinding? = null

    private lateinit var adapter: CancelOrderDialogAdapter
    val viewModel by viewModel<CancelOrderViewModel>()

    interface CancelOrderDialogListener {
        fun onCancelDialogDismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.dark_43
                )
            )
        )
        binding = CancelOrderDialogBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        binding!!.cancelOrderDialogCloseBtn.setOnClickListener {
            dismiss()
        }
        binding!!.cancelOrderDialogConfirm.setOnClickListener { cancelOrder() }
        val reasons = viewModel.getCancellationReasons()
        binding!!.cancelOrderDialogList.layoutManager = LinearLayoutManager(context)
        adapter = CancelOrderDialogAdapter(requireContext(), reasons, this)
        binding!!.cancelOrderDialogList.adapter = adapter

        binding!!.cancelOrderDialogCbOther.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                adapter.clearSelectedReason()
                binding!!.cancelOrderDialogCbOtherInput.visibility = View.VISIBLE
            } else {
                binding!!.cancelOrderDialogCbOtherInput.visibility = View.GONE
                binding!!.cancelOrderDialogCbOtherInput.setText("")
            }
        }

    }

    override fun onCheckedChanged() {
        binding!!.cancelOrderDialogCbOther.isChecked = false
    }

    private fun cancelOrder() {
        var note: String? = null
        if (binding!!.cancelOrderDialogCbOtherInput.getText().isNotEmpty()) {
            note = binding!!.cancelOrderDialogCbOtherInput.getText()
        }
        viewModel.cancelOrder(curOrder.id, adapter.getSelectedReasonId(), note)
        viewModel.cancelOrderEvent.observe(this) { event ->
            event.getContentIfNotHandled()?.let { result ->
                if (result.isSuccess) {
                    dismiss()
                }
            }
        }
        viewModel.progressData.observe(this) {
            handleProgressBar(it)
        }
        viewModel.errorEvent.observe(this) {
            handleErrorEvent(it, binding?.root)
        }
    }

    override fun clearClassVariables() {
        listener.onCancelDialogDismiss()
        binding = null
    }

}