package com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet.sub_screens.cart

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.custom_views.adapters.DividerItemDecorator
import com.bupp.wood_spoon_eaters.databinding.FragmentCartBinding
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.cart_bottom_sheet.CartAdapter
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.cart_bottom_sheet.CartViewModel
import com.bupp.wood_spoon_eaters.features.new_order.sub_screen.upsale_cart_bottom_sheet.UpSaleNCartViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CartFragment : Fragment(), CartAdapter.CartAdapterListener {

    private val viewModel by viewModel<CartViewModel>()
    private val mainViewModel by sharedViewModel<UpSaleNCartViewModel>()
    private val binding: FragmentCartBinding by viewBinding()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initObservers()
    }

    private fun initUi() {
        with(binding){
            cartAdapter = CartAdapter()

            val divider: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.line_divider)
            cartFragList.initSwipeableRecycler(cartAdapter)
            cartFragList.addItemDecoration(DividerItemDecorator(divider))
//            cartFragList.setHasFixedSize(true)
        }
        viewModel.initData()
    }

    private fun initObservers() {
        viewModel.cartLiveData.observe(viewLifecycleOwner, {
            handleCartData(it)
        })
    }

    private fun handleCartData(data: CartViewModel.CartData) {
        with(binding){
            cartAdapter.submitList(data.items)
        }
    }

    override fun onCartBtnClicked() {
        mainViewModel.onCartBtnClick()
    }

}