package com.bupp.wood_spoon_eaters.features.upsale.cart

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.databinding.FragmentCartBinding
import com.bupp.wood_spoon_eaters.features.free_delivery.FreeDeliveryProgressView
import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.*
import com.eatwoodspoon.android_utils.binding.viewBinding
import com.shared.presentation.dialog.adapter.DividerItemDecorator
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class CartFragment : Fragment(R.layout.fragment_cart){

    private val binding by viewBinding(FragmentCartBinding::bind)
    private val viewModel by viewModel<CartViewModel>()
    private lateinit var cartAdapter: CartAdapter

    private lateinit var listener: CartListener

    interface CartListener {
        fun onDishCartClicked(customCartItem: CustomOrderItem)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initObservers()
        observeViewModelState()
    }

    private fun initUI() {
        with(binding) {
            val divider: Drawable? =
                ContextCompat.getDrawable(requireContext(), R.drawable.line_divider)
            cartFragList.addItemDecoration(DividerItemDecorator(divider))
            cartAdapter = CartAdapter(createAdapterListener())
            cartFragList.initSwipeableRecycler(cartAdapter)
        }
    }

    private fun initObservers() {
        viewModel.currentOrderData.observe(viewLifecycleOwner) {
            viewModel.fetchCartData()
        }
    }

    private fun observeViewModelState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        handleCartData(state.cartData)
                    }
                }
            }
        }
    }

    private fun handleCartData(data: CartData?) {
        cartAdapter.submitList(data?.items)
    }


    private fun createAdapterListener(): CartAdapter.CartAdapterListener =
        object : CartAdapter.CartAdapterListener {
            override fun onDishSwipedAdd(cartBaseAdapterItem: CartBaseAdapterItem) {
                when (cartBaseAdapterItem) {
                    is CartAdapterItem -> {
                        viewModel.updateDishInCart(cartBaseAdapterItem.customOrderItem)
                    }
                    else -> {
                    }
                }
            }

            override fun onDishSwipedRemove(cartBaseAdapterItem: CartBaseAdapterItem) {
                when (cartBaseAdapterItem) {
                    is CartAdapterItem -> {
                        viewModel.removeSingleOrderItemId(cartBaseAdapterItem.customOrderItem)
                    }
                    else -> {
                    }
                }
            }

            override fun onCartItemClicked(customCartItem: CustomOrderItem) {
                listener.onDishCartClicked(customCartItem)
                viewModel.logSwipeDishInCart(Constants.EVENT_CLICK_DISH_IN_CART, customCartItem)
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CartListener) {
            listener = context
        } else if (parentFragment is CartListener) {
            this.listener = parentFragment as CartListener
        } else {
            throw ClassCastException("$context must implement CartListener")
        }
    }
}