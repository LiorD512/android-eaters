package com.bupp.wood_spoon_eaters.features.upsale

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.common.FlowEventsManager
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.databinding.FragmentCartBinding
import com.bupp.wood_spoon_eaters.features.free_delivery.FreeDeliveryProgressView
import com.bupp.wood_spoon_eaters.features.free_delivery.FreeDeliveryState
import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.*
import com.eatwoodspoon.android_utils.binding.viewBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class CartFragment : Fragment(), FreeDeliveryProgressView.FreeDeliveryProgressViewListener{

    private val binding by viewBinding(FragmentCartBinding::bind)
    private val viewModel by viewModel<CartViewModel>()
    private val parentViewModel by sharedViewModel<UpSaleNCartViewModel>()
    private lateinit var cartAdapter: CartAdapter

    private lateinit var listener: CartListener

    interface CartListener{
        fun onDishCartClicked(customCartItem: CustomOrderItem)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initObservers()
        observeViewModelState()
        viewModel.logPageEvent(FlowEventsManager.FlowEvents.PAGE_VISIT_CART)
    }

    private fun initUI() {
        with(binding) {
            val divider: Drawable? =
                ContextCompat.getDrawable(requireContext(), R.drawable.line_divider)
            cartFragList.addItemDecoration(DividerItemDecorator(divider))
            cartAdapter = CartAdapter(getAdapterListener())
            cartFragList.initSwipeableRecycler(cartAdapter)
            cartFreeDeliveryView.setFreeDeliveryProgressViewListener(this@CartFragment)

            cartBtn.setOnClickListener {
                parentViewModel.onCheckoutClick()
            }
        }
    }

    private fun initObservers() {
        viewModel.currentOrderData.observe(viewLifecycleOwner) {
            viewModel.fetchCartData()
        }
        viewModel.freeDeliveryData.observe(viewLifecycleOwner) {
            setFreeDeliveryViewState(it)
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
        data?.restaurantName?.let {
            binding.cartTitle.text = it
        }
        cartAdapter.submitList(data?.items)
    }


    private fun setFreeDeliveryViewState(freeDeliveryState: FreeDeliveryState?){
        binding.apply {
            cartFreeDeliveryView.setFreeDeliveryState(freeDeliveryState)
        }
    }

    private fun getAdapterListener(): CartAdapter.CartAdapterListener =
        object : CartAdapter.CartAdapterListener {
            override fun onDishSwipedAdd(cartBaseAdapterItem: CartBaseAdapterItem) {
                when (cartBaseAdapterItem) {
                    is CartAdapterItem -> {
                        val dishId = cartBaseAdapterItem.customOrderItem.orderItem.dish.id
                        val note = cartBaseAdapterItem.customOrderItem.orderItem.notes
                        val orderId = cartBaseAdapterItem.customOrderItem.orderItem.id
                        val currentQuantity = cartBaseAdapterItem.customOrderItem.orderItem.quantity
                        viewModel.updateDishInCart(currentQuantity + 1, dishId, note, orderId)
                        viewModel.logSwipeDishInCart(
                            Constants.EVENT_SWIPE_ADD_DISH_IN_CART,
                            cartBaseAdapterItem.customOrderItem
                        )
                    }
                    else -> {
                    }
                }
            }

            override fun onDishSwipedRemove(cartBaseAdapterItem: CartBaseAdapterItem) {
                when (cartBaseAdapterItem) {
                    is CartAdapterItem -> {
                        val orderItemId = cartBaseAdapterItem.customOrderItem.orderItem.id
                        viewModel.removeSingleOrderItemId(orderItemId)
                        viewModel.logSwipeDishInCart(
                            Constants.EVENT_SWIPE_REMOVE_DISH_IN_CART,
                            cartBaseAdapterItem.customOrderItem
                        )
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

    override fun thresholdAchieved() {
        viewModel.reportThresholdAchievedEvent()
    }

    override fun viewClicked() {
        viewModel.reportViewClickedEvent()
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