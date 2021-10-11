package com.bupp.wood_spoon_eaters.features.track_your_order.menu

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.CancelOrderDialogLayoutBinding
import com.bupp.wood_spoon_eaters.databinding.TrackOrderMenuBottomSheetBinding
import com.bupp.wood_spoon_eaters.dialogs.cancel_order.CancelOrderDialog
import com.bupp.wood_spoon_eaters.features.track_your_order.ActiveOrderTrackerViewModel
import com.bupp.wood_spoon_eaters.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TrackOrderMenuBottomSheet : BottomSheetDialogFragment(), CancelOrderDialog.CancelOrderDialogListener {

    interface TrackOrderMenuListener{
        fun onOrderCanceled()
    }

    var listener: TrackOrderMenuListener? = null
    private val binding: TrackOrderMenuBottomSheetBinding by viewBinding()
    val viewModel by sharedViewModel<ActiveOrderTrackerViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.track_order_menu_bottom_sheet, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FloatingBottomSheetStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resources = resources

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val parent = view.parent as View
            parent.setBackgroundResource(R.drawable.floating_bottom_sheet_no_bkg)
        }

        initUi()
        initObservers()
    }

    private fun initObservers() {
        viewModel.cancelOrderEvent.observe(viewLifecycleOwner, {
            val event = it.getContentIfNotHandled()
            event?.let{
                onOrderCanceled(it.orderStage, it.orderId)
            }
        })
        viewModel.hideCancelBtn.observe(viewLifecycleOwner, {
            if(it){
                binding.trackOrderMenuCancel.visibility = View.GONE
                binding.trackOrderMenuSep.visibility = View.GONE
            }
        })
    }

    private fun initUi() {
        with(binding){
            trackOrderMenuCancel.setOnClickListener {
                viewModel.onCancelClick()
            }
            trackOrderMenuClose.setOnClickListener { dismiss() }
            trackOrderMenuContact.setOnClickListener { contactUs() }
        }

        viewModel.checkIfCanCancel()
    }

    private fun contactUs() {
        val phone = viewModel.getContactUsPhoneNumber()
        Utils.callPhone(requireActivity(), phone)
    }


    private fun onOrderCanceled(orderState: Int, orderId: Long?) {
        orderId?.let{
            if(orderState <= 1){
                CancelOrderDialog(Constants.CANCEL_ORDER_STAGE_1, orderId).show(childFragmentManager, Constants.CANCEL_ORDER_DIALOG_TAG)
            }
            if(orderState == 2){
                CancelOrderDialog(Constants.CANCEL_ORDER_STAGE_2, orderId).show(childFragmentManager, Constants.CANCEL_ORDER_DIALOG_TAG)
            }
            if(orderState == 3){
                CancelOrderDialog(Constants.CANCEL_ORDER_STAGE_3, orderId).show(childFragmentManager, Constants.CANCEL_ORDER_DIALOG_TAG)
            }
        }
    }

    override fun onOrderCanceled() {
        dismiss()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TrackOrderMenuListener) {
            listener = context
        } else if (parentFragment is TrackOrderMenuListener) {
            this.listener = parentFragment as TrackOrderMenuListener
        } else {
            throw RuntimeException("$context must implement TrackOrderMenuListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

}