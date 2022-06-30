package com.bupp.wood_spoon_chef.presentation.features.main.orders

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bupp.wood_spoon_chef.R
import com.bupp.wood_spoon_chef.analytics.event.orders.*
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.databinding.FragmentOrdersBinding
import com.bupp.wood_spoon_chef.presentation.features.base.BaseFragment
import com.bupp.wood_spoon_chef.presentation.features.main.MainActivity
import com.bupp.wood_spoon_chef.presentation.features.main.dialogs.CookingSlotChooserDialog
import com.bupp.wood_spoon_chef.presentation.features.main.dialogs.NationwideShipmentInfoDialog
import com.bupp.wood_spoon_chef.presentation.features.main.orders.order_details.OrderDetailsFragment
import com.bupp.wood_spoon_chef.data.remote.model.CookingSlot
import com.bupp.wood_spoon_chef.presentation.features.main.orders.order_details.ArgumentModelOrderDetails
import com.bupp.wood_spoon_chef.utils.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrdersFragment : BaseFragment(R.layout.fragment_orders),
    OrdersAdapter.CalendarCookingSlotAdapterListener,
    OrderDetailsFragment.SingleOrderDialogListener,
    CookingSlotChooserDialog.CookingSlotChooserListener {

    private var binding: FragmentOrdersBinding? = null
    private var cookingSlotAdapter: OrdersAdapter? = null
    private var lastClickedCookingSlot: CookingSlot? = null

    val viewModel by viewModel<OrdersViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.trackAnalyticsEvent(OrdersScreenOpenedEvent())

        binding = FragmentOrdersBinding.bind(view)

        initUi()
        initObservers()
        getData()

    }

    private fun initUi() {
        binding?.apply {
            ordersFragCalendar.setOnClickListener {
                (activity as MainActivity).onCalenderClick()
                viewModel.trackAnalyticsEvent(OrdersEmptyListClickOnCalendarEvent())
            }
        }
    }

    private fun getData() {
        viewModel.getTraceableCookingSlots()
    }

    private fun initObservers() {
        binding?.apply {
            viewModel.getCookingSlotEvent.observe(viewLifecycleOwner) { event ->
                if (event.isSuccess) {
                    event.cookingSlots?.let { handleData(it) }
                } else {
                    showEmptyLayout()
                    Log.d("wowOrdersFrag", "get CookingSlots Fail")
                }
            }

            viewModel.cancelCookingSlotEvent.observe(viewLifecycleOwner) { event ->
                if (event.isSuccess) {
                    binding?.apply {
                        cookingSlotAdapter?.removeCookingSlot(lastClickedCookingSlot)
                        getData()
                    }
                } else {
                    Toast.makeText(context, "Can't cancel Current Cooking Slot", Toast.LENGTH_SHORT)
                        .show()
                }

            }

            viewModel.progressData.observe(viewLifecycleOwner) {
                handleProgressBar(it)
            }
            viewModel.errorEvent.observe(viewLifecycleOwner) {

            }
        }
    }

    private fun handleData(cookingSlots: List<CookingSlot>) {
        if (cookingSlots.isNotEmpty()) {
            showCookingSlotsList(cookingSlots)
            viewModel.trackAnalyticsEvent(OrdersListOpenedEvent())
        } else {
            showEmptyLayout()
            viewModel.trackAnalyticsEvent(OrdersEmptyListOpenedEvent())
        }
    }

    private fun showEmptyLayout() {
        binding?.apply {
            ordersFragEmptyLayout.visibility = View.VISIBLE
            ordersFragList.visibility = View.GONE
            ordersFragEmptyTitle.text = viewModel.getEmptyLayoutTitle()
        }
    }

    private fun showCookingSlotsList(cookingSlots: List<CookingSlot>) {
        binding?.apply {
            ordersFragEmptyLayout.visibility = View.GONE
            ordersFragList.visibility = View.VISIBLE

            ordersFragList.layoutManager = LinearLayoutManager(context)
            cookingSlotAdapter =
                OrdersAdapter(requireContext(), cookingSlots.toMutableList(), this@OrdersFragment)
            ordersFragList.adapter = cookingSlotAdapter
        }
    }

    override fun onCookingSlotClicked(cookingSlot: CookingSlot) {
        viewModel.trackAnalyticsEvent(OrdersClickOnItemEvent())

        findNavController().apply {
            val action = OrdersFragmentDirections.actionOrderFragmentToOrderDetailsFragment(
                ArgumentModelOrderDetails(argCookingSlot = cookingSlot)
            )
            navigate(action)
        }
    }

    override fun onCookingSlotNationwideClick() {
        NationwideShipmentInfoDialog().show(
            childFragmentManager,
            Constants.NATIONWIDE_SHIPPING_INFO_DIALOG
        )
    }

    override fun onSingleOrderDismiss(shouldRefreshOrderList: Boolean) {
        if (shouldRefreshOrderList) {
            viewModel.getTraceableCookingSlots()
        }
    }

    override fun onCookingSlotShareClick(cookingSlot: CookingSlot) {
        val text = viewModel.getShareText(cookingSlot)
        Utils.shareText(requireActivity(), text)
        viewModel.trackAnalyticsEvent(OrdersClickOnItemShareEvent())
    }

    override fun onCookingSlotMenuClick(cookingSlot: CookingSlot) {
        val cookingSlotChooserDialog = CookingSlotChooserDialog(cookingSlot, this)
        cookingSlotChooserDialog.show(childFragmentManager, Constants.COOKING_SLOT_CHOOSER_TAG)
        viewModel.trackAnalyticsEvent(OrdersClickOnItemMenuEvent())
    }

    override fun onCancelCookingSlot(cookingSlot: CookingSlot) {
        this.lastClickedCookingSlot = cookingSlot
        cookingSlot.id?.let { id ->
            viewModel.cancelCookingSlot(id)
        }
        viewModel.trackAnalyticsEvent(OrdersClickOnItemMenuCancelSlotEvent())
    }

    override fun onEditCookingSlot(cookingSlot: CookingSlot) {
        (activity as MainActivity).startEditCookingSlot(cookingSlot)
        viewModel.trackAnalyticsEvent(OrdersClickOnItemMenuEditSlotEvent())
    }

    override fun clearClassVariables() {
        binding = null
        cookingSlotAdapter = null
    }
}
