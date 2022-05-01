package com.bupp.wood_spoon_chef.presentation.features.main.orders.order_details

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.annotation.Keep
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.common.navigateToCallApp
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.data.remote.model.Order
import com.bupp.wood_spoon_chef.databinding.FragmentOrderDetailsBinding
import com.bupp.wood_spoon_chef.presentation.custom_views.HeaderView
import com.bupp.wood_spoon_chef.presentation.features.base.BaseFragment
import com.bupp.wood_spoon_chef.presentation.features.main.dialogs.NationwideShipmentInfoDialog
import com.bupp.wood_spoon_chef.presentation.features.main.orders.order_details.dialogs.ContactSupportDialog
import com.bupp.wood_spoon_chef.presentation.features.main.orders.order_details.dialogs.ContactSupportDialog.*
import com.bupp.wood_spoon_chef.presentation.features.main.orders.order_details.dialogs.NotAvailableDialog
import com.bupp.wood_spoon_chef.presentation.features.main.orders.order_details.dialogs.cancel_order.CancelOrderDialog
import com.bupp.wood_spoon_chef.utils.DateUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.koin.androidx.viewmodel.ext.android.viewModel

@Parcelize
@Keep
data class ArgumentModelOrderDetails(
    var argCookingSlot: CookingSlot
) : Parcelable

class OrderDetailsFragment : BaseFragment(R.layout.fragment_order_details),
    NotAvailableDialog.NotAvailableDialogListener,
    SingleOrdersAdapter.SingleOrdersAdapterListener,
    CancelOrderDialog.CancelOrderDialogListener,
    HeaderView.HeaderViewListener {

    private var binding: FragmentOrderDetailsBinding? = null
    private var currentCookingSlot: CookingSlot? = null
    private var shouldUpdate: Boolean = false
    private var adapter: SingleOrdersAdapter? = null

    val viewModel by viewModel<OrderDetailsViewModel>()
    val args: OrderDetailsFragmentArgs by navArgs()

    interface SingleOrderDialogListener {
        fun onSingleOrderDismiss(shouldRefreshOrderList: Boolean)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        args.cookingSlot.let {
            currentCookingSlot = it.argCookingSlot
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentOrderDetailsBinding.bind(view)

        initUi()
        initObservers()

        currentCookingSlot?.id?.let {
            viewModel.initData(it)
        }
    }

    private fun initObservers() {
        viewModel.cookingSlotOrders.observe(viewLifecycleOwner) { event ->
            if (event.isSuccess) {
                updateList(event.order!!)
            }
        }
        viewModel.updateCookingSlot.observe(viewLifecycleOwner) { event ->
            if (!event.isSuccess) {
                binding?.apply {
                    singleOrderFragSwitchButton.setOnCheckedChangeListener(null)
                    singleOrderFragSwitchButton.isChecked = !singleOrderFragSwitchButton.isChecked
                    initSwitchListener()
                }
            }
        }
        viewModel.progressData.observe(viewLifecycleOwner) { loading ->
            handleProgressBar(loading)
        }
        viewModel.errorEvent.observe(viewLifecycleOwner) {
            handleErrorEvent(it, binding?.root)
        }
        viewModel.statusUpdateSuccessEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { status ->
                adapter?.setStatusForLastUpdatesItem(status)
            }
        }
        lifecycleScope.launch {
            viewModel.orderCancellationActionFlow.collectLatest {
                when(it) {
                    is OrderCancellationAction.ShowCallSupportDialog -> {
                        showContactSupportDialog(it.tel)
                    }
                    is OrderCancellationAction.ShowCancelOrderDialog -> {
                        showCancelOrderDialog(it.curOrder)
                    }
                    OrderCancellationAction.Default -> { /* */}
                }
            }
        }
    }

    fun initUi() {
        binding?.apply {
            val title =
                DateUtils.parseTwoDates(currentCookingSlot?.startsAt, currentCookingSlot?.endsAt)
            singleOrderFragHeaderView.setType(Constants.HEADER_VIEW_TYPE_TITLE_BACK, title)
            singleOrderFragHeaderView.setHeaderViewListener(this@OrderDetailsFragment)

            currentCookingSlot?.let { currentCookingSlot ->
                if (currentCookingSlot.totalEarnings != null) {
                    singleOrderFragProfit.text = currentCookingSlot.totalEarnings.formattedValue
                    singleOrderFragPotentialProfit.text =
                        currentCookingSlot.potentialEarnings?.formattedValue
                }
            }

            singleOrderFragList.layoutManager = LinearLayoutManager(context)
            adapter = SingleOrdersAdapter(requireContext(), this@OrderDetailsFragment)
            singleOrderFragList.adapter = adapter

            singleOrderFragSwitchButton.isChecked = currentCookingSlot?.isAvailable == true
            initSwitchListener()
        }
    }

    private fun initSwitchListener() {
        binding?.singleOrderFragSwitchButton?.setOnCheckedChangeListener { _, isChecked ->
            shouldUpdate = true
            currentCookingSlot?.id?.let {
                viewModel.updateCookingSlotAvailability(it, isChecked)
            }

            if (!isChecked && viewModel.settings.shouldShowNotAvailableDialog()) {
                val notAvailableDialog = NotAvailableDialog(this)
                notAvailableDialog.show(childFragmentManager, Constants.NOT_AVAILABLE_DIALOG_TAG)
            }
        }
    }

    private fun updateList(orders: List<Order>) {
        adapter!!.updateList(orders.toMutableList())
    }

    override fun onDoNotShowNotAvailable(shouldShow: Boolean) {
        viewModel.onNotAvailableSwitchChanged(shouldShow)
    }

    override fun onChatClick(curOrder: Order) {
    }

    override fun onCancelClick(curOrder: Order) {
        viewModel.onCancelClick(curOrder)
    }

    private fun showCancelOrderDialog(curOrder: Order) {
        val cancelDialog = CancelOrderDialog(curOrder, this)
        cancelDialog.show(childFragmentManager, Constants.CANCEL_ORDER_DIALOG_TAG)
    }

    private fun showContactSupportDialog(tel: String) {
        val listener = object : ContactSupportClickListener {
            override fun onContactSupportClick() {
                activity?.navigateToCallApp(tel)
            }
        }
        ContactSupportDialog(listener)
            .show(childFragmentManager, Constants.CANCEL_ORDER_DIALOG_TAG)
    }

    override fun onWorldwideInfoClick() {
        NationwideShipmentInfoDialog().show(
            childFragmentManager,
            Constants.NATIONWIDE_SHIPPING_INFO_DIALOG
        )
    }

    override fun updateStatus(status: Int, orderId: Long) {
        viewModel.updateStatus(status, orderId)
    }

    override fun onCancelDialogDismiss() {
        viewModel.getCookingSlotOrders()
    }

    override fun onHeaderBackClick() {
        findNavController().popBackStack()
    }

    override fun clearClassVariables() {
        binding = null
    }
}