package com.bupp.wood_spoon_eaters.custom_views.orders_bottom_bar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.bupp.wood_spoon_eaters.R
import com.bupp.wood_spoon_eaters.utils.Constants
import kotlinx.android.synthetic.main.orders_bottom_bar.view.*
import kotlinx.android.synthetic.main.orders_bottom_bar.view.statusBottomBarPrice
import kotlinx.android.synthetic.main.orders_bottom_bar.view.statusBottomBarTitle
import kotlinx.android.synthetic.main.status_bottom_bar.view.*
import java.text.DecimalFormat


class OrdersBottomBar : FrameLayout{

    private var curType: Int = -1
    var showSep = 0
    var ordersVisible = false
    var checkoutVisible = false

    var listener: OrderBottomBatListener? = null
    interface OrderBottomBatListener{
        fun onBottomBarOrdersClick(curType: Int)
        fun onBottomBarCheckoutClick()
    }

    fun setOrdersBottomBarListener(listener: OrderBottomBatListener){
        this.listener = listener
    }



    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.orders_bottom_bar, this, true)

        initUi()

    }

    private fun initUi() {
        bottomBarActiveOrders.setOnClickListener { listener?.onBottomBarOrdersClick(curType) }
        bottomBarCheckout.setOnClickListener { listener?.onBottomBarCheckoutClick() }
    }

    fun handleBottomBar(showActiveOrders: Boolean? = null, showCheckout: Boolean? = null){
       showActiveOrders?.let{
           if(it){
               bottomBarActiveOrders.visibility = View.VISIBLE
               ordersVisible = true
           }else{
               bottomBarActiveOrders.visibility = View.GONE
               ordersVisible = false
           }
       }
       showCheckout?.let{
           if(it){
               bottomBarCheckout.visibility = View.VISIBLE
               checkoutVisible = true
           }else{
               bottomBarCheckout.visibility = View.GONE
               checkoutVisible = false
           }
       }
       checkSepEnable()
   }

    private fun checkSepEnable() {
        if(ordersVisible && checkoutVisible){
            bottomBarSeparator.visibility = View.VISIBLE
        }else{
            bottomBarSeparator.visibility = View.GONE
        }
    }

    fun updateStatusBottomBar(type: Int? = null, price: Double? = null, itemCount: Int? = null) {
        bottomBarActiveOrders.visibility = View.VISIBLE
        ordersVisible = true
        if(type != null){
            this.curType = type
        }
        if(price != null){
            val priceStr = DecimalFormat("##.##").format(price)
            statusBottomBarPrice.text = "$$priceStr"
        }
        when(curType){
            Constants.STATUS_BAR_TYPE_CART -> {
                statusBottomBarPrice.visibility = View.VISIBLE
                if(itemCount != null){
                    statusBottomBarTitle.text = "Add $itemCount To Cart"
                }
            }
            Constants.STATUS_BAR_TYPE_CHECKOUT -> {
                statusBottomBarPrice.visibility = View.VISIBLE
                statusBottomBarTitle.text = "Proceed To Checkout"

                bottomBarCheckout.visibility = View.GONE
                checkoutVisible = false
            }
            Constants.STATUS_BAR_TYPE_FINALIZE -> {
                statusBottomBarTitle.text = "PLACE AN ORDER"
            }
        }
        checkSepEnable()
    }


}