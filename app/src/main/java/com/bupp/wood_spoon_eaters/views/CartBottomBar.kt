package com.bupp.wood_spoon_eaters.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.databinding.OrdersBottomBarBinding
import java.text.DecimalFormat


class CartBottomBar @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private var binding: OrdersBottomBarBinding = OrdersBottomBarBinding.inflate(LayoutInflater.from(context), this, true)


//    private var curType: Int = -1
    private lateinit var curType: BottomBarTypes
    var ordersVisible = false
    var checkoutVisible = false

    var listener: OrderBottomBatListener? = null

    interface OrderBottomBatListener {
        fun onCartBottomBarOrdersClick(curType: BottomBarTypes)
        fun onBottomBarCheckoutClick()
    }

    fun setCartBottomBarListener(listener: OrderBottomBatListener) {
        this.listener = listener
        initUi()
    }

    private fun initUi() {
        with(binding) {
            ordersBottomBarOrder.setOnClickListener { listener?.onCartBottomBarOrdersClick(curType) }
            ordersBottomBarCheckout.setOnClickListener { listener?.onBottomBarCheckoutClick() }
        }
    }

    fun handleBottomBar(showActiveOrders: Boolean? = null, showCheckout: Boolean? = null) {
        with(binding) {
            showActiveOrders?.let {
                if (it) {
                    ordersBottomBarOrder.visibility = View.VISIBLE
                } else {
                    ordersBottomBarOrder.visibility = View.GONE
                }
                    ordersVisible = it
            }
            showCheckout?.let {
                if (it) {
                    ordersBottomBarCheckout.visibility = View.VISIBLE
                } else {
                    ordersBottomBarCheckout.visibility = View.GONE
                }
                    checkoutVisible = it
            }
            checkSepEnable()
        }
    }

    private fun checkSepEnable() {
        with(binding) {
            Log.d("wowOrderBottomBar", "ordersVisible $ordersVisible, checkoutVisible: $checkoutVisible")
                bottomBarSeparator.isVisible = ordersVisible && checkoutVisible
        }
    }

    fun updateStatusBottomBarByType(type: BottomBarTypes? = null, price: Double? = null, itemCount: Int? = null, totalPrice: Double? = null) {
        Log.d(TAG, "bottom bar updating: $type")
        type?.let{
            this.curType = it
        hideAll()
            when (type) {
                BottomBarTypes.ADD_TO_CART -> {
                    val text = context.getString(R.string.bottom_bar_item_counter, itemCount.toString())
                    showUpperBarAndUpdate(text, price)
                }
                BottomBarTypes.PLACE_AN_ORDER ->{
                    val text = context.getString(R.string.bottom_bar_place_an_order)
                    showUpperBarAndUpdate(text, price)
                }
                BottomBarTypes.TRACK_YOUR_ORDER ->{
                    val text = context.getString(R.string.bottom_bar_tract_your_order)
                    showUpperBarAndUpdate(text, price)
                }
                BottomBarTypes.PROCEED_TO_CHECKOUT -> {
                    val text = context.getString(R.string.bottom_bar_procees_to_cart)
                    showLowerBarAndUpdate(text, price)
                }
                BottomBarTypes.ADD_TO_CART_OR_CHECKOUT -> {
                    val text1 = context.getString(R.string.bottom_bar_item_counter, itemCount.toString())
                    showUpperBarAndUpdate(text1, price)

                    val text2 = context.getString(R.string.bottom_bar_procees_to_cart)
                    showLowerBarAndUpdate(text2, totalPrice)
                }
            }
            checkSepEnable()
        }

    }

    private fun showUpperBarAndUpdate(text: String, price: Double?) {
        with(binding){
            ordersVisible = true
            ordersBottomBarOrder.visibility = View.VISIBLE
            ordersBottomBarOrderTitle.text = text
            price?.let {
                val priceStr = DecimalFormat("##.##").format(price)
                ordersBottomBarOrderPrice.text = "$$priceStr"
                ordersBottomBarOrderPrice.visibility = View.VISIBLE
            }
        }
    }


    private fun showLowerBarAndUpdate(text: String, price: Double?) {
        with(binding){
            checkoutVisible = true
            ordersBottomBarCheckout.visibility = View.VISIBLE
            price?.let {
                val checkoutPriceStr = DecimalFormat("##.##").format(it)
                ordersBottomBarCheckoutPrice.text = "$$checkoutPriceStr"
                ordersBottomBarCheckoutPrice.visibility = View.VISIBLE
            }
            ordersBottomBarCheckoutTitle.text = text
        }
    }

    private fun hideAll() {
        with(binding){
            ordersBottomBarOrder.visibility = View.GONE
            ordersBottomBarCheckout.visibility = View.GONE
            checkoutVisible = false
            ordersVisible = false
        }
    }

    enum class BottomBarTypes{
        ADD_TO_CART,
        PLACE_AN_ORDER,
        TRACK_YOUR_ORDER,
        PROCEED_TO_CHECKOUT,
        ADD_TO_CART_OR_CHECKOUT,
    }

    companion object{
        const val TAG = "wowCartBottomBar"
    }


}