package com.bupp.wood_spoon_eaters.features.upsale

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.databinding.FragmentCartBinding
import com.bupp.wood_spoon_eaters.di.abs.LiveEvent
import com.bupp.wood_spoon_eaters.features.free_delivery.FreeDeliveryProgressView
import com.bupp.wood_spoon_eaters.features.free_delivery.FreeDeliveryState
import com.bupp.wood_spoon_eaters.features.locations_and_address.LocationAndAddressActivity
import com.bupp.wood_spoon_eaters.features.order_checkout.upsale_and_cart.*
import com.eatwoodspoon.android_utils.binding.viewBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class CartFragment : Fragment(), FreeDeliveryProgressView.FreeDeliveryProgressViewListener {

    private val binding by viewBinding(FragmentCartBinding::bind)
    private val viewModel by viewModel<CartViewModel>()
    private val parentViewModel by sharedViewModel<UpSaleNCartViewModel>()
    private lateinit var cartAdapter: UpSaleNCartAdapter


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
    }

    private fun initUI() {
        with(binding) {
            val divider: Drawable? =
                ContextCompat.getDrawable(requireContext(), R.drawable.line_divider)
            cartFragList.addItemDecoration(DividerItemDecorator(divider))
            cartAdapter = UpSaleNCartAdapter(getAdapterListener())
            cartFragList.initSwipeableRecycler(cartAdapter)


//            upSaleCartBtn.setOnClickListener {
//                viewModel.onCartBtnClick()
//            }
//
//            upsaleCartCloseBtn.setOnClickListener {
//                onCloseBtnClick()
//            }

            cartFreeDeliveryView.setFreeDeliveryProgressViewListener(this@CartFragment)

            cartBtn.setOnClickListener {
                parentViewModel.onCheckoutClick()
            }

//            viewModel.initData()
        }
    }

    private fun initObservers() {
        viewModel.cartLiveData.observe(viewLifecycleOwner) {
            handleCartData(it)
        }
        viewModel.currentOrderData.observe(viewLifecycleOwner) {
            viewModel.initData()
        }
        viewModel.onDishCartClick.observe(viewLifecycleOwner) {
            handleOnCartDishClick(it)
        }
        viewModel.freeDeliveryData.observe(viewLifecycleOwner) {
            setFreeDeliveryViewState(it)
        }
    }

    private fun handleCartData(data: CartViewModel.CartData?) {
        Log.d(UpSaleNCartBottomSheet.TAG, "handleCartData data: $data")
        data?.restaurantName?.let {
            binding.cartTitle.text = it
        }

        cartAdapter.submitList(data?.items)
//            refreshButtonPosition()

    }

    private fun handleOnCartDishClick(cartDishData: LiveEvent<CustomOrderItem>?) {
        cartDishData?.getContentIfNotHandled()?.let {
//            listener?.onCartDishCLick(it)
        }
    }

    private fun setFreeDeliveryViewState(freeDeliveryState: FreeDeliveryState?){
        binding.apply {
            cartFreeDeliveryView.setFreeDeliveryState(freeDeliveryState)
        }
    }

    private fun getAdapterListener(): UpSaleNCartAdapter.UpSaleNCartAdapterListener =
        object : UpSaleNCartAdapter.UpSaleNCartAdapterListener {
            override fun onDishSwipedAdd(item: CartBaseAdapterItem) {
                when (item) {
                    is CartAdapterItem -> {
                        val dishId = item.customOrderItem.orderItem.dish.id
                        val note = item.customOrderItem.orderItem.notes
                        val orderId = item.customOrderItem.orderItem.id
                        val currentQuantity = item.customOrderItem.orderItem.quantity
                        viewModel.updateDishInCart(currentQuantity + 1, dishId, note, orderId)
                        viewModel.logSwipeDishInCart(
                            Constants.EVENT_SWIPE_ADD_DISH_IN_CART,
                            item.customOrderItem
                        )
                    }
                    is UpsaleAdapterItem -> {
                    }
                    else -> {
                    }
                }
            }

            override fun onDishSwipedRemove(item: CartBaseAdapterItem) {
                when (item) {
                    is CartAdapterItem -> {
                        val orderItemId = item.customOrderItem.orderItem.id
                        viewModel.removeSingleOrderItemId(orderItemId)
                        viewModel.logSwipeDishInCart(
                            Constants.EVENT_SWIPE_REMOVE_DISH_IN_CART,
                            item.customOrderItem
                        )
                    }
                    is UpsaleAdapterItem -> {
                    }
                    else -> {
                    }
                }
            }

            override fun onCartItemClicked(customOrderItem: CustomOrderItem) {
                Log.d(UpSaleNCartBottomSheet.TAG, "onCartItemClicked: $customOrderItem")
                viewModel.onCartItemClicked(customOrderItem)
                viewModel.logSwipeDishInCart(Constants.EVENT_CLICK_DISH_IN_CART, customOrderItem)
            }
        }

    override fun thresholdAchieved() {
        viewModel.reportThresholdAchievedEvent()
    }

    override fun viewClicked() {
        viewModel.reportViewClickedEvent()
    }

}