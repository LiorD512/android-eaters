package com.bupp.wood_spoon_eaters.features.upsale

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.databinding.FragmentUpSaleBinding
import com.bupp.wood_spoon_eaters.features.free_delivery.FreeDeliveryProgressView
import com.bupp.wood_spoon_eaters.features.free_delivery.FreeDeliveryState
import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.*
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.eatwoodspoon.android_utils.binding.viewBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class UpSaleFragment : Fragment(), UpSaleAdapter.UpSaleAdapterListener, FreeDeliveryProgressView.FreeDeliveryProgressViewListener {

    private val binding by viewBinding(FragmentUpSaleBinding::bind)
    private val viewModel by viewModel<UpSaleViewModel>()
    private lateinit var adapter: UpSaleAdapter

    private lateinit var listener: UpSaleListener

    interface UpSaleListener {
        fun onBackButtonClick()
        fun onUpSaleButtonClick()
        fun onCartItemClick(menuItem: MenuItem)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_up_sale, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        observeViewModelState()
        initLiveDataObservers()

        viewModel.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_UPSALE)

    }

    private fun initLiveDataObservers() {
        viewModel.freeDeliveryData.observe(viewLifecycleOwner) {
            setFreeDeliveryViewState(it)
        }
    }

    private fun observeViewModelState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        updateInputsWithState(state)
                    }
                }
            }
        }
    }


    private fun updateInputsWithState(state: UpSaleState) {
        binding.apply {
            updateUpSaleList(state.upSaleItems)
            updateButtonText(state.buttonTitle)
        }
    }

    private fun updateUpSaleList(data: List<UpsaleAdapterItem>?) {
        adapter.submitList(data)
    }

    private fun setFreeDeliveryViewState(freeDeliveryState: FreeDeliveryState?) {
        binding.apply {
            upSaleFragmentFreeDeliveryView.setFreeDeliveryState(freeDeliveryState)
        }
    }

    private fun initUi() {
        initAdapter()
        binding.apply {
            upSaleFragmentBackBtn.setOnClickListener {
                listener.onBackButtonClick()
            }
            upSaleFragmentBtn.setOnClickListener {
                listener.onUpSaleButtonClick()
                viewModel.logUpsaleButtonClickedEvents()
            }
        }
    }

    private fun initAdapter() {
        binding.apply {
            val divider: Drawable? =
                ContextCompat.getDrawable(requireContext(), R.drawable.line_divider)
            upSaleFragmentList.addItemDecoration(DividerItemDecorator(divider))
            adapter = UpSaleAdapter(this@UpSaleFragment)
            upSaleFragmentList.initSwipeableRecycler(adapter)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is UpSaleListener) {
            listener = context
        } else if (parentFragment is UpSaleListener) {
            this.listener = parentFragment as UpSaleListener
        } else {
            throw ClassCastException("$context must implement UpSaleListener")
        }
    }

    override fun onDishSwipedAdd(item: UpsaleAdapterItem) {
        item.menuItem?.dishId?.let {
            viewModel.addDishToCart(1, it)
            viewModel.logSwipeAddDishEvent(item.menuItem)
        }
    }

    override fun onDishSwipedRemove(item: UpsaleAdapterItem) {
        item.menuItem?.dishId?.let {
            viewModel.removeOrderItemsByDishId(it)
            viewModel.logSwipeRemoveDishEvent(item.menuItem)
        }
    }

    override fun onCartItemClicked(item: MenuItem) {
        listener.onCartItemClick(item)
    }

    private fun updateButtonText(title: String){
        binding.apply {
            upSaleFragmentBtn.setTitle(title)
        }
    }

    override fun thresholdAchieved() {
        viewModel.reportThresholdAchievedEvent()
    }

    override fun viewClicked() {
        viewModel.reportViewClickedEvent()
    }

}