package com.bupp.wood_spoon_eaters.features.upsale.upsale

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
import com.bupp.wood_spoon_eaters.databinding.FragmentUpSaleBinding
import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.*
import com.bupp.wood_spoon_eaters.model.MenuItem
import com.eatwoodspoon.android_utils.binding.viewBinding
import com.shared.presentation.dialog.adapter.DividerItemDecorator
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class UpSaleFragment : Fragment(), UpSaleAdapter.UpSaleAdapterListener{

    private val binding by viewBinding(FragmentUpSaleBinding::bind)
    private val viewModel by viewModel<UpSaleViewModel>()
    private lateinit var adapter: UpSaleAdapter

    private lateinit var listener: UpSaleListener

    interface UpSaleListener {
        fun onBackButtonClick()
        fun onUpSaleItemClick(menuItem: MenuItem)
        fun isUpSaleItemsSelected(isSelected: Boolean)
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
        initAdapter()
        observeViewModelState()
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
        }
    }

    private fun updateUpSaleList(data: List<UpsaleAdapterItem>?) {
        adapter.submitList(data)
        (parentFragment as UpSaleNCartBottomSheet).refreshButtonPosition()
        (parentFragment as UpSaleNCartBottomSheet).refreshFreeDevPosition()
        (parentFragment as UpSaleNCartBottomSheet).animateExpand()
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
        item.menuItem?.let {
            viewModel.addDishToCart(it)
            listener.isUpSaleItemsSelected(viewModel.isUpSaleItemsSelected())
        }
    }

    override fun onDishSwipedRemove(item: UpsaleAdapterItem) {
        item.menuItem?.let {
            viewModel.removeOrderItemsByDishId(it)
            listener.isUpSaleItemsSelected(viewModel.isUpSaleItemsSelected())
        }
    }

    override fun onUpsaleItemClicked(item: MenuItem) {
        listener.onUpSaleItemClick(item)
    }

}