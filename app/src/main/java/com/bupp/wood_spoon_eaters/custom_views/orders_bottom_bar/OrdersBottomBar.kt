package com.bupp.wood_spoon_eaters.custom_views.orders_bottom_bar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.bupp.wood_spoon_eaters.R
import kotlinx.android.synthetic.main.orders_bottom_bar.view.*


class OrdersBottomBar : FrameLayout{

    var showSep = 0

    var listener: OrderBottomBatListener? = null
    interface OrderBottomBatListener{
        fun onBottomBarOrdersClick()
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
        bottomBarActiveOrders.setOnClickListener { listener?.onBottomBarOrdersClick() }
        bottomBarCheckout.setOnClickListener { listener?.onBottomBarCheckoutClick() }
    }

    fun handleBottomBar(showActiveOrders: Boolean? = null, showCheckout: Boolean? = null){
        var ordersVisible = false
        var checkoutVisible = false
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
       if(ordersVisible && checkoutVisible){
           bottomBarSeparator.visibility = View.VISIBLE
       }else{
           bottomBarSeparator.visibility = View.GONE
       }
   }


}